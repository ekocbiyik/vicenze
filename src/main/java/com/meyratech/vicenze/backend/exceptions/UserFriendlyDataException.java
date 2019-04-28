package com.meyratech.vicenze.backend.exceptions;

import org.springframework.dao.DataIntegrityViolationException;

/**
 * A data integrity violation exception containing a message intended to be
 * shown to the end user.
 */
public class UserFriendlyDataException extends DataIntegrityViolationException {

    public UserFriendlyDataException(String message) {
        super(message);
    }

}
