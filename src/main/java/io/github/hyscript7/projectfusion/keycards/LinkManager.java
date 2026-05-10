package io.github.hyscript7.projectfusion.keycards;

import io.github.hyscript7.projectfusion.keycards.appliances.Appliance;
import io.github.hyscript7.projectfusion.keycards.appliances.ApplianceService;
import io.github.hyscript7.projectfusion.keycards.readers.Reader;
import io.github.hyscript7.projectfusion.keycards.readers.ReaderService;
import org.bukkit.Location;

import java.util.*;

/**
 * Maintains the in-memory reader → appliance lookup that drives keycard
 * activation.
 * <p>
 * <b>Bug fix:</b> the previous implementation used a {@code List<Appliance>}
 * as the map value, which allowed the same appliance to be appended multiple
 * times when a player clicked it with the Linking Wrench more than once.
 * This caused {@code activate()} / {@code deactivate()} to fire multiple times
 * per swipe and corrupted the active-reader counter.
 * <p>
 * The fix is to use a {@code Set<Appliance>}, which requires (and relies on)
 * {@link Appliance#equals}/{@link Appliance#hashCode} being defined by
 * block-position. A duplicate {@code link()} call is now silently idempotent
 * at both the {@link Appliance#linkedReaders} level (always was a
 * {@code HashSet<Location>}) and the lookup level (now a {@code Set}).
 */
public class LinkManager {

    /** reader → set of appliances it controls */
    private final Map<Reader, Set<Appliance>> links;

    private final ReaderService readerService;
    private final ApplianceService applianceService;

    public LinkManager(ReaderService readerService, ApplianceService applianceService) {
        this.readerService = readerService;
        this.applianceService = applianceService;
        this.links = new HashMap<>();
        loadLookup();
    }

    // -------------------------------------------------------------------------
    // Startup
    // -------------------------------------------------------------------------

    private void loadLookup() {
        links.clear();
        for (Appliance appliance : applianceService.getAllAppliances()) {
            for (Location readerLocation : appliance.getLinkedReaders()) {
                Reader reader = readerService.getReader(readerLocation);
                if (reader != null) {
                    getAppliancesFor(reader).add(appliance);
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Lifecycle purge (called on block-break)
    // -------------------------------------------------------------------------

    public void purgeReader(Reader reader) {
        links.remove(reader);
        readerService.deleteReader(reader);
    }

    public void purgeAppliance(Appliance appliance) {
        for (Reader reader : getReadersFor(appliance)) {
            unlink(reader, appliance, false);
        }
        applianceService.deleteAppliance(appliance);
    }

    // -------------------------------------------------------------------------
    // Link / unlink
    // -------------------------------------------------------------------------

    /**
     * Returns {@code true} if this reader–appliance pair is already linked
     * (so callers can give appropriate feedback without actually linking again).
     */
    public boolean isLinked(Reader reader, Appliance appliance) {
        return appliance.getLinkedReaders().contains(reader.getLocation());
    }

    public void link(Reader reader, Appliance appliance) {
        link(reader, appliance, true);
    }

    private void link(Reader reader, Appliance appliance, boolean save) {
        appliance.linkReader(reader);              // idempotent – HashSet<Location>
        getAppliancesFor(reader).add(appliance);   // idempotent – Set<Appliance>
        if (save) {
            applianceService.saveAppliance(appliance);
        }
    }

    public void unlink(Reader reader, Appliance appliance) {
        unlink(reader, appliance, true);
    }

    private void unlink(Reader reader, Appliance appliance, boolean save) {
        appliance.unlinkReader(reader);
        getAppliancesFor(reader).remove(appliance);
        if (save) {
            applianceService.saveAppliance(appliance);
        }
    }

    // -------------------------------------------------------------------------
    // Queries
    // -------------------------------------------------------------------------

    /** Returns the (live) set of appliances controlled by this reader. */
    public Set<Appliance> getAppliancesFor(Reader reader) {
        return links.computeIfAbsent(reader, r -> new LinkedHashSet<>());
    }

    /** Returns all readers that are currently linked to this appliance. */
    public List<Reader> getReadersFor(Appliance appliance) {
        return appliance.getLinkedReaders().stream()
                .map(readerService::getReader)
                .filter(Objects::nonNull)
                .toList();
    }

    // -------------------------------------------------------------------------
    // Legacy accessor — kept so ReaderManager compiles without changes
    // -------------------------------------------------------------------------

    /** @deprecated Use {@link #getAppliancesFor(Reader)} */
    @Deprecated(forRemoval = true)
    public Collection<Appliance> getAppliances(Reader reader) {
        return getAppliancesFor(reader);
    }
}
