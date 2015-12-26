package ubibots.robotmap;

import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.maps.model.LatLng;


public class GPS implements ConnectionCallbacks, OnConnectionFailedListener,LocationListener{

    private Location mCurrentLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    public Location getmCurrentLocation() {
        return mCurrentLocation;
    }

    public GPS() {
        mCurrentLocation = null;
        mLocationRequest = null;
        buildGoogleApiClient();
        createLocationRequest();
        mGoogleApiClient.connect();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(MapsActivity.context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        try {
            mCurrentLocation = EvilTransform.TransForm(LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient));
            if (mLocationRequest != null) {
                Flag.getGPS = true;
                startLocationUpdates();
            }
        }
        catch (Exception ex){
            System.err.println("Get GPS error!");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    /**
     * If your app accesses the network or does other long-running work after receiving a location update,
     * adjust the fastest interval to a slower value. This adjustment prevents your app from receiving updates
     * it can't use. Once the long-running work is done, set the fastest interval back to a fast value.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = EvilTransform.TransForm(location);
        Flag.getGPS = true;

        /**Debug*/
        System.out.println("Current GPS is " + mCurrentLocation);
    }
}
