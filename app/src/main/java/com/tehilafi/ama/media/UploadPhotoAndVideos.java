package com.tehilafi.ama.media;

import android.content.Context;
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

    public static boolean uploadImageToFirebase(Context mContext, final String name , final Uri contentUri, final String iduser, final String from, final String numAns) {

        storage = FirebaseStorage.getInstance();
        storageReff = storage.getReference();
        StorageReference reffS = null;

        if (from == "pic")
            reffS = storageReff.child( "picture answer/" + numAns );
        else if (from == "video")
            reffS = storageReff.child( "video answer/" + numAns );
        else if (from == "profil")
            reffS = storageReff.child( "profile picture/" + iduser);

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
                    hashMap.put("contentUri", "" + downloadUri);


                }
            }
        }).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        } );
        return true;
    }

    public static boolean uploadImageFromCamera(Context mContext, final byte[] bb, final String numAns) {

        storage = FirebaseStorage.getInstance();
        storageReff = storage.getReference();

        StorageReference reffC = storageReff.child( "picture answer/" + numAns);
        reffC.putBytes(bb).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            }
        } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        } );
        return true;
    }








}