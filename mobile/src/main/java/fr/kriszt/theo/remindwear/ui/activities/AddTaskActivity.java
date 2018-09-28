package fr.kriszt.theo.remindwear.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;

import fr.kriszt.theo.remindwear.R;
import fr.kriszt.theo.remindwear.TasksActivity;
import fr.kriszt.theo.remindwear.tasker.Category;
import fr.kriszt.theo.remindwear.tasker.Task;
import fr.kriszt.theo.remindwear.tasker.Tasker;

public class AddTaskActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    LayoutInflater inflator;
    private Tasker tasker;
    private Task task;
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
    private ImageView addCategory;


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
        spinner.setAdapter(new NewAdapter());
        inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        addCategory  = (ImageView) findViewById(R.id.addCategory);
        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AddCategoryActivity.class);
                startActivity(intent);
            }
        });


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

            Category cat = Tasker.getInstance(getApplicationContext()).getListCategories().get(spinner.getSelectedItemPosition());

            Boolean[] bools = new Boolean[]{
                    checkBoxMonday.isChecked(),
                    checkBoxTuesday.isChecked(),
                    checkBoxWednesday.isChecked(),
                    checkBoxThursday.isChecked(),
                    checkBoxFriday.isChecked(),
                    checkBoxSaturday.isChecked(),
                    checkBoxSunday.isChecked()
            };
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

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {}
    public void onNothingSelected(AdapterView<?> parent) {}

    class NewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return Tasker.getInstance(getApplicationContext()).getListCategories().size();
        }

        @Override
        public Object getItem(int arg0) {
            return Tasker.getInstance(getApplicationContext()).getListCategories().get(arg0);
        }

        @Override
        public long getItemId(int position) {
            return Tasker.getInstance(getApplicationContext()).getListCategories().get(position).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflator.inflate(R.layout.content_text_and_icon_spinner, null);
                Category cat  = (Category) getItem(position);

                ImageView icon = convertView.findViewById(R.id.icon);
                icon.setImageResource(cat.getIcon());

                TextView text =  convertView.findViewById(R.id.name);
                text.setText(cat.getName());

                LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.changeColor);
                linearLayout.setBackgroundColor(cat.getColor());
            }
            return convertView;
        }

    }

}
