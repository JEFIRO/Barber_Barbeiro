package com.example.jefiro.barber_barbeiro.service;

import android.content.Context;
import android.os.Looper;

import com.example.jefiro.barber_barbeiro.R;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Handler;

public class CalendarApi {

    public Calendar getService(Context context) throws IOException, GeneralSecurityException {
        InputStream in = context.getResources().openRawResource(R.raw.credentials);

        GoogleCredential credential = GoogleCredential.fromStream(in)
                .createScoped(Collections.singleton(CalendarScopes.CALENDAR));

        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                credential)
                .setApplicationName("BarberPro")
                .build();
    }

    public String criarCalendarioBarbeiro(Calendar service, String nomeBarbeiro) throws IOException {
        com.google.api.services.calendar.model.Calendar calendar = new com.google.api.services.calendar.model.Calendar();
        calendar.setSummary(nomeBarbeiro + " - Agenda");
        calendar.setTimeZone("America/Sao_Paulo");

        com.google.api.services.calendar.model.Calendar createdCalendar = service.calendars().insert(calendar).execute();
        return createdCalendar.getId();
    }



}
