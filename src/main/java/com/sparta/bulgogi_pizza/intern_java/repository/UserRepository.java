package com.sparta.bulgogi_pizza.intern_java.repository;

import com.sparta.bulgogi_pizza.intern_java.entity.User;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    private static final Map<Long, User> store = new HashMap<>();
    private static final AtomicLong sequence = new AtomicLong(0);

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(sequence.incrementAndGet());
        }

        store.put(user.getId(), user);
        return user;
    }

    public Optional<User> findById(long id) {
        return Optional.ofNullable(store.get(id));
    }

    public Optional<User> findByUsername(String username) {
        return store.values().stream()
            .filter(user -> user.getUsername().equals(username)).findFirst();
    }

    public void clearStore() {
        store.clear();
        sequence.set(0);
    }

}
