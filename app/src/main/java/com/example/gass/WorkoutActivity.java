package com.example.gass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class WorkoutActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference myRefUser;
    private DatabaseReference myRefComp;
    private FirebaseAuth firebaseAuth;

    String userID;

    User user;


    //Definer visuelle elementer
    TextView joinCompTxt;
    EditText joinCompInput;
    Button joinCompBtn;

    TextView createCompTxt;
    Button createCompBtn;
    TextView createCompNewInfo;
    //User user; //Venter lige med at tilføje user klassen. Eller det kan jeg måske faktisk ikke.



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        //Kunne have competition/difficulty som startskærm. Modstander skal joine før timer begynder.
        //- Så kan man høre efter ny data, for at se om den anden bruger er joinet. Hvis de er, begynder countdown.
        //Så kan man konkurrere om at få flest reps i de forskellige øvelser og til sidst. Kunne være musik under øvelserne. Eller bare før og efter.
        //- Under øvelserne lytte efter ny data for at få modstanderens reps på skærmen.
        //Så bliver vinderen kåret, baseret på hvem der har flest reps. Jubel lyd hvis man vinder og citat (rocky, get back up) med inspirerende musik hvis man taber.


        //Assign visuelle elementer til deres pladser i layout fil (activity_workout.xml).
        joinCompTxt = findViewById(R.id.joinCompTxt);
        joinCompInput = findViewById(R.id.joinCompInput);
        joinCompBtn = findViewById(R.id.joinCompBtn);

        createCompTxt = findViewById(R.id.createCompTxt);
        createCompBtn = findViewById(R.id.createCompBtn);
        createCompNewInfo = findViewById(R.id.newCompInfo);

        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getUid();
        user = User.getInstance();
        user.setUser(userID);

        database = FirebaseDatabase.getInstance();
        myRefUser = database.getReference("user");
        myRefComp = database.getReference("competition");







        //Enten tilføje user klasse, eller. Tror faktisk jeg skal bruge user klassen, for at have compID i de forskellige kald og sådan, hvis jeg husker rigtig.
        //user = User.getInstance(this); //Skal så bare have denne user klasse tilbage i login.
        // Eller faktisk ikke, da login virker nu. Skal bare lige tjekke om shared preferences bruges i nedenstående.
        //Men kan bare erstatte med user.getUID i stedet, da jeg er ret sikker på vi ikke behøver det andet.
        //Vi bruger bare user til at gemme ID og sådan i rammen, hvilket er fornuftigt.

        //Skal have skrevet i tekstfelt. Hvis child med det id eksisterer, så skriv deres navn ind og sig "you have now joined". Ellers sig wrong number.
        //Tror create og join virker sådan her. Skal bare lave at den lytter på om den anden bruger er joinet i createComp, for så at begynde konkurrencen/træningen.
        //Ved joincomp skal den bare gå videre, hvis de succesfully joiner, så er den videre for begge og de konkurrerer.

        //kan sige get UID i stedet for getuser. Eventuelt gøre det i starten af appen

        joinCompBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty((joinCompInput.getText()))){ //Hvis den ikke er tom.
                    myRefComp.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) { //Burde måske tjekke om den nuværende bruger allerede er på databasen under denne comp, så man ikke kan konkurrerere med sig selv.
                            if(!dataSnapshot.child(joinCompInput.getText().toString()).child("2").exists()) {
                                if(dataSnapshot.child(joinCompInput.getText().toString()).exists()) { //if competition with inputtet ID exists.
                                    //add user to competition
                                    //databaseSingleton.joinComp(user.getUser(), joinCompInput.getText().toString()); //Kan erstatte nedestående.
                                    myRefComp.child(joinCompInput.getText().toString()).child("2").setValue(user.getUser());
                                    myRefUser.child(user.getUser()).child("CompetitionID" + joinCompInput.getText().toString()).setValue(joinCompInput.getText().toString());
                                    myRefUser.child(user.getUser()).child("CompetitionID" + joinCompInput.getText().toString()).child(user.getUser() + "UserValue").setValue("2");
                                    myRefUser.child(user.getUser()).child("CompetitionID").setValue(Integer.parseInt(joinCompInput.getText().toString()));
                                    joinCompTxt.setText("You have now joined the competition");

                                    //Her sætte timer igang, da begge er joinet og konkurrencen skal i gang.

                                } else{
                                    joinCompTxt.setText("Competition does not exist, try inputting a different CompID");
                                }
                            }else {
                                joinCompTxt.setText("There are already 2 users in this Competition");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });


        createCompBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random random = new Random();
                final int randomCompNumber = random.nextInt(1000) + 1;
                //createCompNewInfo.setText("Competition Number: " + randomCompNumber + user.getUser());
                //Skal muligvis sætte brugernavnet udenfor aktiviteten eller før oncreate.
                //Det virker nu though, men skal bare kunne hente og gemme.



                myRefComp.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {//Kune tjekke om man allerede er gang med en comp, for at man ikke kan være med i flere på en gang. Så kunne man også stoppe compen ved at fjerne det ID der er.
                        //Se om comp eksisterer, ellers lave den.
                        if(!dataSnapshot.child(String.valueOf(randomCompNumber)).exists()){ //Man skal ikke skrive child("Competition"), da det er fra myRefComp allerede
                            //databaseSingleton.createComp(user.getUser(), randomCompNumber); kan erstate nedenstående
                            myRefComp.child(String.valueOf(randomCompNumber)).setValue(null); //Læg den nye comp op på database. Kan bare ikke finde ud af at gøre uden value. Men den må vel godt have value. Men kunne være smart bare at tilføje child. Tror det er uden value nu, da string er tom.
                            //Så inde i workoutDone sige hvis bruger har comp, sæt værdi derind og tilføj nuværende oveni hvis den findes.
                            myRefUser.child(user.getUser()).child("CompetitionID" + randomCompNumber).setValue(String.valueOf(randomCompNumber));
                            myRefUser.child(user.getUser()).child("CompetitionID" + randomCompNumber).child(user.getUser() + "UserValue").setValue("1");
                            myRefUser.child(user.getUser()).child("CompetitionID").setValue(randomCompNumber); //man competer så altid i den seneste comp man er joinet, men de andre er der bare stadig på databasen, bruges ikke.
                            myRefComp.child(String.valueOf(randomCompNumber)).child("1").setValue(user.getUser());
                            //Alt dette er også inde i databaseSingleton, så kan gøres gennem der.
                            createCompNewInfo.setText("You have now created a new competiton. Send this CompID: " + randomCompNumber + " to compete with them");

                            //her lave ny listener, der venter på at den anden joiner og sætter countdown igang. Kan hide og show timer i guess.
                            /*
                            myRefComp.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    //If user2 has joined, start countdown timer der starter træningen når færdig.
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            })
*/


                        } else { //Else lav nyt nummer. fordi det allerede findes.
                            createCompNewInfo.setText("this CompID already exists, press the button again");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }; //Så sådan ud før });
            //onButtonClick: //Ved tryk på start Competition knap
        /*
        Random random = new Random();
        int randomCompNumber = random.nextInt(1000);
        setText("Competition Number: " + randomCompNumber)
        if(Totalreps(på database).exists) {
            myRef.child(String.valueOf(randomCompNumber)).child(user.getUser).setValue(myRefUser.child(user.getUser).child(TotalReps));
        }
        setText("Exercise to win the Competition" +
                "\n Send the code to a friend, for them to join the Competition");
*/
            //Skal så bare gemme den nye totalReps under Comp. Så gemme datoen for upload der også. Se om det er mere end en uge siden, for så at slette hvis sandt.
            //For hver træning lægge en ny totalReps op på både egen bruger (all time), og comp, som slettes hvis der går over en uge. Kan måske også bare være fra hvornår man startede.
            //Hvis ens værdi så er lavere end modstanderens kan der stå i profil at man taber.
            //Man kan joine flere comps på samme tid.
            //Hvis vi har gemt det under brugeren kan det også vises på Profil med all time total Reps.
            //Tror bare ovenstående udkommenterede kan slettes.

        });

    }
}