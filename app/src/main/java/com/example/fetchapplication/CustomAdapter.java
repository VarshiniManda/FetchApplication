package com.example.fetchapplication;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<String> {

    public CustomAdapter(Context context, List<String> data) {
        super(context, 0, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String item = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_layout, parent, false);
        }

        TextView headerText = convertView.findViewById(R.id.header_text);
        TextView detailText = convertView.findViewById(R.id.detail_text);

        String[] parts = item.split("\n");
        headerText.setText(parts[0]);
        detailText.setText(parts[1]);

        return convertView;
    }
}
