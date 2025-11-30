package com.example.jefiro.barber_barbeiro.models.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jefiro.barber_barbeiro.R;
import com.example.jefiro.barber_barbeiro.models.DiaSemana;
import com.example.jefiro.barber_barbeiro.models.Horario;
import com.example.jefiro.barber_barbeiro.models.HorarioAdapter;
import com.example.jefiro.barber_barbeiro.models.Periods;
import com.example.jefiro.barber_barbeiro.models.auth.Login;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HorarioFuncionamento extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HorarioAdapter adapter;
    private List<Horario> lista;

    private FirebaseFirestore db;
    private MaterialButton btnSalvar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horario_funcionamento);

        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerHorarios);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        lista = new ArrayList<>();
        lista.add(new Horario(DiaSemana.SEGUNDA, new ArrayList<>(), false));
        lista.add(new Horario(DiaSemana.TERCA, new ArrayList<>(), false));
        lista.add(new Horario(DiaSemana.QUARTA, new ArrayList<>(), false));
        lista.add(new Horario(DiaSemana.QUINTA, new ArrayList<>(), false));
        lista.add(new Horario(DiaSemana.SEXTA, new ArrayList<>(), false));
        lista.add(new Horario(DiaSemana.SABADO, new ArrayList<>(), false));
        lista.add(new Horario(DiaSemana.DOMINGO, new ArrayList<>(), true));

        adapter = new HorarioAdapter(lista);
        recyclerView.setAdapter(adapter);

        btnSalvar = findViewById(R.id.btnSalvarHorarios);
        btnSalvar.setOnClickListener(v -> salvarHorarios());
    }

    private void salvarHorarios() {
        List<Horario> atuais = adapter.getLista();

        var barbeariaId = FirebaseAuth.getInstance().getCurrentUser();

        if (barbeariaId == null) {
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        }

        for (Horario h : atuais) {
            h.setIdBarbearia(barbeariaId.getUid());

            Map<String, Object> payload = new HashMap<>();
            payload.put("dia", h.getDiaSemana().name());
            payload.put("closed", h.getClosed());

            List<Map<String, String>> periods = new ArrayList<>();
            for (Periods p : h.getPeriods()) {
                Map<String, String> pm = new HashMap<>();
                pm.put("open", p.getOpen());
                pm.put("close", p.getClose());
                periods.add(pm);
            }
            payload.put("periods", periods);

            String docId = h.getDiaSemana().name();

            db.collection("Barbearias")
                    .document(barbeariaId.getUid())
                    .collection("Horarios")
                    .document(docId)
                    .set(payload)
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Erro ao salvar: " + e.getMessage(), Toast.LENGTH_LONG).show()
                    );
        }

        Toast.makeText(this, "Horários salvos!", Toast.LENGTH_SHORT).show();
    }
}
