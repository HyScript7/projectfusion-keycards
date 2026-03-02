package io.github.hyscrip7.projectfusion.keycards.readers;

import java.util.List;

public interface ReaderRepository {
    void save(Reader reader);
    void delete(Reader reader);
    List<Reader> findAll();
    default void shutdown() {};
}
