package narcolepticfrog.rsmm;

import java.util.Collections;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentKeybind;

public class MeterCommand extends CommandBase {
    LiteModRedstoneMultimeter modInstance;

    public MeterCommand(LiteModRedstoneMultimeter modInstance) {
        this.modInstance = modInstance;
    }

    @Override
    public String getName() {
        return "meter";
    }

    private static final String USAGE = "redstonemultimeter.command.meter.usage";

    @Override
    public String getUsage(ICommandSender sender) {
        return USAGE;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            throw new WrongUsageException(USAGE);
        }

        if (args[0].equals("name")) {
            if (modInstance.getNumMeters() <= 0) {
                throw new CommandException("redstonemultimeter.command.meter.rename.noMeters", new TextComponentKeybind("key.redstonemultimeter.toggle"));
            }
            if (args.length == 2) {
                modInstance.renameLastMeter(args[1]);
                notifyCommandListener(sender, this, "redstonemultimeter.command.meter.rename.last", args[1]);
            } else if (args.length == 3) {
                int ix = parseInt(args[1], 0, modInstance.getNumMeters() - 1);
                modInstance.renameMeter(ix, args[2]);
                notifyCommandListener(sender, this, "redstonemultimeter.command.meter.rename.index", ix, args[2]);
            } else {
                throw new WrongUsageException(USAGE);
            }
        } else if (args[0].equals("removeAll")) {
            if (args.length != 1) {
                throw new WrongUsageException(USAGE);
            }
            modInstance.removeAll();
            notifyCommandListener(sender, this, "redstonemultimeter.command.meter.removedAll");
        } else if (args[0].equals("duration")) {
            if (args.length != 2) {
                throw new WrongUsageException(USAGE);
            }
            int duration = parseInt(args[1], 1);
            modInstance.setWindowLength(duration);
            notifyCommandListener(sender, this, "redstonemultimeter.command.meter.duration", duration);
        } else {
            throw new WrongUsageException(USAGE);
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server,
            ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "name", "removeAll", "duration");
        } else {
            return Collections.<String>emptyList();
        }
    }
}
