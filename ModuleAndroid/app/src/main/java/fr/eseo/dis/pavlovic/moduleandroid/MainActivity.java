package fr.eseo.dis.pavlovic.moduleandroid;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import fr.eseo.dis.pavlovic.moduleandroid.data.DeviceList;
import fr.eseo.dis.pavlovic.moduleandroid.utils.LocalPreferences;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context context = getApplicationContext();

        Button list = (Button) findViewById(R.id.list);
        list.setOnClickListener(l -> {startActivity( new Intent(MainActivity.this, DeviceList.class));});

    }

    protected void onResume() {
        super.onResume();

        final Button actionBtn = findViewById(R.id.command);
        final String currentSelectedDevice = LocalPreferences.getInstance(this).getCurrentSelectedDevice();

        if (currentSelectedDevice != null) {
            actionBtn.setEnabled(true);
            actionBtn.setOnClickListener(v -> startActivity(CommandActivity.getStartIntent(this, currentSelectedDevice)));
        } else {
            actionBtn.setEnabled(false);
        }
    }
}
