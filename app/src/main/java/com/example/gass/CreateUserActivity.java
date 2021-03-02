package com.example.gass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

public class CreateUserActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private Button createUserButton;
    private EditText usernameText;
    private EditText emailText;
    private EditText password1;
    private EditText password2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        firebaseAuth = FirebaseAuth.getInstance();

        createUserButton = findViewById(R.id.createUserButton);
        usernameText = findViewById(R.id.userName);
        emailText = findViewById(R.id.email);
        password1 = findViewById(R.id.password1);
        password2 = findViewById(R.id.password2);

        createUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailText.getText().toString();
                String password1St = password1.getText().toString();
                String password2Nd = password2.getText().toString();
                String username = usernameText.getText().toString();


                if(TextUtils.isEmpty(email)){
                    emailText.setError("Enter your email");
                    return;
                }
                //Tjekker om der er indtastet/ om parameter er null, hvis true bliver der returned en besked op med at der skal skrives kode brr brr

                else if(TextUtils.isEmpty(password1St)){
                    password1.setError("Enter your password");
                    return;
                }
                //Tjekker om parameter er null, hvis true, dukker en besked op med at der skal skrives kode ro ro ro din

                else if(TextUtils.isEmpty(password2Nd)){
                    password2.setError("Confirm your password");
                    return;
                }
                //Tjekker om parameter er null, hvis true, dukker en besked op med at der indtastet to forskellige koder brr brr

                else if(!password1St.equals(password2Nd)){
                    password2.setError("Different password");
                    return;
                }

                //Længden af kodeord skal være mindst 6 karakter, pga firebase - the more you know
                else if(password1St.length()<6){
                    password1.setError("Length should be at least 6 characters");
                    return;
                }
                //besked dukker op hvis der ikke bliver indtastet en gyldig mail som indeholder gmail@.com etc. wallah
                else if(!isVallidEmail(email)){
                    emailText.setError("invalid email");
                    return;
                }

                else if(TextUtils.isEmpty(username)){ //Hvis man ikke har brugernavn
                    usernameText.setError("input a username, my brother");
                    return;
                }

                Register(email, password1St, username);
            }
        });

    }

    private void Register(String email, String password1, String username){
        //Når brugerens forsøg på login er succesfuld, vil det føre dem til hjemme siden.
        firebaseAuth.createUserWithEmailAndPassword(email,password1).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    /*
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
                            .build();
                    firebaseAuth.getCurrentUser().updateProfile(profileUpdates);
                     */
                    Toast.makeText(CreateUserActivity.this,"Successfully registered",Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(CreateUserActivity.this, NavigationActivity.class);
                    //Kunne også her returne,  så vi kan bruge den værdi til at ændre activity
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(CreateUserActivity.this,"Sign up fail!",Toast.LENGTH_LONG).show();
                }
            }
        });


    }
    // Tjekker om det en gyldig mail som indeholder gmail@.com etc. skrr
    private Boolean isVallidEmail(CharSequence target){
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

}