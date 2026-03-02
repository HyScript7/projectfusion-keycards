package io.github.hyscrip7.projectfusion.keycards;

import io.github.hyscrip7.projectfusion.keycards.appliances.Appliance;
import io.github.hyscrip7.projectfusion.keycards.appliances.handlers.ApplianceHandler;
import io.github.hyscrip7.projectfusion.keycards.appliances.handlers.ApplianceManager;
import io.github.hyscrip7.projectfusion.keycards.readers.Reader;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class ReaderManager {
    private final LinkManager linkManager;
    private final ApplianceManager applianceManager;

    private final Map<Reader, BukkitTask> activeReaders;

    public ReaderManager(LinkManager linkManager, ApplianceManager applianceManager) {
        this.linkManager = linkManager;
        this.applianceManager = applianceManager;
        this.activeReaders = new HashMap<>();
    }

    public void trigger(Reader reader) {
        if (activeReaders.containsKey(reader)) {
            activeReaders.get(reader).cancel();
            activeReaders.remove(reader);
        }
        activate(reader);
        BukkitTask task = Bukkit.getScheduler().runTaskLater(ProjectFusionKeycards.getPlugin(ProjectFusionKeycards.class), () -> {
            deactivate(reader);
        }, reader.getPulseDurationInTicks());
        activeReaders.put(reader, task);
    }

    private synchronized void activate(Reader reader) {
        for (Appliance appliance : linkManager.getAppliances(reader)) {
            if (appliance.shouldBeInactive()) {
                ApplianceHandler handler = applianceManager.getHandler(appliance);
                if (handler == null) continue;
                handler.activate(appliance.getBlock());
            }
            appliance.incrementActiveReaders(reader);
        }
    }

    private synchronized void deactivate(Reader reader) {
        for (Appliance appliance : linkManager.getAppliances(reader)) {
            appliance.decrementActiveReaders(reader);
            if (appliance.shouldBeInactive()) {
                ApplianceHandler handler = applianceManager.getHandler(appliance);
                if (handler == null) continue;
                handler.deactivate(appliance.getBlock());
            }
        }
        activeReaders.remove(reader);
    }
}
