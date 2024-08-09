package io.github.hyscript7.projectfusion.keycards.data;

import io.github.hyscript7.projectfusion.keycards.Keycards;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.logging.Logger;

public class Config {
    private final FileConfiguration config;
    private final Logger logger = Keycards.getPlugin(Keycards.class).getLogger();
    private List<Keycard> keycards = null;
    private List<KeycardDoor> doors = null;
    private int nextKeycardId = 1;
    private int nextDoorId = 1;
    private int validationIndex = 0;

    public Config() {
        this.config = Keycards.getPlugin(Keycards.class).getConfig();
        try {
            keycards = (List<Keycard>) config.getList("keycards");
        } catch (NullPointerException e) {
            keycards = List.of();
            config.set("keycards", List.of());
            logger.warning("Keycards not found in config, defaulting to empty list. If this is a mistake, please restore the config from a backup if you have one.");
        }
        try {
            doors = (List<KeycardDoor>) config.getList("doors");
        } catch (NullPointerException e) {
            doors = List.of();
            config.set("doors", List.of());
            logger.warning("Doors not found in config, defaulting to empty list. If this is a mistake, please restore the config from a backup if you have one.");
        }
        try {
            nextKeycardId = (int) config.get("keycards-ai");
        } catch (NullPointerException e) {
            nextKeycardId = 0;
            config.set("keycards-ai", 0);
            logger.warning("Keycard id autoincrement not found in config, defaulting to keycard id 0. If this is a mistake, stop the server and set it manually, then restart the server.");
        }
        try {
            nextDoorId = (int) config.get("doors-ai");
        } catch (NullPointerException e) {
            nextDoorId = 0;
            config.set("doors-ai", 0);
            logger.warning("Door id autoincrement not found in config, defaulting to door id 0. If this is a mistake, stop the server and set it manually, then restart the server.");
        }
        try {
            validationIndex = (int) config.get("validity-class");
        } catch (NullPointerException e) {
            validationIndex = 0;
            config.set("validity-class", 0);
            logger.warning("Validation Index was not found in config, defaulting to validation index 0. If this was a mistake, you can change the validation index either in the config or using in-game commands.");
        }
        logger.info("Loaded " + keycards.size() + " keycards and " + doors.size() + " doors.");
        this.keycards.forEach(keycard -> Keycards.getPlugin(Keycards.class).getLogger().info(keycard.serialize().toString()));
        this.doors.forEach(door -> Keycards.getPlugin(Keycards.class).getLogger().info(door.serialize().toString()));
    }

    public void save() {
        this.config.set("keycards", keycards);
        this.config.set("doors", doors);
        this.config.set("keycards-ai", nextKeycardId);
        this.config.set("doors-ai", nextDoorId);
        this.config.set("validity-class", validationIndex);
        Keycards.getPlugin(Keycards.class).saveConfig();
    }

    public List<Keycard> getKeycards() {
        return keycards;
    }

    public List<KeycardDoor> getDoors() {
        return doors;
    }

    public int nextKeycardId() {
        nextKeycardId++;
        return nextKeycardId;
    }

    public int nextDoorId() {
        nextDoorId++;
        return nextDoorId;
    }

    public int getValidationIndex() {
        return validationIndex;
    }

    public void setValidationIndex(int validationIndex) {
        this.validationIndex = validationIndex;
    }

    public boolean requirePermissionToUse() {
        return config.getBoolean("require-use-permission");
    }
}
