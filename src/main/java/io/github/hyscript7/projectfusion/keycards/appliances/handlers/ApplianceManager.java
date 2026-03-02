package io.github.hyscript7.projectfusion.keycards.appliances.handlers;

import io.github.hyscript7.projectfusion.keycards.appliances.Appliance;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class ApplianceManager {
    private final Set<ApplianceHandler> handlers;

    public ApplianceManager(Set<ApplianceHandler> handlers) {
        this.handlers = handlers;
    }

    public ApplianceManager() {
        this.handlers = new HashSet<>();
    }

    public void registerHandler(ApplianceHandler handler) {
        handlers.add(handler);
    }

    public @Nullable ApplianceHandler getHandler(Appliance appliance) {
        for (ApplianceHandler handler : handlers) {
            if (handler.supports(appliance.getBlock())) {
                return handler;
            }
        }
        return null;
    }

}
