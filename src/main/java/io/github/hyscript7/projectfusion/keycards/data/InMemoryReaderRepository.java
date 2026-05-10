package io.github.hyscript7.projectfusion.keycards.data;

import io.github.hyscript7.projectfusion.keycards.readers.Reader;
import io.github.hyscript7.projectfusion.keycards.readers.ReaderRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InMemoryReaderRepository implements ReaderRepository {

    private final Set<Reader> readers = new HashSet<>();

    @Override
    public void save(Reader reader) {
        // Remove-then-add ensures the set holds the latest object when
        // a new Reader is created for an already-known location.
        readers.remove(reader);
        readers.add(reader);
    }

    @Override
    public void delete(Reader reader) {
        readers.remove(reader);
    }

    @Override
    public List<Reader> findAll() {
        return List.copyOf(readers);
    }
}
