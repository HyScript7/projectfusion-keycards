package io.github.hyscript7.projectfusion.keycards.data.yaml;

import io.github.hyscript7.projectfusion.keycards.appliances.Appliance;
import io.github.hyscript7.projectfusion.keycards.data.InMemoryApplianceRepository;
import io.github.hyscript7.projectfusion.keycards.util.LocationUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class YamlApplianceRepository extends InMemoryApplianceRepository {
    private final File dataFile;
    private final YamlConfiguration config;
    // Must be single threaded so that everything happens in the right order
    private final ExecutorService ioExecutor = Executors.newSingleThreadExecutor();

    public YamlApplianceRepository(JavaPlugin plugin) {
        plugin.getDataFolder().mkdirs();
        this.dataFile = new File(plugin.getDataFolder(), "appliances.yml");
        this.config = YamlConfiguration.loadConfiguration(dataFile);
        loadAllFromFile();
    }

    private void loadAllFromFile() {
        if (!dataFile.exists()) return;

        ConfigurationSection section = config.getConfigurationSection("appliances");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            Object obj = section.get(key);
            if (obj instanceof Appliance) {
                super.save((Appliance) obj);
            }
        }
    }

    @Override
    public void save(Appliance appliance) {
        // 1. Update In-Memory immediately (Main Thread)
        super.save(appliance);

        // 2. Queue the File IO (Background Thread)
        String key = LocationUtil.toKey(appliance.getLocation());
        ioExecutor.submit(() -> {
            synchronized (config) {
                config.set("appliances." + key, appliance);
                saveFile();
            }
        });
    }

    @Override
    public void delete(Appliance appliance) {
        // 1. Update In-Memory immediately
        super.delete(appliance);

        // 2. Queue the File IO
        String key = LocationUtil.toKey(appliance.getLocation());
        ioExecutor.submit(() -> {
            synchronized (config) {
                config.set("appliances." + key, null);
                saveFile();
            }
        });
    }

    private void saveFile() {
        try {
            config.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Call this in your plugin's onDisable!
    public void shutdown() {
        ioExecutor.shutdown();
    }
}