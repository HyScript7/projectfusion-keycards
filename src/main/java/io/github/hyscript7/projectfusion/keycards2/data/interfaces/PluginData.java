package io.github.hyscript7.projectfusion.keycards2.data.interfaces;

import java.util.List;

/*
 * This interface describes the Keycards Plugin's internal data.
 * It wraps the PaperMC Configuration Loader and enables us to easily write & read values from it.
 * This class ignores plugin configurations. If you need to change those, use the Config class.
 * This class lets us work with Keycards and Doors, saving them, deleting them or fetching them by id.
 */
public interface PluginData {
    void save();
    void load();
    List<KeyCard> getKeyCards();
    List<CardReader> getCardReaders();
}
