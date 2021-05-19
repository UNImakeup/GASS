package com.example.gass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WorkoutFinished extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference myRefUser;
    User user;
    DatabaseReference myRefComp;
    FirebaseAuth firebaseAuth;
    Button backToNavigation;
    TextView compStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_finished);
        backToNavigation = findViewById(R.id.backToNavigation);
        compStatus = findViewById(R.id.compStatus);

        database = FirebaseDatabase.getInstance();
        myRefComp = database.getReference("competition");
        myRefUser = database.getReference("user");
        user = User.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        //Her den sletter comp fra bruger. Skal så bare fjernes i WorkoutActivity linje 146 når jeg får lavet det her.
        myRefUser.child(user.getUser()).child("CompetitionID").removeValue();

        backToNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WorkoutFinished.this, NavigationActivity.class);
                startActivity(intent);
            }
        });

        //Skal så tilføje eller fjerne point afhængig af resultat.
        // Bare læse en gang fra databasen med reps, ligesom i profil. Sammenligne og tilføje til DB ud fra det.
        //Skal huske vi stadig har ting i user klassen, der er singleton, der kan bruges til nedenstående.
        myRefComp.addListenerForSingleValueEvent(new ValueEventListener() {
            int otherUserCompReps;
            int userCompReps;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(String.valueOf(user.getCompetitionID())).exists()) { //denne ender forkert, burde dække hele sætningen.
                    if (user.getUserCompetitionID() == 1) { //Finder den anden brugers id baseret på ens egen, da der kun burde være 2 i konkurrencen.
                        int otherUserCompID = 2;
                        if (dataSnapshot.child(String.valueOf(user.getCompetitionID())).child(String.valueOf(otherUserCompID)).child("CompReps").exists()) { //her den lavede en fejl i profilen, da den kun tjekkede om den anden bruger havde reps. Ikke om de bare havde joinet overhovedet.
                            otherUserCompReps = Integer.parseInt(dataSnapshot.child(String.valueOf(user.getCompetitionID())).
                                    child(String.valueOf(otherUserCompID)).child("CompReps").getValue(String.class));
                        } else {
                            //compStatus.setText("No one has joined your comp yet. Send the CompID to them, so they can join: " + user.getCompetitionID());
                        }
                        if (dataSnapshot.child(String.valueOf(user.getCompetitionID())).child(String.valueOf(user.getUserCompetitionID())).child("CompReps").exists()) {
                            userCompReps = Integer.parseInt(dataSnapshot.child(String.valueOf(user.getCompetitionID())).
                                    child(String.valueOf(user.getUserCompetitionID())).child("CompReps").getValue(String.class));
                        }
                        if (dataSnapshot.child(String.valueOf(user.getCompetitionID())).child(String.valueOf(otherUserCompID)).child("CompReps").exists()
                                && otherUserCompReps > userCompReps) {
                            //compStatus.setText("you are losing your competition, get to work " + firebaseAuth.getCurrentUser().getDisplayName());
                        } else if(dataSnapshot.child(String.valueOf(user.getCompetitionID())).child(String.valueOf(otherUserCompID)).child("CompReps").exists()
                                && otherUserCompReps < userCompReps){
                            //compStatus.setText("You are winning your competition, " + firebaseAuth.getCurrentUser().getDisplayName() + " you absolute champion");
                        }
//Det her var baseret på at skulle skrive om den anden er joinet eller ej. Det ved vi de er nu.
                    } else if (user.getUserCompetitionID() == 2) { //Tror ikke jeg behøver det her, da jeg har compreps og det andet. Skal så kun redigere i den nuværende brugers stats, da det gøres individuelt på begge.
                        int otherUserCompID = 1;
                        if (dataSnapshot.child(String.valueOf(user.getCompetitionID())).child(String.valueOf(otherUserCompID)).child("CompReps").exists()) {
                            if (dataSnapshot.child(String.valueOf(user.getCompetitionID())).child(String.valueOf(otherUserCompID)).child("CompReps").exists()) {
                                otherUserCompReps = Integer.parseInt(dataSnapshot.child(String.valueOf(user.getCompetitionID())).child(String.valueOf(otherUserCompID)).child("CompReps").getValue(String.class));
                            } //Behøver ikke tjekke om den anden bruger er oprette og skrive at de ikke er, da man er nummer 2 og altså ikke den der oprettede. Der er et andet medlem.
                            if (dataSnapshot.child(String.valueOf(user.getCompetitionID())).child(String.valueOf(user.getUserCompetitionID())).child("CompReps").exists()) {
                                userCompReps = Integer.parseInt(dataSnapshot.child(String.valueOf(user.getCompetitionID())).child(String.valueOf(user.getUserCompetitionID())).child("CompReps").getValue(String.class));
                            }
                            if (otherUserCompReps > userCompReps) {
                                //compStatus.setText("you are losing your competition, get to work " + firebaseAuth.getCurrentUser().getDisplayName());
                            } else {
                                //compStatus.setText("You are winning your competition, " + firebaseAuth.getCurrentUser().getDisplayName() + " you absolute champion");
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        //Resten af træningstingene skal være nogenlunde det samme, skal bare tilføje billeder og den andens reps. Nok 1-2 dages godt arbejde. Færdig fredag agtig
        //Kunne have en workout activity med hver træningsting som et fragment.
        //Så de har samme layout, men indholdet kan være anderledes. Kan starte med bare at tilføje det der skal være her.
    }
}