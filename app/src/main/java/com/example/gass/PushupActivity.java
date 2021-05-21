package com.example.gass;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class PushupActivity extends AppCompatActivity {
    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private SensorEventListener proximitySensorListener;
    CountDownTimer countDownTimer;
    CountDownTimer countDownTimerBefore;
    ExerciseData exerciseData;
    int millisInFuture;
    int countDownInterval;
    User user;
    private FirebaseDatabase database;
    private DatabaseReference myRefUser;
    private DatabaseReference myRefComp;
    TextView compReps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pushup);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        final TextView textview=(TextView) findViewById(R.id.textView);
        final TextView pushupTimer = findViewById(R.id.pushupTimer);
        compReps = findViewById(R.id.compReps);

        final PushupExercise pushupExercise = new PushupExercise(); //Starter med nul reps
        exerciseData = ExerciseData.getInstance();
        millisInFuture = 10000;
        countDownInterval = 1000;
        user = User.getInstance();
        database = FirebaseDatabase.getInstance();
        myRefUser = database.getReference("user");
        myRefComp = database.getReference("competition");



        if(proximitySensor == null){
            Toast.makeText(this, "Proximity sensor not available !", Toast.LENGTH_LONG).show();
            finish();
        }

        myRefComp.addValueEventListener(new ValueEventListener() {
            int otherUserCompReps = 0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Tjekker først om den anden brugers reps eksisterer. For derefter at hente dem og vise dem.
                if(dataSnapshot.child(String.valueOf(user.getCompetitionID())).child(String.valueOf(user.getOtherUserCompID())).child("CompReps").exists()){
                    otherUserCompReps = Integer.parseInt(dataSnapshot.child(String.valueOf(user.getCompetitionID())).child(String.valueOf(user.getOtherUserCompID())).child("CompReps").getValue(String.class));
                }
                compReps.setText("Enemy's Reps: " + otherUserCompReps);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Timer der kører før sensoren går igang og reps tælles. For at gøre sig klar.
        countDownTimerBefore = new CountDownTimer(4000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                pushupTimer.setText(millisUntilFinished/1000 + "");
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onFinish() {
                Toast.makeText(PushupActivity.this, "GO", Toast.LENGTH_SHORT).show();
                countDownTimer.start();
                sensorManager.registerListener(proximitySensorListener, proximitySensor, 2*1000*1000, 1000);

            }
        };
        countDownTimerBefore.start();

        countDownTimer = new CountDownTimer(millisInFuture, countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                pushupTimer.setText(millisUntilFinished/1000 + " Seconds left");
            }

            @Override
            public void onFinish() {
                Toast.makeText(PushupActivity.this,"finish",Toast.LENGTH_SHORT).show();
                exerciseData.addExercise(pushupExercise);
                Intent exercise2 = new Intent(PushupActivity.this, WorkoutFinished.class);
                startActivity(exercise2);
                onStop();
                finish();
            }
        };

        proximitySensorListener = new SensorEventListener() {
            boolean rep;
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float currentValue = sensorEvent.values[0]; //https://www.youtube.com/watch?v=3N5C7M961-k&t=60s
                //vi lavede funktionalitet, videoen viste hvordan man brugte sensor.

                if(currentValue == 5.0){
                    rep = false;
                }
                if (currentValue == 0.0 && rep == false){
                    pushupExercise.addRep();
                    rep = true;
                }
                textview.setText(String.valueOf(pushupExercise.getReps()));
                //Her uploade værdien under compreps under ens egen, som compReps i workoutfinished.
                myRefComp.child(String.valueOf(user.getCompetitionID())).child(String.valueOf(user.getUserCompetitionID())).child("CompReps").setValue(String.valueOf(pushupExercise.getReps())); //Dette er kommet i DatabaseSingleton. Skal bare kalde den her.
                /*
                //Hvis der skal afspilles lyd.
                switch (pushupExercise.getReps()){
                    case 10:
                        haidokenSound.start();
                        break;
                    case 15:
                        bruhexplosionSound.start();
                        break;
                    case 20:
                        yesSound.start();
                }
                 */
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
    }
    @Override
    protected void onStop(){
        super.onStop();
        sensorManager.unregisterListener(proximitySensorListener);
    }
}