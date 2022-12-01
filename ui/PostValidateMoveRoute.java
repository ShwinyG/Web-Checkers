/*
Filename: PostValidateMoveRoute.java
Java_Version: 8 - current
Apache_Maven_Version: 3.6.2
Spark_Version: 2.8.0
 */

package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.InvalidMoveException;
import com.webcheckers.Model.Move;
import com.webcheckers.Model.Player;
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
 * This is the Route class that is used to validate a move in the game.
 * It is used as a POST route in this program.
 */
public class PostValidateMoveRoute implements Route{
    /** This is the Logger object that is used to record server info in the terminal window */
    private static final Logger LOG = Logger.getLogger(PostValidateMoveRoute.class.getName());
    /** This is the java to json string converter */
    private final Gson gson;

    static final String MOVE_PARAM = "actionData";
    static final String USER_PARAM= "user";

    /**
     * This is the Constructor for PostValidateMoveRoute.
     * @param gson the java to json string converter.
     */
    public PostValidateMoveRoute(final Gson gson) {
        this.gson = Objects.requireNonNull(gson, "gson is required");
    }

    /**
     * This is the handle method for this route, that gets invoked when post() is called
     * in WebServer with this object as a parameter.
     * @param request  the HTTP request
     * @param response the HTTP response
     * @return a Message object. INFO type if it is successful,
     * and ERROR type if an error or invalid move occurs.
     */
    @Override
    public Object handle(Request request, Response response){
        final Session httpSession = request.session();
        final GameCenter gameCenter = httpSession.attribute(GetHomeRoute.GAMECENTER_KEY);
        Player user = httpSession.attribute("user");

        Move move = gson.fromJson(request.queryParams("actionData"), Move.class);

        if(move == null){
            LOG.fine("The Move is null");
        }
        try {
            for(Player p : gameCenter.getGame(user.getGameID()).getSpectators()){
                p.setNewMove(false);
            }
            move.setCurState(gameCenter.getGame(user.getGameID()).getdMove(), gameCenter.getGame(user.getGameID()).getIsFirstMove());
            if(move.isValid(gameCenter.getGame(user.getGameID()), gameCenter.getGame(user.getGameID()).getActiveBoardView())){
                gameCenter.getGame(user.getGameID()).makeMove(move, httpSession.attribute("user"));
                gameCenter.getGame(user.getGameID()).addMove(move, httpSession.attribute("user"));
                return this.gson.toJson(Message.info("This is a valid move."));
            }
        }
        catch(InvalidMoveException e){
            return this.gson.toJson(Message.error("This is an invalid move."+ e.getMessage()));
        }

        catch(NullPointerException e){
            if(move == null){
                return this.gson.toJson(Message.error("This move is NULL for some reason?"));
            }
            else if(gameCenter.getGame(user.getGameID()) == null){
                return this.gson.toJson(Message.error("This game is NULL for some reason?"));
            }
            return this.gson.toJson(Message.error("This something is NULL for some reason?"));

        }

        return this.gson.toJson(Message.error("This is an invalid move."));
    }
}


