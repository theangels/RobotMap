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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;


public class GPS implements ConnectionCallbacks, OnConnectionFailedListener,LocationListener{

    private Location currentLocation;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public GPS() {
        currentLocation = null;
        locationRequest = null;
        buildGoogleApiClient();
        createLocationRequest();
        googleApiClient.connect();
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(MapsActivity.context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        try {
            currentLocation = EvilTransform.TransForm(LocationServices.FusedLocationApi.getLastLocation(
                    googleApiClient));
            if (locationRequest != null) {
                Flag.getGPS = true;
                startLocationUpdates();
                LatLng start = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                MapsActivity.mapsActivity.getGoogleMap().moveCamera(CameraUpdateFactory.newLatLng(start));
                MapsActivity.mapsActivity.getGoogleMap().animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
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
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = EvilTransform.TransForm(location);
        Flag.getGPS = true;
    }
}
