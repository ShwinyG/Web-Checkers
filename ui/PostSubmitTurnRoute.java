/*
Filename: PostSubmitTurnRoute.java
Java_Version: 8 - current
Apache_Maven_Version: 3.6.2
Spark_Version: 2.8.0
 */

package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.InvalidMoveException;
import com.webcheckers.Model.Player;
import com.webcheckers.Model.Move;
import com.webcheckers.appl.GameCenter;
import com.webcheckers.util.Message;
import spark.*;

import java.util.Objects;
import java.util.logging.Logger;

/**
 * @author Ashwin Gowda
 * @author Garrett Rudolfs
 * @author Joseph Becker
 * @author Julie Sojkowski
 *
 * This is the Route class that is used to submit a turn in the game.
 * It is used as a POST route in this program.
 */
public class PostSubmitTurnRoute implements Route {
    /** This is the Logger object that is used to record server info in the terminal window */
    private static final Logger LOG = Logger.getLogger(PostSubmitTurnRoute.class.getName());
    /** This is the java to json string converter */
    private final Gson gson;
    /** The color of the player who's turn it has become */
    private String color;

    /**
     * This is the Constructor for PostSubmitTurnRoute.
     * @param gson the java to json string converter.
     */
    public PostSubmitTurnRoute(final Gson gson) {
        this.gson = Objects.requireNonNull(gson, "gson is required");
    }

    /**
     * This is the handle method for this route, that gets invoked when post() is called
     * in WebServer with this object as a parameter.
     * @param request  the HTTP request
     * @param response the HTTP response
     * @return a Message object. INFO type if it is successful,
     * and ERROR type if an error occurs.
     */
    @Override
    public Object handle(Request request, Response response){
        final Session httpSession = request.session();
        final GameCenter gameCenter = httpSession.attribute(GetHomeRoute.GAMECENTER_KEY);
        Player user = httpSession.attribute("user");

        try {
            if(gameCenter.getGame(user.getGameID()).getdMove()){
                throw new InvalidMoveException(" You are in the middle of a double move.");
            }
            if(user.getColor() == Player.Color.RED) {
                for(Move move: gameCenter.getGame(user.getGameID()).getCurTurnMoves()){
                    gameCenter.getGame(user.getGameID()).makeMove(move, gameCenter.getGame(user.getGameID()).getWhitePlayer());
                }
                gameCenter.getGame(user.getGameID()).resetCurTurnMoves();
                gameCenter.getGame(user.getGameID()).setdMove(false);
                gameCenter.getGame(user.getGameID()).setIsFirstMove(true);

                gameCenter.getGame(user.getGameID()).setActiveColor(Player.Color.WHITE);
                color = "White";
            }
            else{
                for(Move move: gameCenter.getGame(user.getGameID()).getCurTurnMoves()){
                    gameCenter.getGame(user.getGameID()).makeMove(move, gameCenter.getGame(user.getGameID()).getRedPlayer());
                }
                gameCenter.getGame(user.getGameID()).resetCurTurnMoves();
                gameCenter.getGame(user.getGameID()).setdMove(false);
                gameCenter.getGame(user.getGameID()).setIsFirstMove(true);

                gameCenter.getGame(user.getGameID()).setActiveColor(Player.Color.RED);
                color = "Red";


            }

            LOG.fine("num pieces red:" + gameCenter.getGame(user.getGameID()).getActiveBoardView().getRedPlayerPieces() + " white: " +
                    gameCenter.getGame(user.getGameID()).getActiveBoardView().getWhitePlayerPieces());

            for(Player p : gameCenter.getGame(user.getGameID()).getSpectators()){
                p.setNewMove(true);
            }
            gameCenter.getGame(user.getGameID()).setFirstTurnSubmitted(true);
            gameCenter.getGame(user.getGameID()).setLastMoveTime(System.currentTimeMillis());

            // returns that it is a valid move
            return this.gson.toJson(Message.info("It is now the " + color + "Player's turn."));
        }
        catch(InvalidMoveException e){
            //returns that it is an invalid move
            return this.gson.toJson(Message.error("This is an invalid move."+ e.getMessage()));
        }
    }
}