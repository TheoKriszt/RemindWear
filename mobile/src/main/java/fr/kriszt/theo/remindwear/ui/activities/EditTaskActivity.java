package fr.kriszt.theo.remindwear.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
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
import fr.kriszt.theo.remindwear.tasker.Category;
import fr.kriszt.theo.remindwear.tasker.Task;
import fr.kriszt.theo.remindwear.tasker.Tasker;


public class EditTaskActivity extends AppCompatActivity {

    LayoutInflater inflator;
    private Tasker tasker;
    private Task task;
    private Category category;
    private Task taskTemp;
    public Calendar calendar = null;

    private CardView cardView;
    private TextView textView;
    private ImageView cancel;
    private ImageView validate;
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

        tasker = Tasker.getInstance(getApplicationContext());

        Intent mIntent = getIntent();
        int id = mIntent.getIntExtra("idTask", 0);
        Task task = tasker.getTaskByID(id);
        category = task.getCategory();
        taskTemp = task;

        calendar =new GregorianCalendar(task.getNextDate().get(Calendar.YEAR), task.getNextDate().get(Calendar.MONTH),
                task.getNextDate().get(Calendar.DAY_OF_MONTH), task.getTimeHour(), task.getTimeMinutes());

        cardView = findViewById(R.id.cardView);
        cardView.setVisibility(View.VISIBLE);

        textView = findViewById(R.id.textView);
        textView.setText("Modifier une tâche");

        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        validate = findViewById(R.id.validate);
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAction();
            }
        });

        name = findViewById(R.id.name);
        name.setText(task.getName());

        description = findViewById(R.id.description);
        description.setText(task.getDescription());

        time_picker_hour = findViewById(R.id.time_picker_hour);
        time_picker_hour.setMinValue(0);
        time_picker_hour.setMaxValue(23);
        time_picker_hour.setValue(task.getTimeHour());

        time_picker_min = findViewById(R.id.time_picker_min);
        time_picker_min.setMinValue(0);
        time_picker_min.setMaxValue(59);
        time_picker_min.setValue(task.getTimeMinutes());
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
        preventBefore.setValue(task.getWarningBefore()/5);
        String[] minuteValues = new String[19];
        for (int i = 0; i < minuteValues.length; i++) {
            String  number = Integer.toString(i*5);
            minuteValues[i] =  number;
        }
        preventBefore.setDisplayedValues(minuteValues);


        editCategory  = findViewById(R.id.editCategory);
        addCategory  = findViewById(R.id.addCategory);
        if(task.getCategory().getName().equals(Tasker.CATEGORY_NONE_TAG) ||
                task.getCategory().getName().equals(Tasker.CATEGORY_SPORT_TAG)){
            editCategory.setVisibility(View.GONE);
        }else{
            editCategory.setVisibility(View.VISIBLE);
        }
        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AddCategoryActivity.class);
                startActivity(intent);
            }
        });

        editCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EditCategoryActivity.class);
                intent.putExtra("idCategory", category.getID());
                startActivity(intent);
            }
        });

        int resPositionCategory = 0;
        ArrayList<Category> listC = tasker.getListCategories();
        for(int c=0;c< listC.size();c++){
            if(listC.get(c).toString().equals(task.getCategory().toString())){
                resPositionCategory = c;
                break;
            }
        }
        spinner = findViewById(R.id.spinner);
        spinner.setAdapter(new NewAdapter(tasker.getListCategories()));
        inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        spinner.setSelection(resPositionCategory);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                category = tasker.getListCategories().get(position);
                if(tasker.getListCategories().get(position).getName().equals(Tasker.CATEGORY_NONE_TAG) ||
                        tasker.getListCategories().get(position).getName().equals(Tasker.CATEGORY_SPORT_TAG)){
                    editCategory.setVisibility(View.GONE);
                }else{
                    editCategory.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}

        });

        calendarView = findViewById(R.id.calendar);
        calendarView.setOnDateChangeListener( new CalendarView.OnDateChangeListener() {
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                calendar = new GregorianCalendar( year, month, dayOfMonth, time_picker_hour.getValue(), time_picker_min.getValue() );
            }
        });
        if(task.getDateDeb() != null){
            calendarView.setDate(task.getDateDeb().getTime().getTime());
        }


        layout_repete = findViewById(R.id.layout_repete);
        checkBox = findViewById(R.id.checkBox);
        if(task.getDateDeb() == null){
            layout_repete.setVisibility(View.VISIBLE);
            checkBox.setChecked(true);
            calendarView.setVisibility(View.GONE);
        }else{
            layout_repete.setVisibility(View.GONE);
            checkBox.setChecked(false);
            calendarView.setVisibility(View.VISIBLE);
        }
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
        checkBoxMonday.setChecked(task.getRepete()[0]);
        checkBoxTuesday = findViewById(R.id.checkBoxTuesday);
        checkBoxTuesday.setChecked(task.getRepete()[1]);
        checkBoxWednesday = findViewById(R.id.checkBoxWednesday);
        checkBoxWednesday.setChecked(task.getRepete()[2]);
        checkBoxThursday = findViewById(R.id.checkBoxThursday);
        checkBoxThursday.setChecked(task.getRepete()[3]);
        checkBoxFriday = findViewById(R.id.checkBoxFriday);
        checkBoxFriday.setChecked(task.getRepete()[4]);
        checkBoxSaturday = findViewById(R.id.checkBoxSaturday);
        checkBoxSaturday.setChecked(task.getRepete()[5]);
        checkBoxSunday = findViewById(R.id.checkBoxSunday);
        checkBoxSunday.setChecked(task.getRepete()[6]);

        submit = findViewById(R.id.submit);
        submit.setText("   Supprimer   ");
        submit.setTextColor(getApplication().getResources().getColor(R.color.colorRed));
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAction();
            }
        });
    }

    private void deleteAction() {
        tasker.unserializeLists();
        tasker.removeTaskByID(taskTemp.getID());
        tasker.serializeLists();

        Intent intent = new Intent(getApplicationContext(), TasksActivity.class);
        //intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    private void submitAction() {
        tasker.unserializeLists();


        if(name.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Ajoutez un titre", Toast.LENGTH_LONG).show();
        }else{
            String mName = name.getText().toString();
            String mDescription = description.getText().toString();
            int mHour = time_picker_hour.getValue();
            int mMin = time_picker_min.getValue();
            int mPreventBefore = preventBefore.getValue()*5;

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
                    tasker.removeTaskByID(taskTemp.getID());
                    tasker.addTask(task);
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
                    tasker.removeTaskByID(taskTemp.getID());
                    tasker.addTask(task);
                    tasker.serializeLists();

                    Intent intent = new Intent(getApplicationContext(), TasksActivity.class);
                    //intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        }
    }

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
