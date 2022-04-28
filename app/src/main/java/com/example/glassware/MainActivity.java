package com.example.glassware;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.instacart.library.truetime.TrueTime;
import com.opencsv.CSVWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.security.cert.CertPathBuilder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private Accelerometer accelerometer;
    private Gyroscope gyroscope;

    //    ArrayList<HashMap> acc_readings = new ArrayList<HashMap>();
//    ArrayList<HashMap> gyro_readings = new ArrayList<HashMap>();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    String writeBool = "False";

    ArrayList<String> acc_readings = new ArrayList<String>();
    ArrayList<String> gyro_readings = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseReference writeRef = database.getReference().child("Write Data");
        writeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                writeBool = snapshot.getValue().toString();
                Log.d("DataSnapshot", "Write: " + writeBool);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    TrueTime.build().initialize();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

//        acc_readings.add("Time 1,3,4,1");
//        acc_readings.add("Time 2,2,1,2");
//        acc_readings.add("Time 3,5,9,0");


        accelerometer = new Accelerometer(this);
        gyroscope = new Gyroscope(this);


        // Listen for changes in accelerometer
//        accelerometer.setListener(new Accelerometer.Listener() {
//            @Override
//            public void onTranslation(float tx, float ty, float tz) {
//
//                // If the data is being recorded, write the values to a hashmap and save them to the array list
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss aa");
//                String format = simpleDateFormat.format(TrueTime.now());
//                Log.d("MainActivity", "Current Timestamp: " + format);
//                AccReading reading = new AccReading(format, tx, ty, tz);
//                writeNewAccReading(format, tx, ty, tz);
//
//                String content = format + "," + Float.toString(tx) + "," + Float.toString(ty) + "," + Float.toString(tz);
//                acc_readings.add(content);
//
//            }
//
//        });

        // Listen for changes in gyroscope
        gyroscope.setListener(new Gyroscope.Listener() {
            @Override
            public void onRotation(float rx, float ry, float rz) {
                // If the data is being recorded, write the values to a hashmap and save them to the array list

                Log.d("GyroscopeListener", "Write: " + writeBool);
                if(writeBool.equals("True")) {
                    if (ry < -0.5 || ry > 0.5) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss aa");
                        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));
                        String format = simpleDateFormat.format(TrueTime.now());
                        Log.d("MainActivity", "Current Timestamp: " + format);

//                    GyroReading reading = new GyroReading(format, rz, ry, rz);
//                    writeNewGyroReading(format, rx, ry, rz);

                        myRef.child("Glass Gyroscope").child(format).setValue("HT");


                        String content = format + "," + Float.toString(rx) + "," + Float.toString(ry) + "," + Float.toString(rz);
                        gyro_readings.add(content);
                    }
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        accelerometer.register();
        gyroscope.register();
    }

    @Override
    protected void onPause() {
        super.onPause();

        accelerometer.unregister();
        gyroscope.unregister();
    }

    protected void onStop() {

//        CSVWriter writer = null;
//        try {
//            writer = new CSVWriter(new FileWriter("C:\\Users\\Cassidy Bradley\\Documents\\790-001\\Glassware\\app\\acc_readings.csv"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        for (String i : acc_readings) {
//            writer.writeNext(new String[]{i});
//        }
//        try {
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        acc_readings.clear();


        super.onStop();

    }

    @IgnoreExtraProperties
    public class AccReading {

        public String timestamp;
        public Float x;
        public Float y;
        public Float z;

        public AccReading() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public AccReading(String timestamp, Float x, Float y, Float z) {
            this.timestamp = timestamp;
            this.x = x;
            this.y = y;
            this.z = z;
        }

    }

    public void writeNewAccReading(String timestamp, Float x, Float y, Float z) {
        AccReading reading = new AccReading(timestamp, x, y, z);

        myRef.child("Glass Accelerometer").child(timestamp).setValue(reading);
    }

    @IgnoreExtraProperties
    public class GyroReading {

        public String timestamp;
        public Float x;
        public Float y;
        public Float z;

        public GyroReading() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public GyroReading(String timestamp, Float x, Float y, Float z) {
            this.timestamp = timestamp;
            this.x = x;
            this.y = y;
            this.z = z;
        }

    }

    public void writeNewGyroReading(String timestamp, Float x, Float y, Float z) {
        GyroReading reading = new GyroReading(timestamp, x, y, z);

        myRef.child("Glass Gyroscope").child(timestamp).setValue(reading);
    }

    public class ProcessData {
        
    }

}

