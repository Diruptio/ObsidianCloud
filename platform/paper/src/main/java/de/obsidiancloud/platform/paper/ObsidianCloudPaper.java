package de.obsidiancloud.platform.paper;

import de.obsidiancloud.platform.ObsidianCloudPlatform;
import org.bukkit.plugin.java.JavaPlugin;

public class ObsidianCloudPaper extends JavaPlugin {
    @Override
    public void onEnable() {
        ObsidianCloudPlatform.onEnable();
    }

    @Override
    public void onDisable() {
        ObsidianCloudPlatform.onDisable();
    }
}
