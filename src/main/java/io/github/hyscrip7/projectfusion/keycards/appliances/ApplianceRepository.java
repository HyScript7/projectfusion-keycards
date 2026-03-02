package io.github.hyscrip7.projectfusion.keycards.appliances;

import java.util.List;

public interface ApplianceRepository {
    void save(Appliance appliance);
    void delete(Appliance appliance);
    List<Appliance> findAll();
    default void shutdown() {};
}
