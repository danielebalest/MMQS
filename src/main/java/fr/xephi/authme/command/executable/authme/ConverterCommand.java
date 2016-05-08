package fr.xephi.authme.command.executable.authme;

import fr.xephi.authme.AuthMe;
import fr.xephi.authme.command.CommandService;
import fr.xephi.authme.command.ExecutableCommand;
import fr.xephi.authme.converter.Converter;
import fr.xephi.authme.converter.CrazyLoginConverter;
import fr.xephi.authme.converter.RakamakConverter;
import fr.xephi.authme.converter.RoyalAuthConverter;
import fr.xephi.authme.converter.SqliteToSql;
import fr.xephi.authme.converter.vAuthConverter;
import fr.xephi.authme.converter.xAuthConverter;
import fr.xephi.authme.output.MessageKey;
import org.bukkit.command.CommandSender;

import javax.inject.Inject;
import java.util.List;

public class ConverterCommand implements ExecutableCommand {

    @Inject
    private AuthMe authMe;

    @Override
    public void executeCommand(CommandSender sender, List<String> arguments, CommandService commandService) {
        // Get the conversion job
        String job = arguments.get(0);

        // Determine the job type
        ConvertType jobType = ConvertType.fromName(job);
        if (jobType == null) {
            commandService.send(sender, MessageKey.ERROR);
            return;
        }

        // Get the proper converter instance
        Converter converter = null;
        switch (jobType) {
            case XAUTH:
                converter = new xAuthConverter(authMe, sender);
                break;
            case CRAZYLOGIN:
                converter = new CrazyLoginConverter(authMe, sender);
                break;
            case RAKAMAK:
                converter = new RakamakConverter(authMe, sender);
                break;
            case ROYALAUTH:
                converter = new RoyalAuthConverter(authMe);
                break;
            case VAUTH:
                converter = new vAuthConverter(authMe, sender);
                break;
            case SQLITETOSQL:
                converter = new SqliteToSql(authMe, sender, commandService.getSettings());
                break;
            default:
                break;
        }

        // Run the convert job
        commandService.runTaskAsynchronously(converter);

        // Show a status message
        sender.sendMessage("[AuthMe] Successfully converted from " + jobType.getName());
    }

    public enum ConvertType {
        XAUTH("xauth"),
        CRAZYLOGIN("crazylogin"),
        RAKAMAK("rakamak"),
        ROYALAUTH("royalauth"),
        VAUTH("vauth"),
        SQLITETOSQL("sqlitetosql");

        final String name;

        ConvertType(String name) {
            this.name = name;
        }

        public static ConvertType fromName(String name) {
            for (ConvertType type : ConvertType.values()) {
                if (type.getName().equalsIgnoreCase(name)) {
                    return type;
                }
            }
            return null;
        }

        String getName() {
            return this.name;
        }
    }
}
