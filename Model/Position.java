/*
Filename: Position.java
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
 * This class holds all the game-state data for the Position of a Space in a WebCheckers's game.
 */
public class Position {
    /** This is the row the Space is in */
    private int row;
    /** This is the column the Space is in */
    private int cell;

    /**
     * This is the Constructor for a Position object.
     * @param row the row of the Position.
     * @param cell the column of the Position.
     */
    public Position(int row, int cell) {
        this.row = row;
        this.cell = cell;
    }

    /**
     * gets the row of an position object
     * @return int: the x coordinate of the position
     */
    public int getRow() {
        return row;
    }

    /**
     * sets the row of a position object
     * @param row the row to set it to
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * gets the Y coordinate of the position
     * @return int: the Y coordinate
     */
    public int getCell() {
        return cell;
    }

    /**
     * sets the y coordinate of the position object
     * @param cell the y coordinate to set it to
     */
    public void setCell(int cell) {
        this.cell = cell;
    }





}
