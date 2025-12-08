package com.example.jefiro.barber_barbeiro.models.Barbeiro;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.jefiro.barber_barbeiro.R;
import com.example.jefiro.barber_barbeiro.models.auth.Login;
import com.example.jefiro.barber_barbeiro.service.CalendarApi;
import com.example.jefiro.barber_barbeiro.service.OnCallBack;
import com.example.jefiro.barber_barbeiro.service.SupaBase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.services.calendar.Calendar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.os.Handler;
import android.os.Looper;


public class BarbeiroForm extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String uuidBarbearia;
    private EditText inputEmail, inputSenha, inputTelefone, inputNome;
    private ActivityResultLauncher<Intent> galeriaLauncher;
    private Uri fotoUri = null;
    private ImageView imageFoto;
    private String supaBaseImgLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_barbeiro_form);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        uuidBarbearia = mAuth.getCurrentUser().getUid();

        inputNome = findViewById(R.id.nomeBarbeiro);
        inputSenha = findViewById(R.id.senhaBarbeiro);
        inputEmail = findViewById(R.id.emailBarbeiro);
        inputTelefone = findViewById(R.id.telefoneBarbeiro);
        imageFoto = findViewById(R.id.imgProfileBarbeiro);
        configurarGaleriaLauncher();

    }


    public void cadastrarBarbeiro(View v) {
        String email = inputEmail.getText().toString();
        String senha = inputSenha.getText().toString();
        String telefone = inputTelefone.getText().toString();
        String nome = inputNome.getText().toString();

        if (email.isEmpty() || senha.isEmpty() || telefone.isEmpty() || nome.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Preencha todos os campos", Toast.LENGTH_LONG).show();
            return;
        }
        if (fotoUri == null) {
            Toast.makeText(getApplicationContext(), "Selecione a foto do barbeiro", Toast.LENGTH_LONG).show();
            return;
        }
        supaBaseImgLink = SupaBase.uploadImageToSupabase(getApplicationContext(), fotoUri);

        Barbeiro barbeiro = new Barbeiro(null, nome, email, supaBaseImgLink, uuidBarbearia);

        criarClienteAuth(barbeiro);
    }

    private void criarClienteAuth(Barbeiro barbeiro) {
        String senha = inputSenha.getText().toString();
        mAuth.createUserWithEmailAndPassword(barbeiro.getEmail(), senha).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            barbeiro.setId(task.getResult().getUser().getUid());
                            criarCalendarioAsync(barbeiro.getNome(), new OnCallBack() {
                                @Override
                                public void onSuccess(String calendarId) {
                                    barbeiro.setCalendario_id(calendarId);

                                    salvarNoBanco(barbeiro);
                                }

                                @Override
                                public void onError(Exception e) {

                                }
                            });

                            Toast.makeText(getApplicationContext(), "Barbeiro cadastrado com sucesso", Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }

    private void salvarNoBanco(Barbeiro barbeiro) {
        db.collection("Barbearias")
                .document(uuidBarbearia)
                .collection("Barbeiros")
                .add(barbeiro)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Barbairo cadastrado com sucesso", Toast.LENGTH_LONG).show();

                            correcaoTemporaria();
                            Log.d("FIRESTORE", "Barbeiro salvo");
                        } else {
                            Log.d("FIRESTORE", "Barbeiro não salvo:");
                        }
                    }
                });

    }

    private void configurarGaleriaLauncher() {
        galeriaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        fotoUri = result.getData().getData();
                        carregarFoto();
                    }
                }
        );
    }

    private void correcaoTemporaria(){
        mAuth.signOut();
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }
    private void criarCalendarioAsync(String barbeiro, OnCallBack callback) {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            String calendarId = null;
            Exception exception = null;

            try {
                CalendarApi calendarApi = new CalendarApi();
                Calendar service = calendarApi.getService(getApplicationContext());
                calendarId = calendarApi.criarCalendarioBarbeiro(service, barbeiro);
            } catch (GeneralSecurityException | IOException e) {
                exception = e;
            }

            String finalCalendarId = calendarId;
            Exception finalException = exception;

            handler.post(() -> {
                if (finalException != null) {
                    callback.onError(finalException);
                } else {
                    callback.onSuccess(finalCalendarId);
                }
            });
        });
    }


    private void carregarFoto() {
        if (fotoUri != null) {
            Glide.with(this)
                    .load(fotoUri)
                    .centerCrop()
                    .circleCrop()
                    .into(imageFoto);

        }
    }

    public void abrirGaleria(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galeriaLauncher.launch(intent);
    }
}