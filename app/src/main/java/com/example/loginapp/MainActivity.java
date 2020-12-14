package com.example.loginapp;

import androidx.appcompat.app.AppCompatActivity;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private ImageView photoImageView;
    private TextView nameTextView;
    private TextView emailTextView;

    private ProfileTracker profileTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        photoImageView = findViewById(R.id.photoImageView);
        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                if (currentProfile != null) {
                    displayProfileInfo(currentProfile);
                }
            }
        };

        if (AccessToken.getCurrentAccessToken() == null) {
            goLoginScreen();
        } else {
            requestEmail(AccessToken.getCurrentAccessToken());

            Profile profile = Profile.getCurrentProfile();
            if (profile != null) {
                displayProfileInfo(profile);
            } else {
                Profile.fetchProfileForCurrentAccessToken();
            }
        }
    }

    private void requestEmail(AccessToken currentAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(currentAccessToken, (object, response) -> {
            if (response.getError() != null) {
                Toast.makeText(getApplicationContext(), response.getError().getErrorMessage(), Toast.LENGTH_LONG).show();
                return;
            }
            try {
                String email = object.getString("email");
                setEmail(email);
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, first_name, last_name, email, gender, birthday, location");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void setEmail(String email) {
        emailTextView.setText(email);
    }

    private void goLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    public void logout(View view) {
        LoginManager.getInstance().logOut();
        goLoginScreen();
    }

    private void displayProfileInfo(Profile profile) {
        String name = profile.getName();
        String photoUrl = profile.getProfilePictureUri(300, 300).toString();

        nameTextView.setText(name);

        Glide.with(getApplicationContext())
                .load(photoUrl)
                .into(photoImageView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        profileTracker.stopTracking();
    }
}