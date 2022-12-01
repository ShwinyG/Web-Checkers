/*
Filename: PostSignOutRoute.java
Java_Version: 8 - current
Apache_Maven_Version: 3.6.2
Spark_Version: 2.8.0
 */

package com.webcheckers.ui;

import com.webcheckers.Model.LobbyModel;
import com.webcheckers.appl.PlayerServices;
import com.webcheckers.util.Message;
import spark.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * @author Ashwin Gowda
 * @author Garrett Rudolfs
 * @author Joseph Becker
 * @author Julie Sojkowski
 *
 * This is the Route class that logs out the player and redirects them back to
 * the home page afterwards. It is used as a POST route in this program.
 */
public class PostSignOutRoute implements Route {
    /** This is the Logger object that is used to record server info in the terminal window */
    private static final Logger LOG = Logger.getLogger(PostSignOutRoute.class.getName());
    /** This is the TemplateEngine used to render the screen */
    private final TemplateEngine templateEngine;

    // These are a bunch of attribute names and a couple String and Message messages.
    static final String TITLE_ATTR = "title";
    static final String VIEW_NAME = "home.ftl";
    static final String NUMPLAYERS_ATTR = "numPlayers";
    static final String ACTIVE_ATTR = "activeGames";
    static final String WAITING_ATTR = "waitingPlayers";
    static final String MSG_ATTR = "message";
    static final String PLAYER_NAMES_ATTR = "playerNames";
    private static final Message SIGNED_OUT_MSG = Message.info("You are now signed out!");

    /**
     * This is the Constructor for PostSignOutRoute.
     * @param templateEngine the HTML template rendering engine.
     */
    public PostSignOutRoute(final TemplateEngine templateEngine) {
        this.templateEngine = Objects.requireNonNull(templateEngine, "templateEngine is required");
    }

    /**
     * This is the handle method for this route, that gets invoked when post() is called
     * in WebServer with this object as a parameter.
     * @param request  the HTTP request
     * @param response the HTTP response
     * @return the rendered HTML for the Home page
     */
    @Override
    public Object handle(Request request, Response response) {
        final Session httpSession = request.session();
        final PlayerServices playerServices = httpSession.attribute(GetHomeRoute.PLAYERSERVICES_KEY);
        Map<String, Object> vm = new HashMap<>();

        // remove the player from the game
        LobbyModel.removePlayer(httpSession.attribute("user"));
        httpSession.removeAttribute("user");
        httpSession.attribute("user", null);
        LOG.fine("player removed: " + true);

        vm.put(TITLE_ATTR, "Welcome!");

        // display a user message in the Home page
        vm.put(MSG_ATTR, SIGNED_OUT_MSG);

        int numPlayers = LobbyModel.getNumberPlayers();

        if (numPlayers < 1) {
            vm.put(NUMPLAYERS_ATTR, null);
            vm.put(ACTIVE_ATTR, null);
            vm.put(WAITING_ATTR, null);

            vm.put("inGamePlayerNames", "There are no players available at this time");
            vm.put("waitingPlayerNames", "There are no players available at this time");
        }
        else {
            vm.put(NUMPLAYERS_ATTR, null);
            vm.put(ACTIVE_ATTR, null);
            vm.put(WAITING_ATTR, null);

            String playerNames = "There are " + numPlayers + " players available at this time";
            vm.put("inGamePlayerNames", playerNames);
            vm.put("waitingPlayerNames", playerNames);
        }

        String finishedGamesMsg = "You must sign in before you can replay finished games.";
        vm.put("finishedGamesMsg", finishedGamesMsg);

        response.redirect(WebServer.HOME_URL);
        return templateEngine.render(new ModelAndView(vm , VIEW_NAME));
    }
}
