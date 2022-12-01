/*
Filename: PostCheckTurnRoute.java
Java_Version: 8 - current
Apache_Maven_Version: 3.6.2
Spark_Version: 2.8.0
 */

package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.Model.Player;
import com.webcheckers.appl.GameCenter;
import com.webcheckers.util.Message;
import spark.*;

import java.util.Objects;

/**
 * @author Ashwin Gowda
 * @author Garrett Rudolfs
 * @author Joseph Becker
 * @author Julie Sojkowski
 *
 * This is the Route class that checks if it is the users turn.
 * It is invoked when it is not the current user's turn and the game has not ended.
 * It is used as a POST route in this program.
 */
public class PostCheckTurnRoute implements Route{
    /** The java to json string converter */
    private final Gson gson;

    /**
     * This is the Constructor for PostCheckTurnRoute.
     * @param gson the java to json string converter.
     */
    public PostCheckTurnRoute(final Gson gson) {
        this.gson = Objects.requireNonNull(gson, "gson is required");
    }

    /**
     * This is the handle method for this route, that gets invoked when post() is called
     * in WebServer with this object as a parameter.
     * @param request  the HTTP request
     * @param response the HTTP response
     * @return a Message object. INFO type with "true" if it the turn is changing;
     * INFO type with "false" if the turn isn't changing; and ERROR type if an error occurs.
     */
    @Override
    public Object handle(Request request, Response response) {
        final Session httpSession = request.session();
        final GameCenter gameCenter = httpSession.attribute(GetHomeRoute.GAMECENTER_KEY);
        Player player = httpSession.attribute("user");

        if(gameCenter.getGame(player.getGameID()) != null &&
                gameCenter.getGame(player.getGameID()).playerResigned() == null){
            if (gameCenter.getGame(player.getGameID()).getActiveColor() == player.getColor()) {
                return this.gson.toJson(Message.info("true"));
            } else {
                return this.gson.toJson(Message.info("false"));
            }
        }
        else {
            //should change the turn / refresh
            return this.gson.toJson(Message.info("true"));
        }
    }
}
