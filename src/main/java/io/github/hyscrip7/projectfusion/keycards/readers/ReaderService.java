package io.github.hyscrip7.projectfusion.keycards.readers;

import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ReaderService {
    private final ReaderRepository readerRepository;

    private final Map<Location, Reader> readers;

    public ReaderService(ReaderRepository readerRepository) {
        this.readerRepository = readerRepository;
        this.readers = new HashMap<>();
        for (Reader reader : readerRepository.findAll()) {
            readers.put(reader.getLocation(), reader);
        }
    }

    public @Nullable Reader getReader(Location location) {
        return readers.get(location);
    }

    public void saveReader(Reader reader) {
        readers.put(reader.getLocation(), reader);
        readerRepository.save(reader);
    }

    public void deleteReader(Reader reader) {
        readers.remove(reader.getLocation());
        readerRepository.delete(reader);
    }
}
