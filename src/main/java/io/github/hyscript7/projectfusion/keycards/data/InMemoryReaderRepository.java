package io.github.hyscript7.projectfusion.keycards.data;

import io.github.hyscript7.projectfusion.keycards.readers.Reader;
import io.github.hyscript7.projectfusion.keycards.readers.ReaderRepository;

import java.util.*;

public class InMemoryReaderRepository implements ReaderRepository {
    private final Set<Reader> readers = new HashSet<>();

    @Override
    public void save(Reader reader) {
        readers.add(reader);
    }

    @Override
    public void delete(Reader reader) {
        readers.remove(reader);
    }

    @Override
    public List<Reader> findAll() {
        return readers.stream().toList();
    }
}
