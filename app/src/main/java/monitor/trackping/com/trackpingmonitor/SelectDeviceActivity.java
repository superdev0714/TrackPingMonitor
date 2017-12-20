package monitor.trackping.com.trackpingmonitor;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectDeviceActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;

    @BindView(R.id.list_device)
    ListView listView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_device);
        ButterKnife.bind(this);

        setTitle("Your Devices");

        //getting FireBase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            finish();
        }

        String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        mDatabase = database.child("users").child(userId);

        progressBar.setVisibility(View.VISIBLE);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                progressBar.setVisibility(View.GONE);
                try {
                    Map<String, Object> objectHashMap = (Map<String, Object>)dataSnapshot.getValue();
                    objectHashMap.remove("email");
                    Set<String> keys = objectHashMap.keySet();

                    ArrayList<String> arrDeviceIds = new ArrayList<String>(keys);

                    ArrayList<HashMap<String, String>> arrDevices = new ArrayList<>();

                    for (int i = 0; i < arrDeviceIds.size(); i++) {

                        String deviceId = arrDeviceIds.get(i);

                        Map<String, Object> deviceDetail = (Map<String, Object>)objectHashMap.get(deviceId);
                        String deviceModel = (String)deviceDetail.get("deviceModel");

                        HashMap<String, String> deviceInfo = new HashMap<>();
                        deviceInfo.put("deviceId", deviceId);
                        deviceInfo.put("deviceModel", deviceModel);

                        arrDevices.add(deviceInfo);
                    }

                    listView.setAdapter(new DevicesAdapter(getApplicationContext(), arrDevices));

                } catch (NullPointerException e) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @OnClick(R.id.btn_back)
    public void onBack(View view) {
        firebaseAuth.signOut();
        finish();
    }

    class DevicesAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<HashMap<String, String>> items;

        public DevicesAdapter(Context context, ArrayList<HashMap<String, String>> items) {
            this.context = context;
            this.items = items;
        }

        public int getCount() {
            return items.size();
        }

        public Object getItem(int position) {
            return items.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(context).
                        inflate(R.layout.layout_list_row_devices, parent, false);
            }

            // get current item to be displayed
            final HashMap<String, String> currentItem = (HashMap<String, String>) getItem(position);

            // get the TextView for item name and item description
            TextView textViewDeviceModel = (TextView)
                    convertView.findViewById(R.id.txtDeviceModel);
            TextView textViewDeviceId = (TextView)
                    convertView.findViewById(R.id.txtDeviceId);

            //sets the text for item name and item description from the current item object
            textViewDeviceModel.setText(currentItem.get("deviceModel"));
            textViewDeviceId.setText(currentItem.get("deviceId"));

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SelectDeviceActivity.this, MapsActivity.class);
                    intent.putExtra("deviceId", currentItem.get("deviceId"));
                    startActivity(intent);
                }
            });

            // returns the view for the current row
            return convertView;
        }

    }
}
