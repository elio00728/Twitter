package com.example.jonathanplay.twitter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {
    private static Database instance = null;
    public static final String DATABASE_NAME = "twitter.db";
    public static final String TWEET_TABLE_NAME = "tweet";
    public static final String TWEET_COLUMN_ID = "id";
    public static final String TWEET_COLUMN_DATE = "date";
    public static final String TWEET_COLUMN_USERNAME = "username";
    public static final String TWEET_COLUMN_CONTENT = "content";

    public Database(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static Database getInstance(Context context) {
        if(instance == null)
            instance = new Database(context,DATABASE_NAME,null,1);
        return instance;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + TWEET_TABLE_NAME +
                        " (" + TWEET_COLUMN_ID + " integer primary key, " + TWEET_COLUMN_DATE + " text, " +
                        TWEET_COLUMN_USERNAME + " text, " + TWEET_COLUMN_CONTENT + " text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tweet");
        onCreate(db);
    }

    public boolean insertTweet(Tweet tweet)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TWEET_COLUMN_DATE, tweet.getDate());
        contentValues.put(TWEET_COLUMN_USERNAME, tweet.getUserName());
        contentValues.put(TWEET_COLUMN_CONTENT, tweet.getContenu());
        db.insert(TWEET_TABLE_NAME, null, contentValues);
        return true;
    }

    public ArrayList<Tweet> getAllTweets()
    {
        ArrayList<Tweet> tweetList = new ArrayList<Tweet>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from " + TWEET_TABLE_NAME, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            Tweet tweet = new Tweet();
            tweet.setDate(res.getString((res.getColumnIndex(TWEET_COLUMN_DATE))));
            tweet.setUserName(res.getString((res.getColumnIndex(TWEET_COLUMN_USERNAME))));
            tweet.setContenu(res.getString((res.getColumnIndex(TWEET_COLUMN_CONTENT))));

            tweetList.add(tweet);
            res.moveToNext();
        }
        return tweetList;
    }
}
