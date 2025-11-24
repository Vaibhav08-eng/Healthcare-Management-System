package com.healthcare.dao;

import com.healthcare.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    Optional<User> findByEmail(String email);

    Optional<User> findById(int id);

    List<User> findAll();

    List<User> search(String keyword);

    int save(User user);

    void update(User user);

    void delete(int userId);
}

