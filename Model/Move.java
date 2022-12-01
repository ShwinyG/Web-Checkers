/*
Filename: Move.java
Java_Version: 8 - current
Apache_Maven_Version: 3.6.2
Spark_Version: 2.8.0
 */

package com.webcheckers.Model;

/*
Move rules:

In each turn, a player can make a simple move, a single jump, or a multiple jump move.
Simple move: Single pieces can move one adjacent square diagonally forward away from the player.
          A piece can only move to a vacant dark square.
Single jump move: A player captures an opponent's piece by jumping over it, diagonally, to an adjacent vacant dark square.
    The opponent's captured piece is removed from the board. The player can never jump over, even without capturing,
    one of the player's own pieces. A player cannot jump the same piece twice.
Multiple jump move: Within one turn, a player can make a multiple jump move with the same piece by jumping from
    vacant dark square to vacant dark square. The player must capture one of the opponent's pieces with each jump.
    The player can capture several pieces with a move of several jumps.
If a jump move is possible, the player must make that jump move. A multiple jump move must be completed.
    The player cannot stop part way through a multiple jump. If the player has a choice of jumps, the player can choose
    among them, regardless of whether some of them are multiple, or not.
When a single piece reaches the row of the board furthest from the player, i.e the king-row, by reason of a simple move,
    or as the completion of a jump, it becomes a king. This ends the player's turn. The opponent crowns the piece
    by placing a second piece on top of it.
A king follows the same move rules as a single piece except that a king can move and jump diagonally forward away from
    the player or diagonally backward toward the player. Within one multiple jump move, the jumps can be any combination
    of forward or backward jumps. At any point, if multiple jumps are available to a king, the player can choose among them.
 */

import com.webcheckers.InvalidMoveException;

/**
 * @author Ashwin Gowda
 * @author Garrett Rudolfs
 * @author Joseph Becker
 * @author Julie Sojkowski
 *
 * This class holds all the game-state data for a move made in a WebChecker game.
 */
public class Move {
    /** This is the start position of the moving piece */
    private Position start;
    /** This is the end position of the moving piece */
    private Position end;
    /** This is a condition that tells whether this move is part of a double capture move */
    private boolean dMove;
    /** This is a condition that tells whether this move is the first move made this turn */
    private boolean isFirstMove;
    /** This is a condition that tells whether after this move is made if it's part of a double capture move */
    private boolean currentdMove;
    /** This condition holds whether the moving piece changed its type during this move. */
    private boolean switchedType;
    /** This holds the information of a piece that was captured this turn, null if no pieces were captured. */
    private Piece capturedPiece;
    /** This is the Color of the Player who made this move */
    private Player.Color playerColor;

    /**
     * This is the Constructor for Move.
     * @param start the start position of the moving piece.
     * @param end the end position of the moving piece.
     */
    public Move(Position start, Position end){
        this.start = start;
        this.end = end;
        this.switchedType = false;
        this.capturedPiece = null;
    }

    /**
     * gets the player color of the move
     * @return returns the player color of the move
     */
    public Player.Color getPlayerColor(){ return playerColor; }

    /**
     * gets the start position of the move
     * @return start position
     */
    public Position getStart() {
        return start;
    }

    /**
     * gets the end position of a move
     * @return the end position
     */
    public Position getEnd() {
        return end;
    }

    /**
     * get if the move is a double move
     * @return bool if it is a double move
     */
    public boolean getdMove(){
        return this.dMove;
    }

    /**
     * gets if it is the first move
     * @return bool: true if it is the first move, false if not
     */
    public boolean getIsFirstMove(){
        return this.isFirstMove;
    }

    /**
     * gets the boolean for whether this Move is still a double move after it's moved.
     * @return true or false, depending on if this is still a double move.
     */
    public boolean getCurrentdMove(){
        return this.currentdMove;
    }

    /**
     * gets whether the moving piece's type changed this move.
     * @return whether the moving piece's type changed this move.
     */
    public boolean getSwitchedType(){
        return this.switchedType;
    }

    /**
     * sets that the moving piece's type changed this move.
     * @param condition true if the piece's type changed, otherwise false.
     */
    public void setSwitchedType(boolean condition){
        this.switchedType = condition;
    }

    /**
     * gets the piece that was captured during this move.
     * @return the piece if there is one, null otherwise.
     */
    public Piece getCapturedPiece(){
        return this.capturedPiece;
    }

    /**
     * stores the piece that was captured during this move.
     * @param piece the piece that was captured.
     */
    public void setCapturedPiece(Piece piece){
        this.capturedPiece = piece;
    }

    /**
     * sets ther player color of the move
     * @param playerColor the color of the player to set
     */
    public void setPlayerColor(Player.Color playerColor){ this.playerColor = playerColor; }

