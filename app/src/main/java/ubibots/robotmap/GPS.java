package ubibots.robotmap;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by TheAngels on 2015/11/28.
 */
public class GPS implements LocationListener {

    private LocationManager mLocManager;
    private boolean mGPSOk;
    private Location mLocation;

    public LocationManager getmLocManager() {
        return mLocManager;
    }

    public void setmLocManager(LocationManager mLocManager) {
        this.mLocManager = mLocManager;
    }

    public boolean ismGPSOk() {
        return mGPSOk;
    }

    public void setmGPSOk(boolean mGPSOk) {
        this.mGPSOk = mGPSOk;
    }

    public Location getmLocation() {
        return mLocation;
    }

    public void setmLocation(Location mLocation) {
        this.mLocation = mLocation;
    }


    @Override
    public void onLocationChanged(Location newLoction)//获取位置
    {
        if (mLocation != null) {

            double Lat = newLoction.getLatitude();
            double Lon = newLoction.getLongitude();

            EvilTransform mid = new EvilTransform();
            mid.transform2Mars(Lat, Lon);
            Lat = mid.getlat();
            Lon = mid.getLon();

            mLocation.setLatitude(Lat);//GpsHere
            mLocation.setLongitude(Lon);//GpsHere

        }
    }

    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub
    }

    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub
    }

    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
    }
     /* LocationListener end */

    public void updateCurrentLoction()//位置改变
    {
        String bestProvider = getBestProvider();
        Location newLoction = null;

        if (bestProvider != null)
            newLoction = mLocManager.getLastKnownLocation(bestProvider);

        if (mLocation == null) {
            mLocation = new Location("");
        }

        if (newLoction != null) {

            double Lat = newLoction.getLatitude();
            double Lon = newLoction.getLongitude();

            EvilTransform mid = new EvilTransform();
            mid.transform2Mars(Lat, Lon);
            Lat = mid.getlat();
            Lon = mid.getLon();

            mLocation.setLatitude(Lat);//GpsHere
            mLocation.setLongitude(Lon);//GpsHere

        }
    }

    public String getBestProvider() {
        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String bestOne = mLocManager.getBestProvider(criteria, true);
        return bestOne;
    }


}
