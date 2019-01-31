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
import fr.kriszt.theo.shared.Constants;

public class EditCategoryActivity extends AppCompatActivity {

    static final String TAG = EditCategoryActivity.class.getSimpleName();
    private LayoutInflater inflator;
    private Category category;
    private ArrayList<Integer> listIcons = new ArrayList<>();
    private EditText title;
    private ImageView colorView;
    private Spinner spinner;
    private ColorPickerView colorPickerView;

    Tasker tasker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Intent mIntent = getIntent();
        int id = mIntent.getIntExtra(Constants.KEY_ID_CATEGORY, 0);
        tasker = Tasker.getInstance(getApplicationContext());
        tasker.unserializeLists();
        category = Tasker.getInstance(getApplicationContext()).getCategoryByID(id);

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

        CardView cardView = findViewById(R.id.cardView);
        cardView.setVisibility(View.VISIBLE);

        TextView textView = findViewById(R.id.textView);
        textView.setText(R.string.Modifier_categorie);

        ImageView cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ImageView validate = findViewById(R.id.validate);
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

        colorPickerView = findViewById(R.id.colorPickerView);
        colorPickerView.setColorListener(new ColorListener() {
            @Override
            public void onColorSelected(ColorEnvelope colorEnvelope) {
                colorView.setBackgroundColor(colorEnvelope.getColor());
            }
        });

        colorView  = findViewById(R.id.colorView);
        colorView.setBackgroundColor(category.getColor());

        Button submit = findViewById(R.id.submit);
        submit.setVisibility(View.GONE);

    }

    private void actionSubmit() {
        if(title.getText().toString().equals("")){
            Toast.makeText(this, "Inscrire le nom de la catégorie", Toast.LENGTH_SHORT).show();
        }else{
            String name = title.getText().toString();
            int color = colorPickerView.getColor();
            int icon = listIcons.get(spinner.getSelectedItemPosition());
            Category newCategory = new Category(name, icon, color);
            tasker.editCategoryById(category.getID(), newCategory);
            tasker.serializeLists();
            onBackPressed();
        }
    }

    class NewAdapter extends BaseAdapter {

        private ArrayList<Integer> items;

        NewAdapter(ArrayList<Integer> items) {
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

        ViewHolder(View view) {
            itemIcon = view.findViewById(R.id.icon);
        }
    }
}
