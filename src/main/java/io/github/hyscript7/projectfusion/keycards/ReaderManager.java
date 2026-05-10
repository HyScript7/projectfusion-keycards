package io.github.hyscript7.projectfusion.keycards;

import io.github.hyscript7.projectfusion.keycards.appliances.Appliance;
import io.github.hyscript7.projectfusion.keycards.appliances.handlers.ApplianceHandler;
import io.github.hyscript7.projectfusion.keycards.appliances.handlers.ApplianceManager;
import io.github.hyscript7.projectfusion.keycards.readers.Reader;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class ReaderManager {

    private final LinkManager linkManager;
    private final ApplianceManager applianceManager;

    /** Tracks the pending deactivation task for each currently-active reader. */
    private final Map<Reader, BukkitTask> activeReaders = new HashMap<>();

    public ReaderManager(LinkManager linkManager, ApplianceManager applianceManager) {
        this.linkManager = linkManager;
        this.applianceManager = applianceManager;
    }

    /**
     * Triggers the reader: resets any existing deactivation timer, activates
     * all linked appliances, then schedules deactivation after the reader's
     * configured pulse duration.
     */
    public void trigger(Reader reader) {
        // Cancel any pending deactivation so retriggering extends the open time
        BukkitTask existing = activeReaders.remove(reader);
        if (existing != null) {
            existing.cancel();
        }

        activate(reader);

        BukkitTask task = Bukkit.getScheduler().runTaskLater(
                ProjectFusionKeycards.getPlugin(ProjectFusionKeycards.class),
                () -> deactivate(reader),
                reader.getPulseDurationInTicks()
        );
        activeReaders.put(reader, task);
    }

    private synchronized void activate(Reader reader) {
        for (Appliance appliance : linkManager.getAppliancesFor(reader)) {
            if (appliance.shouldBeInactive()) {
                ApplianceHandler handler = applianceManager.getHandler(appliance);
                if (handler == null) continue;
                handler.activate(appliance.getBlock());
            }
            appliance.incrementActiveReaders(reader);
        }
    }

    private synchronized void deactivate(Reader reader) {
        for (Appliance appliance : linkManager.getAppliancesFor(reader)) {
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
