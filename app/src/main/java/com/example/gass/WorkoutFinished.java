package com.example.gass;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class WorkoutFinished extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_finished);
        //Her den sletter comp fra bruger. Skal så bare fjernes i WorkoutActivity linje 146 når jeg får lavet det her.
        //myRefUser.child(user.getUser()).child("CompetitionID").removeValue();

    }
}