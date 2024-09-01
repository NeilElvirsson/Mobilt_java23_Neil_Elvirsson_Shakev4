package com.example.androidshakeapp13;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView textView;
    private SensorManager sensorManager;
    private Sensor mySensor;
    private long lastUpdate, actualTime;
    private SeekBar seekBar;
    private ImageView imageView;
    private float currentRotation = 0f;  // Variable to track the current rotation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.grit_academy_logo);

        seekBar = findViewById(R.id.seekBar);

        lastUpdate = System.currentTimeMillis();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mySensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (mySensor == null) {
            Toast.makeText(this, "No accelerometer detected on device", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Log.i("Milkshake", "Device has been shaken!");
            sensorManager.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            return insets;
        });

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            float[] values = event.values;
            float x = values[0];
            float y = values[1];
            float z = values[2];

            float EG = SensorManager.GRAVITY_EARTH;
            float dvAccel = (x * x + y * y + z * z) / (EG * EG);
            textView.setText("X: " + x + "\n" + "Y: " + y + "\n" + "Z: " + z + "\n" + "DevAccel: " + dvAccel);

            seekBar.setProgress((int)(x * 10 + 50));

            if (dvAccel >= 1.5) {
                actualTime = System.currentTimeMillis();
                if ((actualTime - lastUpdate) > 1000) {
                    lastUpdate = actualTime;

                    currentRotation += 90f;
                    imageView.setRotation(currentRotation);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
    }
}