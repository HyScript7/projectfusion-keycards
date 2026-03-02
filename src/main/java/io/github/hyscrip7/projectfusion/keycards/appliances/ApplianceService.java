package io.github.hyscrip7.projectfusion.keycards.appliances;

import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplianceService {
    private final ApplianceRepository applianceRepository;

    private final Map<Location, Appliance> appliances;

    public ApplianceService(ApplianceRepository applianceRepository) {
        this.applianceRepository = applianceRepository;
        this.appliances = new HashMap<>();
        for (Appliance appliance : applianceRepository.findAll()) {
            appliances.put(appliance.getLocation(), appliance);
        }
    }

    public @Nullable Appliance getAppliance(Location location) {
        return appliances.get(location);
    }

    public void saveAppliance(Appliance appliance) {
        appliances.put(appliance.getLocation(), appliance);
        applianceRepository.save(appliance);
    }

    public void deleteAppliance(Appliance appliance) {
        appliances.remove(appliance.getLocation());
        applianceRepository.delete(appliance);
    }

    public List<Appliance> getAllAppliances() {
        return new ArrayList<>(appliances.values());
    }
}
