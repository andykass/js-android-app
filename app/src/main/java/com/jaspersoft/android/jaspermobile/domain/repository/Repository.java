package com.jaspersoft.android.jaspermobile.domain.repository;

import java.util.List;

/**
 * @author Tom Koptel
 * @since 2.5
 */
public interface Repository<T> {
    void add(T item);

    void add(Iterable<T> items);

    void update(T item);

    void remove(T item);

    void remove(Specification specification);

    List<T> query(Specification specification);
}