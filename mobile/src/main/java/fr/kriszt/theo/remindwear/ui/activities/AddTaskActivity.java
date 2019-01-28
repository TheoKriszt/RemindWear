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
import java.util.Date;
import java.util.GregorianCalendar;

import fr.kriszt.theo.remindwear.R;
import fr.kriszt.theo.remindwear.tasker.Category;
import fr.kriszt.theo.remindwear.tasker.Task;
import fr.kriszt.theo.remindwear.tasker.Tasker;
import fr.kriszt.theo.shared.Constants;

public class AddTaskActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = AddTaskActivity.class.getSimpleName();
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

    private Bundle extras = null;

    public AddTaskActivity() {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        if (getIntent() != null && getIntent().getExtras() != null){
            Bundle extras = getIntent().getExtras();
            this.extras = extras;
//            Log.w(TAG, "onCreate: Extras : ");

//            for (String k : extras.keySet()){
//                Log.w(TAG, "onCreate: " + k + " ==> " + extras.get(k));
//            }
        }

        tasker = Tasker.getInstance(this);
        Category categoryTemp = tasker.getListCategories().get(0);

        name = findViewById(R.id.name);
        description = findViewById(R.id.description);

        Calendar forTimePicker = new GregorianCalendar();
        if (extras != null && extras.get("HOUR") != null && extras.get("MINUTES") != null){
            forTimePicker.set(Calendar.HOUR_OF_DAY, extras.getInt("HOUR"));
            forTimePicker.set(Calendar.MINUTE, extras.getInt("MINUTES"));
        }else {

            forTimePicker.add(Calendar.MINUTE, 1);
        }
//        forTimePicker.add(Calendar.MINUTE, 5);

        calendar = new GregorianCalendar();

        time_picker_hour = findViewById(R.id.time_picker_hour);
        time_picker_hour.setMinValue(0);
        time_picker_hour.setMaxValue(23);
        time_picker_hour.setValue(forTimePicker.get(Calendar.HOUR_OF_DAY));

        time_picker_min = findViewById(R.id.time_picker_min);
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
        preventBefore = findViewById(R.id.preventBefore);
        preventBefore.setMinValue(0);
        preventBefore.setMaxValue(18);
        preventBefore.setValue(0);
//        preventBefore.setValue(6);
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

        addCategory  = findViewById(R.id.addCategory);
        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AddCategoryActivity.class);
                startActivityForResult(intent, 41361);
                startActivity(intent);
            }
        });

        editCategory  = findViewById(R.id.editCategory);
        editCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EditCategoryActivity.class);
                intent.putExtra("idCategory", category.getID() );
//                startActivity(intent);
                startActivityForResult(intent, 41360);
            }
        });
        if(categoryTemp.getName().equals(Tasker.CATEGORY_NONE_TAG) ||
                categoryTemp.getName().equals(Tasker.CATEGORY_SPORT_TAG)){
            editCategory.setVisibility(View.GONE);
        }else{
            editCategory.setVisibility(View.VISIBLE);
        }



        prepareCategoriesSpinner();

        calendarView = findViewById(R.id.calendar);
        Calendar today = new GregorianCalendar();
        calendarView.setMinDate(today.getTime().getTime());
        calendarView.setDate(today.getTime().getTime() + 1000);

        calendarView.setOnDateChangeListener( new CalendarView.OnDateChangeListener() {
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                calendar = new GregorianCalendar( year, month, dayOfMonth ,time_picker_hour.getValue(),time_picker_min.getValue());
            }
        });
        layout_repete = findViewById(R.id.layout_repete);
        checkBox = findViewById(R.id.checkBox);
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

        checkBoxMonday = findViewById(R.id.checkBoxMonday);
        checkBoxTuesday = findViewById(R.id.checkBoxTuesday);
        checkBoxFriday = findViewById(R.id.checkBoxFriday);
        checkBoxWednesday = findViewById(R.id.checkBoxWednesday);
        checkBoxThursday = findViewById(R.id.checkBoxThursday);
        checkBoxSaturday = findViewById(R.id.checkBoxSaturday);
        checkBoxSunday = findViewById(R.id.checkBoxSunday);

        submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAction();
            }
        });


        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().get(Constants.KEY_SUBJECT) != null) {
                setValuesFromIntent(getIntent().getExtras());
            } else if (getIntent().getExtras().get(Constants.KEY_DATE) != null) {
                long timeMillis = extras.getLong(Constants.KEY_DATE);
                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTimeInMillis(timeMillis);
                calendarView.setDate(timeMillis);
            }

            if (getIntent().getExtras().get(Constants.KEY_CATEGORY)!= null){
                String cat = getIntent().getExtras().getString(Constants.KEY_CATEGORY);
                Category category = tasker.getCategoryByName(cat);
                if (category != null){
                    spinner.setSelection(tasker.getListCategories().indexOf(category));
                }
            }
        }
    }

    private void prepareCategoriesSpinner() {
        tasker.unserializeLists();
        spinner = findViewById(R.id.spinner);
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
//        spinner.invalidate();
    }

    private void setValuesFromIntent(Bundle extras) {
        String what, cat;
        int hour, minutes;
        long timeMillis = extras.getLong(Constants.KEY_DATE);
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(timeMillis);
        calendarView.setDate(timeMillis);


        cat = extras.getString(Constants.KEY_CATEGORY);
        what = extras.getString(Constants.KEY_SUBJECT);
        minutes = extras.getInt(Constants.KEY_MINUTES);
        hour = extras.getInt(Constants.KEY_HOUR);


        Log.w(TAG, "setValuesFromIntent: Trying to set to category" + cat );
        if (tasker.getCategoryByName(cat) != null){
            category = tasker.getCategoryByName(cat);
            spinner.setSelection(tasker.getListCategories().indexOf(category));
        }

        time_picker_hour.setValue((hour));
        time_picker_min.setValue((minutes));

        if (what != null) {
            name.setText(what);
        }

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
                    c.add(Calendar.MINUTE, -60);

                    if(calendar.before(c)){
                        Toast.makeText(getApplicationContext(), "Ajoutez une date et une heure à venir", Toast.LENGTH_LONG).show();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.w(TAG, "onActivityResult: Category was updated, requestCode is " + requestCode);
        // Check which request we're responding to
        if (requestCode == 41360) {
            Log.w(TAG, "onActivityResult: Une catégorie a été màj");
            // Make sure the request was successful
            prepareCategoriesSpinner();
            if (resultCode == RESULT_OK) {
                Log.w(TAG, "onActivityResult: Category was updated");
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
            itemName = view.findViewById(R.id.name);
            itemIcon = view.findViewById(R.id.icon);
            itemLayout = view.findViewById(R.id.changeColor);
        }
    }

}
