package com.example.jefiro.barber_barbeiro.models.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jefiro.barber_barbeiro.R;
import com.example.jefiro.barber_barbeiro.models.Barbearia.Barbearia;
import com.example.jefiro.barber_barbeiro.models.Barbeiro.BarbeiroForm;
import com.example.jefiro.barber_barbeiro.models.horario.HorarioFuncionamento;
import com.example.jefiro.barber_barbeiro.models.servicoPrestado.ServicosPestado;
import com.example.jefiro.barber_barbeiro.repository.FirestoreRepository;
import com.example.jefiro.barber_barbeiro.service.App;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private TextView tvReceitaHoje, tvTotalHoje, tvNomeBarbearia, tvDataHoje, tvNomeToolbar;
    private LinearLayout btnServicos, btnBarbeiros, btnHorarios;
    private ImageView imgProfile;
    private FirestoreRepository<Barbearia> db;

    private final String COLLECTION = "Barbearias";
    private final String UUID = App.getmAuth().getUid();

    private double valor = 0.0;
    private int totalDia = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = new FirestoreRepository<Barbearia>();

        tvReceitaHoje = view.findViewById(R.id.tvReceitaHoje);
        tvTotalHoje = view.findViewById(R.id.tvTotalHoje);
        tvNomeBarbearia = view.findViewById(R.id.tvNomeBarbearia);
        tvDataHoje = view.findViewById(R.id.tvDataHoje);
        tvNomeToolbar = view.findViewById(R.id.tvNomeToolbar);
        imgProfile = view.findViewById(R.id.imgProfile);
        tvDataHoje.setText(getData());
        btnServicos = view.findViewById(R.id.btnServicos);
        btnHorarios = view.findViewById(R.id.btnHorarios);
        btnBarbeiros = view.findViewById(R.id.btnBarbeiros);

        btnServicos.setOnClickListener(l -> {
            chamaTelaServicos();
        });

        btnHorarios.setOnClickListener(l -> {
            chamaTelaHorarios();
        });

        btnBarbeiros.setOnClickListener(l -> {
            chamaTelaBarbeiros();
        });

        carregarDados();
    }

    private void chamaTelaBarbeiros() {
        startActivity(new Intent(getActivity(), BarbeiroForm.class));
    }

    private void chamaTelaHorarios() {
        startActivity(new Intent(getActivity(), HorarioFuncionamento.class));
    }

    private void carregarDados() {
        carregarBarbearia();
        carregarTotalAgendamentos();
        carregarReceita();
    }

    private void carregarBarbearia() {
        db.getById(COLLECTION, UUID, task -> {
            if (task.isSuccessful()) {
                Barbearia barbearia = task.getResult().toObject(Barbearia.class);
                if (barbearia != null) {
                    tvNomeBarbearia.setText(barbearia.getNome());
                    tvNomeToolbar.setText(barbearia.getNome());
                    carregaImg(barbearia.getFotoUrl());
                }
            }
        });
    }

    private void carregaImg(String url) {
        Glide.with(requireContext())
                .load(url)
                .into(imgProfile);
    }

    private void carregarTotalAgendamentos() {
        db.getSubDocument(COLLECTION, UUID, "Agendamentos", task -> {
            if (task.isSuccessful()) {
                QuerySnapshot snapshot = task.getResult();
                totalDia = (snapshot != null) ? snapshot.size() : 0;
                tvTotalHoje.setText(String.valueOf(totalDia));
            }
        });
    }

    private void carregarReceita() {
        valor = 0.0;

        db.getSubDocument(COLLECTION, UUID, "Agendamentos", task -> {
            if (!task.isSuccessful()) return;

            QuerySnapshot snapshot = task.getResult();
            if (snapshot == null || snapshot.isEmpty()) return;

            for (QueryDocumentSnapshot doc : snapshot) {
                String idServico = doc.getString("idServico");
                if (idServico == null) continue;
                db.getSubDocument(COLLECTION, UUID, "Serviços", idServico, task1 -> {
                    if (task1.isSuccessful()) {
                        DocumentSnapshot servicoSnap = task1.getResult();
                        if (servicoSnap != null && servicoSnap.exists()) {
                            Double preco = servicoSnap.getDouble("preco");
                            if (preco != null) {
                                valor += preco;
                                tvReceitaHoje.setText("R$ " + valor);
                            }
                        }
                    }
                });
            }
        });
    }

    private String getData() {
        LocalDate data = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "EEEE, d 'de' MMMM",
                new Locale("pt", "BR")
        );
        return data.format(formatter);
    }

    private void chamaTelaServicos() {
        startActivity(new Intent(getActivity(), ServicosPestado.class));
    }

}
