package com.devfolks.devfolksnotesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.devfolks.devfolksnotesapp.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {
    private ActivitySignupBinding binding;
    private FirebaseAuth fauth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySignupBinding.inflate(getLayoutInflater());
        View v=binding.getRoot();
        setContentView(v);
        fauth=FirebaseAuth.getInstance();

        binding.signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail= binding.signupEmail.getText().toString().trim();
                String password= binding.signupPassword.getText().toString().trim();
                if(mail.isEmpty()|| password.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Enter all the details first",Toast.LENGTH_SHORT).show();
                }
                else if(password.length()<7){
                    Toast.makeText(getApplicationContext(),"Password length should be greater than 7",Toast.LENGTH_SHORT).show();
                }
                else{   Toast.makeText(getApplicationContext(),mail + password,Toast.LENGTH_SHORT).show();

                        fauth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(),"Registration Successful!",Toast.LENGTH_SHORT).show();
                                    sendEmailVerification();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"Failed to register.",Toast.LENGTH_SHORT).show();
                                }
                            }

                        });
                }
            }
        });
        binding.loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }

    private void sendEmailVerification() {
        FirebaseUser firebaseUser=fauth.getCurrentUser();
        if(firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(),"Verification mail has been sent! Verify and login again.",Toast.LENGTH_SHORT).show();
                    fauth.signOut();
                    finish();
                    startActivity(new Intent(SignupActivity.this, LoginActivity.class));

                }

            });
        }
        else{
            Toast.makeText(getApplicationContext(),"Failed to send verification mail.",Toast.LENGTH_SHORT).show();
        }
    }

}