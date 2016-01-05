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

    private float[] accelerometerValues;
    private float[] magneticFieldValues;

    private TextView textView;

    Direction(){
        accelerometerValues = new float[3];
        magneticFieldValues = new float[3];
        textViewInit();
        sensorManagerInit();
    }

    private void textViewInit(){
        textView = (TextView)MapsActivity.mapsActivity.findViewById(R.id.direction);
        textView.setGravity(Gravity.RIGHT);
        textView.setTextColor(Color.RED);
        textView.setTextSize(15);
    }

    private void calculateOrientation(){
        float[] values = new float[3];
        float[] R = new float[9];
        SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues);
        SensorManager.getOrientation(R, values);
        values[0] = (float)Math.toDegrees(values[0]);
        String view = "现在您所朝向的方位角为北偏东" + values[0] + "°";
        textView.setText(view);
    }

    private void sensorManagerInit() {
        sensorManager = (SensorManager)MapsActivity.mapsActivity.getSystemService(MapsActivity.SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        SensorEventListener sensorLintener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                System.out.println("Test");
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

        sensorManager.registerListener(sensorLintener, accelerometer, Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(sensorLintener, magnetic, Sensor.TYPE_MAGNETIC_FIELD);
    }
}
