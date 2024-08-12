package io.github.hyscript7.projectfusion.keycards2.config.impl;

import io.github.hyscript7.projectfusion.keycards2.KeyCardsPlugin;
import io.github.hyscript7.projectfusion.keycards2.config.interfaces.Config;
import io.github.hyscript7.projectfusion.keycards2.data.impl.PluginDataImpl;
import io.github.hyscript7.projectfusion.keycards2.data.interfaces.PluginData;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigImpl implements Config {
    private final KeyCardsPlugin plugin;
    private final FileConfiguration fileConfiguration;
    private final PluginData pluginData;

    public ConfigImpl(KeyCardsPlugin plugin) {
        this.plugin = plugin;
        this.fileConfiguration = plugin.getConfig();
        this.pluginData = new PluginDataImpl(this);
    }

    @Override
    public void save() {
        plugin.saveConfig();
    }

    @Override
    public void load() {
        fileConfiguration.set("keycards", pluginData.getKeyCards());
        fileConfiguration.set("cardreaders", pluginData.getCardReaders());
        plugin.saveConfig();
    }

    @Override
    public FileConfiguration getConfig() {
        return fileConfiguration;
    }

    @Override
    public PluginData getPluginData() {
        return pluginData;
    }
}
