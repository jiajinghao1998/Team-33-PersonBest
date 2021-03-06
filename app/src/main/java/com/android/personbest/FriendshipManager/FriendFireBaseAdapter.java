package com.android.personbest.FriendshipManager;

import android.provider.DocumentsContract;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.android.personbest.ChatBoxActivity;
import com.android.personbest.MainActivity;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.Serializable;
import java.util.*;


public class FriendFireBaseAdapter extends Observable implements FFireBaseAdapter {
    String COLLECTION_KEY = "FriendList";
    String user;

    String TAG = "FriendFireBaseAdapter";

    public FriendFireBaseAdapter(String user){
        this.user = user;
    }

    public void addFriendById(String name, String id){

        Log.e(TAG,name+id);
        CollectionReference ref = FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(user)
                .collection("friends");

        CollectionReference pending = FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(user)
                .collection("pending");

        CollectionReference friendPending = FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(id)
                .collection("pending");

        CollectionReference friendRef = FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(id)
                .collection("friends");

        Map<String,Object> map = new HashMap<>();
        map.put("name", name);
        /*Log.e(TAG, "Here Added");
        ref.add(map).addOnSuccessListener(result -> {
            Log.e(TAG, "Success added");
        }).addOnFailureListener(error -> {
            Log.e(TAG, error.getLocalizedMessage());
        });
        Log.e(TAG,"ADD FINISH");*/

        DocumentReference docRef = friendPending.document(user);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    // User already in pending list
                    if (document.exists()) {

                        Map<String,Object> friendMap = document.getData();

                        ref.document(id).set(map)
                                .addOnSuccessListener(result->{
                                    Log.d(TAG, "Update User Friend List");
                                })
                                .addOnFailureListener(error->{
                                    Log.e(TAG, "Failed to Update user friendlist");
                                });
                        friendRef.document(user).set(friendMap)
                                .addOnSuccessListener(result->{
                                    Log.d(TAG,"Add user to friend's friendlist");
                                })
                                .addOnFailureListener(error ->{
                                    Log.e(TAG, error.getLocalizedMessage());
                                });

                        // Subscribe to user
                        String topic = generateIDChat(id);
                        subscribeToTopic(topic);

                        friendPending.document(user)
                                .delete()
                                .addOnSuccessListener(result->{
                                    Log.d(TAG,"Modify the friend's pending list");
                                })
                                .addOnFailureListener(error ->{
                                    Log.e(TAG, error.getLocalizedMessage());
                                });
                    } else {
                        pending.document(id).set(map)
                                .addOnSuccessListener(result ->{
                                    Log.d(TAG, "Add to Pending");
                                })
                                .addOnFailureListener(error->{
                                    Log.e(TAG, error.getLocalizedMessage());
                                });

                        // Subscribe to user
                        String topic = generateIDChat(id);
                        subscribeToTopic(topic);
                    }
                } else {
                    Log.d(TAG, "Failed Connection ", task.getException());
                }
            }
        });
    }

    public void getFriendlist(){
        CollectionReference ref = FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(user)
                .collection("friends");

        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document: task.getResult()){
                        setChanged();
                        notifyObservers(document.getId() + "_" +document.getData().get("name"));
                        Log.i(TAG, document.getId() + "=>" + document.getData());
                    }
                }
                else{
                    Log.e(TAG,"Error getting documents: " , task.getException());
                }
            }
        });
    }

    public void hasFriend(OperatorBoolean b){
        CollectionReference ref = FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(user)
                .collection("friends");

        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    b.op(!task.getResult().isEmpty()); // null ptr?
                }
                else{
                    Log.e(TAG,"Error getting documents: " , task.getException());
                }
            }
        });
    }

    public String generateIDChat(String idFriend) {
        String chatId = "";
        if (user.compareTo(idFriend)<0){
            chatId = user+idFriend;
        }
        else {
            chatId = idFriend+user;
        }

        return chatId;
    }

    public void subscribeToTopic(String topic){
        /*FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener(task -> {
                    Log.d(TAG, "Subscribe to " + topic); }
                );*/

        FirebaseMessaging.getInstance ().subscribeToTopic( topic ) .addOnCompleteListener(task -> {
            String msg =  "Subscribed to notifications" ;  if  (!task.isSuccessful()) {
                msg =  "Subscribe to notifications failed" ; }
            Log. d ( TAG , msg); }
        );
    }
}
