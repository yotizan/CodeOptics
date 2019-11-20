package com.my.yotizan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {
    private Button btnUser , btnArtisan;
    private TextView loginTxtView,loginTxtView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btnUser = (Button)findViewById(R.id.btnUser);
        btnArtisan = (Button)findViewById(R.id.btnArtisan);
        loginTxtView = (TextView)findViewById(R.id.txtViewUserLogin);
        loginTxtView2 = (TextView)findViewById(R.id.txtViewArtisanLogin);

        btnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ClientRegister.class));
            }
        });
        btnArtisan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ArtisanRegister.class));
            }
        });



        loginTxtView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ArtisanLogin.class));
            }
        });


    }
}
