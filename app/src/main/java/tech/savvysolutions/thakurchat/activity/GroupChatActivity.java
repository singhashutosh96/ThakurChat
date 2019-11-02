package tech.savvysolutions.thakurchat.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
import tech.savvysolutions.thakurchat.adapter.MessageAdapter;
import tech.savvysolutions.thakurchat.model.Message;
import tech.savvysolutions.thakurchat.model.User;

public class GroupChatActivity extends AppCompatActivity {

    private EditText etMsg;
    private Button btnSend;
    private RecyclerView rvMsg;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private List<Message> msgList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        msgList = new ArrayList<>();


        initializeView();
        setupListeners();
        retriveData();
    }

    private void retriveData() {

        ValueEventListener msgValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshots) {
                msgList.clear();
                for(DataSnapshot singleDataSnapshot : dataSnapshots.getChildren()){
                    msgList.add(singleDataSnapshot.getValue(Message.class));
                }

                MessageAdapter messageAdapter = new MessageAdapter(GroupChatActivity.this,msgList);
                rvMsg.setLayoutManager(new LinearLayoutManager(GroupChatActivity.this));
                rvMsg.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } ;

        databaseReference.child("messages").addValueEventListener(msgValueEventListener);

    }

    private void setupListeners() {

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });



    }

    private void sendMessage() {
        final String msg,email;
        msg = etMsg.getText().toString().trim();

        if(TextUtils.isEmpty(msg)){
            etMsg.setText("");
            return;
        }

        etMsg.setText("");

       email =mAuth.getCurrentUser().getEmail();

        ValueEventListener usersListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshots) {
                for ( DataSnapshot singleDataSnapshot : dataSnapshots.getChildren()){
                    User user = singleDataSnapshot.getValue(User.class);
                    if(user.getEmail().equals(email))
                    {
                        Message newMessage = new Message();
                        newMessage.setMsg(msg);
                        newMessage.setName(user.getName());
                       databaseReference.child("messages").push().setValue(newMessage);
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
        etMsg = findViewById(R.id.et_msg);
        btnSend = findViewById(R.id.btn_send);
        rvMsg = findViewById(R.id.rv_msg);
    }
}
