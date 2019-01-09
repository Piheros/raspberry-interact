package com.example.techergu.raspberryInteract.ui.scan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.techergu.raspberryInteract.R;
import com.example.techergu.raspberryInteract.data.model.Device;

import java.util.ArrayList;

public class DeviceAdapter  extends ArrayAdapter<Device> {
    public DeviceAdapter(Context context, ArrayList<Device> devices) {
        super(context, 0, devices);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Device device = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_device , parent, false);
        }
        // Lookup view for data population
        TextView deviceID = (TextView) convertView.findViewById(R.id.deviceMAC);
        TextView deviceName = (TextView) convertView.findViewById(R.id.deviceName);
        // Populate the data into the template view using the data object
        deviceName.setText(device.name);
        deviceID.setText(String.valueOf(device.id));
        // Return the completed view to render on screen
        return convertView;
    }
}