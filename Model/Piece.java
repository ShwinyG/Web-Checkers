/*
Filename: Piece.java
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
 * This class holds all the game-state data for a single Piece in the WebChecker's game.
 */
public class Piece{
    /** This enum holds the different types of Piece there are */
    public enum Type {SINGLE, KING}
    /** This is the type of Piece this Piece is */
    private Type type;
    /** This is the Color of this Piece */
    private Player.Color color;

    /**
     * This is the Constructor for a Piece object.
     * @param color the Color of the Piece.
     */
    public Piece(Player.Color color){
        this.color = color;
        this.type = Type.SINGLE;
    }

    /**
     * gets the color of a piece
     * @return Player.Color the color
     */
    public synchronized Player.Color getColor(){
        return this.color;
    }

    /**
     * get the type of piece
     * @return the type of piece
     */
    public synchronized Type getType(){
        return type;
    }

    /**
     * set the type of piece
     * @param type the type of the piece to set to
     */
    public synchronized void setType(Type type){
        this.type = type;
    }
}
