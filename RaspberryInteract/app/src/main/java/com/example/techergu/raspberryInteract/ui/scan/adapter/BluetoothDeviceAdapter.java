package com.example.techergu.raspberryInteract.ui.scan.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.techergu.raspberryInteract.R;

import java.util.ArrayList;

public class BluetoothDeviceAdapter  extends ArrayAdapter<BluetoothDevice> {
    public BluetoothDeviceAdapter(Context context, ArrayList<BluetoothDevice> bthDevices) {
        super(context, 0, bthDevices);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        BluetoothDevice bthDevice = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_device, parent, false);
        }
        // Lookup view for data population
        TextView deviceMAC = (TextView) convertView.findViewById(R.id.deviceMAC);
        TextView deviceName = (TextView) convertView.findViewById(R.id.deviceName);
        // Populate the data into the template view using the data object
        String name = (bthDevice.getName() != null) ? bthDevice.getName() : "NO NAME";
        deviceName.setText(name);
        deviceMAC.setText(String.valueOf(bthDevice.getAddress()));
        // Return the completed view to render on screen
        return convertView;
    }

}