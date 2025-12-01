package com.example.jefiro.barber_barbeiro.models.servicoPrestado;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.jefiro.barber_barbeiro.R;
import com.example.jefiro.barber_barbeiro.models.auth.Login;
import com.example.jefiro.barber_barbeiro.models.horario.Horario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ServicosPestado extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    private LinearLayout containerServicos;
    private Button btnAdicionarServico, btnSalvar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicos_pestado);

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        containerServicos = findViewById(R.id.containerServicos);
        btnAdicionarServico = findViewById(R.id.btnAdicionarServico);
        btnSalvar = findViewById(R.id.btnSalvar);

        btnAdicionarServico.setOnClickListener(v -> adicionarServicoCard());
        btnSalvar.setOnClickListener(v -> salvarServicos());

        getService();
    }

    private void getService() {
        var id = mAuth.getCurrentUser().getUid();

        if (id.isEmpty()) {
            startActivity(new Intent(this, Login.class));
        }
        db.collection("Barbearias")
                .document(id)
                .collection("Servicos")
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Service cheio", Toast.LENGTH_LONG).show();

                        List<Servicos> servicos = new ArrayList<>();

                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            servicos.add(doc.toObject(Servicos.class));
                        }

                        if (!servicos.isEmpty()) {
                            adicionarServicoCard(servicos);
                            Toast.makeText(getApplicationContext(), "Service vaxio", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        adicionarServicoCard();
                    }
                });

    }

    private void adicionarServicoCard(List<Servicos> servicos) {

        for (Servicos item : servicos) {

            View card = getLayoutInflater().inflate(R.layout.item_servico, containerServicos, false);

            EditText edtValor = card.findViewById(R.id.txtServicoValor);
            EditText edtDuracao = card.findViewById(R.id.txtServicoDuracao);
            EditText edtNome = card.findViewById(R.id.txtServicoNome);

            // Nome
            edtNome.setText(item.getNome());

            edtDuracao.setText(item.getDuracao());

            NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
            edtValor.setText(nf.format(item.getPreco()));

            edtValor.addTextChangedListener(new TextWatcher() {
                private String current = "";

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!s.toString().equals(current)) {

                        edtValor.removeTextChangedListener(this);

                        String clean = s.toString().replaceAll("[R$,.\\s]", "");

                        double parsed = clean.isEmpty() ? 0 : Double.parseDouble(clean) / 100;

                        String formatted = nf.format(parsed);

                        current = formatted;
                        edtValor.setText(formatted);
                        edtValor.setSelection(formatted.length());

                        edtValor.addTextChangedListener(this);
                    }
                }
            });

            containerServicos.addView(card);
        }
    }


    private void adicionarServicoCard() {
        View card = getLayoutInflater().inflate(R.layout.item_servico, containerServicos, false);

        EditText edtValor = card.findViewById(R.id.txtServicoValor);
        EditText edtDuracao = card.findViewById(R.id.txtServicoDuracao);

        edtValor.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    edtValor.removeTextChangedListener(this);
                    String clean = s.toString().replaceAll("[R$,.\\s]", "");
                    double parsed = clean.isEmpty() ? 0 : Double.parseDouble(clean) / 100;
                    String formatted = NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(parsed);
                    current = formatted;
                    edtValor.setText(formatted);
                    edtValor.setSelection(formatted.length());
                    edtValor.addTextChangedListener(this);
                }
            }
        });

        edtDuracao.setFocusable(false);
        edtDuracao.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    ServicosPestado.this,
                    (view, hourOfDay, minute) -> edtDuracao.setText(String.format("%02d:%02d", hourOfDay, minute)),
                    0, 0, true
            );
            timePickerDialog.show();
        });

        containerServicos.addView(card);
    }

    private void salvarServicos() {
        int count = containerServicos.getChildCount();
        if (count == 0) {
            Toast.makeText(this, "Nenhum serviço adicionado", Toast.LENGTH_SHORT).show();
            return;
        }

        for (int i = 0; i < count; i++) {
            View card = containerServicos.getChildAt(i);
            EditText edtNome = card.findViewById(R.id.txtServicoNome);
            EditText edtValor = card.findViewById(R.id.txtServicoValor);
            EditText edtDuracao = card.findViewById(R.id.txtServicoDuracao);

            String nome = edtNome.getText().toString().trim();
            String valor = edtValor.getText().toString().trim();
            String duracao = edtDuracao.getText().toString().trim();

            if (nome.isEmpty() || valor.isEmpty() || duracao.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos do serviço " + (i + 1), Toast.LENGTH_SHORT).show();
                return;
            }
            var idBarbearia = mAuth.getCurrentUser().getUid();
            if (idBarbearia.isEmpty()) {
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
                return;
            }
            String valorClean = valor.replaceAll("[^\\d,]", "").replace(",", ".");
            double valorDouble = Double.parseDouble(valorClean);
            Servicos servicos = new Servicos(idBarbearia, nome, duracao, valorDouble);

            salvarServico(servicos);
        }

        Toast.makeText(this, "Todos os serviços salvos com sucesso!", Toast.LENGTH_SHORT).show();
    }

    private void salvarServico(Servicos servicos) {

        db.collection("Barbearias")
                .document(servicos.getBarbearia_id())
                .collection("Servicos")
                .add(servicos)
                .addOnSuccessListener(docRef -> {
                    Log.d("Servico", "Servicos salvo: " + docRef.getId());
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("Servico", "Erro ao salvar Servicos", e);
                    return;
                });
    }
}
