package fr.eseo.dis.pavlovic.moduleandroid.data;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fr.eseo.dis.pavlovic.moduleandroid.CommandActivity;
import fr.eseo.dis.pavlovic.moduleandroid.R;
import fr.eseo.dis.pavlovic.moduleandroid.data.adapter.LedDeviceAdapter;
import fr.eseo.dis.pavlovic.moduleandroid.data.services.ApiService;
import fr.eseo.dis.pavlovic.moduleandroid.utils.LedStatus;
import fr.eseo.dis.pavlovic.moduleandroid.utils.LocalPreferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceList extends AppCompatActivity {

    private LedDeviceAdapter adapter;
    private ArrayList<LedStatus> leds = new ArrayList<>();
    public LedStatus ledStatus = new LedStatus();

    private final ApiService apiService = ApiService.Builder.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.device_list);
        adapter = new LedDeviceAdapter(this, leds);

        ListView listView = findViewById(R.id.listDevice);
        listView.setAdapter(adapter);

        listView.setClickable(true);
        listView.setOnItemClickListener(listClick);
    }

    private AdapterView.OnItemClickListener listClick = (parent, view, position, id) -> {
        final LedStatus ledStatus = adapter.getItem(position);
        startActivity(CommandActivity.getStartIntent(this, ledStatus.getIdentifier()));
    };

    @Override
    protected void onResume() {
        super.onResume();
        adapter.clear();
        refreshLedState();
    }

    private void refreshLedState() {
        apiService.readStatusList().enqueue(new Callback<List<LedStatus>>() {
            @Override
            public void onResponse(Call<List<LedStatus>> call, Response<List<LedStatus>> ledStatusResponse) {
                runOnUiThread(() -> {
                    boolean newStatus = ledStatus.getStatus();
                    if (ledStatusResponse.body() != null) {
                        for(LedStatus ledStatus : ledStatusResponse.body()) {
                            adapter.add(ledStatus);
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<List<LedStatus>> call, Throwable t) {
                t.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(DeviceList.this, "Erreur de refresh serveur", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }





}
