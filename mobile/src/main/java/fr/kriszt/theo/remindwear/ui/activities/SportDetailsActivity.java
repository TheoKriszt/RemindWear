package fr.kriszt.theo.remindwear.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

import fr.kriszt.theo.remindwear.R;
import fr.kriszt.theo.remindwear.tasker.SportTask;
import fr.kriszt.theo.remindwear.tasker.Tasker;
import fr.kriszt.theo.shared.Constants;
import fr.kriszt.theo.shared.Coordinates;
import fr.kriszt.theo.shared.SportType;
import fr.kriszt.theo.shared.data.SportDataSet;

public class SportDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = SportDetailsActivity.class.getSimpleName();
    private SportTask sTask;

    private GraphView graph;
    private Tasker tasker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_task_details);
        tasker = Tasker.getInstance(this);


        int id = getIntent().getIntExtra(Constants.KEY_TASK_ID, -1);
        sTask = tasker.getSportTaskByID(id);

        String res;
        TextView steps = findViewById(R.id.steps);
        res = String.valueOf(sTask.getSteps());
        steps.setText(res);

        TextView heart = findViewById(R.id.heart);
        res = String.valueOf(sTask.getHeart());
        heart.setText(res);

        TextView distance = findViewById(R.id.distance);
        res = String.valueOf(String.format("%.2f", sTask.getDistance()));
        distance.setText(res);

        long s = sTask.getDuration();

        res = String.format("%02d : %02d : %02d", s / 3600, (s % 3600) / 60, (s % 60));

        TextView duration = findViewById(R.id.duration);
        duration.setText(res);

        if (sTask.getDataset().hasGPS()) {
            graph = findViewById(R.id.graph);
            loadMap();
        }

        hideUnusedFields(sTask.getDataset());
    }

    private void loadMap() {

        if (!sTask.getDataset().hasGPS()) {
            return;
        }

        ArrayList<DataPoint> listPoint = new ArrayList<>();
        int i = 0;
        for (Coordinates c : sTask.getListCoord()) {
            if (c != null) {
                listPoint.add(new DataPoint(i++, c.getAltitude()));
            }
        }

        DataPoint[] simpleArray = new DataPoint[listPoint.size()];
        listPoint.toArray(simpleArray);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(simpleArray);

        series.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        series.setDrawBackground(true);
        graph.addSeries(series);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

    }

    private void hideUnusedFields(SportDataSet dataset) {
        CardView stepsCard = findViewById(R.id.stepsCard);
        CardView heartCard = findViewById(R.id.heartCard);
        CardView distanceCard = findViewById(R.id.distanceCard);
        CardView durationCard = findViewById(R.id.durationCard);
        CardView mapCard = findViewById(R.id.mapCard);
        CardView elevationCard = findViewById(R.id.elevationCard);

        if (dataset.getSportType() == SportType.SPORT_BIKE ||
                !dataset.hasPodometer()) {
            stepsCard.setVisibility(View.GONE);
        }

        if (!dataset.hasCardiometer()) {
            heartCard.setVisibility(View.GONE);
        }

        if (dataset.getSportType() == SportType.SPORT_WALK ||
                !dataset.hasGPS()) {
            distanceCard.setVisibility(View.GONE);
            mapCard.setVisibility(View.GONE);
            elevationCard.setVisibility(View.GONE);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (!sTask.getDataset().hasGPS()) {
            return;
        }

        ArrayList<LatLng> listTracker = new ArrayList<>();
        for (Coordinates c : sTask.getListCoord()) {
            if (c != null) {
                listTracker.add(new LatLng(c.getLat(), c.getLng()));
            }
        }


        double minLat = 200;
        double maxLat = -200;
        double minLng = 200;
        double maxLng = -200;
        for (LatLng x : listTracker) {
            minLat = Math.min(minLat, x.latitude);
            maxLat = Math.max(maxLat, x.latitude);
            minLng = Math.min(minLng, x.longitude);
            maxLng = Math.max(maxLng, x.longitude);
        }

        LatLngBounds bounds = new LatLngBounds(new LatLng(minLat, minLng), new LatLng(maxLat, maxLng));
        LatLng avg = bounds.getCenter();

        googleMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .color(getResources().getColor(R.color.colorPrimaryDark))
                .addAll(listTracker));

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 10);
        try {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(avg, 6));
            googleMap.animateCamera(cu);
        } catch (Exception e) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(avg, 6));
        }

    }

    public void onDelete(View view) {
        tasker.removeSportTask(sTask);
        tasker.serializeLists();
        tasker.unserializeLists();
        finish();
    }
}
