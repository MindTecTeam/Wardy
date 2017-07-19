package com.invictos.ssid.wardy;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    private EditText mUserView;
    private EditText mPasswordView;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private Button mLogin;
    private BroadcastReceiver broadcastReceiver;
    @Override


    protected void onResume() {
        super.onResume();
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.i(TAG,"M"+intent.getExtras().get("latitude")+" "+intent.getExtras().get("longitude"));
                    //textview.append("\n" +intent.getExtras().get("coordinates"));
                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUserView = (EditText) findViewById(R.id.editText_user);
        mPasswordView = (EditText) findViewById(R.id.editText_password);
        mLogin = (Button) findViewById(R.id.button_login);
        
        if(!runtime_permissions())  enable_button();

    }

    private void enable_button() {
        Log.i(TAG,"ok");
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String request = "{\"id\": \""+ mUserView.getText().toString()+"\", \"password\": \""+ mPasswordView.getText().toString()+"\"}";
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(JSON, request);
                Request req = new Request.Builder()
                        .url("https://w5nikh46ic.execute-api.sa-east-1.amazonaws.com/dev/loginowner")
                        .post(body)
                        .build();
                client.newCall(req).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String respuesta = response.body().string();
                        if (respuesta.equals("{\"message\":\"Permitido\"}")  || respuesta.equals("{\"message\":\"Usuario Logeado\"}") ){
                            Log.i(TAG,"ok?");
                            startGps();
                            goPrincipalView();
                        }
                        else {
                            Log.i(TAG,respuesta);
                        }
                    }
                });
            }
        });
    }


    private boolean runtime_permissions() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission
                .ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest
                .permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    100);
            return true;
        }
        return false;
    }

    private void startGps() {
        Intent i = new Intent(getApplicationContext(), GPS_Service.class);
        startService(i);
    }

    private void goPrincipalView() {
        Intent i = new Intent(this, PrincipalActivity.class);
        startActivity(i);
    }
}
