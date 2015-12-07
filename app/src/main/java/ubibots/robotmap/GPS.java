package ubibots.robotmap;

import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

/**
 * Created by TheAngels on 2015/12/5.
 */
public class GPS implements ConnectionCallbacks,OnConnectionFailedListener{

    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;

    public Location getmLastLocation() {
        return mLastLocation;
    }

    public GPS(){
        buildGoogleApiClient();
        mLastLocation = null;
    }

    protected synchronized void buildGoogleApiClient() {
        System.out.println("Hello World!");
        mGoogleApiClient = new GoogleApiClient.Builder(MapsActivity.mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        System.out.println("Connect!");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            System.out.println("GPS is " + mLastLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        System.out.println(connectionResult);
    }
}
