package com.johnturkson.podcasts.model;

public class Player {
    private static Player INSTANCE = new Player();
    
    public static Player getInstance() {
        return INSTANCE;
    }
    
    public void restore() {
        
    }
}
