package com.example.jefiro.barber_barbeiro.models.horario;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.jefiro.barber_barbeiro.R;
import com.example.jefiro.barber_barbeiro.models.auth.Login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HorarioFuncionamento extends AppCompatActivity {

    private LinearLayout containerDias;
    private MaterialButton btnSalvar;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horario_funcionamento);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        containerDias = findViewById(R.id.containerDias);
        btnSalvar = findViewById(R.id.btnSalvarHorarios);

        btnSalvar.setOnClickListener(v -> salvarHorarios());

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, Login.class));
            finish();
            return;
        }

        db.collection("Barbearias")
                .document(user.getUid())
                .collection("Horarios_Funcionamento")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (queryDocumentSnapshots.isEmpty()) {
                        carregarDadosEConstruirTela();
                        return;
                    }

                    Map<String, Horario> mapa = new HashMap<>();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Horario h = doc.toObject(Horario.class);
                        mapa.put(h.getDiaSemana().name(), h);
                    }
                    construirInterface(mapa);
                })
                .addOnFailureListener(e -> {
                    Log.e("FIREBASE", "Erro: ", e);
                });
    }
    private void carregarDadosEConstruirTela() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
            return;
        }

        btnSalvar.setEnabled(false);
        btnSalvar.setText("Carregando...");

        db.collection("Barbearias")
                .document(user.getUid())
                .collection("Horarios")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Map<String, Horario> mapaHorarios = new HashMap<>();

                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            try {
                                Horario h = converterDocumentoParaHorario(doc);
                                mapaHorarios.put(h.getDiaSemana().name(), h);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    construirInterface(mapaHorarios);

                    btnSalvar.setEnabled(true);
                    btnSalvar.setText("Salvar Alterações");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao carregar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    construirInterface(new HashMap<>());
                    btnSalvar.setEnabled(true);
                    btnSalvar.setText("Salvar Alterações");
                });
    }

    private void construirInterface(Map<String, Horario> dadosExistentes) {
        containerDias.removeAllViews();

        for (DiaSemana diaEnum : DiaSemana.values()) {

            View viewItem = getLayoutInflater().inflate(R.layout.item_horario, containerDias, false);

            TextView txtDia = viewItem.findViewById(R.id.txtDiaSemana);
            SwitchCompat switchAberto = viewItem.findViewById(R.id.switchAberto);
            LinearLayout layoutHorarios = viewItem.findViewById(R.id.layoutHorariosDia);

            EditText editP1Abertura = viewItem.findViewById(R.id.editP1Abertura);
            EditText editP1Fechamento = viewItem.findViewById(R.id.editP1Fechamento);
            EditText editP2Abertura = viewItem.findViewById(R.id.editP2Abertura);
            EditText editP2Fechamento = viewItem.findViewById(R.id.editP2Fechamento);


            txtDia.setText(formatarNomeDia(diaEnum));

            Horario h = dadosExistentes.get(diaEnum.name());

            boolean isClosed = true;

            if (h != null) {
                isClosed = h.getClosed();
                if (!isClosed && h.getPeriods() != null) {
                    if (h.getPeriods().size() > 0) {
                        editP1Abertura.setText(h.getPeriods().get(0).getOpen());
                        editP1Fechamento.setText(h.getPeriods().get(0).getClose());
                    }
                    if (h.getPeriods().size() > 1) {
                        editP2Abertura.setText(h.getPeriods().get(1).getOpen());
                        editP2Fechamento.setText(h.getPeriods().get(1).getClose());
                    }
                }
            } else {
                isClosed = (diaEnum == DiaSemana.DOMINGO);
            }

            switchAberto.setChecked(!isClosed);
            switchAberto.setText(isClosed ? "Fechado" : "Aberto");
            layoutHorarios.setVisibility(isClosed ? View.GONE : View.VISIBLE);

            switchAberto.setOnCheckedChangeListener((btn, isChecked) -> {
                switchAberto.setText(isChecked ? "Aberto" : "Fechado");
                layoutHorarios.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            });

            configurarTimePicker(editP1Abertura);
            configurarTimePicker(editP1Fechamento);
            configurarTimePicker(editP2Abertura);
            configurarTimePicker(editP2Fechamento);

            containerDias.addView(viewItem);
        }
    }

    private void salvarHorarios() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        btnSalvar.setEnabled(false);
        btnSalvar.setText("Salvando...");


        int childCount = containerDias.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View viewItem = containerDias.getChildAt(i);

            DiaSemana diaEnum = DiaSemana.values()[i];

            SwitchCompat switchAberto = viewItem.findViewById(R.id.switchAberto);
            EditText editP1Abertura = viewItem.findViewById(R.id.editP1Abertura);
            EditText editP1Fechamento = viewItem.findViewById(R.id.editP1Fechamento);
            EditText editP2Abertura = viewItem.findViewById(R.id.editP2Abertura);
            EditText editP2Fechamento = viewItem.findViewById(R.id.editP2Fechamento);

            boolean closed = !switchAberto.isChecked();

            List<Periods> periodsList = new ArrayList<>();

            if (!closed) {
                if (validarCampoHora(editP1Abertura) && validarCampoHora(editP1Fechamento)) {
                    periodsList.add(new Periods(editP1Abertura.getText().toString(), editP1Fechamento.getText().toString()));
                }
                if (validarCampoHora(editP2Abertura) && validarCampoHora(editP2Fechamento)) {
                    periodsList.add(new Periods(editP2Abertura.getText().toString(), editP2Fechamento.getText().toString()));
                }
            }

            Horario horario = new Horario(diaEnum, periodsList, closed);
            horario.setIdBarbearia(auth.getCurrentUser().getUid());
            Log.d("Horario", horario.toString());

            db.collection("Barbearias")
                    .document(horario.getIdBarbearia())
                    .collection("Horarios_Funcionamento")
                    .add(horario)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Horarios cadastrado com sucesso", Toast.LENGTH_LONG).show();
                                finish();
                                Log.d("FIRESTORE", "Horario salvo");
                            } else {
                                Log.d("FIRESTORE", "Horario não salvo:");
                            }
                        }
                    });
        }
    }

    private void configurarTimePicker(EditText editText) {
        editText.setFocusable(false);
        editText.setClickable(true);
        editText.setOnClickListener(v -> {
            int hour = 8;
            int minute = 0;

            new TimePickerDialog(this, (view, hourOfDay, minuteOfHour) -> {
                String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour);
                editText.setText(time);
            }, hour, minute, true).show();
        });
    }

    private boolean validarCampoHora(EditText edt) {
        return edt.getText() != null && !edt.getText().toString().trim().isEmpty();
    }

    private String formatarNomeDia(DiaSemana dia) {
        String nome = dia.name().toLowerCase();
        return nome.substring(0, 1).toUpperCase() + nome.substring(1);
    }

    private Horario converterDocumentoParaHorario(DocumentSnapshot doc) {
        Horario h = new Horario(DiaSemana.valueOf(doc.getId()), new ArrayList<>(), false);
        h.setClosed(doc.getBoolean("closed"));

        List<Map<String, String>> periodsRaw = (List<Map<String, String>>) doc.get("periods");
        List<Periods> periodsList = new ArrayList<>();

        if (periodsRaw != null) {
            for (Map<String, String> p : periodsRaw) {
                periodsList.add(new Periods(p.get("open"), p.get("close")));
            }
        }
        h.setPeriods(periodsList);
        return h;
    }
}