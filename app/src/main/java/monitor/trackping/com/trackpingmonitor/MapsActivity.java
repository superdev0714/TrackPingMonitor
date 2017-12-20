package monitor.trackping.com.trackpingmonitor;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private String deviceId;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    ArrayList<Object> arrLocations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        Intent intent = getIntent();
        deviceId = intent.getStringExtra("deviceId");
        setTitle("Your device's Information");

        firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        mDatabase = database.child("users").child(userId).child(deviceId);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    Map<String, Object> objectHashMap = (Map<String, Object>)dataSnapshot.getValue();
                    String deviceModel = (String)objectHashMap.get("deviceModel");
                    setTitle(deviceModel + "'s locations");

                    Map<String, Object> locationsMap = (Map<String, Object>)objectHashMap.get("locations");
                    arrLocations = new ArrayList<Object>(locationsMap.values());

                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(MapsActivity.this);

                } catch (NullPointerException e) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        for (int i = 0; i < arrLocations.size(); i++) {
            Map<String, Object> location = (Map<String, Object>)arrLocations.get(i);
            double latitude = (double)location.get("latitude");
            double longitude = (double)location.get("longitude");
            long battery = (long)location.get("battery");
            String time = (String)location.get("time");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddkkmmss");
            String dateTime = time;
            try {
                Date locationTime = dateFormat.parse(time);
                SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd:kk:mm:ss");
                dateTime = newDateFormat.format(locationTime);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            LatLng sydney = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(sydney).title(dateTime).snippet(String.valueOf(battery)+"%"));

            if (i == arrLocations.size() - 1) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            }

        }

    }
}
