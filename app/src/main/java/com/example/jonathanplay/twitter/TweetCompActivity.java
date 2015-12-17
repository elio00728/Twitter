package com.example.jonathanplay.twitter;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TweetCompActivity extends AppCompatActivity {

    private Button bcreateTweetBtn;
    private EditText inputTweetComp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_comp);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create tweet button
        bcreateTweetBtn = (Button) findViewById(R.id.createTweetBtn);
        inputTweetComp = (EditText) findViewById(R.id.inputTweetComp);
        bcreateTweetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(TweetCompActivity.this, inputTweetComp.getText(), Toast.LENGTH_SHORT).show();
                NetworkManager.getInstance(TweetCompActivity.this).postTweet(inputTweetComp.getText().toString());
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                Intent intent = new Intent(TweetCompActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

}
