package fr.kriszt.theo.remindwear.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import java.util.ArrayList;
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
    private Category category;

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
    private ImageView editCategory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);


        tasker = Tasker.getInstance(this);
        Category categoryTemp = tasker.getListCategories().get(0);

        name = (EditText) findViewById(R.id.name);
        description = (EditText) findViewById(R.id.description);

        Calendar forTimePicker = new GregorianCalendar();
        forTimePicker.add(Calendar.MINUTE, 5);

        calendar = new GregorianCalendar();
        calendar.add(Calendar.DAY_OF_MONTH, -20);

        time_picker_hour = (NumberPicker) findViewById(R.id.time_picker_hour);
        time_picker_hour.setMinValue(0);
        time_picker_hour.setMaxValue(23);
        time_picker_hour.setValue(forTimePicker.get(Calendar.HOUR));

        time_picker_min = (NumberPicker) findViewById(R.id.time_picker_min);
        time_picker_min.setMinValue(0);
        time_picker_min.setMaxValue(59);
        time_picker_min.setValue(forTimePicker.get(Calendar.MINUTE));
        time_picker_min.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                calendar = new GregorianCalendar( calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH) ,time_picker_hour.getValue(),time_picker_min.getValue());
            }
        });

        time_picker_hour.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                calendar = new GregorianCalendar( calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH) ,time_picker_hour.getValue(),time_picker_min.getValue());
            }
        });
        preventBefore = (NumberPicker) findViewById(R.id.preventBefore);
        preventBefore.setMinValue(0);
        preventBefore.setMaxValue(18);
        preventBefore.setValue(6);
        String[] minuteValues = new String[19];
        for (int i = 0; i < minuteValues.length; i++) {
            String number = "";
            if(i == 0){
                number = Integer.toString(i*5)+" minute";
            }else{
                number = Integer.toString(i*5)+" minutes";
            }
            minuteValues[i] =  number;
        }
        preventBefore.setDisplayedValues(minuteValues);

        addCategory  = (ImageView) findViewById(R.id.addCategory);
        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AddCategoryActivity.class);
                startActivity(intent);
            }
        });

        editCategory  = (ImageView) findViewById(R.id.editCategory);
        editCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EditCategoryActivity.class);
                intent.putExtra("idCategory", category.getID() );
                startActivity(intent);
            }
        });
        if(categoryTemp.getName().equals(Tasker.CATEGORY_NONE_TAG) ||
                categoryTemp.getName().equals(Tasker.CATEGORY_SPORT_TAG)){
            editCategory.setVisibility(View.GONE);
        }else{
            editCategory.setVisibility(View.VISIBLE);
        }

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(new NewAdapter(tasker.getListCategories()));
        inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                category = tasker.getListCategories().get(position);
                if(category.getName().equals(Tasker.CATEGORY_NONE_TAG) ||
                        category.getName().equals(Tasker.CATEGORY_SPORT_TAG)){
                    editCategory.setVisibility(View.GONE);
                }else{
                    editCategory.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}

        });

        calendarView = (CalendarView) findViewById(R.id.calendar);
        calendarView.setOnDateChangeListener( new CalendarView.OnDateChangeListener() {
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                calendar = new GregorianCalendar( year, month, dayOfMonth ,time_picker_hour.getValue(),time_picker_min.getValue());
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


        tasker.unserializeLists();

        if(name.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Ajoutez un titre", Toast.LENGTH_LONG).show();
        }else{
            String mName = name.getText().toString();
            String mDescription = description.getText().toString();
            int mHour = time_picker_hour.getValue();
            int mMin = time_picker_min.getValue();
            int mPreventBefore = preventBefore.getValue();

            Category cat = tasker.getListCategories().get(spinner.getSelectedItemPosition());

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
                    Calendar c = new GregorianCalendar();
                    if(calendar.before(c)){
                        Toast.makeText(getApplicationContext(), "Ajoutez une date a partir d'aujourd'hui", Toast.LENGTH_LONG).show();
                        return;
                    }
                    task = new Task(mName, mDescription, cat, calendar, mPreventBefore, mHour, mMin);



                    tasker.unserializeLists();
                    tasker.getInstance(getApplicationContext()).addTask(task);
                    tasker.serializeLists();

                    Intent intent = new Intent(getApplicationContext(), TasksActivity.class);
                    //intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
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

                    tasker.unserializeLists();
                    tasker.getInstance(getApplicationContext()).addTask(task);
                    tasker.serializeLists();

                    Intent intent = new Intent(getApplicationContext(), TasksActivity.class);
                    //intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        }

    }

    @Override
    public void onResume(){
        super.onResume();
        tasker.unserializeLists();

    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {}
    public void onNothingSelected(AdapterView<?> parent) {}

    class NewAdapter extends BaseAdapter {

        private ArrayList<Category> items;

        public NewAdapter(ArrayList<Category> items) {
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position ;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = inflator.inflate(R.layout.content_text_and_icon_spinner, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }else{
                viewHolder  = (ViewHolder) convertView.getTag();
            }
            Category cat  = (Category) getItem(position);

            viewHolder.itemName.setText(cat.getName());
            viewHolder.itemIcon.setImageResource(cat.getIcon());
            viewHolder.itemLayout.setBackgroundColor(cat.getColor());

            return convertView;
        }

    }

    private class ViewHolder {
        TextView itemName;
        ImageView itemIcon;
        LinearLayout itemLayout;

        public ViewHolder(View view) {
            itemName = (TextView)view.findViewById(R.id.name);
            itemIcon = (ImageView) view.findViewById(R.id.icon);
            itemLayout = (LinearLayout) view.findViewById(R.id.changeColor);
        }
    }

}
