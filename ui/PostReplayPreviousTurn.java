/*
Filename: PostReplayPreviousTurn.java
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
import com.webcheckers.appl.ReplayModeController;
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
 * This is the Route class that is used to move backwards a turn in replay mode.
 * It is used as a POST route in this program.
 */
public class PostReplayPreviousTurn implements Route {
    /**
     * This is the Logger object that is used to record server info in the terminal window
     */
    private static final Logger LOG = Logger.getLogger(PostSubmitTurnRoute.class.getName());
    /**
     * This is the java to json string converter
     */
    private final Gson gson;
    /**
     * The color of the player who's turn it has become
     */
    private String color;

    static final String CONTROLLER_KEY = "replayController";

    /**
     * This is the Constructor for PostReplayPreviousTurn.
     *
     * @param gson the java to json string converter.
     */
    public PostReplayPreviousTurn(final Gson gson) {
        this.gson = Objects.requireNonNull(gson, "gson is required");
    }

    /**
     * This is the handle method for this route, that gets invoked when post() is called
     * in WebServer with this object as a parameter.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @return a Message object. INFO type if it is successful,
     * and ERROR type if an error occurs.
     */
    @Override
    public Object handle(Request request, Response response) {
        final Session httpSession = request.session();
        ReplayModeController controls = httpSession.attribute(CONTROLLER_KEY);

        if(controls.getTurnCounter() > 0){
            controls.undoMove(controls.getGame().getActiveColor());
            return this.gson.toJson(Message.info("true"));
        }
        else{
            return this.gson.toJson(Message.info("false"));
        }
    }
}

