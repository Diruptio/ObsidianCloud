package de.obsidiancloud.node.command;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.common.ObsidianCloudAPI;
import de.obsidiancloud.common.command.Command;
import de.obsidiancloud.common.command.CommandExecutor;
import de.obsidiancloud.node.local.LocalOCServer;
import de.obsidiancloud.node.network.packets.N2NScreenPacket;
import de.obsidiancloud.node.remote.RemoteOCServer;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public class ScreenCommand extends Command {
    public ScreenCommand() {
        super("screen");
        setDescription("Enables/disables screen mirroring of a server.");
        setUsage("screen <Server>");
        addAlias("scr");
    }

    @Override
    public void execute(@NotNull CommandExecutor executor, @NotNull String[] args) {
        if (args.length == 0) {
            executor.sendMessage("§cUsage: " + getUsage(executor));
            return;
        }

        OCServer server = ObsidianCloudAPI.get().getServer(args[0]);
        if (server == null) {
            executor.sendMessage("§cThe server §e" + args[0] + " §cdoes not exist.");
        } else if (server instanceof LocalOCServer localServer) {
            Set<CommandExecutor> screenReaders = localServer.getScreenReaders();
            if (screenReaders.contains(executor)) {
                screenReaders.remove(executor);
                executor.sendMessage("§aScreen mirroring of §e" + args[0] + " §ahas been disabled.");
            } else {
                screenReaders.add(executor);
                executor.sendMessage("§aScreen mirroring of §e" + args[0] + " §ahas been enabled.");
            }
        } else if (server instanceof RemoteOCServer remoteServer) {
            N2NScreenPacket packet = new N2NScreenPacket();
            packet.setExecutor(executor);
            packet.setServer(remoteServer.getName());
            remoteServer.getNode().getConnection().send(packet);
        }
    }
}
