package io.github.hyscript7.projectfusion.keycards2;

import io.github.hyscript7.projectfusion.keycards2.config.impl.ConfigImpl;
import io.github.hyscript7.projectfusion.keycards2.config.interfaces.Config;
import org.bukkit.plugin.java.JavaPlugin;

public class KeyCardsPlugin extends JavaPlugin {
    private Config config;
    @Override
    public void onEnable() {
        // TODO: Register custom serializable classes
        // Create default config if it doesn't exist yet
        saveResource("config.yml", false);
        // TODO: Create default data file if it doesn't exist yet
        // TODO: Register custom listeners
    }

    @Override
    public void onDisable() {
    }
}
