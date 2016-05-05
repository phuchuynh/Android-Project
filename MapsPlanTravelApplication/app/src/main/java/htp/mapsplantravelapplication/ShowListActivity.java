package htp.mapsplantravelapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import htp.mapsplantravelapplication.adapter.PlanAdapter;
import htp.mapsplantravelapplication.datasource.DatabaseHander;
import htp.mapsplantravelapplication.model.ObjectPlan;

public class ShowListActivity extends AppCompatActivity {
    LinearLayout lnPlan;
    TextView placePlan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);


        // Construct the data source
        ArrayList<ObjectPlan> arrayOfPlans = new ArrayList<ObjectPlan>();
        DatabaseHander hander =new DatabaseHander(ShowListActivity.this);
        Intent intent = getIntent();
        ObjectPlan objectPlan = (ObjectPlan) intent.getSerializableExtra("objPlan");

        arrayOfPlans = hander.getAllPlan(objectPlan.getLat(),objectPlan.getLng());

        Toast.makeText(ShowListActivity.this, arrayOfPlans.size() + ""+objectPlan.getPlace(),  Toast.LENGTH_LONG).show();
        // Create the adapter to convert the array to views
        PlanAdapter adapter = new PlanAdapter(this, arrayOfPlans);
        ListView listView = (ListView) findViewById(R.id.list_plan);
        listView.setAdapter(adapter);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        getMenuInflater().inflate(R.menu.menu_show_list, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_view_back:
                Intent intent = new Intent(ShowListActivity.this, MapsActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
