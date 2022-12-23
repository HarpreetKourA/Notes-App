package com.devfolks.devfolksnotesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.devfolks.devfolksnotesapp.databinding.ActivityEditNoteBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class editNoteActivity extends AppCompatActivity {
    private ActivityEditNoteBinding binding;
    private FirebaseAuth fauth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityEditNoteBinding.inflate(getLayoutInflater());
        View v=binding.getRoot();
        setContentView(v);
        setSupportActionBar(binding.toolbarOfEditNote);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Intent data= getIntent();
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        binding.editContentOfNote.setText(data.getStringExtra("content"));
        binding.editTitleOfNote.setText(data.getStringExtra("title"));
        binding.saveEditNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newTitle=binding.editTitleOfNote.getText().toString();
                String newContent=binding.editContentOfNote.getText().toString();
                if(newTitle.isEmpty()|| newContent.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Fields can't be empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    DocumentReference documentReference=firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(data.getStringExtra("noteId"));
                    Map<String, Object> note=new HashMap<>();
                    note.put("title",newTitle);
                    note.put("content",newContent);
                    documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(),"Note is updated",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(editNoteActivity.this,NotesActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"Failed to update",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                //Toast.makeText(getApplicationContext(),"Save btn click",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}