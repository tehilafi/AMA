package com.tehilafi.ama.media;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class UploadPhotoAndVideos {

    public static StorageReference storageReff;
    public static FirebaseStorage storage;


    public static void uploadImageToFirebase(final String name , final Uri contentUri, final String iduser) {

        storage = FirebaseStorage.getInstance();
        storageReff = storage.getReference();

        StorageReference reffS = storageReff.child( "profile picture/" + iduser);
        final String timestamp = "" + System.currentTimeMillis();
        reffS.putFile(contentUri).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                Uri downloadUri = uriTask.getResult();
                if(uriTask.isSuccessful()){
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("title", "" + name);
                    hashMap.put("timestamp", "" + timestamp);
                    hashMap.put("videoUri", "" + downloadUri);
                }
            }
        }).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        } );
    }








}