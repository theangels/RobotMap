package ubibots.robotmap;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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
    private final Timer mGetGPSTimer = new Timer();
    private final Timer mGetDestTimer = new Timer();
    private GetRoute mGetRoute;
    private GetRoute.DownloadTask mDownloadTask;
    private Button mButton;
    private MarkerOptions mMarkerOption;
    private Marker mMarker;
    private Control mControl;

    public GoogleMap getmMap() {
        return mMap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the MapFragment and get notified when the map is ready to be used.
        MapInit();
        MarkerInit();
        GPSInit();
        GetRouteInit();
        ButtonInit();
        ControlInit();
    }

    private void MapInit(){
        mapsActivity = this;
        mContext = this;
        options = new GoogleMapOptions()
                .mapType(GoogleMap.MAP_TYPE_NORMAL)
                .compassEnabled(true)
                .rotateGesturesEnabled(true)
                .tiltGesturesEnabled(true);
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.newInstance(options);
    }

    private void GPSInit(){
        mGPS = new GPS();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Message message = new Message();
                message.what = 1;
                getGPSHandler.sendMessage(message);
            }
        };
        mGetGPSTimer.schedule(task, 1000, 3000);//推迟 间断
    }

    private void GetRouteInit(){
        mGetRoute = new GetRoute();
        mDownloadTask = mGetRoute.new DownloadTask();
    }

    private void ButtonInit(){
        mButton = (Button)findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mControl.requirePlace();
            }
        });
    }

    private void MarkerInit(){
        mMarkerOption = new MarkerOptions();
    }

    private void ControlInit(){
        mControl = new Control();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Message message = new Message();
                message.what = 1;
                controlHandler.sendMessage(message);
            }
        };
        mGetDestTimer.schedule(task, 1000, 3000);//推迟 间断
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

    //不间断获取GPS 3秒一次
    Handler getGPSHandler = new Handler() {
        @Override
        public synchronized void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            // 要做的事情

            /**Debug*/
            System.out.println(mGPS.getmCurrentLocation());

            if(mGPS.getmCurrentLocation() != null){
                Location mLocation = mGPS.getmCurrentLocation();
                LatLng initial = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                if(mMarker!=null){
                    mMarker.remove();
                }
                mMarkerOption
                        .position(initial)
                        .title("Marker in Initial");
                mMarker = MapsActivity.mapsActivity.getmMap().addMarker(mMarkerOption);
            }

            super.handleMessage(msg);
        }
    };

    //不间断询问导航 3秒一次
    Handler controlHandler = new Handler() {
        @Override
        public synchronized void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            // 要做的事情

            /**Debug*/
            System.out.println(mControl.getDest());
                if (mGPS.getmCurrentLocation() != null && mControl.isRequireFinish()) {
                    mControl.setRequireFinish(false);

                    LatLng op = new LatLng(mGPS.getmCurrentLocation().getLatitude(), mGPS.getmCurrentLocation().getLongitude());
                    if (mControl.getDest().compareTo("图书馆") == 0) {
                        LatLng ed = new LatLng(30.3285390, 120.1559760);//图书馆
                        mDownloadTask.execute(mGetRoute.getDirectionsUrl(op, ed));
                    }
                }

            super.handleMessage(msg);
        }
    };
}
