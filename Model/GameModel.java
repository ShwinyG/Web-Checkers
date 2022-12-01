/*
Filename: GameModel.java
Java_Version: 8 - current
Apache_Maven_Version: 3.6.2
Spark_Version: 2.8.0
 */

package com.webcheckers.Model;

import com.webcheckers.InvalidMoveException;

import java.util.ArrayList;
import java.util.logging.Logger;
/**
 * @author Ashwin Gowda
 * @author Garrett Rudolfs
 * @author Joseph Becker
 * @author Julie Sojkowski
 *
 * This class holds all the game-state data for an instance of a WebChecker game.
 */
public class GameModel{
    /** This is the Logger object that is used to record server info in the terminal window */
    private static final Logger LOG = Logger.getLogger(GameModel.class.getName());
    /** This enum holds the different modes that a game can be in */
    public enum Mode {PLAY, SPECTATOR, REPLAY }
    /** This is the red player in the game */
    private Player redPlayer;
    /** This is the white player in the game */
    private Player whitePlayer;
    /** This is the board from the red players perspective in the game */
    private BoardView redBoard;
    /** This is the board from the white players perspective in the game */
    private BoardView whiteBoard;
    /** This is the color of the player who's current turn it is */
    private Player.Color activeColor;
    /** This is the Mode of view this game is in */
    private Mode viewMode;
    /** This is the number used to identify this game Object */
    private int gameID;
    /** This condition is used to tell if a backup move is being made */
    private boolean undoingMove;
    /** This condition is used to tell whether a double capture move is possible */
    private boolean dMove;
    /** This condition tells whether or not a move has been made yet for the current turn */
    private boolean isFirstMove;
    /** This is the color of the Player who resigned from the game; it's null if nobody resigned */
    private Player.Color resignedColor;
    /** This is an ArrayList that holds all of the moves that have been made this game */
    private ArrayList<Move> moveHistory = new ArrayList<>();
    /** This is an ArrayList that holds all of the moves that have been made this turn */
    private ArrayList<Move> curTurnMoves = new ArrayList<>();
    /** This is an ArrayList that holds all of the spectators */
    private ArrayList<Player> spectators = new ArrayList<>();
    /** This is the system time for when the latest move was made */
    private long lastMoveTime;
    /** This is a conditional that checks to see if a turn has been submitted this game yet */
    private boolean firstTurnSubmitted;

    /**
     * This is the Constructor for a GameModel.
     * @param redPlayer the Player who's pieces are red.
     * @param whitePlayer the Player who's pieces are white.
     * @param viewMode the Mode of view this game is in.
     * @param id the identifier for this GameModel object.
     */
    public GameModel(Player redPlayer, Player whitePlayer, Mode viewMode, int id){
        this.redPlayer = redPlayer;
        this.redPlayer.setViewMode(Player.Mode.PLAY);
        this.whitePlayer = whitePlayer;
        this.whitePlayer.setViewMode(Player.Mode.PLAY);
        this.activeColor = Player.Color.RED;
        this.redBoard = new BoardView(Player.Color.RED);
        this.whiteBoard = new BoardView(Player.Color.WHITE);
        this.viewMode = viewMode;
        this.gameID = id;
        this.dMove = false;
        this.isFirstMove = true;
        this.resignedColor = null;
        this.undoingMove = false;
        this.lastMoveTime = 0;
        this.firstTurnSubmitted = false;
    }

    public boolean addSpectator(Player player){
       if(player.getViewMode() == Player.Mode.SPECTATOR && player.getGameID() == gameID) {
           return this.spectators.add(player);
       }
       return false;
    }

