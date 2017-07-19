package com.invictos.ssid.wardy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.invictos.ssid.wardy.MainActivity.JSON;

public class PrincipalActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = PrincipalActivity.class.getName();
    private GoogleMap mapa;
    private Button mSolicitud;
    private double lat;
    private double longit;

    private BroadcastReceiver broadcastReceiver;
    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    //Log.i(TAG,"P"+intent.getExtras().get("latitude")+" "+intent.getExtras().get("longitude"));
                    //textview.append("\n" +intent.getExtras().get("coordinates"));
                    lat = Double.parseDouble((intent.getExtras().get("latitude")).toString());
                    longit = Double.parseDouble((intent.getExtras().get("longitude")).toString());
                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        mSolicitud = (Button) findViewById(R.id.buttonSolicitud);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mSolicitud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String request = "{\"id\": \"OW01\", \"lat\": \"" + lat + "\", \"long\": \"" + longit + "\"}";
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(JSON, request);
                Request req = new Request.Builder()
                        .url("https://w5nikh46ic.execute-api.sa-east-1.amazonaws.com/dev/askdriver")
                        .post(body)
                        .build();
                client.newCall(req).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.i(TAG,response.body().string());
                    }
                });
            }
        });



    }
    @Override
    public void onMapReady(GoogleMap map) {
        mapa = map;
        Log.i(TAG,"Coor "+lat+" "+longit);
        LatLng ubicacion = new LatLng(-12.06652371, -77.08014608);
        mapa.addMarker(new MarkerOptions().position(ubicacion).title("Aqui"));
        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion,15));
    }


}