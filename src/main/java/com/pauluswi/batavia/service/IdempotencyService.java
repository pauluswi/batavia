package com.pauluswi.batavia.service;

public interface IdempotencyService {
    boolean contains(String key);
    Object get(String key);
    void put(String key, Object value);
}
