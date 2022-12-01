package com.webcheckers.appl;

import com.webcheckers.InvalidMoveException;
import com.webcheckers.Model.*;

import java.util.logging.Logger;

public class ReplayModeController {
    /** This is the Logger object that is used to record server info in the terminal window */
    private static final Logger LOG = Logger.getLogger(ReplayModeController.class.getName());
    /** This is the game being replayed on the Web Server */
    private GameModel game;
    private int turnCounter;
    private Move lastMessedWithMove;

    /**
     * Construct a new ReplayModeController object.
     * @param game the game being replayed
     */
    public ReplayModeController(GameModel game) {
        this.game = game;
        this.turnCounter = 0;
    }

    /**
     * gets the current game model
     * @return the GameModel with the supplied identifier.
     */
    public synchronized GameModel getGame(){
        return this.game;
    }

    public void increaseTurn(){
        this.turnCounter++;
    }

    public void decreaseTurn(){
        this.turnCounter--;
    }

    public int getTurnCounter(){
        return turnCounter;
    }

    public void undoMove(Player.Color activeColor){
        try{
            if(game.getActiveColor() == Player.Color.RED){
                game.setActiveColor(Player.Color.WHITE);
            }
            else{
                game.setActiveColor(Player.Color.RED);
            }
            while(true){
                Move hisMove = game.getMoveReplay(turnCounter - 1);
                Move undo = new Move(hisMove.getEnd(), hisMove.getStart());
                this.game.makeMove(undo, game.getRedPlayer());
                this.game.makeMove(undo, game.getWhitePlayer());

                if(hisMove.getSwitchedType()){
                    if(game.getActiveColor() == Player.Color.RED){
                        this.game.getRedBoard().getRow(undo.getEnd().getRow()).getSpace(undo.getEnd().getCell()).getPiece().setType(Piece.Type.SINGLE);
                        int endrow = (Math.abs(undo.getEnd().getRow() - 8) -1);
                        int endcell = (Math.abs(undo.getEnd().getCell() - 8) -1);
                        this.game.getWhiteBoard().getRow(endrow).getSpace(endcell).getPiece().setType(Piece.Type.SINGLE);
                    }
                    else{
                        this.game.getWhiteBoard().getRow(undo.getEnd().getRow()).getSpace(undo.getEnd().getCell()).getPiece().setType(Piece.Type.SINGLE);
                        int endrow = (Math.abs(undo.getEnd().getRow() - 8) -1);
                        int endcell = (Math.abs(undo.getEnd().getCell() - 8) -1);
                        this.game.getRedBoard().getRow(endrow).getSpace(endcell).getPiece().setType(Piece.Type.SINGLE);
                    }
                }

                if(undo.isCaptureMove()){
                    int capRow = undo.getEnd().getRow() - ((undo.getEnd().getRow() - undo.getStart().getRow())/2);
                    int capCell = undo.getEnd().getCell() - ((undo.getEnd().getCell() - undo.getStart().getCell())/2);
                    if(game.getActiveColor() == Player.Color.RED){
                        Space capturedSpaceRed = game.getRedBoard().getSpaceInRowList(capRow, capCell);
                        capRow = (Math.abs(capRow - 8) -1);
                        capCell = (Math.abs(capCell - 8) -1);
                        Space capturedSpaceWhite = game.getWhiteBoard().getSpaceInRowList(capRow, capCell);
                        capturedSpaceRed.setPiece(hisMove.getCapturedPiece());
                        capturedSpaceWhite.setPiece(hisMove.getCapturedPiece());
                    }
                    else{
                        Space capturedSpaceWhite = game.getWhiteBoard().getSpaceInRowList(capRow, capCell);
                        capRow = (Math.abs(capRow - 8) -1);
                        capCell = (Math.abs(capCell - 8) -1);
                        Space capturedSpaceRed = game.getRedBoard().getSpaceInRowList(capRow, capCell);
                        capturedSpaceRed.setPiece(hisMove.getCapturedPiece());
                        capturedSpaceWhite.setPiece(hisMove.getCapturedPiece());
                    }
                    game.getRedBoard().addPiece(activeColor);
                    game.getWhiteBoard().addPiece(activeColor);
                }
                turnCounter--;
                if(!game.getMoveReplay(turnCounter).getIsFirstMove()){
                    continue;
                }
                this.lastMessedWithMove = hisMove;
                break;
            }
        }
        catch(InvalidMoveException e){
            LOG.finer("error: " + e.getMessage());
        }
    }

    public void redoMove(Player.Color activeColor){
        try{
            while(true){
                turnCounter++;
                this.game.makeMove(game.getMoveReplay(turnCounter - 1), game.getRedPlayer());
                this.game.makeMove(game.getMoveReplay(turnCounter - 1), game.getWhitePlayer());
                if(game.getMoveReplay(turnCounter - 1).getCurrentdMove()){
                    continue;
                }
                lastMessedWithMove = game.getMoveReplay(turnCounter - 1);
                break;
            }
            if(game.getActiveColor() == Player.Color.RED){
                game.setActiveColor(Player.Color.WHITE);
            }
            else{
                game.setActiveColor(Player.Color.RED);
            }
        }
        catch(InvalidMoveException e){
            LOG.finer("error: " + e.getMessage());
        }
    }

    public Move getLastMessedWithMove(){
        return this.lastMessedWithMove;
    }
}
