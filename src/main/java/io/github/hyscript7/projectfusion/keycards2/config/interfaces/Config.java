package io.github.hyscript7.projectfusion.keycards2.config.interfaces;

/*
 * This interface describes the Keycards Configuration file.
 * It wraps the PaperMC Configuration Loader and enables us to easily write & read values from it.
 * This class ignores internal plugin data. If you need to change those, use the PluginData class.
 */
public interface Config {
    void save();
    void load();
}
