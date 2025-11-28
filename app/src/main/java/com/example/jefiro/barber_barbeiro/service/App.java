package com.example.jefiro.barber_barbeiro.service;

import android.app.Application;
import com.google.android.libraries.places.api.Places;

public class App extends Application {
    private static final String PLACES_API_KEY = "AIzaSyDEEuT2RDu7rW0cov1rHGNRf74cxigslGU";

    @Override
    public void onCreate() {
        super.onCreate();
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), PLACES_API_KEY);
        }
    }
}