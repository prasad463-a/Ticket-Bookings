package com.railway_system.Ticket.Booking.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.railway_system.Ticket.Booking.entity.User;
import com.railway_system.Ticket.Booking.repository.UserRepository;

@Repository
public class UserDao {

    private final UserRepository repository;

    public UserDao(UserRepository repository) {
        this.repository = repository;
    }

    public User saveUser(User user) {
        return repository.save(user);
    }

    public Optional<User> getUserById(int id) {
        return repository.findById( id);
    }

    public List<User> getAllUser() {
        return repository.findAll();
    }

    public User updateUser(User user) {
        return repository.save(user);
    }

    public void delete(int id) {
        repository.deleteById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return repository.findByEmail(email);
    }

}