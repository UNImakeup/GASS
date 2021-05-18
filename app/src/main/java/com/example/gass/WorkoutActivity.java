package com.example.gass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
    private ValueEventListener listener;

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
        userID = firebaseAuth.getUid(); //Henter ID fra firebase. Tror der er noget der går galt her. Som om den ikke logger ud.
        user = User.getInstance();
        user.setUser(userID);
        //Her sætter vi den nuværende bruger til at være det ID. Det gemmes dog kun i rammen.

        database = FirebaseDatabase.getInstance();
        myRefUser = database.getReference("user");
        myRefComp = database.getReference("competition");


        //Her henter vi brugerens compID, for at kunne bruge den i næste lyt til databasen (nedenunder). Så det eneste nedenstående blok gør, er at hente brugerens compID, så vi kan bruge det når vi skal se om konkurrenten er der.
        // Vi gør dette, da vi kun behøver at lytte på  det en gang (addlistenerforsinglevalueevent), samt fordi det er besværligt/uoverskueligt at skulle lytte efter meget forskellig data i samme blok.
        myRefUser.addListenerForSingleValueEvent(new ValueEventListener() {
            int competitionID;
            int userCompetitionID;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int otherUserCompID;
                if(dataSnapshot.child(user.getUser()).child("CompetitionID").exists()) { //Hvis den nuværende bruger er en del af en competition. Her trækker den på den comp brugeren sidst har joinet.
                    competitionID =  dataSnapshot.child(user.getUser()).child("CompetitionID").getValue(Integer.class);
                    user.setCompetitionID(competitionID); //Selve ID'et på konkurrencen gemmes
                    userCompetitionID = Integer.parseInt(dataSnapshot.child(user.getUser()).child("CompetitionID" + competitionID).
                            child(user.getUser() + "UserValue").getValue(String.class));
                    user.setUserCompetitionID(userCompetitionID); //Selve ID'et på brugeren i konkurrencen (1 eller 2) gemmes.

                        if (userCompetitionID == 1) { //Finder den anden brugers id baseret på ens egen, da der kun burde være 2 i konkurrencen.
                            otherUserCompID = 2;
                        } else {
                            otherUserCompID = 1;
                        }
                        user.setOtherUserCompID(otherUserCompID);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //her lave ny listener, der venter på at den anden joiner og sætter countdown igang. Kan hide og show timer i guess.
        listener = myRefComp.addValueEventListener(new ValueEventListener() { //Tror jeg skal have selve værdien der opdateres her. Det kræver at jeg gemmer otherusercompID i brugeren
            //Boolean ass = false;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // If currentuser has joined, hvilket de har ifølge ovenstående, så if user2 has joined, start countdown timer der starter træningen når færdig.
                //Ved ikke om dette kode står det forkerte sted though. Om det skal udenfor denne search og bare skal tjekke om der er 2 brugere på den comp som den nuværende bruger er på.
                //Hvis man dog har en gammel comp vil den dog igangsætte, med mindre vi sletter den efter, hvilket kan gøres ved at skrive "" ved tallet i guess. Så bare give en 1 point i highscore/level hvis man vinder. Så ville det være rigtigt, hvis onCreate kører som loop agtig. Det ved jeg faktisk ikke, men hvis den er i toppen og hele tiden tjekker burde det jo fungere.
                //Hvordan tjekker jeg om anden bruger er joinet?
                /*
                if(ass){
                    joinCompTxt.setText("fack");
                } else {
                    joinCompTxt.setText("pls");
                }
                ass = true;
                 */

                //Hvordan henter vi den anden brugers compID uden at bruge user.getotherusercompid, eller bare at gemme den der, da den ikke opdateres lige nu.
                if(user.getCompetitionID() == 1){ //Dette er tilføjet, hvis man trykker create comp.
                    user.setOtherUserCompID(2);
                }

                if (snapshot.child(String.valueOf(user.getCompetitionID())).exists()) { //hvis den nuværende bruger er en del af en competition. Et eller andet her fungerer ikke.
                    if (snapshot.child(String.valueOf(user.getCompetitionID())).child(String.valueOf(user.getOtherUserCompID())).exists()) { //Hvis den anden bruger eksisterer. Skal lige tilføje otherusercomp fra user
                        //countdowntimer og næste aktivitet. Starter bare med næste aktivitet.
                        //onStop() køres automatisk, hvor lytteren stopper.
                        /*
                        String ass = snapshot.child(String.valueOf(user.getCompetitionID())).child(String.valueOf(user.getOtherUserCompID())).getValue().toString();
                        String assntitty = snapshot.child(String.valueOf(user.getCompetitionID())).child(String.valueOf(user.getUserCompetitionID())).getValue().toString();
                        joinCompTxt.setText(ass + "    " + assntitty + "     " + user.getCompetitionID());
                         */

                        //Inde i WorkoutFinished kan man så slette konkurrencen, både fra brugeren og comp. Behøver dog kun at gøre det fra brugeren, tbh. Så bare
                        //myRefComp.child(String.valueOf(user.getCompetitionID())).removeValue();
                        myRefUser.child(user.getUser()).child("CompetitionID").removeValue();


                        //Virker nu, her skal der bare være timer. Tror det burde virke for begge ender. For at den ikke går direkte videre, kan man slette konkurrencen efter, det burde løse det.
                        //Virker ikke for brugeren der joiner.
                        Intent intent = new Intent(WorkoutActivity.this, WorkoutFinished.class);
                        startActivity(intent);
                    }
                }
                //Det her virker nu. Hvis den anden er joinet. Ikke helt sikker på hvad problemet var. Er træt og ændrede en masse ting, men det virker nu.

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        /*
        //her lave ny listener, der venter på at den anden joiner og sætter countdown igang. Kan hide og show timer i guess.
        listener = myRefComp.addValueEventListener(new ValueEventListener() { //Tror jeg skal have selve værdien der opdateres her. Det kræver at jeg gemmer otherusercompID i brugeren
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // If currentuser has joined, hvilket de har ifølge ovenstående, så if user2 has joined, start countdown timer der starter træningen når færdig.
                //Ved ikke om dette kode står det forkerte sted though. Om det skal udenfor denne search og bare skal tjekke om der er 2 brugere på den comp som den nuværende bruger er på.
                //Hvis man dog har en gammel comp vil den dog igangsætte, med mindre vi sletter den efter, hvilket kan gøres ved at skrive "" ved tallet i guess. Så bare give en 1 point i highscore/level hvis man vinder. Så ville det være rigtigt, hvis onCreate kører som loop agtig. Det ved jeg faktisk ikke, men hvis den er i toppen og hele tiden tjekker burde det jo fungere.
                //Hvordan tjekker jeg om anden bruger er joinet?
                if (user.getUserCompetitionID() == 1) { //Finder den anden brugers id baseret på ens egen, da der kun burde være 2 i konkurrencen.
                    user.setOtherUserCompID(2);
                }

                if (snapshot.child(String.valueOf(user.getCompetitionID())).exists()) { //hvis den nuværende bruger er en del af en competition. Et eller andet her fungerer ikke.
                    if (snapshot.child(String.valueOf(user.getCompetitionID())).child(String.valueOf(user.getOtherUserCompID())).exists()) { //Hvis den anden bruger eksisterer. Skal lige tilføje otherusercomp fra user
                        //countdowntimer og næste aktivitet. Starter bare med næste aktivitet.
                        //onStop() køres automatisk, hvor lytteren stopper.
                        String ass = snapshot.child(String.valueOf(user.getCompetitionID())).child(String.valueOf(user.getOtherUserCompID())).getValue().toString();
                        String assntitty = snapshot.child(String.valueOf(user.getCompetitionID())).child(String.valueOf(user.getUserCompetitionID())).getValue().toString();
                        joinCompTxt.setText(ass + "    " + assntitty + "     " + user.getCompetitionID());
                        //Intent intent = new Intent(WorkoutActivity.this, NavigationActivity.class);
                        //startActivity(intent);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        */

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

                                    //Fordi den anden er joinet først, da de lavede den.
                                    user.setUserCompetitionID(2);
                                    user.setOtherUserCompID(1);
                                    user.setCompetitionID(Integer.parseInt(joinCompInput.getText().toString()));

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
                            //Fordi man er den første der joiner.
                            user.setUserCompetitionID(1); //har gjort dette, for at valueeventlistener kan bruge den.
                            user.setOtherUserCompID(2);
                            user.setCompetitionID(randomCompNumber);

                        } else { //Else lav nyt nummer. fordi det allerede findes.
                            createCompNewInfo.setText("this CompID already exists, press the button again");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            };
        });

    }

    @Override
    protected void onDestroy() {
        myRefComp.removeEventListener(listener);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        myRefComp.removeEventListener(listener);
        super.onStop();
    }
}