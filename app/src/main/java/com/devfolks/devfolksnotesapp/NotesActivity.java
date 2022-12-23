package com.devfolks.devfolksnotesapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.devfolks.devfolksnotesapp.databinding.ActivityNotesBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

public class NotesActivity extends AppCompatActivity {
    private ActivityNotesBinding binding;
    private FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    FirestoreRecyclerAdapter<firebaseModel, NoteViewHolder> noteAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityNotesBinding.inflate(getLayoutInflater());
        View v=binding.getRoot();
        setContentView(v);
        getSupportActionBar().setTitle("All Notes");
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore=FirebaseFirestore.getInstance();
        binding.createNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(NotesActivity.this, CreateNoteActivity.class);
                startActivity(i);
            }
        });
        Query query=firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").orderBy("title",Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<firebaseModel> allUserNotes=new FirestoreRecyclerOptions.Builder<firebaseModel>().setQuery(query,firebaseModel.class).build();
        noteAdapter=new FirestoreRecyclerAdapter<firebaseModel, NoteViewHolder>(allUserNotes) {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i, @NonNull firebaseModel firebaseModel) {

                int colorCode=getRandomColor();
                ImageView popupButton=noteViewHolder.itemView.findViewById(R.id.menuPopBtn);

                noteViewHolder.mnote.setBackgroundColor(noteViewHolder.itemView.getResources().getColor(colorCode,null));
                noteViewHolder.noteTitle.setText(firebaseModel.getTitle());
                noteViewHolder.noteContent.setText(firebaseModel.getContent());
                String docId=noteAdapter.getSnapshots().getSnapshot(i).getId();

                noteViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i= new Intent(v.getContext(),NoteDetailsActivity.class);
                        i.putExtra("title",firebaseModel.getTitle());
                        i.putExtra("content",firebaseModel.getContent());
                        i.putExtra("noteId",docId);
                        v.getContext().startActivity(i);
                    }
                });
                popupButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu= new PopupMenu(v.getContext(),v);
                        popupMenu.setGravity(Gravity.START);
                        popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                Intent i= new Intent(v.getContext(),editNoteActivity.class);
                                i.putExtra("title",firebaseModel.getTitle());
                                i.putExtra("content",firebaseModel.getContent());
                                i.putExtra("noteId",docId);
                                v.getContext().startActivity(i);
                                return false;
                            }
                        });
                        popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {

                                DocumentReference documentReference=firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(docId);
                                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getApplicationContext(),"Note is deleted",Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(),"Failed to delete",Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return false;
                            }
                        });
                        popupMenu.show();
                    }
                });
            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_layout,parent,false);
                return new NoteViewHolder(v);
            }
        };
        recyclerView=findViewById(R.id.recView);
        recyclerView.setHasFixedSize(true);
        staggeredGridLayoutManager=new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(noteAdapter);
    }
    public class NoteViewHolder extends RecyclerView.ViewHolder{
        private TextView noteTitle, noteContent;
        LinearLayout mnote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle=itemView.findViewById(R.id.noteTitle);
            noteContent=itemView.findViewById(R.id.noteContent);
            mnote=itemView.findViewById(R.id.note);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(NotesActivity.this,LoginActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(noteAdapter!=null){
            noteAdapter.stopListening();
        }
    }
    private int getRandomColor(){
        List<Integer> colorCode=new ArrayList<>();
        colorCode.add(R.color.green);
        colorCode.add(R.color.blue);
        colorCode.add(R.color.pink);
        colorCode.add(R.color.red);
        colorCode.add(R.color.yellow);
        colorCode.add(R.color.purple);
        colorCode.add(R.color.dpurple);
        colorCode.add(R.color.spurple);
        colorCode.add(R.color.orange);
        colorCode.add(R.color.spink);
        colorCode.add(R.color.teal);
        colorCode.add(R.color.bgreen);
        colorCode.add(R.color.grey);
        Random random=new Random();
        int number=random.nextInt(colorCode.size());
        return colorCode.get(number);

    }

}