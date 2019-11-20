package com.my.yotizan;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SubmitDocuments extends AppCompatActivity {
    //Define global variables for the buttons, text view, Progressbar and URI
    Button selectFile, upload;
    TextView notification;
    Uri pdfUri; //URI are actually URLs that are meant for local storage

    //Define references to Firebase Storage and Firebase Database
    FirebaseStorage storage; //Used for uploading files e.g pdf
    FirebaseDatabase database;//Used to store URLs of uploaded files. We will use these URLs to locate our files in future
    ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create Objects for Storage and Database
        storage = FirebaseStorage.getInstance();// Return an object of current firebase storage.
        database = FirebaseDatabase.getInstance();//Return object of Firebase database.

        //Create Objects for the Buttons
        selectFile = findViewById(R.id.selectFile);
        upload = findViewById(R.id.uploadDocuments);

        //Create object for the textView
        notification = findViewById(R.id.notification);

        //EventListener for Select File Button
        selectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What will happen when we click the select file button

                //We will be checking for permission to read our internal storage
                if(ContextCompat.checkSelfPermission(SubmitDocuments.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                {
                    selectPDF();
                }
                else{//The application doesnt have permission to read the internal storage
                    ActivityCompat.requestPermissions(SubmitDocuments.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
                }
            }
        });

        //Code for the Upload Button
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Code to upload our file into firebase
                if(pdfUri!= null){//the user has selected the file
                    uploadFile(pdfUri);
                }
                else
                {
                    Toast.makeText(SubmitDocuments.this,"Please select a file",Toast.LENGTH_SHORT).show();
                }

            }
        });



    }

    //this method is for uploading our file
    private void uploadFile(Uri pdfUri) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Uploading File...");
        progressDialog.setProgress(0);
        progressDialog.show();
        final String  fileName = System.currentTimeMillis() + "";
        StorageReference storageReference = storage.getReference();//Get the path in which our file will be stored. Returns path

        //This means our file is now uploaded to Firebase Storage
        storageReference.child("Uploads").child(fileName).putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //This method will only get invoked if our file is successfully uploaded into Firebase
                String url = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                DatabaseReference reference = database.getReference();//return the path to root

                //The URL for our file is successfully uploaded to our database
                reference.child(fileName).setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //check whether our url is successfully uploaded or not
                        if(task.isSuccessful())
                        {
                            Toast.makeText(SubmitDocuments.this,"File is successfully uploaded", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(SubmitDocuments.this,"File did not upload successfully", Toast.LENGTH_SHORT).show();

                        }                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //This denotes that our file is not succesfully uploaded
                Toast.makeText(SubmitDocuments.this,"File did not upload successfully", Toast.LENGTH_SHORT).show();



            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                //this method will help us track the status of our file being uploaded
                int currentProgress = (int)(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                progressDialog.setProgress(currentProgress);
            }
        });

    }

    //This method will run when the user gives permission to read from external storage


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        if(requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            selectPDF();
        }
        else{
            Toast.makeText(SubmitDocuments.this, "PLease provide permission",Toast.LENGTH_LONG).show();
        }
    }

    private void selectPDF() {

        //Our task will be to offer user to select a fie using file manager
        //We will be using intents.

        //Create an Intent Object
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);// To fetch files
        startActivityForResult(intent,86);//Launch intent


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        //this method will get invoked by Android to allow us to know if a user has selected a file or not
        if(requestCode == 86 && resultCode == RESULT_OK && data!= null)
        {
            pdfUri = data.getData(); //Return URI of Selected File..
            notification.setText("A file is selected: " + data.getData().getLastPathSegment());

        }
        else
        {
            Toast.makeText(SubmitDocuments.this,"Please select a file", Toast.LENGTH_SHORT).show();
        }
    }
}
