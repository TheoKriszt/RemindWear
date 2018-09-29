package fr.kriszt.theo.remindwear.ui.activities;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.skydoves.colorpickerpreference.ColorEnvelope;
import com.skydoves.colorpickerpreference.ColorListener;
import com.skydoves.colorpickerpreference.ColorPickerView;

import java.util.ArrayList;
import java.util.List;

import fr.kriszt.theo.remindwear.R;
import fr.kriszt.theo.remindwear.tasker.Category;
import fr.kriszt.theo.remindwear.tasker.Tasker;

public class AddCategoryActivity extends AppCompatActivity {

    LayoutInflater inflator;
    private int color;
    private int icon;
    private String name;
    List<Integer> listIcons = new ArrayList<>();

    private EditText title;
    private Spinner spinner;
    private ColorPickerView colorPickerView;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        //TODO add other icons
        listIcons.add(R.drawable.ic_base_0);
        listIcons.add(R.drawable.ic_base_1);
        listIcons.add(R.drawable.ic_base_2);
        listIcons.add(R.drawable.ic_base_3);
        listIcons.add(R.drawable.ic_base_4);
        listIcons.add(R.drawable.ic_base_5);
        listIcons.add(R.drawable.ic_base_6);
        listIcons.add(R.drawable.ic_base_7);
        listIcons.add(R.drawable.ic_base_8);
        listIcons.add(R.drawable.ic_base_9);
        listIcons.add(R.drawable.ic_base_10);
        listIcons.add(R.drawable.ic_base_11);
        listIcons.add(R.drawable.ic_base_12);
        listIcons.add(R.drawable.ic_base_13);
        listIcons.add(R.drawable.ic_base_14);
        listIcons.add(R.drawable.ic_base_15);
        listIcons.add(R.drawable.baseline_directions_run_24);
        listIcons.add(R.drawable.ic_base_17);
        listIcons.add(R.drawable.ic_base_18);
        listIcons.add(R.drawable.ic_base_20);


        title = (EditText) findViewById(R.id.name);

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(new NewAdapter());
        inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        colorPickerView = (ColorPickerView) findViewById(R.id.colorPickerView);
        colorPickerView.setColorListener(new ColorListener() {
            @Override
            public void onColorSelected(ColorEnvelope colorEnvelope) {
                ImageView colorView  = (ImageView) findViewById(R.id.colorView);
                colorView.setBackgroundColor(colorEnvelope.getColor());
            }
        });

        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionSubmit();
            }
        });



    }

    private void actionSubmit() {
        if(title.getText().toString().equals("")){
            Toast.makeText(this, "Inscrire le nom de la catégorie", Toast.LENGTH_SHORT).show();
        }else{
            this.name = title.getText().toString();
            this.color = colorPickerView.getColor();
            this.icon = listIcons.get(spinner.getSelectedItemPosition());
            Category newCategory = new Category(this.name, this.icon, this.color);
            Tasker.getInstance(getApplicationContext()).addCategory(newCategory);
            Tasker.serializeLists();
            onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    class NewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return listIcons.size();
        }

        @Override
        public Object getItem(int arg0) {
            return listIcons.get(arg0);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflator.inflate(R.layout.content_icon_spinner, null);

                ImageView icon = convertView.findViewById(R.id.icon);

                int ic = listIcons.get(position);
                icon.setImageResource(ic);

            }
            return convertView;
        }

    }

}
