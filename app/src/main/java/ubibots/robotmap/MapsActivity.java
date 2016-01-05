package ubibots.robotmap;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
    public static Context context;

    private GoogleMap googleMap;
    private GPS mGPS;
    private Target target;
    private Route route;
    private Direction direction;

    private final Timer getGPSTimer = new Timer();
    private final Timer getDestTimer = new Timer();
    private final Timer findTheWayTimer = new Timer();

    private Route.DownloadTask downloadTask;
    private MarkerOptions markerOption;
    private Marker marker;
    private TextView textView;

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mapInit();
        markerInit();
        GPSInit();
        getRouteInit();
        buttonInit();
        getTargetInit();
        getDirectionInit();
    }

    private void mapInit(){
        mapsActivity = this;
        context = this;
        GoogleMapOptions options = new GoogleMapOptions()
                .mapType(GoogleMap.MAP_TYPE_NORMAL)
                .compassEnabled(true)
                .rotateGesturesEnabled(true)
                .tiltGesturesEnabled(true);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        MapFragment.newInstance(options);
    }

    private void GPSInit(){
        mGPS = new GPS();
        getGPSTimer.schedule(getGPSTask, 1000, 2000);//推迟 间断
    }

    private void getRouteInit(){
        route = new Route();
        downloadTask = route.new DownloadTask();
    }

    private void buttonInit(){
        Button mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                target.requirePlace();
                if(!Flag.launchRequire) {
                    getDestTimer.schedule(getRouteTask, 1000, 2000);//推迟 间断
                    Flag.launchRequire = true;
                }
            }
        });
    }

    private void markerInit(){
        markerOption = new MarkerOptions();
    }

    private void getTargetInit(){
        target = new Target();
        textView = (TextView)findViewById(R.id.tonextpoint);
        textView.setGravity(Gravity.RIGHT);
        textView.setTextColor(Color.RED);
        textView.setTextSize(15);
    }

    private void getDirectionInit(){
        direction = new Direction();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//        googleMap.setMyLocationEnabled(true);
    }

    TimerTask getGPSTask = new TimerTask() {
        @Override
        public void run() {
            Message message = new Message();
            message.what = 1;
            getGPSHandler.sendMessage(message);
        }
    };
    /**不间断获取GPS 3秒一次*/
    Handler getGPSHandler = new Handler() {
        @Override
        public synchronized void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            // 要做的事情
            switch (msg.what) {
                case 1:
                    /**Debug
                    System.out.println(mGPS.getmCurrentLocation());
                    */
                    if (mGPS.getCurrentLocation() != null) {
                        Location mLocation = mGPS.getCurrentLocation();
                        LatLng initial = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                        if (marker != null) {
                            marker.remove();
                        }
                        markerOption
                                .position(initial)
                                .title("Marker in Initial");
                        marker = googleMap.addMarker(markerOption);
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    TimerTask getRouteTask = new TimerTask() {
        @Override
        public void run() {
            Message message = new Message();
            message.what = 1;
            getRouteHandler.sendMessage(message);
        }
    };
    /**不间断询问导航 3秒一次*/
    Handler getRouteHandler = new Handler() {
        @Override
        public synchronized void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            // 要做的事情
            switch (msg.what) {
                case 1:
                    /**Debug
                    System.out.println(target.getDest());
                    */
                    if (Flag.getGPS && Flag.requireFinish) {
                        Flag.getGPS = false;
                        Flag.requireFinish = false;
                        LatLng op = new LatLng(mGPS.getCurrentLocation().getLatitude(), mGPS.getCurrentLocation().getLongitude());
                        if (target.getDest().compareTo("图书馆") == 0) {
                            LatLng ed = new LatLng(30.3285390, 120.1559760);//图书馆
                            downloadTask.execute(route.getDirectionsUrl(op, ed));
                            findTheWayTimer.schedule(findTheWayTask, 1000, 2000);//推迟 间断
                        }
                        getRouteTask.cancel();
                        Flag.launchRequire = false;
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    TimerTask findTheWayTask = new TimerTask() {
        @Override
        public void run() {
            Message message = new Message();
            message.what = 1;
            findTheWayHandler.sendMessage(message);
        }
    };
    /**不间断询问导航 3秒一次*/
    Handler findTheWayHandler = new Handler() {
        @Override
        public synchronized void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            // 要做的事情
            switch (msg.what) {
                case 1:
                    if(Flag.getRouteFinish) {
                        if (Flag.reachPoint == route.getmPoint().size()) {
                            findTheWayTimer.cancel();
                            Flag.reachPoint = -1;
                        }
                        else {
                            LatLng op = new LatLng(mGPS.getCurrentLocation().getLatitude(), mGPS.getCurrentLocation().getLongitude());
                            LatLng ed = route.getmPoint().get(Flag.reachPoint + 1);
                            double azimuth = Route.getAzimuth(op, ed);
                            double distance = Route.getDistance(op, ed);
                            if (distance <= 5)
                                Flag.reachPoint++;
                            String howToNextPoint = "现在到达第 " + Flag.reachPoint + "个点,距离下一个点" + "\n" + "向北偏东: " + String.format("%.2f", azimuth) + "\n" + "距离: " + String.format("%.2f", distance);
                            textView.setText(howToNextPoint);
                        }
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };
}