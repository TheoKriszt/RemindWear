package fr.kriszt.theo.remindwear.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.skydoves.colorpickerpreference.ColorEnvelope;
import com.skydoves.colorpickerpreference.ColorListener;
import com.skydoves.colorpickerpreference.ColorPickerView;

import java.util.ArrayList;

import fr.kriszt.theo.remindwear.R;
import fr.kriszt.theo.remindwear.tasker.Category;
import fr.kriszt.theo.remindwear.tasker.Task;
import fr.kriszt.theo.remindwear.tasker.Tasker;

public class EditCategoryActivity extends AppCompatActivity {

    static final String TAG = EditCategoryActivity.class.getSimpleName();
    private LayoutInflater inflator;
    private int color;
    private int icon;
    private String name;
    private Category category;
    private ArrayList<Integer> listIcons = new ArrayList<>();

    Tasker tasker;

    private EditText title;
    private ImageView colorView;
    private Spinner spinner;
    private ColorPickerView colorPickerView;
    private Button submit;
    private CardView cardView;
    private TextView textView;
    private ImageView cancel;
    private ImageView validate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Intent mIntent = getIntent();
        int id = mIntent.getIntExtra("idCategory", 0);
        tasker = Tasker.getInstance(getApplicationContext());
        tasker.unserializeLists();
        category = Tasker.getInstance(getApplicationContext()).getCategoryByID(id);
        Category tempCategory = category;


        listIcons.add(R.drawable.baseline_directions_run_24);
        listIcons.add(R.drawable.ic_base_1);
        listIcons.add(R.drawable.ic_base_18);
        listIcons.add(R.drawable.ic_base_6);
        listIcons.add(R.drawable.ic_base_4);
        listIcons.add(R.drawable.ic_base_5);
        listIcons.add(R.drawable.ic_base_9);
        listIcons.add(R.drawable.ic_base_0);
        listIcons.add(R.drawable.ic_base_20);
        listIcons.add(R.drawable.ic_base_7);
        listIcons.add(R.drawable.ic_base_8);
        listIcons.add(R.drawable.ic_base_10);
        listIcons.add(R.drawable.ic_base_11);
        listIcons.add(R.drawable.ic_base_12);
        listIcons.add(R.drawable.ic_base_13);
        listIcons.add(R.drawable.ic_base_14);
        listIcons.add(R.drawable.ic_base_2);
        listIcons.add(R.drawable.ic_base_3);
        listIcons.add(R.drawable.ic_base_15);
        listIcons.add(R.drawable.ic_base_17);

        cardView = findViewById(R.id.cardView);
        cardView.setVisibility(View.VISIBLE);

        textView = findViewById(R.id.textView);
        textView.setText("Modifier une catégorie");

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
                actionSubmit();
            }
        });

        title = findViewById(R.id.name);
        title.setText(category.getName());
        int resPositionCategory = 0;
        ArrayList<Integer> listC = listIcons;
        for(int c=0;c< listC.size();c++){
            if(listC.get(c) == category.getIcon()){
                resPositionCategory = c;
                break;
            }
        }
        spinner = findViewById(R.id.spinner);
        spinner.setAdapter(new NewAdapter(listIcons));
        spinner.setSelection(resPositionCategory);
        inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //TODO set colorpicker
        colorPickerView = findViewById(R.id.colorPickerView);
        colorPickerView.setColorListener(new ColorListener() {
            @Override
            public void onColorSelected(ColorEnvelope colorEnvelope) {
                colorView.setBackgroundColor(colorEnvelope.getColor());
            }
        });

        colorView  = findViewById(R.id.colorView);
        colorView.setBackgroundColor(category.getColor());

        submit = findViewById(R.id.submit);
        submit.setVisibility(View.GONE);

    }

    private void actionSubmit() {
        if(title.getText().toString().equals("")){
            Toast.makeText(this, "Inscrire le nom de la catégorie", Toast.LENGTH_SHORT).show();
        }else{
            this.name = title.getText().toString();
            this.color = colorPickerView.getColor();
            this.icon = listIcons.get(spinner.getSelectedItemPosition());
            Category newCategory = new Category(this.name, this.icon, this.color);
            Log.e("this.name", this.name);
            Log.e("this.color", String.valueOf(this.color));
            Log.e("this.icon", String.valueOf(this.icon));
            tasker.editCategoryById(category.getID(), newCategory);
            tasker.serializeLists();
            onBackPressed();
        }
    }

    class NewAdapter extends BaseAdapter {

        private ArrayList<Integer> items;

        public NewAdapter(ArrayList<Integer> items) {
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
                convertView = inflator.inflate(R.layout.content_icon_spinner, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }else{
                viewHolder  = (ViewHolder) convertView.getTag();
            }
            int ic = (Integer) getItem(position);

            viewHolder.itemIcon.setImageResource(ic);
            return convertView;
        }


    }

    private class ViewHolder {
        ImageView itemIcon;

        public ViewHolder(View view) {
            itemIcon = view.findViewById(R.id.icon);
        }
    }
}
