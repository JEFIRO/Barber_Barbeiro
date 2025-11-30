package com.example.jefiro.barber_barbeiro.models.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.jefiro.barber_barbeiro.R;
import com.example.jefiro.barber_barbeiro.models.Barbearia.BarbeariaForm;
import com.example.jefiro.barber_barbeiro.models.home.HomePage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private TextInputEditText editEmail, editSenha;
    private TextView tvEsqueceuSenha, tvLoginBarbearia;

    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            startActivity(new Intent(this, HomePage.class));
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editEmail = findViewById(R.id.editEmail);
        editSenha = findViewById(R.id.editSenha);
    }


    public void logarUser(View v) {
        String email = editEmail.getText().toString();
        String senha = editSenha.getText().toString();

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Preencha todos os campos", Toast.LENGTH_LONG).show();
            return;
        }

        authUser(email, senha);
    }

    private void authUser(String email, String senha) {
        mAuth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(getApplicationContext(), HomePage.class);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        task.getException();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Email ou senha invalido", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    public void cadastrarUser(View e){
        startActivity(new Intent(getApplicationContext(), BarbeariaForm.class));
        finish();
    }

}

