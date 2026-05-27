package com.example.demo.user.service;

import com.example.demo.user.dto.CreateUserRequest;
import com.example.demo.user.dto.UpdateUserRequest;
import com.example.demo.user.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserService {

    private final AtomicLong idGenerator = new AtomicLong(1000);
    private final Map<Long, User> userStore = new ConcurrentHashMap<>();

    public UserService() {
        saveSeedUser("alice", "alice@example.com", 25, "Shanghai");
        saveSeedUser("bob", "bob@example.com", 30, "Beijing");
    }

    public User create(CreateUserRequest request) {
        validateEmailUnique(request.getEmail(), null);
        Long id = idGenerator.incrementAndGet();
        User user = new User(id, request.getUsername(), request.getEmail(), request.getAge(), request.getCity());
        userStore.put(id, user);
        return user;
    }

    public List<User> list() {
        List<User> users = new ArrayList<>(userStore.values());
        users.sort(Comparator.comparing(User::getId));
        return users;
    }

    public User getById(Long id) {
        User user = userStore.get(id);
        if (user == null) {
            throw new IllegalArgumentException("user not found: " + id);
        }
        return user;
    }

    public User update(Long id, UpdateUserRequest request) {
        User existing = getById(id);
        validateEmailUnique(request.getEmail(), id);
        existing.setUsername(request.getUsername());
        existing.setEmail(request.getEmail());
        existing.setAge(request.getAge());
        existing.setCity(request.getCity());
        return existing;
    }

    public User delete(Long id) {
        User removed = userStore.remove(id);
        if (removed == null) {
            throw new IllegalArgumentException("user not found: " + id);
        }
        return removed;
    }

    private void validateEmailUnique(String email, Long currentId) {
        boolean exists = userStore.values().stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email)
                        && (currentId == null || !user.getId().equals(currentId)));
        if (exists) {
            throw new IllegalArgumentException("email already exists: " + email);
        }
    }

    private void saveSeedUser(String username, String email, Integer age, String city) {
        Long id = idGenerator.incrementAndGet();
        userStore.put(id, new User(id, username, email, age, city));
    }
}
