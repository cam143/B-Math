package com.example.battlemath;

/**
 * Defines several constants used between {@link BattleMathService} and the UI.
 */
public interface Constants {

    // Message types sent from the BattleMathService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_QUESTION = 6;
    
    // Key names received from the BattleMathService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    
    // Read Message
    public static final String ANSWER = "answer";
    public static final String QUESTION = "question";
    public static final String START_GAME = "start";
    public static final String GAME_OVER = "game_over";
}