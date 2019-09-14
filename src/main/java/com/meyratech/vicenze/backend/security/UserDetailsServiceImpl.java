package com.meyratech.vicenze.backend.security;

import com.meyratech.vicenze.backend.model.User;
import com.meyratech.vicenze.backend.repository.dao.IUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Implements the {@link UserDetailsService}.
 * <p>
 * This implementation searches for {@link User} entities by the e-mail address
 * supplied in the login screen.
 */
@Service
@Primary
public class UserDetailsServiceImpl implements UserDetailsService {

    private final IUserDao IUserDao;

    @Autowired
    public UserDetailsServiceImpl(IUserDao IUserDao) {
        this.IUserDao = IUserDao;
    }

    /**
     * Recovers the {@link User} from the database using the e-mail address supplied
     * in the login screen. If the user is found, returns a
     * {@link org.springframework.security.core.userdetails.User}.
     *
     * @param username User's e-mail address
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = IUserDao.findByEmailIgnoreCase(username);
        if (null == user) {
            throw new UsernameNotFoundException("No user present with username: " + username);
        } else {
            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    user.isActive(),
                    true,
                    true,
                    !user.isLocked(),
                    Collections.singletonList(new SimpleGrantedAuthority(user.getRole()))
            );
        }
    }
}