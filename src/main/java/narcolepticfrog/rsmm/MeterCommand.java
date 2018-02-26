package narcolepticfrog.rsmm;

import narcolepticfrog.rsmm.server.RSMMServer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentKeybind;
import net.minecraft.util.text.TextComponentString;

import java.util.Collections;
import java.util.List;

public class MeterCommand extends CommandBase {
    RSMMServer rsmmServer;

    public MeterCommand(RSMMServer rsmmServer) {
        this.rsmmServer = rsmmServer;
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

        if (!(sender instanceof EntityPlayerMP)) {
            return;
        }
        EntityPlayerMP player = (EntityPlayerMP)sender;

        if (args[0].equals("name")) {
            if (rsmmServer.getNumMeters(player) <= 0) {
                throw new CommandException("redstonemultimeter.command.meter.rename.noMeters", new TextComponentKeybind("key.redstonemultimeter.toggle"));
            }
            if (args.length == 2) {
                rsmmServer.renameLastMeter(player, args[1]);
                notifyCommandListener(sender, this, "redstonemultimeter.command.meter.rename.last", args[1]);
            } else if (args.length == 3) {
                int ix = parseInt(args[1], 0, rsmmServer.getNumMeters(player) - 1);
                rsmmServer.renameMeter(player, ix, args[2]);
                notifyCommandListener(sender, this, "redstonemultimeter.command.meter.rename.index", ix, args[2]);
            } else {
                throw new WrongUsageException(USAGE);
            }
        } else if (args[0].equals("color")) {
            if (rsmmServer.getNumMeters(player) <= 0) {
                throw new CommandException("redstonemultimeter.command.meter.recolor.noMeters", new TextComponentKeybind("key.redstonemultimeter.toggle"));
            }
            if (args.length == 2) {
                rsmmServer.recolorLastMeter(player, ColorUtils.parseColor(args[1]));
                notifyCommandListener(sender, this, "redstonemultimeter.command.meter.recolor.last", args[1]);
            } else if (args.length == 3) {
                int ix = parseInt(args[1], 0, rsmmServer.getNumMeters(player) - 1);
                rsmmServer.recolorMeter(player, ix, ColorUtils.parseColor(args[2]));
                notifyCommandListener(sender, this, "redstonemultimeter.command.meter.recolor.index", ix, args[2]);
            } else {
                throw new WrongUsageException(USAGE);
            }
        } else if (args[0].equals("removeAll")) {
            if (args.length != 1) {
                throw new WrongUsageException(USAGE);
            }
            rsmmServer.removeAllMeters(player);
            notifyCommandListener(sender, this, "redstonemultimeter.command.meter.removedAll");
        } else if (args[0].equals("group")) {
            if (args.length != 2) {
                throw new WrongUsageException(USAGE);
            }
            rsmmServer.changePlayerSubscription(player, args[1]);
            notifyCommandListener(sender, this, "redstonemultimeter.command.meter.subscribed",
                    args[1]);
        } else if (args[0].equals("listGroups")) {

            TextComponentString response = new TextComponentString("Meter Groups:");
            for (String name : rsmmServer.getGroupNames()) {
                response.appendText("\n  " + name);
            }
            sender.sendMessage(response);

        } else {
            throw new WrongUsageException(USAGE);
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server,
            ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "name", "color", "removeAll", "group", "listGroups");
        } else if (args.length == 2 && args[0].equals("group")) {
            return getListOfStringsMatchingLastWord(args, rsmmServer.getGroupNames());
        } else {
            return Collections.<String>emptyList();
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
    
}
