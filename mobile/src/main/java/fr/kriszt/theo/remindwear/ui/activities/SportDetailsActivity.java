package fr.kriszt.theo.remindwear.ui.activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

import fr.kriszt.theo.remindwear.R;

public class SportDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GraphView graph;
    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_details);

        graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        series.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        series.setDrawBackground(true);
        graph.addSeries(series);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        ArrayList<LatLng> listTracker = new ArrayList<>();
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
            if(x.latitude < minLat){
                minLat = x.latitude;
            }
            if(x.latitude > maxLat){
                maxLat = x.latitude;
            }
            if(x.longitude < minLng){
                minLng = x.longitude;
            }
            if(x.longitude > maxLng){
                maxLng = x.longitude;
            }
        }

        LatLngBounds bounds = new LatLngBounds(new LatLng(minLat, minLng), new LatLng(maxLat, maxLng));
        LatLng avg = bounds.getCenter();

        Polyline polyline = googleMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .color(getResources().getColor(R.color.colorPrimaryDark))
                .addAll(listTracker));

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 10);
        try {
            mMap.animateCamera(cu);
        }catch(Exception e){
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(avg, 6));
        }

    }
}
