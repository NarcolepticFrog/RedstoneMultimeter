package narcolepticfrog.rsmm;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import java.util.Arrays;

public class MeterCommand extends CommandBase {
    LiteModRedstoneMultimeter modInstance;

    public MeterCommand(LiteModRedstoneMultimeter modInstance) {
        this.modInstance = modInstance;
    }

    @Override
    public String getName() {
        return "meter";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return  "/meter name [name]\n" +
                "/meter name [ix] [name]\n" +
                "/meter removeAll\n" +
                "/meter duration [ticks]";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args[0].equals("name")) {
            if (args.length == 2) {
                modInstance.renameLastMeter(args[1]);
            } else {
                try {
                    int ix = Integer.parseInt(args[1]);
                    modInstance.renameMeter(ix, args[2]);
                } catch (Exception e) {
                    throw new CommandException("index must be an integer");
                }
            }
        } else if (args[0].equals("removeAll")) {
            modInstance.removeAll();
        } else if (args[0].equals("duration")) {
            try {
                int duration = Integer.parseInt(args[1]);
                modInstance.setDuration(duration);
            } catch (Exception e) {
                throw new CommandException("duration must be an integer");
            }
        }
    }
}
