/*
Filename: ResignGameRoute.java
Java_Version: 8 - current
Apache_Maven_Version: 3.6.2
Spark_Version: 2.8.0
 */

package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.Model.Player;
import com.webcheckers.appl.GameCenter;
import com.webcheckers.appl.PlayerServices;
import com.webcheckers.util.Message;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

import java.util.Objects;

/**
 * @author Ashwin Gowda
 * @author Garrett Rudolfs
 * @author Joseph Becker
 * @author Julie Sojkowski
 *
 * This is the Route class that is used to resign from a game.
 * It is used as a POST route in this program.
 */
public class ResignGameRoute implements Route{
    /** This is the java to json string converter */
    private final Gson gson;

    /**
     * This is the Constructor for ResignGameRoute
     * @param gson the java to json string converter.
     */
    public ResignGameRoute(final Gson gson) {
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

        if(gameCenter.getGame(user.getGameID()).playerResigned() == null){
            gameCenter.getGame(user.getGameID()).resign(user.getColor());
        }
        else{
            gameCenter.archiveGame(user.getGameID());
        }

        return this.gson.toJson(Message.info("You have resigned from the game."));
    }
}
