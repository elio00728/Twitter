package com.example.jonathanplay.twitter;

import java.util.EventListener;

/**
 * Created by jonathanplay on 16/12/2015.
 */
public abstract class Listener implements EventListener {
    public abstract void timelineChanged(String t);
}
