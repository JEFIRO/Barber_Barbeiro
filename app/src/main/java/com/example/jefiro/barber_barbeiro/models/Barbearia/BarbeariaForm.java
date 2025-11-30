package com.example.jefiro.barber_barbeiro.models.Barbearia;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.jefiro.barber_barbeiro.R;
import com.example.jefiro.barber_barbeiro.models.maps.Maps;
import com.example.jefiro.barber_barbeiro.service.SupaBase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class BarbeariaForm extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private TextInputEditText editNome, editTelefone, editSenha, editConfirmarSenha, editEmail;
    private TextInputEditText editEndereco, editNumero, editBairro, editCidade;
    private AutoCompleteTextView spinnerEstado;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String uuid;
    private ImageView imageFoto;
    private ImageButton btnRemoverFoto;
    private Button btnSelecionarFoto;
    private LinearLayout layoutStep1, layoutStep2, layoutStep3;
    private TextView step1, step2, step3;
    private View line1, line2;
    private Button btnVoltar, btnContinuar, btnEscolherEndereco;
    private int currentStep = 1;
    private Uri fotoUri = null;
    private Double selectedLat = null;
    private Double selectedLng = null;
    private ActivityResultLauncher<Intent> galeriaLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barbearia_form);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        inicializarViews();
        configurarEstados();
        configurarGaleriaLauncher();
        configurarListeners();
        atualizarUI();
    }

    private void inicializarViews() {

        editEmail = findViewById(R.id.editEmail);
        editNome = findViewById(R.id.editNome);
        editTelefone = findViewById(R.id.editTelefone);
        editSenha = findViewById(R.id.editSenha);
        editConfirmarSenha = findViewById(R.id.editConfirmarSenha);

        editEndereco = findViewById(R.id.editEndereco);
        editNumero = findViewById(R.id.editNumero);
        editBairro = findViewById(R.id.editBairro);
        editCidade = findViewById(R.id.editCidade);
        spinnerEstado = findViewById(R.id.spinnerEstado);
        btnEscolherEndereco = findViewById(R.id.btnEscolherEndereco);

        imageFoto = findViewById(R.id.imageFoto);
        btnRemoverFoto = findViewById(R.id.btnRemoverFoto);
        btnSelecionarFoto = findViewById(R.id.btnSelecionarFoto);

        // Layouts
        layoutStep1 = findViewById(R.id.layoutStep1);
        layoutStep2 = findViewById(R.id.layoutStep2);
        layoutStep3 = findViewById(R.id.layoutStep3);

        step1 = findViewById(R.id.step1);
        step2 = findViewById(R.id.step2);
        step3 = findViewById(R.id.step3);
        line1 = findViewById(R.id.line1);
        line2 = findViewById(R.id.line2);

        btnVoltar = findViewById(R.id.btnVoltar);
        btnContinuar = findViewById(R.id.btnContinuar);
    }

    private void configurarEstados() {
        String[] estados = {"BA", "SP", "RJ", "MG", "ES", "PE", "CE", "PR", "RS", "SC"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                estados
        );
        spinnerEstado.setAdapter(adapter);
    }

    private void configurarListeners() {
        btnContinuar.setOnClickListener(v -> avancarStep());
        btnVoltar.setOnClickListener(v -> voltarStep());
        btnSelecionarFoto.setOnClickListener(v -> verificarPermissao());
        btnRemoverFoto.setOnClickListener(v -> removerFoto());
        btnEscolherEndereco.setOnClickListener(v -> enderecoViaPlace());

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

    private void avancarStep() {
        if (validarStepAtual()) {
            if (currentStep < 3) {
                currentStep++;
                atualizarUI();
            } else {
                enviarCadastro();
            }
        }
    }

    private boolean validarStepAtual() {
        switch (currentStep) {
            case 1:
                return validarStep1();
            case 2:
                return validarStep2();
            case 3:
                return validarStep3();
            default:
                return false;
        }
    }

    private boolean validarStep1() {
        String nome = editNome.getText().toString().trim();
        String telefone = editTelefone.getText().toString().trim();
        String senha = editSenha.getText().toString().trim();
        String confirmarSenha = editConfirmarSenha.getText().toString().trim();

        if (TextUtils.isEmpty(nome)) {
            editNome.setError("Campo obrigatório");
            editNome.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(telefone) || telefone.length() < 10) {
            editTelefone.setError("Telefone inválido");
            editTelefone.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(senha) || senha.length() < 6) {
            editSenha.setError("Senha deve ter no mínimo 6 caracteres");
            editSenha.requestFocus();
            return false;
        }

        if (!senha.equals(confirmarSenha)) {
            editConfirmarSenha.setError("As senhas não coincidem");
            editConfirmarSenha.requestFocus();
            return false;
        }

        return true;
    }

    private boolean validarStep2() {
        String endereco = editEndereco.getText().toString().trim();
        String numero = editNumero.getText().toString().trim();
        String bairro = editBairro.getText().toString().trim();
        String cidade = editCidade.getText().toString().trim();
        String estado = spinnerEstado.getText().toString().trim();

        if (TextUtils.isEmpty(endereco)) {
            editEndereco.setError("Campo obrigatório");
            editEndereco.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(numero)) {
            editNumero.setError("Campo obrigatório");
            editNumero.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(bairro)) {
            editBairro.setError("Campo obrigatório");
            editBairro.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(cidade)) {
            editCidade.setError("Campo obrigatório");
            editCidade.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(estado)) {
            Toast.makeText(this, "Selecione um estado", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean validarStep3() {
        if (fotoUri == null) {
            Toast.makeText(this, "Adicione uma foto da barbearia", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void atualizarUI() {

        layoutStep1.setVisibility(currentStep == 1 ? View.VISIBLE : View.GONE);
        layoutStep2.setVisibility(currentStep == 2 ? View.VISIBLE : View.GONE);
        layoutStep3.setVisibility(currentStep == 3 ? View.VISIBLE : View.GONE);

        btnVoltar.setVisibility(currentStep > 1 ? View.VISIBLE : View.GONE);

        if (currentStep == 3) {
            btnContinuar.setText("Enviar Cadastro");
        } else {
            btnContinuar.setText("Continuar");
        }

        atualizarProgressIndicator();
    }

    private void atualizarProgressIndicator() {

        if (currentStep >= 1) {
            step1.setBackgroundResource(R.drawable.circle_step_active);
            step1.setTextColor(getResources().getColor(R.color.blue));
        }

        // Step 2
        if (currentStep >= 2) {
            step2.setBackgroundResource(R.drawable.circle_step_active);
            step2.setTextColor(getResources().getColor(R.color.blue));
            line1.setBackgroundColor(getResources().getColor(R.color.white));
        } else {
            step2.setBackgroundResource(R.drawable.circle_step_inactive);
            step2.setTextColor(getResources().getColor(R.color.white));
        }

        // Step 3
        if (currentStep >= 3) {
            step3.setBackgroundResource(R.drawable.circle_step_active);
            step3.setTextColor(getResources().getColor(R.color.blue));
            line2.setBackgroundColor(getResources().getColor(R.color.white));
        } else {
            step3.setBackgroundResource(R.drawable.circle_step_inactive);
            step3.setTextColor(getResources().getColor(R.color.white));
        }
    }

    private void voltarStep() {
        if (currentStep > 1) {
            currentStep--;
            atualizarUI();
        }
    }

    private void verificarPermissao() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        PERMISSION_REQUEST_CODE
                );
            } else {
                abrirGaleria();
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE
                );
            } else {
                abrirGaleria();
            }
        }
    }

    private void enderecoViaPlace() {
        Intent i = new Intent(getApplicationContext(), Maps.class);
        startActivityForResult(i, 1001);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {

            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                abrirGaleria();

            } else {
                Toast.makeText(this,
                        "Permissão necessária para selecionar foto",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK) {

            double lat = data.getDoubleExtra("lat", 0);
            double lon = data.getDoubleExtra("lon", 0);

            selectedLat = lat;
            selectedLng = lon;

            ArrayList<String> endereco = data.getStringArrayListExtra("endereco");

            if (!endereco.isEmpty()) {
                setEndereco(endereco);
                return;
            }

        }
    }

    private void setEndereco(List<String> endereco) {
        if (endereco.size() >= 6) {
            Log.d("MAPS", "Endereço selecionado: " + endereco);

            editEndereco.setText(endereco.get(0));
            editNumero.setText(endereco.get(1));
            editBairro.setText(endereco.get(2));
            editCidade.setText(endereco.get(3));
            spinnerEstado.setText(endereco.get(4));
        }

    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galeriaLauncher.launch(intent);
    }


    private void carregarFoto() {
        if (fotoUri != null) {

            Glide.with(this)
                    .load(fotoUri)
                    .centerCrop()
                    .into(imageFoto);

            btnRemoverFoto.setVisibility(View.VISIBLE);
        }
    }

    private void removerFoto() {
        fotoUri = null;

        imageFoto.setImageResource(android.R.drawable.ic_menu_camera);
        btnRemoverFoto.setVisibility(View.GONE);
    }

    private void enviarCadastro() {
        String nome = editNome.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String telefone = editTelefone.getText().toString().trim();


        Barbearia barbearia = new Barbearia(nome, telefone, email, null, null);

        criarBarbearia(barbearia);
    }

    private void criarClientAuth(Barbearia barbearia, String senha) {
        mAuth.createUserWithEmailAndPassword(barbearia.getEmail(), senha)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String novoUuid = task.getResult().getUser().getUid();

                            barbearia.setId(novoUuid);

                            Log.d("BARBEARIA", "Usuário criado: " + novoUuid + ". Salvando dados...");
                            salvaClienteNoBanco(barbearia);

                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Erro ao criar usuário: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void criarBarbearia(Barbearia barbearia) {
        String senha = editSenha.getText().toString().trim();

        criarClientAuth(barbearia, senha);
    }

    private void salvaClienteNoBanco(Barbearia barbearia) {
        if (fotoUri != null) {
            barbearia.setFotoUrl(SupaBase.uploadImageToSupabase(getApplicationContext(), fotoUri));
        }

        db.collection("Barbearias")
                .document(barbearia.getId())
                .set(barbearia)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            salvarEnderecosSubcolecao(barbearia.getId());
                            Toast.makeText(getApplicationContext(), "Tudo certo", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Tudo errado", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void salvarEnderecosSubcolecao(String barbeariaId) {
        String numero = editNumero.getText().toString().trim();
        String bairro = editBairro.getText().toString().trim();
        String cidade = editCidade.getText().toString().trim();
        String estado = spinnerEstado.getText().toString().trim();

        Endereco endereco = new Endereco(numero, bairro, cidade, estado, selectedLat, selectedLng);


        db.collection("Barbearias")
                .document(barbeariaId)
                .collection("Enderecos")
                .add(endereco)
                .addOnSuccessListener(docRef -> {
                    Log.d("FIRESTORE", "Endereço salvo: " + docRef.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e("FIRESTORE", "Erro ao salvar endereço", e);
                });

        Toast.makeText(getApplicationContext(), "Tudo certo!", Toast.LENGTH_LONG).show();
    }


}