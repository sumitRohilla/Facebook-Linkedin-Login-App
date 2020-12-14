package com.example.loginapp;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shantanudeshmukh.linkedinsdk.LinkedInBuilder;
import com.shantanudeshmukh.linkedinsdk.helpers.LinkedInUser;
import com.shantanudeshmukh.linkedinsdk.helpers.OnBasicProfileListener;
import com.squareup.picasso.Picasso;

public class LinkedinLoginView extends AppCompatActivity {


    private LinkedInUser user;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linkedin_login);

        ImageView profilePic = findViewById(R.id.profile_pic);
        TextView userName = findViewById(R.id.name);
        TextView userEmail = findViewById(R.id.email);


        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            user = (LinkedInUser) extras.get("user");

        }


        LinkedInBuilder.retrieveBasicProfile(user.getAccessToken(), user.getAccessTokenExpiry(), new OnBasicProfileListener() {
            @Override
            public void onDataRetrievalStart() {

            }

            @Override
            public void onDataSuccess(LinkedInUser user) {

                userName.setText(user.getFirstName() + " " + user.getLastName());

                userEmail.setText(user.getEmail());

                Picasso.get().load(user.getProfileUrl()).into(profilePic);
            }

            @Override
            public void onDataFailed(int errCode, String errMessage) {

            }
        });
    }



    public void lnLogout(View view) {
        goLoginScreen();
    }

    private void goLoginScreen() {

        Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
