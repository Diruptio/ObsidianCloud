package de.obsidiancloud.platform.paper;

import de.obsidiancloud.common.OCServer;
import de.obsidiancloud.platform.ObsidianCloudPlatform;
import de.obsidiancloud.platform.paper.listener.PlayerListener;
import de.obsidiancloud.platform.paper.local.LocalPaperOCServer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class ObsidianCloudPaper extends JavaPlugin {
    private static ObsidianCloudPaper instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        String serverData = System.getenv("OC_SERVER_DATA");
        ObsidianCloudPlatform.onEnable(
                new LocalPaperOCServer(OCServer.TransferableServerData.fromString(serverData)));

        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
    }

    @Override
    public void onDisable() {
        ObsidianCloudPlatform.onDisable();
    }

    public static @NotNull ObsidianCloudPaper getInstance() {
        return instance;
    }
}
