package ubibots.robotmap;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Timer;
import java.util.TimerTask;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    public static MapsActivity mapsActivity;
    public static Context mContext;
    private GoogleMap mMap;
    private GoogleMapOptions options;
    private MapFragment mapFragment;
    private GPS mGPS;
    private final Timer timer = new Timer();
    public GoogleMap getmMap() {
        return mMap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the MapFragment and get notified when the map is ready to be used.
        Init();
    }

    private void Init(){
        mapsActivity = this;
        mContext = this;
        mGPS = new GPS();
        options = new GoogleMapOptions()
            .mapType(GoogleMap.MAP_TYPE_NORMAL)
            .compassEnabled(true)
            .rotateGesturesEnabled(true)
            .tiltGesturesEnabled(true);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        };
        timer.schedule(task, 1000, 3000);//推迟发送 发送间断
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.newInstance(options);
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
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);
    }

    //不间断发送信息0.4秒一次
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            // 要做的事情
            System.out.println(mGPS.getmCurrentLocation());
            if(mGPS.getmCurrentLocation() != null){
                Location mLocation = mGPS.getmCurrentLocation();
                LatLng initial = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());

                MapsActivity.mapsActivity.getmMap().addMarker(new MarkerOptions()
                        .position(initial)
                        .title("Marker in Initial"));
                timer.cancel();
            }
            super.handleMessage(msg);
        }
    };
}
