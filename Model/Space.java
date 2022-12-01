/*
Filename: Space.java
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
 * This class holds all the game-state data for a Space on the board in a WebCheckers's game.
 */
public class Space{
    /** This enum holds the different colors a Space can be */
    public enum Color {BLACK, WHITE}
    /** This is the column index for this space on the board */
    private int cellIdx;
    /** This is the Color of this Space */
    private Color color;
    /** This is the Piece on this Space if there is one */
    private Piece piece;

    /**
     * This is the Constructor for a Space object.
     * @param spaceColor the Color of this Space.
     * @param cellIdx the column index for this space on the board.
     * @param pieceColor the Color of the Piece in this Space if there is one.
     */
    public Space(Color spaceColor, int cellIdx, Player.Color pieceColor){
        this.cellIdx = cellIdx;
        this.color = spaceColor;
        if(spaceColor == Color.BLACK && pieceColor != null){
            this.piece = new Piece(pieceColor);
        }
        else{
            this.piece = null;
        }
    }

    /**
     * chekcks if a space is valid to move to
     * @return ture if the color is black and there is no piece on it
     */
    public synchronized boolean isValid(){
        return (this.color == Color.BLACK && this.piece == null);
    }

    /**
     * gets the x coordinate of a space
     * @return the x coordinate of the space
     */
    public synchronized int getCellIdx(){
        return cellIdx;
    }

    /**
     * gets the piece on the space
     * @return the piece, null if there is none
     */
    public synchronized Piece getPiece(){
        return this.piece;
    }

    /**
     * sets the piece on the space
     * @param piece the piece to put on the square
     * @return the piece moved
     */
    public synchronized Piece setPiece(Piece piece){
        this.piece = piece;
        return this.piece;
    }

    /**
     * gets the color of the space
     * @return the color of the space
     */
    public synchronized Color getColor() {return this.color;}
}
