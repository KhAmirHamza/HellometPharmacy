package com.hellomet.pharmacy.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hellomet.pharmacy.Adapter.ChatAdapter;
import com.hellomet.pharmacy.Model.Chat;
import com.hellomet.pharmacy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";

    RecyclerView recy_chat;
    EditText edt_message;
    MaterialButton btn_send;
    String owner_id;
    String order_id;
    String chat_with;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        owner_id = getIntent().getStringExtra("owner_id");
        order_id = getIntent().getStringExtra("order_id");
        chat_with = getIntent().getStringExtra("chat_with");


        edt_message = findViewById(R.id.edt_message);
        btn_send = findViewById(R.id.btn_send);

        recy_chat = findViewById(R.id.recy_chat);
        recy_chat.setHasFixedSize(true);
        recy_chat.setLayoutManager(new LinearLayoutManager(this));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
/*
        db.collection("Chat_Order").document(order_id)
                .collection(chat_with).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<Chat> chats = new ArrayList<>();
                    QueryDocumentSnapshot document1 = null;
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        chats.add(document.toObject(Chat.class));
                       document1 = document;
                    }
                    Log.d(TAG, document1.getId() + " => " + document1.getData());
                    Log.d(TAG, "onSuccess: Chats lenght: "+chats.size());
                    ChatAdapter chatAdapter = new ChatAdapter(ChatActivity.this, chats, user_id);
                    recy_chat.setAdapter(chatAdapter);
                } else {
                    // Log.d(TAG, "Error getting documents: ", task.getException());
                    Toast.makeText(ChatActivity.this, "Nothing Found!", Toast.LENGTH_SHORT).show();
                }
            }
        });*/

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = edt_message.getText().toString().trim();
                if (!message.isEmpty()) {
                    Chat chat = new Chat(owner_id, message, new Date().getTime());
                    db.collection("Chat_Order").document(order_id)
                            .collection(chat_with).add(chat)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    edt_message.setText("");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ChatActivity.this, "Message not Send, Try Again.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Chat_Order").document(order_id)
                .collection(chat_with)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (!value.isEmpty()) {
                            List<Chat> chats = new ArrayList<>();
                            for (QueryDocumentSnapshot document : value) {
                                // Chat chat = new Chat(document.getString(""))
                                chats.add(document.toObject(Chat.class));
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }

                                ChatAdapter chatAdapter = new ChatAdapter(ChatActivity.this, chats, owner_id);
                                recy_chat.setAdapter(chatAdapter);
                                recy_chat.smoothScrollToPosition(recy_chat.getAdapter().getItemCount() - 1);


                        } else {
                            // Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}