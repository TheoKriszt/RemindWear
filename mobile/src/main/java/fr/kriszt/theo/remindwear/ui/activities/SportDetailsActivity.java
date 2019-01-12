package fr.kriszt.theo.remindwear.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;

import fr.kriszt.theo.remindwear.R;
import fr.kriszt.theo.remindwear.ui.fragments.SportTaskListFragment;
import fr.kriszt.theo.shared.Coordinates;
import fr.kriszt.theo.remindwear.tasker.SportTask;
import fr.kriszt.theo.remindwear.tasker.Tasker;
import fr.kriszt.theo.shared.SportType;
import fr.kriszt.theo.shared.data.SportDataSet;

public class SportDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = SportDetailsActivity.class.getSimpleName();
    private List<Coordinates> listCoordinate;
    private SportTask sTask;

    private GraphView graph;
    private GoogleMap mMap;
    private TextView steps;
    private TextView heart;
    private TextView distance;
    private TextView duration;
    private Tasker tasker;
    private CardView stepsCard, heartCard, distanceCard, durationCard, mapCard, elevationCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_task_details);
        tasker = Tasker.getInstance(this);


        int id = getIntent().getIntExtra("idSportTask", -1);
//        long startTime = getIntent().getLongExtra("startTime+", -1);
        Log.w(TAG, "onCreate: SportTask ID = " + id);
        sTask = tasker.getSportTaskByID(id);
        Log.w(TAG, "onCreate: extracted task : " + sTask);
        listCoordinate = sTask.getListCoord();
        Log.w(TAG, "onCreate: " + listCoordinate.size() + " coords found : ");
        Log.w(TAG, "onCreate: " + listCoordinate);

        graph = findViewById(R.id.graph);


        if (sTask.getDataset().hasGPS()) {
            loadMap();
        }


        String res;
        steps = findViewById(R.id.steps);
        res = String.valueOf(sTask.getSteps());
        steps.setText(res);

        heart = findViewById(R.id.heart);
        res = String.valueOf(sTask.getHeart());
        heart.setText(res);

        distance = findViewById(R.id.distance);
//        sTask.caculateDistance();
        res = String.valueOf(String.format("%.2f", sTask.getDistance()));
        distance.setText(res);

        duration = findViewById(R.id.duration);
        long s = sTask.getDuration();
        res = String.format("%02d : %02d : %02d", s / 3600, (s % 3600) / 60, (s % 60));
        duration.setText(res);

        hideUnusedFields(sTask.getDataset());
    }

    private void loadMap() {

        ArrayList<DataPoint> listPoint = new ArrayList<>();
        int i = 0;
        for (Coordinates c : sTask.getListCoord()){
            if (c != null){
                listPoint.add(new DataPoint(i++, c.getAltitude()));
            }
        }

        DataPoint[] simpleArray = new DataPoint[ listPoint.size() ];
        listPoint.toArray( simpleArray );
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(simpleArray);

        series.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        series.setDrawBackground(true);
        graph.addSeries(series);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void hideUnusedFields(SportDataSet dataset) {
        stepsCard = findViewById(R.id.stepsCard);
        heartCard = findViewById(R.id.heartCard);
        distanceCard = findViewById(R.id.distanceCard);
        durationCard = findViewById(R.id.durationCard);
        mapCard = findViewById(R.id.mapCard);
        elevationCard = findViewById(R.id.elevationCard);

//        Toast.makeText(this, "Type : " + dataset.getSportType(), Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "Has Podo : " + dataset.hasPodometer(), Toast.LENGTH_SHORT).show();

        if (dataset.getSportType() == SportType.SPORT_BIKE ||
                !dataset.hasPodometer()){
            stepsCard.setVisibility(View.GONE);
        }

        if (!dataset.hasCardiometer()){
            heartCard.setVisibility(View.GONE);
        }

        if (dataset.getSportType() == SportType.SPORT_WALK ||
                !dataset.hasGPS()){
            distanceCard.setVisibility(View.GONE);
            mapCard.setVisibility(View.GONE);
            elevationCard.setVisibility(View.GONE);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.w(TAG, "onMapReady: ");
        mMap = googleMap;

        ArrayList<LatLng> listTracker = new ArrayList<>();
        for(Coordinates c : sTask.getListCoord()){
            if (c != null) {
                listTracker.add(new LatLng(c.getLat(),c.getLng()));
            }
        }


        double minLat = 200;
        double maxLat = -200;
        double minLng = 200;
        double maxLng = -200;
        for(LatLng x : listTracker){
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
            mMap.animateCamera(cu);
        }catch(Exception e){
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