    public boolean isSpectator(Player player){
        for(int i =0; i< spectators.size(); i++) {
            if (player == this.spectators.get(i)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Player> getSpectators() {
        return spectators;
    }

    public void setLastMoveTime(long l){
        this.lastMoveTime = l;
    }

    public long getLastMoveTime(){
        return lastMoveTime;
    }

    public boolean getFirstTurnSubmitted(){
        return this.firstTurnSubmitted;
    }

    public void setFirstTurnSubmitted(boolean condition){
        this.firstTurnSubmitted = condition;
    }

    public boolean removeSpectator(Player player){
        return this.spectators.remove(player);
    }

    /**
     * gets the id of the game
     * @return the id of the game
     */
    public int getGameID(){ return gameID; }

    /**
     * get the red player board
     * @return the red players board
     */
    public synchronized BoardView getRedBoard(){
        return redBoard;
    }

    /**
     * gets the white board
     * @return the white players board
     */
    public synchronized BoardView getWhiteBoard(){
        return whiteBoard;
    }

    /** gets the view mode
     * @return the current view mode
     */
    public Mode getViewMode() {
        return viewMode;
    }

    /**
     * sets the view mode for the game
     * @param viewMode the new view mode
     */
    public void setViewMode(Mode viewMode) {
        this.viewMode = viewMode;
    }
    /**
     * get the red player
     * @return the red player
     */
    public Player getRedPlayer() {
        return redPlayer;
    }

    /**
     * gets the white player
     * @return the white player
     */
    public Player getWhitePlayer() {
        return whitePlayer;
    }

    /**
     * checks the active player and returns that player board view
     * @return the active player board view
     */
    public synchronized BoardView getActiveBoardView(){
        if(activeColor == Player.Color.RED) {
            return redBoard;
        }
        return whiteBoard;
    }

    /**
     * gets the active player's color
     * @return Player.Color: the active player's color
     */
    public synchronized Player.Color getActiveColor() {
        return activeColor;
    }

    /**
     * gets the move history
     * @return list of previous moves
     */
    public ArrayList<Move> getMoveHistory() {
        return moveHistory;
    }

    /**
     * gets the starting position of the most recent move
     * @return the most recent move
     */
    public Position getStartingPosition() {
        return moveHistory.get(moveHistory.size() - 1).getStart();
    }

    /**
     * the ending position of the move
     * @return the ending position of the most recent move
     */
    public Position getEndingPosition() {
        return moveHistory.get(moveHistory.size() - 1).getEnd();
    }

    /**
     * gets a move on the move list of that player
     * @param index the index from the most recent move to get (0 for the most recent move)
     * @return the move at the index
     */
    public Move getMove(int index){
        return moveHistory.get(moveHistory.size() - index -1);
    }

    /**
     * gets a move on the move list for replay mode.
     * @param index the index from the least recent move.
     * @return the move at the index.
     */
    public Move getMoveReplay(int index){
        return moveHistory.get(index);
    }

    /**
     * gets the current turns move
     * @return list of the current turns moves
     */
    public ArrayList<Move> getCurTurnMoves() {
        return curTurnMoves;
    }

    /**
     * gets if the move is double or not
     * @return true if it is a double move, false if not
     */
    public boolean getdMove(){
        return this.dMove;
    }

    /**
     * gets if the move is the firs tmove
     * @return bool: true if it is the first move, false if not
     */
    public boolean getIsFirstMove(){
        return this.isFirstMove;
    }

    /**
     * This method gets whether a player resigned from the game or not.
     * @return the color of the player who resigned, or null if no one did.
     */
    public synchronized Player.Color playerResigned() {
        return resignedColor;
    }

    /**
     * sets the double move
     * @param condition if the move is double or not
     */
    public void setdMove(boolean condition){
        this.dMove = condition;
    }

    /**
     * sets if the move is the first move
     * @param condition if the move is the first move or not
     */
    public void setIsFirstMove(boolean condition){
        this.isFirstMove = condition;
    }

    /**
     * sets the active player
     * @param currentColor the color to set the active player too
     */
    public synchronized void setActiveColor(Player.Color currentColor) {
        activeColor = currentColor;
    }

    /**
     * undoes the most recent move
     */
    public void undoLastMove(){
        Position target = getStartingPosition();
        Position current = getEndingPosition();

        Move undoMove = new Move(current, target);

        Move pastmove = getMove(0);
        undoMove.setCurState(pastmove.getdMove(), pastmove.getIsFirstMove());
        undoMove.setSwitchedType(pastmove.getSwitchedType());
        this.dMove = pastmove.getdMove();
        this.isFirstMove = pastmove.getIsFirstMove();

        if(getActiveColor() == Player.Color.RED){
            Player cur_player = getRedPlayer();

            undoingMove = true;
            try{
                makeMove(undoMove, cur_player);
                if(undoMove.isCaptureMove()){
                    int capRow = target.getRow() - ((target.getRow() - current.getRow())/2);
                    int capCell = target.getCell() - ((target.getCell() - current.getCell())/2);
                    Space capturedSpace = redBoard.getSpaceInRowList(capRow, capCell);
                    capturedSpace.setPiece(pastmove.getCapturedPiece());
                    redBoard.addPiece(Player.Color.WHITE);
                    whiteBoard.addPiece(Player.Color.WHITE);
                }
            }
            catch(InvalidMoveException e){
                LOG.fine("Invalid Move, can not undo");
            }
        }
        else{
            Player cur_player = getWhitePlayer();

            undoingMove = true;
            try{
                makeMove(undoMove, cur_player);
                if(undoMove.isCaptureMove()){
                    int capRow = target.getRow() - ((target.getRow() - current.getRow())/2);
                    int capCell = target.getCell() - ((target.getCell() - current.getCell())/2);
                    Space capturedSpace = whiteBoard.getSpaceInRowList(capRow, capCell);
                    capturedSpace.setPiece(pastmove.getCapturedPiece());
                    redBoard.addPiece(Player.Color.RED);
                    whiteBoard.addPiece(Player.Color.RED);
                }
            }
            catch(InvalidMoveException e){
                LOG.fine("Invalid Move, can not undo");
            }
        }

        undoingMove = false;
    }


    /**
     * If a player has won it returns the winning player, if there is no winner it returns null
     * @return the name of the player that won
     */
    public String playerWon(){
        if(getActiveBoardView().getRedPlayerPieces() <= 0){
            return this.whitePlayer.getName();
        }
        else if(getActiveBoardView().getWhitePlayerPieces() <= 0){
            return this.redPlayer.getName();
        }
        else {
            return null;
        }
    }

    /**
     * This method sets that a player resigned from the game.
     * @param color the color of the resigned player.
     */
    public synchronized void resign(Player.Color color){
        this.resignedColor = color;
    }

    /**
     * adds a move to the move list for that player
     * @param move the move to add
     * @param player the player to add the move too
     */
    public void addMove(Move move, Player player){
        Player.Color colorToAdd = player.getColor();
        move.setPlayerColor(colorToAdd);
        moveHistory.add(move);
        curTurnMoves.add(move);
    }

    /**
     * resets the current turns moves to an empty list
     */
    public void resetCurTurnMoves(){
        this.curTurnMoves.clear();
    }

    /**
     * This method makes a move on the inputted player's board.
     * @param move the move to make
     * @param player the player that made the move
     * @return "valid" if the move is valid
     * @throws InvalidMoveException when the move is invalid
     */
    public String makeMove(Move move, Player player) throws InvalidMoveException{
        Position start = move.getStart();
        Position end = move.getEnd();
        String s = " ";
        if(move.isCaptureMove()) {
            if (player == redPlayer){

                // change the piece location in the red board
                int startrow = 0;
                int endrow = 0;
                int startcell = 0;
                int endcell = 0;
                if(player.getColor() != activeColor){
                    startrow = (Math.abs(start.getRow() - 8) -1);
                    endrow = (Math.abs(end.getRow() - 8) -1);
                    startcell = (Math.abs(start.getCell() - 8) -1);
                    endcell = (Math.abs(end.getCell() - 8) -1);
                }
                else{
                    startrow = start.getRow();
                    endrow = end.getRow();
                    startcell = start.getCell();
                    endcell = end.getCell();
                }

                Space startSpace = redBoard.getSpaceInRowList(startrow, startcell);
                //LOG.fine("start: " + startSpace.getPiece() + " row: " + start.getRow() + " col: " + start.getCell());
                Space endSpace = redBoard.getSpaceInRowList(endrow, endcell);
                Piece pieceToChangePositions = startSpace.getPiece();
                if(player.getColor() == activeColor){
                    if(endrow == 0 && pieceToChangePositions.getType() == Piece.Type.SINGLE){
                        move.setSwitchedType(true);
                        pieceToChangePositions.setType(Piece.Type.KING);
                    }
                }
                else{
                    if(endrow == 7 && pieceToChangePositions.getType() == Piece.Type.SINGLE){
                        move.setSwitchedType(true);
                        pieceToChangePositions.setType(Piece.Type.KING);
                    }
                }
                startSpace.setPiece(null);
                endSpace.setPiece(pieceToChangePositions);

                int capRow = endrow - ((endrow - startrow)/2);
                int capCell = endcell - ((endcell - startcell)/2);
                Space capturedSpace = redBoard.getSpaceInRowList(capRow, capCell);
                move.setCapturedPiece(capturedSpace.getPiece());
                capturedSpace.setPiece(null);
                if(activeColor == Player.Color.RED){
                    redBoard.removePiece(Player.Color.WHITE);
                }
                else{
                    redBoard.removePiece(Player.Color.RED);
                }
                //LOG.fine("end: " + endSpace.getPiece() + " row: " + end.getRow() + " col: " + end.getCell());

                if(undoingMove){
                    if(move.getSwitchedType()){
                        pieceToChangePositions.setType(Piece.Type.SINGLE);
                    }
                    moveHistory.remove(moveHistory.size() - 1);
                    curTurnMoves.remove(curTurnMoves.size() - 1);
                }

                return "valid";
            }
            else if (player == whitePlayer) {

                // change the piece location in the white board
                int startrow = 0;
                int endrow = 0;
                int startcell = 0;
                int endcell = 0;
                if(player.getColor() != activeColor){
                    startrow = (Math.abs(start.getRow() - 8) -1);
                    endrow = (Math.abs(end.getRow() - 8) -1);
                    startcell = (Math.abs(start.getCell() - 8) -1);
                    endcell = (Math.abs(end.getCell() - 8) -1);
                }
                else{
                    startrow = start.getRow();
                    endrow = end.getRow();
                    startcell = start.getCell();
                    endcell = end.getCell();
                }

                Space startSpace = whiteBoard.getSpaceInRowList(startrow, startcell);
                Space endSpace = whiteBoard.getSpaceInRowList(endrow, endcell);
                Piece pieceToChangePositions = startSpace.getPiece();
                if(player.getColor() == activeColor){
                    if(endrow == 0 && pieceToChangePositions.getType() == Piece.Type.SINGLE){
                        move.setSwitchedType(true);
                        pieceToChangePositions.setType(Piece.Type.KING);
                    }
                }
                else{
                    if(endrow == 7 && pieceToChangePositions.getType() == Piece.Type.SINGLE){
                        move.setSwitchedType(true);
                        pieceToChangePositions.setType(Piece.Type.KING);
                    }
                }
                startSpace.setPiece(null);
                endSpace.setPiece(pieceToChangePositions);

                int capRow = endrow - ((endrow - startrow)/2);
                int capCell = endcell - ((endcell - startcell)/2);
                Space capturedSpace = whiteBoard.getSpaceInRowList(capRow, capCell);
                move.setCapturedPiece(capturedSpace.getPiece());
                capturedSpace.setPiece(null);
                if(activeColor == Player.Color.RED){
                    whiteBoard.removePiece(Player.Color.WHITE);
                }
                else{
                    whiteBoard.removePiece(Player.Color.RED);
                }

                if(undoingMove){
                    if(move.getSwitchedType()){
                        pieceToChangePositions.setType(Piece.Type.SINGLE);
                    }
                    moveHistory.remove(moveHistory.size() - 1);
                    curTurnMoves.remove(curTurnMoves.size() - 1);
                }

                return "valid";
            }
        }
        else{
            if(player == redPlayer){

                // change the piece location in the red board
                int startrow = 0;
                int endrow = 0;
                int startcell = 0;
                int endcell = 0;
                if(player.getColor() != activeColor){
                    startrow = (Math.abs(start.getRow() - 8) -1);
                    endrow = (Math.abs(end.getRow() - 8) -1);
                    startcell = (Math.abs(start.getCell() - 8) -1);
                    endcell = (Math.abs(end.getCell() - 8) -1);
                }
                else{
                    startrow = start.getRow();
                    endrow = end.getRow();
                    startcell = start.getCell();
                    endcell = end.getCell();
                }

                Space startSpace = redBoard.getSpaceInRowList(startrow, startcell);
                //LOG.fine("start: " + startSpace.getPiece() + " row: " + start.getRow() + " col: " + start.getCell());
                Space endSpace = redBoard.getSpaceInRowList(endrow, endcell);
                Piece pieceToChangePositions = startSpace.getPiece();
                if(player.getColor() == activeColor){
                    if(endrow == 0 && pieceToChangePositions.getType() == Piece.Type.SINGLE){
                        move.setSwitchedType(true);
                        pieceToChangePositions.setType(Piece.Type.KING);
                    }
                }
                else{
                    if(endrow == 7 && pieceToChangePositions.getType() == Piece.Type.SINGLE){
                        move.setSwitchedType(true);
                        pieceToChangePositions.setType(Piece.Type.KING);
                    }
                }
                startSpace.setPiece(null);
                endSpace.setPiece(pieceToChangePositions);
                //LOG.fine("end: " + endSpace.getPiece() + " row: " + end.getRow() + " col: " + end.getCell());

                if(undoingMove){
                    if(move.getSwitchedType()){
                        pieceToChangePositions.setType(Piece.Type.SINGLE);
                    }
                    moveHistory.remove(moveHistory.size() - 1);
                    curTurnMoves.remove(curTurnMoves.size() - 1);
                }

                return "valid";

            } else if (player == whitePlayer) {

                // change the piece location in the white board
                int startrow = 0;
                int endrow = 0;
                int startcell = 0;
                int endcell = 0;
                if(player.getColor() != activeColor){
                    startrow = (Math.abs(start.getRow() - 8) -1);
                    endrow = (Math.abs(end.getRow() - 8) -1);
                    startcell = (Math.abs(start.getCell() - 8) -1);
                    endcell = (Math.abs(end.getCell() - 8) -1);
                }
                else{
                    startrow = start.getRow();
                    endrow = end.getRow();
                    startcell = start.getCell();
                    endcell = end.getCell();
                }

                Space startSpace = whiteBoard.getSpaceInRowList(startrow, startcell);
                Space endSpace = whiteBoard.getSpaceInRowList(endrow, endcell);
                Piece pieceToChangePositions = startSpace.getPiece();
                if(player.getColor() == activeColor){
                    if(endrow == 0 && pieceToChangePositions.getType() == Piece.Type.SINGLE){
                        move.setSwitchedType(true);
                        pieceToChangePositions.setType(Piece.Type.KING);
                    }
                }
                else{
                    if(endrow == 7 && pieceToChangePositions.getType() == Piece.Type.SINGLE){
                        move.setSwitchedType(true);
                        pieceToChangePositions.setType(Piece.Type.KING);
                    }
                }
                startSpace.setPiece(null);
                endSpace.setPiece(pieceToChangePositions);

                if(undoingMove){
                    if(move.getSwitchedType()){
                        pieceToChangePositions.setType(Piece.Type.SINGLE);
                    }
                    moveHistory.remove(moveHistory.size() - 1);
                    curTurnMoves.remove(curTurnMoves.size() - 1);
                }

                return "valid";
            }
        }
        if(undoingMove){
            moveHistory.remove(moveHistory.size() - 1);
            curTurnMoves.remove(curTurnMoves.size() - 1);
        }

        return "valid";

    }

    /**
     * This method is used to manually end the game.
     * @param color the color of the player who won.
     */
    public void endTheGame(Player.Color color){
        if(color == Player.Color.RED){
            redBoard.endTheGame(Player.Color.WHITE);
            whiteBoard.endTheGame(Player.Color.WHITE);
        }
        else{
            redBoard.endTheGame(Player.Color.RED);
            whiteBoard.endTheGame(Player.Color.RED);
        }
    }

    public GameModel copyGame(GameModel gameToCopy){
        GameModel newGame = new GameModel(gameToCopy.redPlayer, gameToCopy.whitePlayer, gameToCopy.viewMode, gameToCopy.gameID);
        newGame.redPlayer = gameToCopy.redPlayer;
        newGame.whitePlayer = gameToCopy.whitePlayer;
        newGame.activeColor = gameToCopy.activeColor;
        newGame.redBoard = gameToCopy.redBoard;
        newGame.whiteBoard = gameToCopy.whiteBoard;
        newGame.viewMode = gameToCopy.viewMode;
        newGame.gameID = gameToCopy.gameID;
        newGame.dMove = gameToCopy.dMove;
        newGame.isFirstMove = gameToCopy.isFirstMove;
        newGame.resignedColor = gameToCopy.resignedColor;
        newGame.undoingMove = gameToCopy.undoingMove;
        newGame.moveHistory = gameToCopy.moveHistory;
        newGame.curTurnMoves = gameToCopy.curTurnMoves;
        return newGame;
    }

    public void resetGame(){
        this.redBoard = new BoardView(Player.Color.RED);
        this.whiteBoard = new BoardView(Player.Color.WHITE);
        this.activeColor = Player.Color.RED;
    }

}
