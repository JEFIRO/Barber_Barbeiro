package com.example.jefiro.barber_barbeiro.repository;

public interface FirestoreCallback<T> {
    void onSuccess(T result);
    void onFailure(Exception e);
}
