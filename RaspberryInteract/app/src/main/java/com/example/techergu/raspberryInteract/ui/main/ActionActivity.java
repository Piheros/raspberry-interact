package com.example.techergu.raspberryInteract.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.techergu.raspberryInteract.R;
import com.example.techergu.raspberryInteract.data.model.LocalPreferences;
import com.example.techergu.raspberryInteract.data.service.ApiService;
import com.example.techergu.raspberryInteract.remote.LedStatus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActionActivity extends AppCompatActivity {

    public Button refresh;
    public ImageView status;
    public Button btnNetwork;
    public LedStatus ledStatus = new LedStatus();

    private final ApiService apiService = ApiService.Builder.getInstance();

    public static Intent getStartIntent(final Context ctx) {
        return new Intent(ctx, ActionActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String currentSelectedDevice = LocalPreferences.getInstance(this).getCurrentSelectedDevice();

        if (currentSelectedDevice == null) {
            Toast.makeText(this, "Aucun périphérique connu", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            ledStatus.setIdentifier(currentSelectedDevice);

            refresh = findViewById(R.id.refresh);
            status = findViewById(R.id.imageViewOFF);
            btnNetwork = findViewById(R.id.button);

            refresh.setOnClickListener(v -> refreshLedState());
            btnNetwork.setOnClickListener(v -> toggleWithNetwork());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshLedState();
    }

    private void refreshLedState() {
        apiService.readStatus(ledStatus.getIdentifier()).enqueue(new Callback<LedStatus>() {
            @Override
            public void onResponse(Call<LedStatus> call, Response<LedStatus> ledStatusResponse) {
                runOnUiThread(() -> {
                    boolean newStatus = ledStatus.getStatus();
                    if (ledStatusResponse.body() != null) {
                        newStatus = ledStatusResponse.body().getStatus(); // LedStatus
                    }
                    setLedState(newStatus);
                });
            }

            @Override
            public void onFailure(Call<LedStatus> call, Throwable t) {
                t.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(ActionActivity.this, "Erreur de refresh serveur", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void toggleWithNetwork(){
        boolean newStatus = !ledStatus.getStatus();
        LedStatus newStatus2 = new LedStatus().setIdentifier(ledStatus.getIdentifier()).setStatus(newStatus);
        apiService.writeStatus(newStatus2).enqueue(new Callback<LedStatus>() {
            @Override
            public void onResponse(Call<LedStatus> call, Response<LedStatus> ledStatusResponse) {
                runOnUiThread(() -> {

                    if (ledStatusResponse.body() != null) {
                        ledStatus.setStatus(ledStatusResponse.body().getStatus()); // LedStatus
                    }
                    setLedState(newStatus);
                });
            }

            @Override
            public void onFailure(Call<LedStatus> call, Throwable t) {
                t.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(ActionActivity.this, "Erreur de connexion au serveur", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    void setLedState(boolean isActive){
        ledStatus.setStatus(isActive);
        status.setImageResource(isActive ? R.drawable.lampeon : R.drawable.lampeoff );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
