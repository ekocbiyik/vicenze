package com.meyratech.vicenze.backend.security;


import com.meyratech.vicenze.backend.model.User;

@FunctionalInterface
public interface CurrentUser {

    User getUser();
}
