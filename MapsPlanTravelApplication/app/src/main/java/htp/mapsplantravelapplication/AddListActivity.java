package htp.mapsplantravelapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.emmasuzuki.easyform.EasyFormEditText;
import com.emmasuzuki.easyform.EasyTextInputLayout;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import htp.mapsplantravelapplication.datasource.DatabaseHander;
import htp.mapsplantravelapplication.model.ObjectPlan;

public class AddListActivity extends AppCompatActivity implements
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {
    Button btnAdd;
    EasyTextInputLayout tTitLe, tDate, tLng, tLat, tPlace;
    EasyFormEditText tContent;
    MenuItem btnBack;
    String SelectDate;
    boolean isDateShowed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);
        findViewById();
        tDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        AddListActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
                isDateShowed = true;
            }
        });
        tDate.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !isDateShowed) {
                    Calendar now = Calendar.getInstance();
                    DatePickerDialog dpd = DatePickerDialog.newInstance(
                            AddListActivity.this,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                    );
                    dpd.show(getFragmentManager(), "Datepickerdialog");
                }
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titles = AddListActivity.this.tTitLe.getEditText().getText().toString();
                String date = AddListActivity.this.tDate.getEditText().getText().toString();

                DateFormat format = new SimpleDateFormat("yyyy/M/d HH:mm");
                Date newdate = new Date();
                Timestamp timeStampDate = new Timestamp(newdate.getTime());
                try {
                    newdate = (Date) format.parse(date);
                    timeStampDate = new Timestamp(newdate.getTime()/1000);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String place = AddListActivity.this.tPlace.getEditText().getText().toString();

                String lat = AddListActivity.this.tLat.getEditText().getText().toString();
                String lng = AddListActivity.this.tLng.getEditText().getText().toString();
                String content = AddListActivity.this.tContent.getEditableText().toString();
                //Add DB
                ObjectPlan objectPlan = new ObjectPlan();
                objectPlan.setTitle(titles);
                objectPlan.setDate(timeStampDate);
                objectPlan.setPlace(place);
                objectPlan.setLat(Double.valueOf(lat));
                objectPlan.setLng(Double.valueOf(lng));
                objectPlan.setContent(content);
                DatabaseHander hander = new DatabaseHander(AddListActivity.this);
                boolean result = hander.addNewList(objectPlan);
                if (result) {
                    Toast.makeText(getApplicationContext(),"insert success", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(AddListActivity.this, MapsActivity.class);
                    startActivity(intent);
                }
            }
        });

        Intent intent = getIntent();
        ObjectPlan objectPlan = (ObjectPlan) intent.getSerializableExtra("objPlan");
        tPlace.getEditText().setText(objectPlan.getPlace());
        tLat.getEditText().setText(String.valueOf("" + objectPlan.getLat()));
        tLng.getEditText().setText(String.valueOf("" + objectPlan.getLng()));


    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        SelectDate = year + "-" + (++monthOfYear) + "-" + dayOfMonth;
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                AddListActivity.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );
        tpd.show(getFragmentManager(), "Timepickerdialog");
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        SelectDate = SelectDate + " " + hourOfDay + ":" + minute;
        tDate.getEditText().setText(SelectDate);
        isDateShowed = false;
    }

    public void findViewById() {
        tTitLe = (EasyTextInputLayout) findViewById(R.id.txt_Title);
        tDate = (EasyTextInputLayout) findViewById(R.id.txt_Date);
        tContent = (EasyFormEditText) findViewById(R.id.txt_Content);
        tPlace = (EasyTextInputLayout) findViewById(R.id.txt_Place);
        tLat = (EasyTextInputLayout) findViewById(R.id.place_check_lat);
        tLng = (EasyTextInputLayout) findViewById(R.id.place_check_lng);
        btnAdd = (Button) findViewById(R.id.submit_button);
        btnBack = (MenuItem) findViewById(R.id.action_add);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        getMenuInflater().inflate(R.menu.menu_add_list, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent intent = new Intent(AddListActivity.this, MapsActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
