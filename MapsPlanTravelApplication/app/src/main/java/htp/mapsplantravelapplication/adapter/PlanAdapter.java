package htp.mapsplantravelapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import htp.mapsplantravelapplication.R;
import htp.mapsplantravelapplication.model.ObjectPlan;

/**
 * Created by phuchtgc60244 on 5/3/2016.
 */
public class PlanAdapter extends ArrayAdapter<ObjectPlan> {
    public PlanAdapter(Context context, ArrayList<ObjectPlan> plans) {
        super(context, 0, plans);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ObjectPlan plan = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.plan, parent, false);
        }
        // Lookup view for data population
        TextView plan_title = (TextView) convertView.findViewById(R.id.plan_title);
        TextView plan_time = (TextView) convertView.findViewById(R.id.plan_time);
        TextView plan_latlng = (TextView) convertView.findViewById(R.id.plan_latlng);
        // Populate the data into the template view using the data object
        plan_title.setText(plan.getTitle());
        plan_time.setText(plan.getDate().toString());
       // plan_latlng.setText(plan.getLat() + ", " + plan.getLng());
        plan_latlng.setText(plan.getPlace());
        // Return the completed view to render on screen
        return convertView;
    }
}