    /**
     * checks if a move is valid
     * @param game the game the move was made on
     * @param board the board the move was made on
     * @return true if valid, throws exception if not
     * @throws InvalidMoveException when move is invalid
     */
    public boolean isValid(GameModel game, BoardView board) throws InvalidMoveException {
        if(!game.getIsFirstMove()){
            if(!game.getdMove()){
                throw new InvalidMoveException("You already made a move.");
            }
        }
        if(isNormalMove() && !game.getdMove()){
            for(Row row: board.getRows()){
                for(Space space: row.getSpaces()){
                    if(space.getPiece() != null){
                        if(space.getPiece().getColor() == game.getActiveColor()){
                            if(captureMoveAvailable(game, board, row.getIndex(), space.getCellIdx(),
                                    board.getRow(row.getIndex()).getSpace(space.getCellIdx()).getPiece().getType())){
                                throw new InvalidMoveException("You must make a capture move since you can.");
                            }
                        }
                    }
                }
            }
            if(board.getRow(start.getRow()).getSpace(start.getCell()).getPiece().getType() == Piece.Type.SINGLE){
                if(end.getRow() > start.getRow()){
                    //is not a valid move
                    throw new InvalidMoveException("Piece cannot be moved backward.");
                }
                else{
                    game.setIsFirstMove(false);
                    return(board.getRow(end.getRow()).getSpace(end.getCell()).isValid());
                }
            }
            else{
                game.setIsFirstMove(false);
                return(board.getRow(end.getRow()).getSpace(end.getCell()).isValid());
            }
        }
        else if(isCaptureMove()){
            if(board.getRow(start.getRow()).getSpace(start.getCell()).getPiece().getType() == Piece.Type.SINGLE){
                if(end.getRow() > start.getRow()){
                    throw new InvalidMoveException("Piece can't be moved backwards.");
                }
                else{
                    int capturedPieceRow = end.getRow() - ((end.getRow() - start.getRow())/2);
                    int capturedPieceCell = end.getCell() - ((end.getCell() - start.getCell())/2);
                    if(board.getRow(capturedPieceRow).getSpace(capturedPieceCell).getPiece() != null){
                        if(board.getRow(capturedPieceRow).getSpace(capturedPieceCell).getPiece().getColor() != game.getActiveColor()){
                            game.setIsFirstMove(false);
                            if(end.getRow() == 0){
                                game.setdMove(captureMoveAvailable(game, board, end.getRow(), end.getCell(),
                                        Piece.Type.KING));
                                this.setCurrentdMove(captureMoveAvailable(game, board, end.getRow(), end.getCell(),
                                        Piece.Type.KING));
                            }
                            else{
                                game.setdMove(captureMoveAvailable(game, board, end.getRow(), end.getCell(),
                                        Piece.Type.SINGLE));
                                this.setCurrentdMove(captureMoveAvailable(game, board, end.getRow(), end.getCell(),
                                        Piece.Type.SINGLE));
                            }
                            return(board.getRow(end.getRow()).getSpace(end.getCell()).isValid());
                        }
                        else{
                            throw new InvalidMoveException("Piece isn't allowed to capture a " + game.getActiveColor() + " piece.");
                        }
                    }
                    else{
                        throw new InvalidMoveException("Piece can't capture a piece that doesn't exist.");
                    }
                }
            }
            else{
                int capturedPieceRow = end.getRow() - ((end.getRow() - start.getRow())/2);
                int capturedPieceCell = end.getCell() - ((end.getCell() - start.getCell())/2);
                if(board.getRow(capturedPieceRow).getSpace(capturedPieceCell).getPiece() != null){
                    if(board.getRow(capturedPieceRow).getSpace(capturedPieceCell).getPiece().getColor() != game.getActiveColor()){
                        game.setIsFirstMove(false);
                        game.setdMove(captureMoveAvailable(game, board, end.getRow(), end.getCell(),
                                board.getRow(start.getRow()).getSpace(start.getCell()).getPiece().getType()));
                        this.setCurrentdMove(captureMoveAvailable(game, board, end.getRow(), end.getCell(),
                                board.getRow(start.getRow()).getSpace(start.getCell()).getPiece().getType()));
                        return(board.getRow(end.getRow()).getSpace(end.getCell()).isValid());
                    }
                    else{
                        throw new InvalidMoveException("Piece isn't allowed to capture a " + game.getActiveColor() + " piece.");
                    }
                }
                else{
                    throw new InvalidMoveException("Piece can't capture a piece that doesn't exist.");
                }
            }
        }
        else{
            throw new InvalidMoveException("Piece must be moved to a different space.");
        }
    }

