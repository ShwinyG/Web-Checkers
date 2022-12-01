/*
Filename: BoardView.java
Java_Version: 8 - current
Apache_Maven_Version: 3.6.2
Spark_Version: 2.8.0
 */

package com.webcheckers.Model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Ashwin Gowda
 * @author Garrett Rudolfs
 * @author Joseph Becker
 * @author Julie Sojkowski
 *
 * This class holds the game-state data for a WebChecker game's board as a list
 * of Row objects.
 */
public class BoardView implements Iterable<Row>{
    /** This is the list of rows that make up the board */
    private List<Row> rows;
    /** The number of pieces the red player currently has */
    private int redPlayerPieces;
    /** The number of pieces the white player currently has */
    private int whitePlayerPieces;
    /** The starting number of pieces for each player */
    private static final int START_NUM_PIECES = 12;

    /**
     * This is the Constructor for BoardView; it initializes the Row list in accordance with
     * what the starting state of a checkers board is supposed to be like.
     * @param color the color of the Player who's pieces are on the bottom side of this board.
     */
    public BoardView(Player.Color color){
        this.rows = new ArrayList<>(8);
        this.redPlayerPieces = START_NUM_PIECES;
        this.whitePlayerPieces = START_NUM_PIECES;
        for(int i = 1; i <= 8; i++) {
            if(i <= 3){
                if(color == Player.Color.RED){
                    if(i%2 == 1){
                        this.rows.add(new Row(false, i - 1, Player.Color.WHITE));
                    }
                    else{
                        this.rows.add(new Row(true, i - 1, Player.Color.WHITE));
                    }
                }
                else{
                    if(i%2 == 1){
                        this.rows.add(new Row(false, i - 1, Player.Color.RED));
                    }
                    else{
                        this.rows.add(new Row(true, i - 1, Player.Color.RED));
                    }
                }
            }
            else if(i >= 6){
                if(color == Player.Color.RED){
                    if(i%2 == 1){
                        this.rows.add(new Row(false, i - 1, Player.Color.RED));
                    }
                    else{
                        this.rows.add(new Row(true, i - 1, Player.Color.RED));
                    }
                }
                else{
                    if(i%2 == 1){
                        this.rows.add(new Row(false, i - 1, Player.Color.WHITE));
                    }
                    else{
                        this.rows.add(new Row(true, i - 1, Player.Color.WHITE));
                    }
                }
            }
            else{
                if(i%2 == 1){
                    this.rows.add(new Row(false, i - 1, null));
                }
                else{
                    this.rows.add(new Row(true, i - 1, null));
                }
            }
        }
    }

    /**
     * This method increases the number of pieces a player has when
     * that player gains a piece.
     * @param color the Player's color.
     */
    public void addPiece(Player.Color color){
        if(color == Player.Color.RED){
            this.redPlayerPieces++;
        }
        else{
            this.whitePlayerPieces++;
        }
    }

    /**
     * This method decreases the number of pieces a player has when
     * that player loses a piece.
     * @param color the Player's color.
     */
    public void removePiece(Player.Color color){
        if(color == Player.Color.RED){
            this.redPlayerPieces--;
        }
        else{
            this.whitePlayerPieces--;
        }
    }

    /**
     * sets the player number of pieces to a number
     * used in unit tests
     * @param num the number to set player pieces to
     */
    public void setRedPlayerPieces(int num){
        redPlayerPieces = num;
    }

    /**
     * sets the player number of pieces to a number
     * used in unit tests
     * @param num the number to set player pieces to
     */
    public void setWhitePlayerPieces(int num){
        whitePlayerPieces = num;
    }

    /**
     * gets the number of red player pieces left
     * @return int: the number of red player pieces
     */
    public int getRedPlayerPieces(){
        return this.redPlayerPieces;
    }

    /**
     * gets the white player pieces
     * @return int: the number of white player pieces
     */
    public int getWhitePlayerPieces(){
        return this.whitePlayerPieces;
    }

    /**
     * This method is used by GameModel to manually end the game.
     * @param color the color of the player who loses.
     */
    public void endTheGame(Player.Color color){
        if(color == Player.Color.RED){
            this.redPlayerPieces = 0;
        }
        else{
            this.whitePlayerPieces = 0;
        }
    }

    /**
     * gets the row list
     * @return list of rows
     */
    public List<Row> getRows() {
        return rows;
    }

    /**
     * gets the list of spaces
     * @param i the y coordinate of the row
     * @return the list of rows if the index is valid, null if not
     */
    public Row getRow(int i){
        if(i<8) {
            return rows.get(i);
        }
        return null;
    }

    /**
     * gets a space in the row list
     * @param row the row of the space (y coordinate)
     * @param col the column of the space (x coordinate)
     * @return the space at (row, col) null if there indexes are out of bounds
     */
    public Space getSpaceInRowList(int row, int col){
        if(row < 8 && col <8)
            return rows.get(row).getSpace(col);
        return null;
    }

    /**
     * gets an iterator for the rows
     * @return the iterator for the list
     */
    @Override
    public synchronized Iterator<Row> iterator(){
        return this.rows.iterator();
    }
}
