package monitor.trackping.com.trackpingmonitor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    private String deviceId;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        deviceId = intent.getStringExtra("deviceId");
        setTitle("Your device's Information");

        firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        mDatabase = database.child("users").child(userId).child(deviceId);

        progressBar.setVisibility(View.VISIBLE);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                progressBar.setVisibility(View.GONE);
                try {
                    Map<String, Object> objectHashMap = (Map<String, Object>)dataSnapshot.getValue();
                    String deviceModel = (String)objectHashMap.get("deviceModel");
                    setTitle(deviceModel + "'s locations");

                    Map<String, Object> locationsMap = (Map<String, Object>)objectHashMap.get("locations");
                    ArrayList<Object> arrLocations = new ArrayList<Object>(locationsMap.values());


                } catch (NullPointerException e) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
