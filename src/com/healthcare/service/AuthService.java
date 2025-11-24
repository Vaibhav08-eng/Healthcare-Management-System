package com.healthcare.service;

import com.healthcare.dao.UserDao;
import com.healthcare.dao.UserDaoImpl;
import com.healthcare.model.User;
import com.healthcare.util.PasswordUtil;

import java.util.Optional;

/**
 * Handles authentication logic for the login screen.
 */
public class AuthService {

    private final UserDao userDao;

    public AuthService() {
        this(new UserDaoImpl());
    }

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User login(String email, String password) {
        Optional<User> userOpt = userDao.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if ("ACTIVE".equalsIgnoreCase(user.getStatus())
                    && PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
                return user;
            }
        }
        return null;
    }
}

