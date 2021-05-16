package com.example.gass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private Button loginButton;
    private EditText emailText;
    private EditText passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //kan bare rykkes ind i anden aktivitet + copy+paste xml fil. Så lave denne til login side, ligesom i den anden app.
        //Ovenstående kommentar er done

        loginButton = findViewById(R.id.loginButton);
        emailText = findViewById(R.id.email);
        passwordText = findViewById(R.id.password);
        Button createUserButton = findViewById(R.id.createUser);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        if(user != null){ //Hvis brugeren er logget ind, sender vi dem til hjemmeskærmen/navigationsmenu.
            Intent createUserIntent = new Intent(MainActivity.this, NavigationActivity.class);
            startActivity(createUserIntent);
            myRef.setValue("Hello, World!");

        }


        //Lav login feature, så er brugersystemet oppe at køre. Mangler selvfølgelig det med realtime databasen, men nærmest done.
        //Ovenstående er done


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=emailText.getText().toString();
                String password=passwordText.getText().toString();
                //Tjekker om parameter er null, hvis true, dukker en besked op med at der skal indtastes en email brr brr

                if(TextUtils.isEmpty(email)){
                    emailText.setError("Enter your email");
                    return;
                }
                //Tjekker om parameter er null, hvis true, dukker en besked op med at der skal indtastes en kode brr brr

                else if(TextUtils.isEmpty(password)){
                    passwordText.setError("Enter your password");
                    return;
                }
                login(email, password);
            }
        });


        createUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createUserIntent = new Intent(MainActivity.this, CreateUserActivity.class);
                startActivity(createUserIntent);
            }
        });

    }

    private void login(String email, String password){

        // Når brugerens forsøg på login er succesfuld, vil det føre dem til en tom side som indeholder en knap som returner dem login siden.
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) { //https://www.youtube.com/watch?v=Z-RE1QuUWPg
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this,"Login Successfully",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, NavigationActivity.class);
                    startActivity(intent);
                    //Kunne returne værdi i stedet, for at man så kan bruge det til at beslutte om man kan videre, for mvvm struktur.
                    finish();
                }
                else{
                    Toast.makeText(MainActivity.this,"Sign In fail!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}