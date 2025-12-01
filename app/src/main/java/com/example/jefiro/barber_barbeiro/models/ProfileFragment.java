package com.example.jefiro.barber_barbeiro.models;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.jefiro.barber_barbeiro.R;
import com.example.jefiro.barber_barbeiro.models.Barbearia.Barbearia;
import com.example.jefiro.barber_barbeiro.models.auth.Login;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ShapeableImageView img;
    private EditText inputNome, inputEmail, inputTelefone;
    private Barbearia barbearia;
    private Button btnSair;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        inputNome = view.findViewById(R.id.edtNomeBarbearia);
        inputEmail = view.findViewById(R.id.edtEmailBarbearia);
        inputTelefone = view.findViewById(R.id.edtTelefoneBarbearia);
        img = view.findViewById(R.id.imgViewBarbearia);
        btnSair = view.findViewById(R.id.btnSair);

        btnSair.setOnClickListener(this::signOut);

        buscarBarbearia();

        return view;
    }

    private void signOut(View v) {
        mAuth.signOut();
        startActivity(new Intent(getActivity(), Login.class));
        getActivity().finish();
    }

    private void buscarBarbearia() {
        if (mAuth.getCurrentUser() == null) {
            Log.e("AUTH", "Usuário não logado!");
            return;
        }

        String idBarbearia = mAuth.getCurrentUser().getUid();

        db.collection("Barbearias")
                .document(idBarbearia)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                        barbearia = documentSnapshot.toObject(Barbearia.class);

                        if (barbearia == null) return;

                        inputNome.setText(barbearia.getNome());
                        inputTelefone.setText(barbearia.getTelefone());
                        inputEmail.setText(barbearia.getEmail());

                        if (getContext() != null) {
                            Glide.with(requireContext())
                                    .load(barbearia.getFotoUrl())
                                    .into(img);
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Log.e("FIREBASE", "Erro ao buscar dados: " + e.getMessage())
                );
    }
}
