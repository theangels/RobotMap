package ubibots.robotmap;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Route {

    private ArrayList<LatLng> mPoint;

    /**Debug*/
    private ArrayList<Marker> mMarker;

    public ArrayList<LatLng> getmPoint() {
        return mPoint;
    }

    Route() {
        mMarker = new ArrayList<>();
    }

    /**
     * 通过起点终点，组合成url
     */
    public String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + ","
                + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Travelling Mode
        String mode = "mode=walking";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + parameters;
        System.out.println("getDerectionsURL--->: " + url);
        return url;
    }

    /**
     * 从Url下载json信息
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.e("", "Exception while downloading url" + e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        System.out.println("url:" + strUrl + "---->   downloadurl:" + data);
        return data;
    }

    /**
     * Fetches data from url passed
     */
    public class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * 解析json类
     */
    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
                System.out.println("do in background:" + routes);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            mPoint = new ArrayList<LatLng>();
            /***/
            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    mPoint.add(position);
                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(3);

                // Changing the color polyline according to the mode
                lineOptions.color(Color.BLUE);
            }
            // Drawing polyline in the Google Map for the i-th route
            MapsActivity.mapsActivity.getGoogleMap().addPolyline(lineOptions);


            /**Debug*/
            for (int i = 0; i < mMarker.size(); i++) {
                mMarker.get(i).remove();
            }
            mMarker.clear();
            for (int i = 0; i < mPoint.size(); i++) {
                mMarker.add(MapsActivity.mapsActivity.getGoogleMap().addMarker(new MarkerOptions()
                        .position(mPoint.get(i))
                        .title("" + i)));
            }
            Flag.getRouteFinish = true;
        }
    }

    private class DirectionsJSONParser {
        /**
         * Receives a JSONObject and returns a list of lists containing latitude and
         * longitude
         */
        public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

            List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
            JSONArray jRoutes = null;
            JSONArray jLegs = null;
            JSONArray jSteps = null;

            try {

                jRoutes = jObject.getJSONArray("routes");

                /** Traversing all routes */
                for (int i = 0; i < jRoutes.length(); i++) {
                    jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                    List path = new ArrayList<HashMap<String, String>>();

                    /** Traversing all legs */
                    for (int j = 0; j < jLegs.length(); j++) {
                        jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                        /** Traversing all steps */
                        for (int k = 0; k < jSteps.length(); k++) {
                            String polyline = "";
                            polyline = (String) ((JSONObject) ((JSONObject) jSteps
                                    .get(k)).get("polyline")).get("points");
                            List<LatLng> list = decodePoly(polyline);

                            /** Traversing all points */
                            for (int l = 0; l < list.size(); l++) {
                                HashMap<String, String> hm = new HashMap<String, String>();
                                hm.put("lat",
                                        Double.toString(((LatLng) list.get(l)).latitude));
                                hm.put("lng",
                                        Double.toString(((LatLng) list.get(l)).longitude));
                                path.add(hm);
                            }
                        }
                        routes.add(path);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
            }
            return routes;
        }

        /**
         * Method to decode polyline points Courtesy :
         * jeffreysambells.com/2010/05/27
         * /decoding-polylines-from-google-maps-direction-api-with-java
         */
        private List<LatLng> decodePoly(String encoded) {

            List<LatLng> poly = new ArrayList<LatLng>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }
            return poly;
        }
    }

    private static double angToRad(double angle_d)  ///度数转弧度
    {
        double Pi = 3.1415926535898;
        double rad1;
        rad1 = angle_d * Pi / 180;
        return rad1;
    }

    public static double getAzimuth(LatLng X, LatLng Y) {
        double azimuth;
        double averageLat = (X.latitude + Y.latitude) / 2;
        if (X.latitude - Y.latitude == 0) {
            azimuth = 90;
        } else {
            azimuth = Math.atan((X.longitude - Y.longitude) * Math.cos(angToRad(averageLat)) / (X.latitude - Y.latitude)) * 180 / Math.PI;
        }
        if (X.latitude > Y.latitude) {
            azimuth = azimuth + 180;
        }
        if (azimuth < 0) {
            azimuth = 360 + azimuth;
        }
        return azimuth;
    }

    public static double getDistance(LatLng X, LatLng Y) {
        double dis = Math.sqrt(Math.pow(
                        (X.longitude - Y.longitude) * Math.PI * 6371229 * Math.cos((X.latitude + Y.latitude) / 2 * Math.PI / 180) / 180, 2
                ) + Math.pow(
                        (X.latitude - Y.latitude) * Math.PI * 6371229 / 180, 2
                )
        );
        return dis;
    }
}
