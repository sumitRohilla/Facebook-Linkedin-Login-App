package com.example.loginapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.shantanudeshmukh.linkedinsdk.LinkedInBuilder;
import com.shantanudeshmukh.linkedinsdk.helpers.LinkedInUser;
import com.shantanudeshmukh.linkedinsdk.helpers.OnBasicProfileListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;

public class LoginActivity extends AppCompatActivity {

    private CallbackManager callbackManager;


    public static final int LINKEDIN_REQUEST = 99;
    public static String clientID;
    public static String clientSecret;
    public static String redirectUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getCredentials();

        callbackManager = CallbackManager.Factory.create();

        Button facebookBtn = (Button) findViewById(R.id.login_button_fb);
        Button linkedinBtn = findViewById(R.id.login_button_ln);

        facebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Collections.singletonList("email"));
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        goFacebookScreen();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getApplicationContext(), R.string.cancel_login, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(getApplicationContext(), R.string.error_login, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });



        linkedinBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                LinkedInBuilder.getInstance(LoginActivity.this)
                        .setClientID(clientID)
                        .setClientSecret(clientSecret)
                        .setRedirectURI(redirectUrl)
                        .authenticate(LINKEDIN_REQUEST);

                Toast.makeText(linkedinBtn.getContext(), "Linkedin Login", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void goFacebookScreen() {
        Toast.makeText(this, "Facebook Login", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    private void goLinkedinScreen(LinkedInUser user) {

        Intent intent = new Intent(LoginActivity.this, LinkedinLoginView.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);


        /*
         * Linked in Activity result
         * */

        if (requestCode == LINKEDIN_REQUEST && data != null) {

            if (resultCode == RESULT_OK) {

                //Successfully signed in and retrieved data
                LinkedInUser user = data.getParcelableExtra("social_login");
                goLinkedinScreen(user);

            } else {

                //print the error
                Log.i("LINKEDIN ERR", data.getStringExtra("err_message"));

                if (data.getIntExtra("err_code", 0) == LinkedInBuilder.ERROR_USER_DENIED) {
                    //user denied access to account
                    Toast.makeText(this, "User Denied Access", Toast.LENGTH_SHORT).show();
                } else if (data.getIntExtra("err_code", 0) == LinkedInBuilder.ERROR_USER_DENIED) {
                    //some error occured
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void getCredentials() {
        try {

            InputStream is = getAssets().open("linkedin-credentials.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            JSONObject linkedinCred = new JSONObject(json);
            clientID = linkedinCred.getString("client_id");
            clientSecret = linkedinCred.getString("client_secret");
            redirectUrl = linkedinCred.getString("redirect_url");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
