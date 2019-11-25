package com.optics.artizan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import dmax.dialog.SpotsDialog;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.optics.artizan.Model.User;
import com.rengwuxian.materialedittext.MaterialEditText;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    Button btnSignIn, btnRegister, btnSignUp;
    RelativeLayout rootLayout;

    //Declare Firebase
    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Init Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users");


        //Initialize View
        btnRegister = (Button)findViewById(R.id.btnRegister);
        btnSignIn = (Button)findViewById(R.id.btnSignIn);
        rootLayout = (RelativeLayout)findViewById(R.id.rootLayout);


        //Event Listeners
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    showRegisterDialog();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginDialog();
            }
        });

    }

    private void showLoginDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("LOGIN");
        dialog.setMessage("Please use email to sign in");

        LayoutInflater inflater = LayoutInflater.from(this);
        View login_layout = inflater.inflate(R.layout.layout_login, null);

        final EditText edtArtisanEmail = login_layout.findViewById(R.id.edtEmail);
        final EditText edtPassword = login_layout.findViewById(R.id.edtPassword);


        dialog.setView(login_layout);

        //Set Button
        dialog.setPositiveButton("SIGN IN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                dialog.dismiss();


                //Check Validation
                if(TextUtils.isEmpty(edtArtisanEmail.getText().toString())){
                    Snackbar.make(rootLayout, "Please enter email address", Snackbar.LENGTH_SHORT).show();
                    return;
                }


                if(TextUtils.isEmpty(edtPassword.getText().toString())){
                    Snackbar.make(rootLayout, "Password too short. Please enter a password longer than 5 characters", Snackbar.LENGTH_SHORT).show();
                    return;
                }


                final android.app.AlertDialog waitingDialog = new SpotsDialog.Builder().setContext(MainActivity.this).build();
                waitingDialog.show();

                //Login
                auth.signInWithEmailAndPassword(edtArtisanEmail.getText().toString(), edtPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                waitingDialog.dismiss();

                                    startActivity(new Intent(MainActivity.this, Welcome.class));
                                    finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(rootLayout, "Failed" + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                });



            }
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
            }
        });




        dialog.show();
    }








    private void showRegisterDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("REGISTER");
        dialog.setMessage("Please use email to register");



        LayoutInflater inflater = LayoutInflater.from(this);
        View register_layout = inflater.inflate(R.layout.layout_register, null);

        final MaterialEditText edtArtisanEmail = register_layout.findViewById(R.id.edtEmail);
        final MaterialEditText edtName = register_layout.findViewById(R.id.edtName);
        final MaterialEditText edtCellNo = register_layout.findViewById(R.id.edtCellNo);
        final MaterialEditText edtIdNumber = register_layout.findViewById(R.id.edtIdNumber);
        final MaterialEditText edtPassword = register_layout.findViewById(R.id.edtPassword);


        dialog.setView(register_layout);




        //Set Button
        dialog.setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();


                //Check Validation
                if(TextUtils.isEmpty(edtArtisanEmail.getText().toString())){
                    Snackbar.make(rootLayout, "Please enter email address", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(edtName.getText().toString())){
                    Snackbar.make(rootLayout, "Please enter your name", Snackbar.LENGTH_SHORT).show();
                    return;
                }


                if(TextUtils.isEmpty(edtCellNo.getText().toString())){
                    Snackbar.make(rootLayout, "Please enter your cellphone number", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(edtIdNumber.getText().toString())){
                    Snackbar.make(rootLayout, "Please enter your 13 digit long ID Number", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(edtPassword.getText().toString())){
                    Snackbar.make(rootLayout, "Password too short. Please enter a password longer than 5 characters", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(edtPassword.getText().toString().length() < 3){
                    Snackbar.make(rootLayout, "Password is too short. Please enter a longer password",Snackbar.LENGTH_SHORT).show();
                    return;
                }




                //Register new user
                auth.createUserWithEmailAndPassword(edtArtisanEmail.getText().toString(), edtPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                    //Save user to database
                                User user = new User();
                                user.setEmail(edtArtisanEmail.getText().toString());
                                user.setName(edtName.getText().toString());
                                user.setPhone(edtCellNo.getText().toString());
                                user.setIdNumber(edtIdNumber.getText().toString());
                                user.setPassword(edtPassword.getText().toString());

                                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Snackbar.make(rootLayout, "Sign Up successful", Snackbar.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Snackbar.make(rootLayout, "Failed" + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                            }
                                        });


                            }
                        })
                     .addOnFailureListener(new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception e) {
                             Snackbar.make(rootLayout, "Failed" + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                         }
                     });

            }
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
            }
        });

        dialog.show();
    }
}
