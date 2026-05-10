package io.github.hyscript7.projectfusion.keycards.data;

import io.github.hyscript7.projectfusion.keycards.appliances.Appliance;
import io.github.hyscript7.projectfusion.keycards.appliances.ApplianceRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InMemoryApplianceRepository implements ApplianceRepository {

    private final Set<Appliance> appliances = new HashSet<>();

    @Override
    public void save(Appliance appliance) {
        // Remove-then-add ensures the set holds the latest object when
        // a new Appliance is created for an already-known location.
        appliances.remove(appliance);
        appliances.add(appliance);
    }

    @Override
    public void delete(Appliance appliance) {
        appliances.remove(appliance);
    }

    @Override
    public List<Appliance> findAll() {
        return List.copyOf(appliances);
    }
}
