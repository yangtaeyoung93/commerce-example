package com.example.orders;

public class InvalidOrderStateException extends RuntimeException {
    public InvalidOrderStateException(long id, OrderStatus status, String action) {
        super("Cannot " + action + " order " + id + " when status is " + status);
    }
}
