/*
Filename: Player.java
Java_Version: 8 - current
Apache_Maven_Version: 3.6.2
Spark_Version: 2.8.0
 */

package com.webcheckers.Model;
/**
 * @author Ashwin Gowda
 * @author Garrett Rudolfs
 * @author Joseph Becker
 * @author Julie Sojkowski
 *
 * This class holds all the game-state data for a Player in WebCheckers.
 */
public class Player {
    public enum Mode {PLAY, SPECTATOR, REPLAY, NONE }
    /** This enum holds the different types of Color that a player can be */
    public enum Color {RED, WHITE, NONE}
    /** This is the name of the Player */
    private String name;
    /** This condition tells whether a player is currently in a game */
    private boolean isInGame;
    /** This condition tells whether a player just entered a full game */
    private boolean justEnteredFullGame;
    /** This is the Color of the Player */
    private Color color;
    /** This is the mode of the Player */
    private Mode viewMode;
    /** This is the identifier for the game the player is in, if they are in a game */
    private int gameID;

    private Boolean lastMove;

    /**
     * This is the Constructor for a Player object.
     * @param Name the name of the Player.
     */
    public Player(String Name){
        name = Name;
        color= Color.NONE;
        isInGame = false;
        gameID = 0;
    }

    /**
     * sets the view mode for a player
     * @param mode the mode to set the viewMode to
     */
    public void setViewMode(Mode mode){
        this.viewMode = mode;
    }

    public boolean getNewMove(){
        return lastMove;
    }

    public void setNewMove(boolean b){
        lastMove = b;
    }

    /**
     * gets the view mode for a player
     * @return the view mode
     */
    public Mode getViewMode(){
        return this.viewMode;
    }

    /**
     * get the color of the Player
     * @return Color: the color of the Player
     */
    public Color getColor(){
        return color;
    }

    /**
     * set the color of the Player
     * @param color the color being set to the Player
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Checks if a Player is in a game
     * @return true if they are in a game, false if not
     */
    public boolean getIsInGame(){
        if(this.isInGame) return true;
        return false;
    }

    /**
     * Checks if a player just entered a full game.
     * @return true if the game is full, false if not.
     */
    public boolean getJustEnteredFullGame(){
        return justEnteredFullGame;
    }

    /**
     * Checks if a player is in a game and then sets the opposite.
     */
    public synchronized void setIsInGame(){
        if(this.isInGame) {
            isInGame  = false;
        }
        else {
            isInGame = true;
        }
    }

    /**
     * Sets if the Player just entered a full game.
     * @return true if yes, false if no.
     */
    public boolean setJustEnteredFullGame(){
        if(this.justEnteredFullGame) {
            justEnteredFullGame  = false;
            return false;
        }
        else {
            justEnteredFullGame = true;
            return true;
        }
    }

    /**
     * gets the name of a player as a string
     * @return string: the name of the player
     */
    public String getName(){
        return this.name;
    }

    /**
     * This method sets the game id that this player is in. It is seperate from
     * setIsInGame() in order to know what game to erase after both players
     * have exited it.
     * @param gameID the id of the game the player is in.
     */
    public void setGameID(int gameID){
        this.gameID = gameID;
    }

    /**
     * This is a get function for the game id.
     * @return an integer id for the game the player is in, 0 if the player isn't in a game.
     */
    public int getGameID(){
        return this.gameID;
    }
}
