package com.example.medialysplayer;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  {

    ListView myListView;
    ArrayList<String>  arrayList;
    ArrayAdapter myArrayAdapter;
    MediaPlayer myMediaPlayer;
    Button myButton;
    TextView myTextView;
    SensorManager sensorManager;
    Sensor lightSensor;
    SensorEventListener lightEventListener;
    float maxValue;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        myListView = findViewById(R.id.myListView);
        myButton = findViewById(R.id.myButton);
        myTextView = findViewById(R.id.myTextView);
        arrayList = new ArrayList<String>();
        Field[] fields = R.raw.class.getFields();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        maxValue = lightSensor.getMaximumRange();
        myTextView.setText("" + maxValue);
        myTextView.setTextSize(18);


        for (int i = 0; i < fields.length; i++) {
            arrayList.add(fields[i].getName());
        }
        //      arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        myArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, arrayList);
        myListView.setAdapter(myArrayAdapter);
        if(lightSensor == null) {
            Toast.makeText(this, "lightsensoren bliver ikke kaldt", Toast.LENGTH_SHORT).show();
        }
        lightEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float value = event.values[0];
                myTextView.setText("" + value);
                getSupportActionBar().setTitle("lysindfald: " + value);


                myTextView.setText(String.valueOf(event.values[0]));
                if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                    myTextView.setText(String.valueOf(event.values[0]));
                    if (event.values[0] < 3 && myMediaPlayer != null) {
                        myMediaPlayer.pause();
                    } else if (event.values[0] > 8 && myMediaPlayer != null) {
                        myMediaPlayer.start();
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };


        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                if (myMediaPlayer != null) {
                    myMediaPlayer.release();
                }
                int resId = getResources().getIdentifier(arrayList.get(i).toString(), "raw", getPackageName());
                myMediaPlayer = MediaPlayer.create(MainActivity.this, resId);
                myMediaPlayer.start();

            }
        });

        myButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (myMediaPlayer != null) {
                    myMediaPlayer.stop();
                }

            }
        });




    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(lightEventListener,lightSensor, sensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(myMediaPlayer != null) {
            myMediaPlayer.release();
        }
        sensorManager.unregisterListener(lightEventListener);
    }


/*    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }*/
}
