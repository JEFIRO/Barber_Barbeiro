package com.example.jefiro.barber_barbeiro.models.maps;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jefiro.barber_barbeiro.R;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.MapEventsOverlay;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class Maps extends AppCompatActivity {

    private MapView map;

    private double selectedLat, selectedLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        setContentView(R.layout.activity_maps);

        map = findViewById(R.id.map);
        map.setMultiTouchControls(true);

        map.getController().setZoom(15.0);

        CurrentLocation locationHelper = new CurrentLocation(this);

        locationHelper.getLastLocation(this, (lat, lon) -> {
            GeoPoint startPoint = new GeoPoint(lat, lon);
            map.getController().setCenter(startPoint);
            colocarMarcador(startPoint);
        });

        MapEventsOverlay overlayEventos = new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {

                selectedLat = p.getLatitude();
                selectedLon = p.getLongitude();

                colocarMarcador(p);


                List<String> endereco = pegarEnderecoEscrito(selectedLat, selectedLon);
                ArrayList<String> enderecoArray = new ArrayList<>(endereco);

                Intent result = new Intent();
                result.putExtra("lat", selectedLat);
                result.putExtra("lon", selectedLon);
                result.putStringArrayListExtra("endereco", enderecoArray);

                setResult(RESULT_OK, result);
                finish();

                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        });

        map.getOverlays().add(overlayEventos);
    }

    private List<String> pegarEnderecoEscrito(double lat, double lon) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> lista = geocoder.getFromLocation(lat, lon, 1);
            List<String> response = new ArrayList<>();
            if (lista != null && !lista.isEmpty()) {
                Address address = lista.get(0);


                if (address != null) {
                    String rua = address.getThoroughfare();
                    String numero = address.getSubThoroughfare();
                    String bairro = address.getSubLocality();
                    String cidade = address.getLocality();
                    String estado = address.getAdminArea();
                    String cep = address.getPostalCode();

                    if (rua == null) rua = "";
                    if (numero == null) numero = "";
                    if (bairro == null) bairro = "";
                    if (cidade == null) cidade = "";
                    if (estado == null) estado = "";
                    if (cep == null) cep = "";
                    cidade = address.getLocality();
                    if (cidade == null || cidade.isEmpty()) {
                        cidade = address.getSubAdminArea();
                    }
                    response.add(rua);
                    response.add(numero);
                    response.add(bairro);
                    response.add(cidade);
                    response.add(estado);
                    response.add(cep);
                }
                return response;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private Marker marcadorSelecionado;

    private void colocarMarcador(GeoPoint ponto) {
        if (marcadorSelecionado != null) {
            map.getOverlays().remove(marcadorSelecionado);
        }
        
        marcadorSelecionado = new Marker(map);
        marcadorSelecionado.setPosition(ponto);
        marcadorSelecionado.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marcadorSelecionado.setTitle("Local selecionado");

        map.getOverlays().add(marcadorSelecionado);
        map.invalidate();
    }



    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }
}
