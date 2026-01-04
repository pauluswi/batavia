package com.pauluswi.batavia.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryIdempotencyService implements IdempotencyService {

    private final ConcurrentHashMap<String, Object> store = new ConcurrentHashMap<>();

    @Override
    public boolean contains(String key) {
        return store.containsKey(key);
    }

    @Override
    public Object get(String key) {
        return store.get(key);
    }

    @Override
    public void put(String key, Object value) {
        store.put(key, value);
    }
}
