package io.github.hyscript7.projectfusion.keycards.data.yaml;

import io.github.hyscript7.projectfusion.keycards.data.InMemoryReaderRepository;
import io.github.hyscript7.projectfusion.keycards.readers.Reader;
import io.github.hyscript7.projectfusion.keycards.util.LocationUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class YamlReaderRepository extends InMemoryReaderRepository {
    private final File dataFile;
    private final YamlConfiguration config;
    // Must be single threaded so that everything happens in the right order
    private final ExecutorService ioExecutor = Executors.newSingleThreadExecutor();

    public YamlReaderRepository(JavaPlugin plugin) {
        plugin.getDataFolder().mkdirs();
        this.dataFile = new File(plugin.getDataFolder(), "readers.yml");
        this.config = YamlConfiguration.loadConfiguration(dataFile);
        loadAllFromFile();
    }

    private void loadAllFromFile() {
        if (!dataFile.exists()) return;

        ConfigurationSection section = config.getConfigurationSection("readers");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            Object obj = section.get(key);
            if (obj instanceof Reader) {
                // Load into the in-memory parent map
                super.save((Reader) obj);
            }
        }
    }

    @Override
    public void save(Reader reader) {
        // 1. Update In-Memory (Instant for the game)
        super.save(reader);

        // 2. Queue Disk IO (Background)
        String key = LocationUtil.toKey(reader.getLocation());
        ioExecutor.submit(() -> {
            synchronized (config) {
                config.set("readers." + key, reader);
                saveFile();
            }
        });
    }

    @Override
    public void delete(Reader reader) {
        // 1. Update In-Memory
        super.delete(reader);

        // 2. Queue Disk IO
        String key = LocationUtil.toKey(reader.getLocation());
        ioExecutor.submit(() -> {
            synchronized (config) {
                config.set("readers." + key, null); // Deletes the entry
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

    public void shutdown() {
        ioExecutor.shutdown();
    }
}