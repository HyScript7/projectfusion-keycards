package io.github.hyscript7.projectfusion.keycards2.config.interfaces;

import io.github.hyscript7.projectfusion.keycards2.data.interfaces.PluginData;
import org.bukkit.configuration.file.FileConfiguration;

/*
 * This interface describes the Keycards Configuration file.
 * It wraps the PaperMC Configuration Loader and enables us to easily write & read values from it.
 * This class ignores internal plugin data. If you need to change those, use the PluginData class.
 */
public interface Config {
    void save();
    void load();

    FileConfiguration getConfig();
    PluginData getPluginData();
}
