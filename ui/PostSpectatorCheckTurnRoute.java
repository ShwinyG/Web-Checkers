package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.Model.Player;
import com.webcheckers.appl.GameCenter;
import com.webcheckers.util.Message;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static com.webcheckers.ui.WebServer.HOME_URL;

public class PostSpectatorCheckTurnRoute implements Route {
    /** The java to json string converter */
    private final Gson gson;
    private static final Logger LOG = Logger.getLogger(PostSpectatorCheckTurnRoute.class.getName());

    /**
     * This is the Constructor for PostCheckTurnRoute.
     * @param gson the java to json string converter.
     */
    public PostSpectatorCheckTurnRoute(final Gson gson) {
        this.gson = Objects.requireNonNull(gson, "gson is required");
    }

    /**
     * This is the handle method for this route, that gets invoked when post() is called
     * in WebServer with this object as a parameter.
     * @param request  the HTTP request
     * @param response the HTTP response
     * @return a Message object. INFO type with "true" if it the board is changing (new turn or game is over);
     * INFO type with "false" if the board isn't changing; and ERROR type if an error occurs.
     */
    @Override
    public Object handle(Request request, Response response) {
        final Session httpSession = request.session();
        final GameCenter gameCenter = httpSession.attribute(GetHomeRoute.GAMECENTER_KEY);
        Player user = httpSession.attribute("user");

        // if the game is null redirect
        if(gameCenter.getGame(user.getGameID()) == null){
            return this.gson.toJson(Message.info("false"));
        }

         if(gameCenter.getGame(user.getGameID()).playerResigned() == null &&  // no one has resigned
                gameCenter.getGame(user.getGameID()).playerWon() == null){       // no player has won

            // check if game state has changed
            //if (gameCenter.getGame(player.getGameID()).getActiveColor() == player.getColor()) {
            if (user.getNewMove()) {
                user.setNewMove(false);
                return this.gson.toJson(Message.info("true"));
            } else {
                if(!gameCenter.getGame(user.getGameID()).getFirstTurnSubmitted()){
                    return this.gson.toJson(Message.info("Waiting for first move"));
                }
                long time =(System.currentTimeMillis()-gameCenter.getGame(user.getGameID()).getLastMoveTime()) / 1000;
                int minutes = (int) time / 60;

                long seconds = time % 60;
                String message = "Last move was about " + minutes + " minutes and "+ seconds + " seconds ago";
                return this.gson.toJson(Message.info(message));
            }
        }
        else {
            //should change the turn / refresh
             user.setNewMove(false);
            return this.gson.toJson(Message.info("true"));
        }

    }
}
