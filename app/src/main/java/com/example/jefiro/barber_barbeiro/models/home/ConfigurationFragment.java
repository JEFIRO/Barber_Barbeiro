package com.example.jefiro.barber_barbeiro.models.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.jefiro.barber_barbeiro.R;
import com.example.jefiro.barber_barbeiro.models.Barbeiro.BarbeiroForm;
import com.example.jefiro.barber_barbeiro.models.servicoPrestado.ServicosPestado;
import com.example.jefiro.barber_barbeiro.models.horario.HorarioFuncionamento;

public class ConfigurationFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_configuration, container, false);

        view.findViewById(R.id.btnCadastrarBarbeiro).setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), BarbeiroForm.class);
            startActivity(intent);
        });
        view.findViewById(R.id.btnHorarioFuncionamento).setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), HorarioFuncionamento.class);
            startActivity(intent);
        });

        view.findViewById(R.id.btnSevicos).setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), ServicosPestado.class);
            startActivity(intent);
        });

        return view;
    }
}