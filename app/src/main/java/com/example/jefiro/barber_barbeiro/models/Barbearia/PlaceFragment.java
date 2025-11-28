package com.example.jefiro.barber_barbeiro.models.Barbearia;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.example.jefiro.barber_barbeiro.R;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.gms.common.api.Status;

import java.util.Arrays;

public class PlaceFragment extends Fragment {

    private static final String TAG = "PlaceFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place, container, false);


        if (!Places.isInitialized()) {
            Places.initialize(requireContext(),"AIzaSyDEEuT2RDu7rW0cov1rHGNRf74cxigslGU");
        }


        AutocompleteSupportFragment autocompleteFragment =
                (AutocompleteSupportFragment) getChildFragmentManager()
                        .findFragmentById(R.id.fragment_place);

        if (autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields(Arrays.asList(
                    Place.Field.ID,
                    Place.Field.NAME,
                    Place.Field.LAT_LNG
            ));

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                }

                @Override
                public void onError(@NonNull Status status) {
                    Log.e(TAG, "Erro: " + status);
                }
            });
        }

        return view;
    }
}
