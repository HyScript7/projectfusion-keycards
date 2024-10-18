package io.github.hyscript7.projectfusion.keycards2;

import io.github.hyscript7.projectfusion.keycards2.blocks.impl.CardReaderBlockFactoryImpl;
import io.github.hyscript7.projectfusion.keycards2.blocks.interfaces.CardReaderBlockFactory;
import io.github.hyscript7.projectfusion.keycards2.config.impl.ConfigImpl;
import io.github.hyscript7.projectfusion.keycards2.config.interfaces.Config;
import io.github.hyscript7.projectfusion.keycards2.data.impl.CardReaderImpl;
import io.github.hyscript7.projectfusion.keycards2.data.impl.KeyCardImpl;
import io.github.hyscript7.projectfusion.keycards2.items.impl.CardReaderItemFactoryImpl;
import io.github.hyscript7.projectfusion.keycards2.items.interfaces.CardReaderItemFactory;
import io.github.hyscript7.projectfusion.keycards2.listeners.CardReaderCreateListener;
import io.github.hyscript7.projectfusion.keycards2.listeners.CardReaderDestroyListener;
import io.github.hyscript7.projectfusion.keycards2.listeners.CardReaderInteractListener;
import org.bukkit.Bukkit;
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
        // Create singleton config
        config = new ConfigImpl(this);
        // Load config
        config.load();
        // Internals
        CardReaderItemFactory cardReaderItemFactory = new CardReaderItemFactoryImpl();
        CardReaderBlockFactory cardReaderBlockFactory = new CardReaderBlockFactoryImpl();
        // Register commands
        // Register listeners
        getServer().getPluginManager().registerEvents(new CardReaderCreateListener(config.getPluginData().getCardReaderAggregator(), cardReaderItemFactory, cardReaderBlockFactory), this);
        getServer().getPluginManager().registerEvents(new CardReaderDestroyListener(config.getPluginData().getCardReaderAggregator(), cardReaderBlockFactory), this);
        getServer().getPluginManager().registerEvents(new CardReaderInteractListener(config.getPluginData().getCardReaderAggregator(), cardReaderItemFactory, cardReaderBlockFactory, config.getPluginData().getKeyCardAggregator()), this);
    }

    @Override
    public void onDisable() {
        config.save();
    }

    public Config getKeyCardsConfig() {
        return config;
    }
}
