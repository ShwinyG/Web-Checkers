/*
Filename: PostSignInRoutee.java
Java_Version: 8 - current
Apache_Maven_Version: 3.6.2
Spark_Version: 2.8.0
 */

package com.webcheckers.ui;

import com.webcheckers.Model.GameModel;
import com.webcheckers.Model.LobbyModel;
import com.webcheckers.Model.Player;
import com.webcheckers.appl.GameCenter;
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
 * This is the Route class that logs in the player and redirects them back to
 * the home page afterwards. It is used as a POST route in this program.
 */
public class PostSignInRoute implements Route {
    /** This is the Logger object that is used to record server info in the terminal window */
    private static final Logger LOG = Logger.getLogger(PostSignInRoute.class.getName());
    /** This is the TemplateEngine used to render the screen */
    private final TemplateEngine templateEngine;

    // These are a bunch of attribute names and a couple String and Message messages.
    static final String TITLE_ATTR = "title";
    static final String TITLE = "Web Checkers Sign in Page";
    static final String VIEW_NAME = "signin.ftl";
    static final String NUMPLAYERS_ATTR = "numPlayers";
    static final String ACTIVE_ATTR = "activeGames";
    static final String WAITING_ATTR = "waitingPlayers";
    static final String CUR_USER = "currentUser";
    static final String MSG_ATTR = "message";
    static final String PLAYER_NAMES_ATTR = "playerNames";
    public static final Message SIGNED_IN_MSG = Message.info("You are now signed in!");
    public static final Message NOT_SIGNED_IN_MSG = Message.info("Invalid username request, please choose something else");

    /**
     * This is the Constructor for PostSignInRoute.
     * @param templateEngine the HTML template rendering engine.
     */
    public PostSignInRoute(final TemplateEngine templateEngine) {
        this.templateEngine = Objects.requireNonNull(templateEngine, "templateEngine is required");
    }

    /**
     * This is the handle method for this route, that gets invoked when post() is called
     * in WebServer with this object as a parameter.
     * @param request  the HTTP request
     * @param response the HTTP response
     * @return If the user name is valid, then the rendered HTML for the Home page.
     * Otherwise, the rendered HTML for the Sing-in page.
     */
    @Override
    public Object handle(Request request, Response response) {
        final Session httpSession = request.session();
        final PlayerServices playerServices = httpSession.attribute(GetHomeRoute.PLAYERSERVICES_KEY);
        Map<String, Object> vm = new HashMap<>();
        final String userName = request.queryParams("id");

        // make a player using the username given as id
        Player user = new Player(userName);

        if (playerServices != null) {
            // bool to check if the player was added
            boolean playerAdded = LobbyModel.addPlayer(user);
            // make this new player the current player
            httpSession.attribute("user", user);
            // log that a user was added
            LOG.fine("player " + user.getName() + " added: " + playerAdded);

            // if the player was not added (the username was invalid):
            if (!playerAdded) {
                vm.put(TITLE_ATTR, TITLE);
                // display a user message that the username is invalid in the Home page
                vm.put(MSG_ATTR, NOT_SIGNED_IN_MSG);
                // return to the sign in page so the user can try again
                return templateEngine.render(new ModelAndView(vm, VIEW_NAME));
            }
        }
        vm.put(TITLE_ATTR, TITLE);

        // display a user message in the Home page
        vm.put(MSG_ATTR, SIGNED_IN_MSG);

        int numPlayers = LobbyModel.getNumberPlayers();

        if(numPlayers < 2) {        // there are not enough players for a game
            vm.put(NUMPLAYERS_ATTR, null);
            vm.put(ACTIVE_ATTR, null);
            vm.put(WAITING_ATTR, null);

            LOG.fine("no players");
            String noPlayers = "There are no players available at this time";
            vm.put("inGamePlayerNames", noPlayers);
            vm.put("waitingPlayerNames", noPlayers);
        }
        else { // display the players names in a list
            LOG.fine("I'm in here");
            vm.put(NUMPLAYERS_ATTR, true);
            String[] playerNamesInGame = LobbyModel.getAllUsers(httpSession.attribute("user"), "inGame");
            String[] playerNamesNotInGame = LobbyModel.getAllUsers(httpSession.attribute("user"), "notInGame");

            if(playerNamesInGame[0] == null){
                vm.put(ACTIVE_ATTR, null);
                vm.put("inGamePlayerNames", "No players in game at the moment");
            }
            else{
                vm.put(ACTIVE_ATTR, true);
                vm.put("inGamePlayerNames", playerNamesInGame);
            }

            if(playerNamesNotInGame[0] == null){
                vm.put(WAITING_ATTR, null);
                LOG.fine("huh");
                vm.put("waitingPlayerNames", "No players are not in game at the moment");
            }
            else{
                vm.put(WAITING_ATTR, true);
                vm.put("waitingPlayerNames", playerNamesNotInGame);
            }

        }
        vm.put(CUR_USER, user);
        try {
            GameCenter gameCenter = httpSession.attribute(GetHomeRoute.GAMECENTER_KEY);
            GameModel[] archiveList = gameCenter.getArchives();
            vm.put("archiveGames", archiveList);
        }catch(NullPointerException e){
            LOG.fine("null pointer");
        }

        // render the View
        return templateEngine.render(new ModelAndView(vm , GetHomeRoute.VIEW_NAME));
    }
}
