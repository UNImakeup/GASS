package com.example.gass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
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
    ExerciseData exerciseData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_finished);

        TextView workoutSummary = findViewById(R.id.workoutResult);

        //final MediaPlayer lyd = MediaPlayer.create(this, R.raw.wow); //Create sound

        exerciseData = ExerciseData.getInstance(); //Hent exerciseData så vi kan printe resultater

        backToNavigation = findViewById(R.id.backToNavigation);
        compStatus = findViewById(R.id.compStatus);

        database = FirebaseDatabase.getInstance();
        myRefComp = database.getReference("competition");
        myRefUser = database.getReference("user");
        user = User.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();


        backToNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //Knap der går tilbage til navigation.
                exerciseData.clearExercises(); //For at cleare øvelserne, så man kan træne igen.
                Intent intent = new Intent(WorkoutFinished.this, NavigationActivity.class);
                startActivity(intent);
            }
        });


        //vise tekst. Måske bruge metode i ExerciseData, print og sådan. Vise getSum til sidst.
        workoutSummary.setText("Pushups: " + exerciseData.getExercises().get(0).getReps() + " lol sum " + exerciseData.getSum()
        );



        //Fint at det her slettes/ikke kan hentes igen. Skal bare hente den anden brugers reps sammenligne og lægge egne op.
        //Mangler bare at kunne hente fra bruger objektet.Kan vi nu
        //Her lægger vi reps op på bruger i  databasen
        myRefUser.addListenerForSingleValueEvent(new ValueEventListener() {
            int totalReps = 0;
            int competitionID;
            int userCompetitionID;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(user.getUser()).child("TotalReps").exists()){
                    totalReps +=  dataSnapshot.child(user.getUser()).child("TotalReps").getValue(Integer.class); //add current totalreps.//Virker lige nu første gang, prøver at få det til at virke nå den nuværende værdi skal oveni. Tror værdierne fucker. Det var det, nu virker det. https://stackoverflow.com/questions/55042570/cast-datasnapshot-from-firebase-to-integer-failed
                    //Det virker nu, men kommentarer viser tankeprocess.
                }

                totalReps += exerciseData.getSum(); //Her lægger vi de nye reps, til de gamle.
                myRefUser.child(user.getUser()).child("TotalReps").setValue(totalReps); //For at sætte totalreps.

                //Gør vi for at kunne bruge det i den næste, for at sætte værdien i det næste listenerting.
                //Så compID + ID for enkelt bruger. Så vi kan opdatere værdien i comp branchen, for deres branch.
                if(dataSnapshot.child(user.getUser()).child("CompetitionID").exists()) {
                    competitionID =  dataSnapshot.child(user.getUser()).child("CompetitionID").getValue(Integer.class);
                    user.setCompetitionID(competitionID);
                    userCompetitionID = Integer.parseInt(dataSnapshot.child(user.getUser()).child("CompetitionID" + competitionID).child(user.getUser() + "UserValue").getValue(String.class));
                    user.setUserCompetitionID(userCompetitionID);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Kunne gøre nedenstående hver gang reps registreres. Tror ikke man behøver lytte for at lægge ting op. Kun for at hente.
        myRefComp.addListenerForSingleValueEvent(new ValueEventListener() {
            int currentUserCompReps = 0;
            int otherUserCompReps = 0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(String.valueOf(user.getCompetitionID())).exists())
                    //I nedenstående skal jeg ikke uploade noget, bare tjekke hvad den nuværende bruger og anden brugers reps er.
                    //tre if sætninger, i tilfælde af at den nuværende eller tidligere bruger ikke har nogle reps.
                    //1. tjekker om begge har reps. 2. tjekker om egne har og anden ikke har.
                    if(dataSnapshot.child(String.valueOf(user.getCompetitionID())).child(String.valueOf(user.getUserCompetitionID())).child("CompReps").exists()
                       && dataSnapshot.child(String.valueOf(user.getCompetitionID())).child(String.valueOf(user.getOtherUserCompID())).child("CompReps").exists()
                    ) {
                        currentUserCompReps = Integer.parseInt(dataSnapshot.child(String.valueOf(user.getCompetitionID())).child(String.valueOf(user.getUserCompetitionID())).child("CompReps").getValue(String.class));
                        otherUserCompReps = Integer.parseInt(dataSnapshot.child(String.valueOf(user.getCompetitionID())).child(String.valueOf(user.getOtherUserCompID())).child("CompReps").getValue(String.class));
                    }else if(dataSnapshot.child(String.valueOf(user.getCompetitionID())).child(String.valueOf(user.getUserCompetitionID())).child("CompReps").exists()
                            && !dataSnapshot.child(String.valueOf(user.getCompetitionID())).child(String.valueOf(user.getOtherUserCompID())).child("CompReps").exists()
                    ){
                        currentUserCompReps = Integer.parseInt(dataSnapshot.child(String.valueOf(user.getCompetitionID())).child(String.valueOf(user.getUserCompetitionID())).child("CompReps").getValue(String.class));
                    }else if(!dataSnapshot.child(String.valueOf(user.getCompetitionID())).child(String.valueOf(user.getUserCompetitionID())).child("CompReps").exists()
                            && dataSnapshot.child(String.valueOf(user.getCompetitionID())).child(String.valueOf(user.getOtherUserCompID())).child("CompReps").exists()
                    ){
                        otherUserCompReps = Integer.parseInt(dataSnapshot.child(String.valueOf(user.getCompetitionID())).child(String.valueOf(user.getOtherUserCompID())).child("CompReps").getValue(String.class));
                    }

                //Så har jeg begge brugeres resultater. Kan så tjekke hvem der har flest og give resultat.
                compStatus.setText("Your reps " + currentUserCompReps + " other user's " + otherUserCompReps);
                //Af en eller anden grund gemmer de begge under samme sted, som er det forkerte. De gemmer under det som clearreps gør.
                //Her den sletter comp fra bruger. Skal så bare fjernes i WorkoutActivity linje 146 når jeg får lavet det her.
                myRefUser.child(user.getUser()).child("CompetitionID").removeValue();
                user.clearComp(); //
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Her skal vi bare vise den andens reps, gemme et point under brugeren, hvis de vandt og Fjerne competitionID værdien efter, hvilket vi har gjort.

        /*
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

         */



        //Her den sletter comp fra bruger. Skal så bare fjernes i WorkoutActivity linje 146 når jeg får lavet det her.
        myRefUser.child(user.getUser()).child("CompetitionID").removeValue();


        //Resten af træningstingene skal være nogenlunde det samme, skal bare tilføje billeder og den andens reps. Nok 1-2 dages godt arbejde. Færdig fredag agtig
        //Kunne have en workout activity med hver træningsting som et fragment.
        //Så de har samme layout, men indholdet kan være anderledes. Kan starte med bare at tilføje det der skal være her.
    }
}