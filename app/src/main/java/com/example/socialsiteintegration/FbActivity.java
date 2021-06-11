package com.example.socialsiteintegration;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class FbActivity extends AppCompatActivity {
    private LoginButton loginButton;
    private ImageView ImageView;
    private TextView username,usermail;

    private CallbackManager callbackManager;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fb);


        loginButton=findViewById(R.id.login_button);
        username=findViewById(R.id.username);
        usermail=findViewById(R.id.usermail);
        ImageView=findViewById(R.id.imageView);

        callbackManager=CallbackManager.Factory.create();
        loginButton.setPermissions(Arrays.asList("email","public_profile"));
        checkLoginStatus();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(LoginResult loginResult) {
                String image_url="https://graph.facebook.com/"+loginResult.getAccessToken().getUserId()+ "/picture?return_ssl_resources=1";
                Picasso.get().load(image_url).into(ImageView);
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }
    AccessTokenTracker tokenTracker=new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            if(currentAccessToken==null) {
                username.setText("");
                usermail.setText("");
                ImageView.setImageResource(0);
                Toast.makeText(FbActivity.this, "User Logged out", Toast.LENGTH_SHORT).show();
            }
            else{
                loaduserProfile(currentAccessToken);
            }
        }
    };
    @SuppressLint("SetTextI18n")
    private void loaduserProfile(AccessToken newAccessToken){
        GraphRequest request=GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response)
            {
                try {
                    String first_name = object.getString("first_name");
                    String last_name = object.getString("last_name");
                    String email = object.getString("email");


                    usermail.setText(email);
                    username.setText(first_name+" "+last_name);

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters=new Bundle();
        parameters.putString("fields","first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void checkLoginStatus(){
        if(AccessToken.getCurrentAccessToken()!=null){
            loaduserProfile(AccessToken.getCurrentAccessToken());
        }
    }
}



















