package io.github.hyscript7.projectfusion.keycards2;

import io.github.hyscript7.projectfusion.keycards2.config.interfaces.Config;
import io.github.hyscript7.projectfusion.keycards2.data.impl.CardReaderImpl;
import io.github.hyscript7.projectfusion.keycards2.data.impl.KeyCardImpl;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

public class KeyCardsPlugin extends JavaPlugin {
    private Config config;
    @Override
    public void onEnable() {
        // Register custom serializable classes
        ConfigurationSerialization.registerClass(KeyCardImpl.class);
        ConfigurationSerialization.registerClass(CardReaderImpl.class);
        // Create default config if it doesn't exist yet
        saveResource("config.yml", false);
        // TODO: Register custom listeners
    }

    @Override
    public void onDisable() {
    }
}
