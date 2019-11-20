package com.my.yotizan;

import android.content.Intent;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ClientRegister extends AppCompatActivity {
    private Button registerBtn;
    private EditText emailField, clientPhoneNo, passwordField;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextView loginTxtView;
    private CheckBox termsAndConditions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_register);


        registerBtn = (Button)findViewById(R.id.btnSignUp);
        emailField = (EditText)findViewById(R.id.txtEmail);
        clientPhoneNo = (EditText)findViewById(R.id.txtCellNo);
        passwordField = (EditText)findViewById(R.id.txtPassword);

        termsAndConditions = (CheckBox) findViewById(R.id.cbxConditions);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Clients");

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ClientRegister.this, "SUBMITTING INFORMATION...", Toast.LENGTH_LONG).show();
                final String Telephone = clientPhoneNo.getText().toString().trim();

                final String email = emailField.getText().toString().trim();
                final String password = passwordField.getText().toString().trim();
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(Telephone)&&!TextUtils.isEmpty(password)){
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            String user_id = mAuth.getCurrentUser().getUid();
                            DatabaseReference current_user_db = mDatabase.child(user_id);
                            current_user_db.child("CellPhone").setValue(Telephone);
                            current_user_db.child("EmailAddress").setValue(email);
                            current_user_db.child("Password").setValue(password);

                            current_user_db.child("Address").setValue("Default");
                            Toast.makeText(ClientRegister.this, "Registeration Succesful", Toast.LENGTH_SHORT).show();
                            Intent regIntent = new Intent(ClientRegister.this, HomeActivity.class);
                            regIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(regIntent);
                        }
                    });
                }else {

                    Toast.makeText(ClientRegister.this, "Complete all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}