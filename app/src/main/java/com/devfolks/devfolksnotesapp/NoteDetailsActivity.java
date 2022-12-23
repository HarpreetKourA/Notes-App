package com.devfolks.devfolksnotesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.devfolks.devfolksnotesapp.databinding.ActivityNoteDetailsBinding;

public class NoteDetailsActivity extends AppCompatActivity {
    private ActivityNoteDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityNoteDetailsBinding.inflate(getLayoutInflater());
        View v=binding.getRoot();
        setContentView(v);
        Intent data= getIntent();
        setSupportActionBar(binding.toolbarOfNoteDetails);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.gotoEditNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(NoteDetailsActivity.this, editNoteActivity.class);
                i.putExtra("title",data.getStringExtra("title"));
                i.putExtra("content",data.getStringExtra("content"));
                i.putExtra("noteId",data.getStringExtra("noteId"));
                v.getContext().startActivity(i);
            }
        });
        binding.contentOfNoteDetails.setText(data.getStringExtra("content"));
        binding.titleOfNoteDetails.setText(data.getStringExtra("title"));

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}