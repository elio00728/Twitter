package com.example.jonathanplay.twitter;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TweetListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


            // Récupération timeline
            Listener lt = new Listener() {
                @Override
                public void timelineChanged(final String data) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (data != null) {
                                    Toast.makeText(TweetListActivity.this, "Timeline reçue !", Toast.LENGTH_SHORT).show();
                                    List<Tweet> tweetList = new ArrayList<Tweet>();
                                    ArrayList<Tweet> tweetListToDisplay = new ArrayList<Tweet>();
                                    Database db = Database.getInstance(TweetListActivity.this);
                                    JSONArray tweetJsonArray = new JSONArray(data);

                                    // Récupération des Tweets dans la chaîne JSON
                                    for(int i=0; i < tweetJsonArray.length(); i++){
                                        JSONObject jsonTweet = tweetJsonArray.getJSONObject(i);
                                        Tweet tweet = new Tweet();

                                        // Récupération données générales
                                        tweet.setDate(jsonTweet.getString("created_at"));
                                        tweet.setContenu(jsonTweet.getString("text"));

                                        // Récupération userName
                                        JSONObject jsonUser = jsonTweet.getJSONObject("user");
                                        tweet.setUserName(jsonUser.getString("name"));

                                        // Ajout du Tweet dans la liste
                                        // tweetList.add(tweet);

                                        // Ajout du Tweet dans la base de donnée SQLLite
                                        db.insertTweet(tweet);
                                    }

                                    //Récupération de tous les Tweets enregistrés en base
                                    tweetListToDisplay = db.getAllTweets();
                                    MyAdapter adapter = new MyAdapter(TweetListActivity.this, R.layout.tweet_list, tweetListToDisplay);
                                    ListView listView = (ListView) findViewById(R.id.listView);
                                    listView.setAdapter(adapter);

                                } else {
                                    Toast.makeText(TweetListActivity.this, "Erreur lors de la récupération de la timeline !", Toast.LENGTH_SHORT).show();
                                }
                            }
                            catch (Exception e){
                                System.out.println(e.toString());
                            }
                        }
                    });
                }
            };
            NetworkManager.getInstance(TweetListActivity.this).getTimeLine(lt);


        /* ------------------------------------------ */
        /*List<TextAndDate> list = new ArrayList<>();
        TextAndDate item = new TextAndDate();

        item.setDate("12/11/2015");
        item.setText("Every day for us something new");
        list.add(item);
        item = new TextAndDate();
        item.setDate("18/11/2015");
        item.setText("Open mind for a different view");
        list.add(item);
        item = new TextAndDate();
        item.setDate("23/11/2015");
        item.setText("And nothing else matters");
        list.add(item);

        MyAdapter adapter = new MyAdapter(this, R.layout.tweet_list,list);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);*/
        /* ------------------------------------------ */

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Intent intent = new Intent(TweetListActivity.this, TweetCompActivity.class);
                startActivity(intent);
            }
        });
    }

}
