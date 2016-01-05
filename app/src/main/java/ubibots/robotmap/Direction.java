package ubibots.robotmap;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Gravity;
import android.widget.TextView;

public class Direction  {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetic;
    SensorEventListener sensorLintener;

    private float direction = 0;

    private float[] accelerometerValues;
    private float[] magneticFieldValues;

    /**Debug*/
    private TextView textView;

    public SensorManager getSensorManager() {
        return sensorManager;
    }

    public Sensor getMagnetic() {
        return magnetic;
    }

    public Sensor getAccelerometer() {
        return accelerometer;
    }

    public SensorEventListener getSensorLintener() {
        return sensorLintener;
    }

    public float getDirection() {
        return direction;
    }

    Direction(){
        accelerometerValues = new float[3];
        magneticFieldValues = new float[3];
        /**Debug*/
        textViewInit();
        sensorManagerInit();
    }

    /**Debug*/
    private void textViewInit(){
        textView = (TextView)MapsActivity.mapsActivity.findViewById(R.id.direction);
        textView.setGravity(Gravity.LEFT);
        textView.setTextColor(Color.RED);
        textView.setTextSize(15);
    }

    private void calculateOrientation(){
        float[] values = new float[3];
        float[] R = new float[9];
        SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues);
        SensorManager.getOrientation(R, values);
        direction = values[0] = (float)Math.toDegrees(values[0]);
        String view = "现方位角为北偏东" + String.format("%.2f",values[0]) + "°";
        textView.setText(view);
    }

    private void sensorManagerInit() {
        sensorManager = (SensorManager)MapsActivity.mapsActivity.getSystemService(MapsActivity.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        sensorLintener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    accelerometerValues = event.values;
                }
                if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                    magneticFieldValues = event.values;
                }
                calculateOrientation();
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };
    }
}
