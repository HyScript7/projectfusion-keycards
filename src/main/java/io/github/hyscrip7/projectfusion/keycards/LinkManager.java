package io.github.hyscrip7.projectfusion.keycards;

import io.github.hyscrip7.projectfusion.keycards.appliances.Appliance;
import io.github.hyscrip7.projectfusion.keycards.appliances.ApplianceService;
import io.github.hyscrip7.projectfusion.keycards.readers.Reader;
import io.github.hyscrip7.projectfusion.keycards.readers.ReaderService;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinkManager {
    private final Map<Reader, List<Appliance>> links;

    private final ReaderService readerService;
    private final ApplianceService applianceService;

    public LinkManager(ReaderService readerService, ApplianceService applianceService) {
        this.readerService = readerService;
        this.applianceService = applianceService;
        this.links = new HashMap<>();
        loadLookup();
    }

    private void loadLookup() {
        links.clear();
        for (Appliance appliance : applianceService.getAllAppliances()) {
            for (Location readerLocation : appliance.getLinkedReaders()) {
                Reader reader = readerService.getReader(readerLocation);
                getAppliances(reader).add(appliance);
            }
        }
    }

    public void purgeReader(Reader reader) {
        links.remove(reader);
        readerService.deleteReader(reader);
    }

    public void purgeAppliance(Appliance appliance) {
        for (Reader reader : getReaders(appliance)) {
            unlink(reader, appliance, false);
        }
        applianceService.deleteAppliance(appliance);
    }

    public void link(Reader reader, Appliance appliance) {
        link(reader, appliance, true);
    }

    private void link(Reader reader, Appliance appliance, boolean save) {
        appliance.linkReader(reader);
        getAppliances(reader).add(appliance);
        if (save) {
            applianceService.saveAppliance(appliance);
        }
    }

    public void unlink(Reader reader, Appliance appliance) {
        unlink(reader, appliance, true);
    }

    private void unlink(Reader reader, Appliance appliance, boolean save) {
        appliance.unlinkReader(reader);
        getAppliances(reader).remove(appliance);
        if (save) {
            applianceService.saveAppliance(appliance);
        }
    }

    public List<Appliance> getAppliances(Reader reader) {
        return links.computeIfAbsent(reader, r -> new ArrayList<>());
    }

    public List<Reader> getReaders(Appliance appliance) {
        return appliance.getLinkedReaders().stream().map(readerService::getReader).toList();
    }
}
