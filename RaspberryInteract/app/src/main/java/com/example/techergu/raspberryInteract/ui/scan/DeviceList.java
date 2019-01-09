package com.example.techergu.raspberryInteract.ui.scan;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.techergu.raspberryInteract.R;
import com.example.techergu.raspberryInteract.data.manager.BluetoothLEManager;
import com.example.techergu.raspberryInteract.data.model.LocalPreferences;
import com.example.techergu.raspberryInteract.ui.scan.adapter.BluetoothDeviceAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class DeviceList extends AppCompatActivity {

    private final  static  int REQUEST_LOCATION_CODE = 1235;
    private final  static  int REQUEST_ENABLED_LOCATION_CODE = 1236;
    private final  static  long SCAN_DURATION_MS = 10_000L;
    private final  static  int  REQUEST_ENABLE_BLE = 999;

    private final Runnable scanDevicesRunnable = () -> stopScan();
    private final Stack<BluetoothGattCharacteristic> charsStack = new Stack<>();

    private boolean isScanning;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDeviceAdapter adapter;
    private ArrayList<BluetoothDevice> devices= new ArrayList<>();
    private Handler scanningHandler = new Handler();
    private BluetoothGatt currentBluetoothGatt = null; // current connection to BLE device
    private android.bluetooth.BluetoothDevice selectedDevice;
    private ListView rvDevices = null;
    private Button scanBtn = null;
    private TextView tvCurrentConnexion = null;
    private Button disconnectBtn = null;
    private Button toggleLed = null;

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
            super.onServicesDiscovered(gatt, status);
            runOnUiThread(() -> {
                Toast.makeText(DeviceList.this, "Services discovered with success", Toast.LENGTH_SHORT).show();
                setUiMode(true);
            });
        }

        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            runOnUiThread(() -> {
                switch (newState) {
                    case BluetoothGatt.STATE_CONNECTED:
                        currentBluetoothGatt.discoverServices(); // start services
                        break;
                    case BluetoothProfile.STATE_DISCONNECTED:
                        gatt.close();
                        setUiMode(false);
                        break;
                }
            });
        }

        @Override
        public void onCharacteristicWrite(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, final int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }
    };

    private void setUiMode(boolean isConnected) {
        if (isConnected) {
            // Connecté à un périphérique, passage en node action BLE
            adapter.clear();
            rvDevices.setVisibility(View.GONE);
            scanBtn.setVisibility(View.GONE);

            tvCurrentConnexion.setVisibility(View.VISIBLE);
            tvCurrentConnexion.setText(String.format("Connecté à : %s", selectedDevice.getName()));
            disconnectBtn.setVisibility(View.VISIBLE);
            toggleLed.setVisibility(View.VISIBLE);
        } else {
            // Non connecté, reset de la vue.
            rvDevices.setVisibility(View.VISIBLE);
            scanBtn.setVisibility(View.VISIBLE);
            tvCurrentConnexion.setVisibility(View.GONE);
            disconnectBtn.setVisibility(View.GONE);
            toggleLed.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.device_list);
        adapter = new BluetoothDeviceAdapter(this, devices);

        ListView listView = findViewById(R.id.lvItems);
        listView.setAdapter(adapter);

        listView.setClickable(true);
        listView.setOnItemClickListener(listClick);

        Button scan = (Button) findViewById(R.id.scanButton);
        scan.setOnClickListener(l -> {checkPermissions();});

        Button dis = (Button) findViewById(R.id.disButton);
        dis.setOnClickListener(l -> {discconnectFromCurrentDevice();});

        toggleLed = findViewById(R.id.ledButton);
        toggleLed.setOnClickListener(v -> toggleLed());

        this.rvDevices = findViewById(R.id.lvItems);
        this.scanBtn = findViewById(R.id.scanButton);
        this.tvCurrentConnexion = findViewById(R.id.coText);
        this.disconnectBtn = findViewById(R.id.disButton);
        this.toggleLed = findViewById(R.id.ledButton);
        setUiMode(false);


    }

    private void discconnectFromCurrentDevice() {
        if(currentBluetoothGatt != null) {
            currentBluetoothGatt.disconnect();
        }
    }

    private AdapterView.OnItemClickListener listClick = (parent, view, position, id) -> {
        final BluetoothDevice item = adapter.getItem(position);
        BluetoothLEManager.getInstance().setCurrentDevice(item);
        selectedDevice = item;
        LocalPreferences.getInstance(this).saveCurrentSelectedDevice(item.getName());

        // C'est ici que l'on va se connecter à notre périphérique
        // Dans un second temps…
        connectToCurrentDevice();
    };

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkForLocationEnabled();
            } else {
                checkPermissions(); // force permission
            }
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
        } else {
            checkForLocationEnabled();
        }
    }

    private void checkForLocationEnabled() {
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (lm != null) {
            final boolean gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            final boolean network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!gps_enabled || !network_enabled) {
                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_ENABLED_LOCATION_CODE);
            } else {
                setupBLE();
            }
        } else {
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_ENABLED_LOCATION_CODE);
        }
    }

    private void setupBLE() {
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            bluetoothAdapter = bluetoothManager.getAdapter();
        }

        if (bluetoothManager == null || !bluetoothAdapter.isEnabled()) { // bluetooth is off
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BLE);
        } else {
            scanNearbyDevices(); // start scanning by default
        }
    }


    private void scanNearbyDevices() {
        if (isScanning) {
            return;
        }

        isScanning = true;
        scanningHandler.postDelayed(scanDevicesRunnable, SCAN_DURATION_MS);

        final ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
        final List<ScanFilter> scanFilters = new ArrayList<>();

        // Create ScanFilters
        //scanFilters.add(new ScanFilter.Builder().setServiceUuid(new ParcelUuid(BluetoothLEManager.DEVICE_UUID)).build());

        bluetoothAdapter.getBluetoothLeScanner().startScan(scanFilters, settings, bleLollipopScanCallback);
    }
    private final ScanCallback bleLollipopScanCallback = new ScanCallback() {
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onScanResult(final int callbackType, final ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice bluetoothDevice = result.getDevice();

            if(!devices.contains(bluetoothDevice)){
                adapter.add(bluetoothDevice);
            }

            // C'est ici qu'il faut l'ajouter à l'adapter
        }

        @Override
        public void onScanFailed(final int errorCode) {
            super.onScanFailed(errorCode);
            Toast.makeText(DeviceList.this, getString(R.string.ble_scan_error), Toast.LENGTH_SHORT).show();
        }
    };

    private void stopScan() {
        bluetoothAdapter.getBluetoothLeScanner().stopScan(bleLollipopScanCallback);
        isScanning = false;
    }

    private void connectToCurrentDevice() {
        if (selectedDevice != null) {
            Toast.makeText(this, "Connexion en cours…", Toast.LENGTH_SHORT).show();
            currentBluetoothGatt = selectedDevice.connectGatt(this, false, gattCallback);
        }
    }

    /**
     * Send the current configuration to pins
     */
    private void sendConfiguration() {
        if (currentBluetoothGatt == null) {
            Toast.makeText(this, "Non Connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        /*EditText ledGPIOPin = findViewById(R.id.editTextPin);
        EditText buttonGPIOPin = findViewById(R.id.editTextPin);
        final String pinLed = ledGPIOPin.getText().toString();
        final String pinButton = buttonGPIOPin.getText().toString();

        final BluetoothGattService service = currentBluetoothGatt.getService(BluetoothLEManager.DEVICE_UUID);
        if (service == null) {
            Toast.makeText(this, "UUID Introuvable", Toast.LENGTH_SHORT).show();
            return;
        }

        final BluetoothGattCharacteristic buttonCharact = service.getCharacteristic(BluetoothLEManager.CHARACTERISTIC_BUTTON_PIN_UUID);
        final BluetoothGattCharacteristic ledCharact = service.getCharacteristic(BluetoothLEManager.CHARACTERISTIC_LED_PIN_UUID);

        buttonCharact.setValue(pinButton);
        ledCharact.setValue(pinLed);

        currentBluetoothGatt.writeCharacteristic(buttonCharact); // async code, you cannot send 2 characteristics at the same time!
        charsStack.add(ledCharact); // stack the next write*/
    }

    private void toggleLed(){
        if (currentBluetoothGatt == null) {
            Toast.makeText(this, "Non Connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        final BluetoothGattService service = currentBluetoothGatt.getService(BluetoothLEManager.DEVICE_UUID);
        if (service == null) {
            Toast.makeText(this, "UUID Introuvable", Toast.LENGTH_SHORT).show();
            return;
        }

        final BluetoothGattCharacteristic toggleLed = service.getCharacteristic(BluetoothLEManager.CHARACTERISTIC_TOGGLE_LED_UUID);
        toggleLed.setValue("1");
        currentBluetoothGatt.writeCharacteristic(toggleLed);


    }

}
