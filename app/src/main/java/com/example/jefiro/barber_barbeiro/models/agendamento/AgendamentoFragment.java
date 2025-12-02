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
import com.example.jefiro.barber_barbeiro.service.App;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AgendamentoFragment extends Fragment {
    private ViewGroup containerServicos;

    String idBarbearia;
    Servicos servico;
    Barbearia barbearia;
    Barbeiro barbeiro;
//    Cliente cliente;

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

            // Status
            if (!agendamento.isConfirmado()) {
                tvStatus.setText("Pendente");
                tvStatus.setBackgroundColor(getResources().getColor(R.color.yellow));
                tvStatus.setTextColor(getResources().getColor(R.color.black));
            }

            // Data + hora
            tvDataHora.setText(agendamento.juntarDataHora());

            // BUSCA SERVIÇO
            getServico(agendamento.getIdServico(), serv -> {
                if (serv != null) {
                    tvServico.setText(serv.getNome());
                    tvPreco.setText(String.valueOf(serv.getPreco()));
                }
            });

            // BUSCA BARBEIRO
            getBarbeiro(agendamento.getIdBarbeiro(), barb -> {
                if (barb != null) {
                    tvBarbeiro.setText(barb.getNome());
                    tvBarbeiro.setBackgroundColor(Color.parseColor("#0E3A5D"));
                }
            });

            // BUSCA CLIENTE
//            getCliente(agendamento.getIdCliente(), cli -> {
//                if (cli != null) {
//                    tvCliente.setText(cli.getNome());
//                }
//            });

            // BUSCA BARBEARIA
//            getBarbearia(barb -> {
//                if (barb != null) {
//                    tvEndereco.setText(barb.getEndereco());
//                }
//            });

            containerServicos.addView(card);
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

//    private void getCliente(String idCliente, OnClienteLoaded callback) {
//        App.getDb()
//                .collection("Barbearias")
//                .document(idBarbearia)
//                .collection("Clientes")
//                .document(idCliente)
//                .get()
//                .addOnSuccessListener(doc ->
//                        callback.onLoaded(doc.toObject(Cliente.class))
//                );
//    }

    private void getBarbearia(OnBarbeariaLoaded callback) {
        App.getDb()
                .collection("Barbearias")
                .document(idBarbearia)
                .get()
                .addOnSuccessListener(doc ->
                        callback.onLoaded(doc.toObject(Barbearia.class))
                );
    }


    interface OnServicoLoaded {
        void onLoaded(Servicos servico);
    }

    interface OnBarbeiroLoaded {
        void onLoaded(Barbeiro barbeiro);
    }

    //    interface OnClienteLoaded { void onLoaded(Cliente cliente); }
    interface OnBarbeariaLoaded {
        void onLoaded(Barbearia barbearia);
    }

}
