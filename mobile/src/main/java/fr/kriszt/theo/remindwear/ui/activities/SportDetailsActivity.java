package fr.kriszt.theo.remindwear.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import fr.kriszt.theo.remindwear.tasker.Coordonate;
import fr.kriszt.theo.remindwear.tasker.SportTask;
import fr.kriszt.theo.remindwear.tasker.Tasker;

public class SportDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ArrayList<Coordonate> listCoordonate;
    private SportTask sTask;

    private GraphView graph;
    private GoogleMap mMap;
    private TextView steps;
    private TextView heart;
    private TextView distance;
    private TextView duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_task_details);


        int id = getIntent().getIntExtra("idSportTask", 0);
        sTask = Tasker.getSportTaskByID(id);
        listCoordonate = sTask.getListCoord();

        graph = (GraphView) findViewById(R.id.graph);

        ArrayList<DataPoint> listPoint = new ArrayList<>();
        //TODO attention ordonné la liste par x croissant
        for(int i=0; i<sTask.getListCoord().size(); i++){
            listPoint.add(new DataPoint(i,sTask.getListCoord().get(i).getHeight()));
        }

        //TODO REMOVE
        listPoint.add(new DataPoint(1.3, 200));
        listPoint.add(new DataPoint(2, 250));
        listPoint.add(new DataPoint(3.5, 300));
        listPoint.add(new DataPoint(4, 650));
        listPoint.add(new DataPoint(5, 498));
        listPoint.add(new DataPoint(32, 200));


        DataPoint[] simpleArray = new DataPoint[ listPoint.size() ];
        listPoint.toArray( simpleArray );
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(simpleArray);

        series.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        series.setDrawBackground(true);
        graph.addSeries(series);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        String res = "";
        steps = (TextView) findViewById(R.id.steps);
        res = String.valueOf(sTask.getSteps());
        steps.setText(res);

        heart = (TextView) findViewById(R.id.heart);
        res = String.valueOf(sTask.getHeart());
        heart.setText(res);

        distance = (TextView) findViewById(R.id.distance);
        sTask.caculateDistance();
        res = String.valueOf(sTask.getDistance());
        distance.setText(res);

        duration = (TextView) findViewById(R.id.duration);
        long s = sTask.getDuration();
        res = String.format("%d : %02d : %02d", s / 3600, (s % 3600) / 60, (s % 60));
        duration.setText(res);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        ArrayList<LatLng> listTracker = new ArrayList<>();
        for(Coordonate c : sTask.getListCoord()){
            listTracker.add(new LatLng(c.getLat(),c.getLng()));
        }

        //TODO REMOVE
        listTracker.add(new LatLng(-35.016, 143.321));
        listTracker.add(new LatLng(-34.747, 145.592));
        listTracker.add(new LatLng(-34.364, 147.891));
        listTracker.add(new LatLng(-33.501, 150.217));
        listTracker.add(new LatLng(-32.306, 149.248));
        listTracker.add(new LatLng(-32.491, 147.309));

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
}
