/*
Filename: Row.java
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
 * This class holds all the game-state data for a Row of the board in a WebCheckers's game.
 */
public class Row implements Iterable<Space>{
    /** This is a list of the spaces in this row */
    private List<Space> spaces;
    /** This is the position of this Row on the WebChecker's game's board */
    private int index;

    /**
     * Creates an ArrayList of spaces alternating black or white, and sets pieces
     * on spaces of the Row where necessary.
     * @param rowStartBlack if the row should start black or not
     * @param index the x coordinate of the row
     * @param pieceColor the color of the piece on the for the space in the row to have (if applicable)
     */
    public Row(boolean rowStartBlack, int index, Player.Color pieceColor){
        this.spaces = new ArrayList<>(8);
        this.index = index;
        if(rowStartBlack){
            for(int i = 1; i <= 8; i++){
                if(i%2 == 1){
                    spaces.add(new Space(Space.Color.BLACK, i - 1, pieceColor));
                }
                else{
                    spaces.add(new Space(Space.Color.WHITE, i - 1, pieceColor));
                }
            }
        }
        else{
            for(int i = 1; i <= 8; i++){
                if(i%2 == 1){
                    spaces.add(new Space(Space.Color.WHITE, i - 1, pieceColor));
                }
                else{
                    spaces.add(new Space(Space.Color.BLACK, i - 1, pieceColor));
                }
            }
        }
    }

    /**
     * gets the list of spaces
     * @return the list of spaces
     */
    public List<Space> getSpaces() {
        return spaces;
    }

    /**
     * gets a space at the index
     * @param i the x coordinate of the space
     * @return the space at teh index
     */
    public Space getSpace(int i) {
        return spaces.get(i);
    }

    /**
     * get the x coordinate of a space
     * @return  the x coordinate for the space
     */
    public synchronized int getIndex(){
        return index;
    }

    /**
     * iterates through the list of spaces
     * @return the iterator
     */
    @Override
    public synchronized Iterator<Space> iterator(){
        return this.spaces.iterator();
    }
}
