package fr.kriszt.theo.remindwear.ui.activities;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import fr.kriszt.theo.remindwear.R;
import fr.kriszt.theo.remindwear.TasksActivity;
import fr.kriszt.theo.remindwear.tasker.Category;
import fr.kriszt.theo.remindwear.tasker.Task;
import fr.kriszt.theo.remindwear.tasker.Tasker;

public class AddTaskActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Tasker tasker;
    private Task task;
    private Category category;
    public Calendar calendar = null;

    private EditText name;
    private EditText description;
    private NumberPicker time_picker_hour;
    private NumberPicker time_picker_min;
    private Spinner spinner;
    private CheckBox checkBox;
    private CalendarView calendarView;
    private LinearLayout layout_repete;
    private CheckBox checkBoxMonday;
    private CheckBox checkBoxTuesday;
    private CheckBox checkBoxWednesday;
    private CheckBox checkBoxThursday;
    private CheckBox checkBoxFriday;
    private CheckBox checkBoxSaturday;
    private CheckBox checkBoxSunday;
    private Button submit;
    private NumberPicker preventBefore;



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        name = (EditText) findViewById(R.id.name);
        description = (EditText) findViewById(R.id.description);

        time_picker_hour = (NumberPicker) findViewById(R.id.time_picker_hour);
        time_picker_hour.setMinValue(0);
        time_picker_hour.setMaxValue(23);
        time_picker_hour.setValue(12);
        time_picker_min = (NumberPicker) findViewById(R.id.time_picker_min);
        time_picker_min.setMinValue(0);
        time_picker_min.setMaxValue(59);
        time_picker_min.setValue(30);
        preventBefore = (NumberPicker) findViewById(R.id.preventBefore);
        preventBefore.setMinValue(0);
        preventBefore.setValue(30);
        preventBefore.setMaxValue(59);

        spinner = (Spinner) findViewById(R.id.spinner);
        List<String> listCat = new ArrayList<>();
        for(Category c : Tasker.getInstance(getApplicationContext()).getListCategories()){
            listCat.add(c.getName());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_item, listCat);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        /*spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);

            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });*/


        calendarView = (CalendarView) findViewById(R.id.calendar);
        calendarView.setOnDateChangeListener( new CalendarView.OnDateChangeListener() {
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                calendar = new GregorianCalendar( year, month, dayOfMonth );
            }
        });
        layout_repete = (LinearLayout) findViewById(R.id.layout_repete);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(checkBox.isChecked()){
                    calendarView.setVisibility(View.GONE);
                    layout_repete.setVisibility(View.VISIBLE);
                }else{
                    calendarView.setVisibility(View.VISIBLE);
                    layout_repete.setVisibility(View.GONE);
                }
            }
        });

        checkBoxMonday = (CheckBox) findViewById(R.id.checkBoxMonday);
        checkBoxTuesday = (CheckBox) findViewById(R.id.checkBoxTuesday);
        checkBoxWednesday = (CheckBox) findViewById(R.id.checkBoxWednesday);
        checkBoxThursday = (CheckBox) findViewById(R.id.checkBoxThursday);
        checkBoxFriday = (CheckBox) findViewById(R.id.checkBoxFriday);
        checkBoxSaturday = (CheckBox) findViewById(R.id.checkBoxSaturday);
        checkBoxSunday = (CheckBox) findViewById(R.id.checkBoxSunday);

        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAction();
            }
        });

    }

    private void submitAction(){

        tasker = new Tasker(getApplicationContext());
        tasker.unserializeLists();


        if(name.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Ajoutez un titre", Toast.LENGTH_LONG).show();
        }else{
            String mName = name.getText().toString();
            String mDescription = description.getText().toString();
            int mHour = time_picker_hour.getValue();
            int mMin = time_picker_min.getValue();
            int mPreventBefore = preventBefore.getValue();

            //TODO
            Category cat = new Category("name",0);
            for(Category c : Tasker.getInstance(getApplicationContext()).getListCategories()){
                if(c.getName().equals(spinner.getSelectedItem().toString())){
                    cat = c;
                }
            }

            Boolean[] bools = new Boolean[]{
                    checkBoxMonday.isChecked(),
                    checkBoxTuesday.isChecked(),
                    checkBoxWednesday.isChecked(),
                    checkBoxThursday.isChecked(),
                    checkBoxFriday.isChecked(),
                    checkBoxSaturday.isChecked(),
                    checkBoxSunday.isChecked()
            };
            for(Boolean b : bools){
                Log.e("EEEEEEEEEEEE",b.toString());
            }
            if(!checkBox.isChecked()){
                if(calendar == null){
                    Toast.makeText(getApplicationContext(), "Ajoutez une date", Toast.LENGTH_LONG).show();
                    return;
                }else{
                    task = new Task(mName, mDescription, cat, calendar, mPreventBefore, mHour, mMin);
                    Tasker.getInstance(getApplicationContext()).addTask(task);
                    Tasker.serializeLists();
                    Intent intent = new Intent(this, TasksActivity.class);
                    startActivity(intent);
                }
            }else{
                Boolean bool = false;
                for(Boolean b : bools){
                    if(b){
                        bool = true;
                       break;
                    }
                }
                if(!bool){
                    Toast.makeText(getApplicationContext(), "Ajoutez une répétition", Toast.LENGTH_LONG).show();
                    return;
                }else{
                    task = new Task(mName, mDescription, cat, null, mPreventBefore, mHour, mMin, bools);
                    Tasker.getInstance(getApplicationContext()).addTask(task);
                    Tasker.serializeLists();
                    Intent intent = new Intent(this, TasksActivity.class);
                    startActivity(intent);
                }
            }
        }

    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

}
