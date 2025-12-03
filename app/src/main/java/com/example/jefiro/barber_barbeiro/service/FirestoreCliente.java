package com.example.jefiro.barber_barbeiro.service;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.jefiro.barber_barbeiro.repository.FirestoreCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FirestoreCliente {
    private final String PROJECT_ID = "barber-40482";
    private final String API_KEY = "AIzaSyCOMkIUkssCqSB-_N1KEyv0uEzCrCF_Af8";
    private final String MOBILESDK_APP_ID = "1:738440643488:android:59b64bcd9d640188cb1ac5";
    private final String CLIENTE = "Clientes";

    private FirebaseFirestore firestoreSegundo;

    public FirestoreCliente(Context context) {

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setProjectId(PROJECT_ID)
                .setApplicationId(MOBILESDK_APP_ID)
                .setApiKey(API_KEY)
                .build();

        FirebaseApp secondApp = FirebaseApp.initializeApp(context, options, "SegundoBanco");
        firestoreSegundo = FirebaseFirestore.getInstance(secondApp);
    }

    public FirebaseFirestore getDatabase() {
        return firestoreSegundo;
    }

    public void getCliente(FirestoreCallback<List<Map<String, Object>>> callback) {

        firestoreSegundo.collection(CLIENTE)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        List<Map<String, Object>> mapList = new ArrayList<>();

                        for (DocumentSnapshot doc : task.getResult()) {
                            mapList.add(doc.getData());
                        }

                        callback.onSuccess(mapList);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    public void getClienteID(String idCliente, FirestoreCallback<Map<String, Object>> callback) {

        firestoreSegundo.collection(CLIENTE)
                .document(idCliente)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        DocumentSnapshot doc = task.getResult();

                        if (doc != null && doc.exists()) {
                            callback.onSuccess(doc.getData());
                        } else {
                            callback.onFailure(new Exception("Cliente não encontrado"));
                        }

                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }


}
