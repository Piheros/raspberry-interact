package fr.eseo.dis.pavlovic.moduleandroid.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import fr.eseo.dis.pavlovic.moduleandroid.R;
import fr.eseo.dis.pavlovic.moduleandroid.utils.LedStatus;

public class LedDeviceAdapter extends ArrayAdapter<LedStatus> {

    public LedDeviceAdapter(Context context, ArrayList<LedStatus> ledStatus) {
        super(context, 0, ledStatus);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        LedStatus ledStatus = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_device, parent, false);
        }

        // Lookup view for data population
        TextView deviceID = (TextView) convertView.findViewById(R.id.deviceId);
        ImageView deviceledStatus = (ImageView) convertView.findViewById(R.id.ledImg);
        ImageView deviceledStatusOff = (ImageView) convertView.findViewById(R.id.ledImgOff);

        // Populate the data into the template view using the data object
        deviceID.setText(ledStatus.getIdentifier());

        if(ledStatus.getStatus()) {
            deviceledStatus.setVisibility(View.VISIBLE);
            deviceledStatusOff.setVisibility(View.GONE);
        }else{
            deviceledStatus.setVisibility(View.GONE);
            deviceledStatusOff.setVisibility(View.VISIBLE);
        }

        // Return the completed view to render on screen
        return convertView;
    }

}