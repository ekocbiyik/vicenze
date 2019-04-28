package com.meyratech.vicenze.backend.repository.service;

import com.meyratech.vicenze.backend.model.AbstractEntity;
import com.meyratech.vicenze.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;

public interface CrudService<T extends AbstractEntity> {

    JpaRepository<T, Long> getRepository();

    default T save(User currentUser, T entity) {
        return getRepository().saveAndFlush(entity);
    }

    default void delete(User currentUser, T entity) {
        if (entity == null) {
            throw new EntityNotFoundException();
        }
        getRepository().delete(entity);
    }

    default void delete(User currentUser, long id) {
        delete(currentUser, load(id));
    }

    default long count() {
        return getRepository().count();
    }

    default T load(long id) {
        T entity = getRepository().findById(id).orElse(null);
        if (entity == null) {
            throw new EntityNotFoundException();
        }
        return entity;
    }

    T createNew(User currentUser);

    List<T> findAll();
}
