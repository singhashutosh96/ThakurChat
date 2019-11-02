package tech.savvysolutions.thakurchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import tech.savvysolutions.thakurchat.R;
import tech.savvysolutions.thakurchat.adapter.StatusAdapter;
import tech.savvysolutions.thakurchat.model.User;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rvStatus;
    private Button btnGroupChat;
    private Button btnLogout;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    private List<User> users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        users = new ArrayList<>();


        initializeView();
        setupListeners();
        retriveData();
    }

    private void retriveData() {
        ValueEventListener userValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshots) {
                users.clear();
                for(DataSnapshot singleDataSnapshot : dataSnapshots.getChildren()){
                    users.add(singleDataSnapshot.getValue(User.class));
                }

                addToRecyclerView(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        databaseReference.child("users").addValueEventListener(userValueEventListener);
    }

    private void addToRecyclerView(List<User> users) {
        StatusAdapter adapter = new StatusAdapter(HomeActivity.this,users);
        rvStatus.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
        rvStatus.setAdapter(adapter);
    }

    private void setupListeners() {

        btnGroupChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent groupChatIntent = new Intent(HomeActivity.this,GroupChatActivity.class);
                startActivity(groupChatIntent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeTheStatus();
                mAuth.signOut();
                Intent loginIntent = new Intent(HomeActivity.this,LoginActivity.class);
                startActivity(loginIntent);
                finish();

            }
        });
    }

    private void changeTheStatus() {

        final String email =mAuth.getCurrentUser().getEmail();

        ValueEventListener usersListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshots) {
                for ( DataSnapshot singleDataSnapshot : dataSnapshots.getChildren()){
                    User user = singleDataSnapshot.getValue(User.class);
                    if(user.getEmail().equals(email))
                    {
                        user.setStatus(false);
                        databaseReference.child("users").child(singleDataSnapshot.getKey()).setValue(user);

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        databaseReference.child("users").addListenerForSingleValueEvent(usersListener);



    }

    private void initializeView() {
        rvStatus = findViewById(R.id.rv_status);
        btnGroupChat = findViewById(R.id.btn_group_chat);
        btnLogout = findViewById(R.id.btn_logout);
    }
}
