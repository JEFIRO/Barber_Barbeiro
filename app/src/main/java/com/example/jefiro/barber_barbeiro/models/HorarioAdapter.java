package com.example.jefiro.barber_barbeiro.models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jefiro.barber_barbeiro.R;

import java.util.List;
public class HorarioAdapter extends RecyclerView.Adapter<HorarioAdapter.ViewHolder> {

    private List<Horario> lista;

    public HorarioAdapter(List<Horario> lista) {
        this.lista = lista;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_horario, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int position) {

        Horario horario = lista.get(position);

        h.txtDiaSemana.setText(horario.getDiaSemana().name());

        h.switchAberto.setChecked(!horario.getClosed());

        h.layoutHorarios.setVisibility(horario.getClosed() ? View.GONE : View.VISIBLE);

        h.switchAberto.setOnCheckedChangeListener((buttonView, isChecked) -> {
            horario.setClosed(!isChecked);
            h.layoutHorarios.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDiaSemana;
        SwitchCompat switchAberto;
        LinearLayout layoutHorarios;

        public ViewHolder(View itemView) {
            super(itemView);

            txtDiaSemana   = itemView.findViewById(R.id.txtDiaSemana);
            switchAberto   = itemView.findViewById(R.id.switchAberto);
            layoutHorarios = itemView.findViewById(R.id.layoutHorariosDia);
        }
    }

    public List<Horario> getLista() {
        return lista;
    }
}
