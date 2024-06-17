package de.obsidiancloud.platform.paper;

import de.obsidiancloud.platform.ObsidianCloudPlatform;
import de.obsidiancloud.platform.paper.listener.PlayerListener;
import de.obsidiancloud.platform.paper.local.LocalPaperOCServer;
import org.bukkit.plugin.java.JavaPlugin;

public class ObsidianCloudPaper extends JavaPlugin {
    @Override
    public void onEnable() {
        ObsidianCloudPlatform.onEnable();
        String serverTask = System.getenv("OC_SERVER_TASK");
        String serverName = System.getenv("OC_SERVER_NAME");
        boolean serverAutoStart = Boolean.parseBoolean(System.getenv("OC_SERVER_AUTOSTART"));
        ObsidianCloudPlatform.onEnable(
                new LocalPaperOCServer(serverTask, serverName, serverAutoStart));
    }

    @Override
    public void onDisable() {
        ObsidianCloudPlatform.onDisable();
    }
}
