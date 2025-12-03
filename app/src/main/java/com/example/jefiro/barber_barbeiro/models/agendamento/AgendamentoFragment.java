package com.example.jefiro.barber_barbeiro.models.agendamento;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.jefiro.barber_barbeiro.R;
import com.example.jefiro.barber_barbeiro.models.Barbearia.Barbearia;
import com.example.jefiro.barber_barbeiro.models.Barbeiro.Barbeiro;
import com.example.jefiro.barber_barbeiro.models.servicoPrestado.Servicos;
import com.example.jefiro.barber_barbeiro.repository.FirestoreCallback;
import com.example.jefiro.barber_barbeiro.service.App;
import com.example.jefiro.barber_barbeiro.service.FirestoreCliente;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AgendamentoFragment extends Fragment {
    private ViewGroup containerServicos;

    String idBarbearia;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idBarbearia = App.getmAuth().getCurrentUser().getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_agendamento, container, false);
        containerServicos = v.findViewById(R.id.conteinerAgendamentos);

        getAgendamentos();

        return v;
    }

    private void getAgendamentos() {
        App.getDb().collection("Barbearias")
                .document(idBarbearia)
                .collection("Agendamentos")
                .get()
                .addOnSuccessListener(query -> {

                    List<Agendamento> agendamentos = new ArrayList<>();
                    for (DocumentSnapshot doc : query) {
                        agendamentos.add(doc.toObject(Agendamento.class));
                    }

                    addItem(agendamentos);
                });
    }

    private void addItem(List<Agendamento> agendamentos) {

        for (Agendamento agendamento : agendamentos) {

            View card = getLayoutInflater().inflate(R.layout.item_agendamento, containerServicos, false);

            TextView tvStatus = card.findViewById(R.id.tvStatus);
            TextView tvDataHora = card.findViewById(R.id.tvDataHora);
            TextView tvServico = card.findViewById(R.id.tvServico);
            TextView tvBarbeiro = card.findViewById(R.id.tvBarbeiro);
            TextView tvCliente = card.findViewById(R.id.tvCliente);
            TextView tvPreco = card.findViewById(R.id.tvPreco);
            TextView tvEndereco = card.findViewById(R.id.tvEndereco);

            if (!agendamento.isConfirmado()) {
                tvStatus.setText("Pendente");
                tvStatus.setBackgroundColor(getResources().getColor(R.color.yellow));
                tvStatus.setTextColor(getResources().getColor(R.color.black));
            }

            tvDataHora.setText(agendamento.juntarDataHora());

            getServico(agendamento.getIdServico(), serv -> {
                if (serv != null) {
                    tvServico.setText(serv.getNome());
                    tvPreco.setText(String.valueOf(serv.getPreco()));
                }
            });

            getBarbeiro(agendamento.getIdBarbeiro(), barb -> {
                if (barb != null) {
                    tvBarbeiro.setText(barb.getNome());
                    tvBarbeiro.setBackgroundColor(Color.parseColor("#0E3A5D"));
                }
            });
            containerServicos.addView(card);
            getServico(agendamento.getIdCliente(), cliente -> {
                if (cliente != null) {
                    getCliente(new FirestoreCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            tvCliente.setText(result);
                        }

                        @Override
                        public void onFailure(Exception e) {

                        }
                    });
                }
            });
        }
    }

    private void getServico(String idServicos, OnServicoLoaded callback) {
        App.getDb()
                .collection("Barbearias")
                .document(idBarbearia)
                .collection("Servicos")
                .document(idServicos)
                .get()
                .addOnSuccessListener(doc ->
                        callback.onLoaded(doc.toObject(Servicos.class))
                );
    }

    private void getBarbeiro(String idBarbeiro, OnBarbeiroLoaded callback) {
        App.getDb()
                .collection("Barbearias")
                .document(idBarbearia)
                .collection("Barbeiros")
                .document(idBarbeiro)
                .get()
                .addOnSuccessListener(doc ->
                        callback.onLoaded(doc.toObject(Barbeiro.class))
                );
    }

    private void getBarbearia(OnBarbeariaLoaded callback) {
        App.getDb()
                .collection("Barbearias")
                .document(idBarbearia)
                .get()
                .addOnSuccessListener(doc ->
                        callback.onLoaded(doc.toObject(Barbearia.class))
                );
    }

    public void getCliente(FirestoreCallback<String> callback) {

        new FirestoreCliente(getContext()).getClienteID("", new FirestoreCallback<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                callback.onSuccess(String.valueOf(result.get("nome")));
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }


    interface OnServicoLoaded {
        void onLoaded(Servicos servico);
    }

    interface OnBarbeiroLoaded {
        void onLoaded(Barbeiro barbeiro);
    }

    interface OnBarbeariaLoaded {
        void onLoaded(Barbearia barbearia);
    }


}