    /**
     * checks if the move is a capture move
     * @return bool true if the move is a capture move, false if not
     */
    public boolean isCaptureMove(){
        if(Math.abs(this.end.getCell() - this.start.getCell()) == 2){
            if(!(start.getRow() == end.getRow() || start.getCell() == end.getCell())){
                return true;
            }
        }
        return false;
    }

    /**
     * checks if the move is a normal move
     * @return bool true if normal, false if not
     */
    public boolean isNormalMove(){
        return Math.abs(this.end.getCell() - this.start.getCell()) == 1;
    }

    /**
     * sets the current sate
     * @param dMove if the move is a double move
     * @param isFirstMove if the move is the first move
     */
    public void setCurState(boolean dMove, boolean isFirstMove){
        this.dMove = dMove;
        this.isFirstMove = isFirstMove;
    }

    /**
     * sets the dMove state, it's necessary
     * @param dMove if the move is a double move
     */
    public void setCurrentdMove(boolean dMove){
        this.currentdMove = dMove;
    }

    /**
     * This method checks if a capture move can be made from the given row
     * and cell of a space.
     * @param game the WebChecker game.
     * @param board the active Player's board.
     * @param row the row of the Space being checked.
     * @param cell the cell of the Space being checked.
     * @param type the type of Piece on the Space being checked.
     * @return whether a capture move can be made from the given space or not.
     */
    public boolean captureMoveAvailable(GameModel game, BoardView board, int row, int cell, Piece.Type type){
        if(type == Piece.Type.SINGLE){
            int check1cell = cell - 1;
            int check1row = row - 1;
            int check2cell = cell + 1;
            int check2row = row - 1;
            if(check1cell <= 6 && check1cell >= 1 && check1row <=6 && check1row >=1){
                if(board.getRow(check1row).getSpace(check1cell).getPiece() != null){
                    if(board.getRow(check1row).getSpace(check1cell).getPiece().getColor() != game.getActiveColor()){
                        if(board.getRow(check1row - 1).getSpace(check1cell - 1).isValid()){
                            return true;
                        }
                    }
                }
            }
            if(check2cell <= 6 && check2cell >= 1 && check2row <=6 && check2row >=1){
                if(board.getRow(check2row).getSpace(check2cell).getPiece() != null){
                    if(board.getRow(check2row).getSpace(check2cell).getPiece().getColor() != game.getActiveColor()){
                        if(board.getRow(check2row - 1).getSpace(check2cell + 1).isValid()){
                            return true;
                        }
                    }
                }
            }
            return false;
        }
        else{
            int check1cell = cell - 1;
            int check1row = row - 1;
            int check2cell = cell + 1;
            int check2row = row - 1;
            int check3cell = cell - 1;
            int check3row = row + 1;
            int check4cell = cell + 1;
            int check4row = row + 1;
            if(!(check1cell - 1 == start.getCell() && check1row - 1 == start.getRow())){
                if(check1cell <= 6 && check1cell >= 1 && check1row <=6 && check1row >=1){
                    if(board.getRow(check1row).getSpace(check1cell).getPiece() != null){
                        if(board.getRow(check1row).getSpace(check1cell).getPiece().getColor() != game.getActiveColor()){
                            if(board.getRow(check1row - 1).getSpace(check1cell - 1).isValid()){
                                return true;
                            }
                        }
                    }
                }
            }
            if(!(check2cell + 1 == start.getCell() && check2row - 1 == start.getRow())){
                if(check2cell <= 6 && check2cell >= 1 && check2row <=6 && check2row >=1){
                    if(board.getRow(check2row).getSpace(check2cell).getPiece() != null){
                        if(board.getRow(check2row).getSpace(check2cell).getPiece().getColor() != game.getActiveColor()){
                            if(board.getRow(check2row - 1).getSpace(check2cell + 1).isValid()){
                                return true;
                            }
                        }
                    }
                }
            }
            if(!(check3cell - 1 == start.getCell() && check3row + 1 == start.getRow())){
                if(check3cell <= 6 && check3cell >= 1 && check3row <=6 && check3row >=1){
                    if(board.getRow(check3row).getSpace(check3cell).getPiece() != null){
                        if(board.getRow(check3row).getSpace(check3cell).getPiece().getColor() != game.getActiveColor()){
                            if(board.getRow(check3row + 1).getSpace(check3cell - 1).isValid()){
                                return true;
                            }
                        }
                    }
                }
            }
            if(!(check4cell + 1 == start.getCell() && check4row + 1 == start.getRow())){
                if(check4cell <= 6 && check4cell >= 1 && check4row <=6 && check4row >=1){
                    if(board.getRow(check4row).getSpace(check4cell).getPiece() != null){
                        if(board.getRow(check4row).getSpace(check4cell).getPiece().getColor() != game.getActiveColor()){
                            if(board.getRow(check4row + 1).getSpace(check4cell + 1).isValid()){
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }
    }
}
