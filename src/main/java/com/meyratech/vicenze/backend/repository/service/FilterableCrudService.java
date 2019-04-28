package com.meyratech.vicenze.backend.repository.service;

import com.meyratech.vicenze.backend.model.AbstractEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface FilterableCrudService<T extends AbstractEntity> extends CrudService<T> {

    Page<T> findAnyMatching(Optional<String> filter, Pageable pageable);

    long countAnyMatching(Optional<String> filter);

    T findById(Long id);

}
