package io.github.hyscrip7.projectfusion.keycards.data;

import io.github.hyscrip7.projectfusion.keycards.appliances.Appliance;
import io.github.hyscrip7.projectfusion.keycards.appliances.ApplianceRepository;

import java.util.*;

public class InMemoryApplianceRepository implements ApplianceRepository {
    private final Set<Appliance> appliances = new HashSet<>();

    @Override
    public void save(Appliance appliance) {
        appliances.add(appliance);
    }

    @Override
    public void delete(Appliance appliance) {
        appliances.remove(appliance);
    }

    @Override
    public List<Appliance> findAll() {
        return appliances.stream().toList();
    }
}
