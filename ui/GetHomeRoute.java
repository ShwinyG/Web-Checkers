/*
Filename: GetHomeRoute.java
Java_Version: 8 - current
Apache_Maven_Version: 3.6.2
Spark_Version: 2.8.0
 */

package com.webcheckers.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import com.webcheckers.Model.GameModel;
import com.webcheckers.Model.LobbyModel;
import com.webcheckers.Model.Player;
import com.webcheckers.appl.GameCenter;
import com.webcheckers.appl.PlayerServices;
import spark.*;

import com.webcheckers.util.Message;

/**
 * @author Ashwin Gowda
 * @author Garrett Rudolfs
 * @author Joseph Becker
 * @author Julie Sojkowski
 *
 * This is the Route used to get the home page and render it on the server.
 * It is used as a GET route in this program.
 */
public class GetHomeRoute implements Route {
    /** This is the Logger object that is used to record server info in the terminal window */
    private static final Logger LOG = Logger.getLogger(GetHomeRoute.class.getName());
    /** This is the TemplateEngine used to render the screen */
    private final TemplateEngine templateEngine;
    /** This is the PlayerServices controller used to interact with the lobby */
    private final PlayerServices playerServices;
    /** This is the GameCenter controller used to interact with the game */
    private final GameCenter gameCenter;

    // These are a bunch of attribute names and a couple String and Message messages.
    static final String PLAYERSERVICES_KEY = "playerServices";
    static final String GAMECENTER_KEY = "gameCenter";
    static final String TITLE_ATTR = "title";
    static final String MSG_ATTR = "message";
    static final String TITLE = "Welcome to WebCheckers Game!";
    static final String VIEW_NAME = "home.ftl";
    static final String NUMPLAYERS_ATTR = "numPlayers";
    static final String ACTIVE_ATTR = "activeGames";
    static final String WAITING_ATTR = "waitingPlayers";
    static final Message WELCOME_MSG = Message.info("Welcome to the world of online Checkers.");
    private static final Message GAME_FULL_ERROR_MSG = Message.info("Error: Selected Player is already in a game.");

    // SessionTimeoutWactchdog stuff. In seconds.
    static final String TIMEOUT_SESSION_KEY = "timeoutWatchdog";
    static final int SESSION_TIMEOUT_PERIOD = 120;

    /**
     * Create the Spark Route (UI controller) to handle all {@code GET /} HTTP requests.
     *
     * @param templateEngine the HTML template rendering engine
     */
    public GetHomeRoute(final TemplateEngine templateEngine, PlayerServices playerServices,
                      GameCenter gameCenter) {
        this.templateEngine = Objects.requireNonNull(templateEngine, "templateEngine is required");
        this.playerServices = playerServices;
        this.gameCenter = gameCenter;
        LOG.config("GetHomeRoute is initialized.");
    }

    /**
     * Render the WebCheckers Home page.
     * @param request  the HTTP request
     * @param response the HTTP response
     * @return the rendered HTML for the Home page
     */
    @Override
    public Object handle(Request request, Response response) {
        LOG.finer("GetHomeRoute is invoked.");

        // retrieve the HTTP session
        final Session httpSession = request.session();

        // start building the View-Model
        final Map<String, Object> vm = new HashMap<>();
        vm.put(TITLE_ATTR, TITLE);

        // if this is a brand new browser session or a session that timed out
        if (httpSession.attribute(PLAYERSERVICES_KEY) == null) {
            // get the object that will provide client-specific services for this player
            httpSession.attribute(PLAYERSERVICES_KEY, this.playerServices);
            httpSession.attribute(GAMECENTER_KEY, this.gameCenter);

            // setup session timeout. The valueUnbound() method in the SessionTimeoutWatchdog will
            // be called when the session is invalidated. The next invocation of this route will
            // have a new Session object with no attributes.
            // Note: The following may not work and is currently unnecessary, so it is commented out.
            /*
            httpSession.attribute(TIMEOUT_SESSION_KEY, new SessionTimeoutWatchdog(this.playerServices, request.ip()));
            httpSession.maxInactiveInterval(SESSION_TIMEOUT_PERIOD);
            System.out.println("SessionTimeoutWatchdog has been created.");
            httpSession.maxInactiveInterval(10);
            */
        }

        int numPlayers = LobbyModel.getNumberPlayers();
        // current player is logged in
        if (httpSession.attribute("user") != null) {
            for (Player player : playerServices.currentLobby().getPlayerList()) {
                Player thePlayer = httpSession.attribute("user");
                if (0 == player.getName().compareTo(thePlayer.getName())) {
                    vm.put("currentUser", player);
                }
            }
            // This should use player services to get the number of players.
            if (numPlayers < 2) {

                vm.put(NUMPLAYERS_ATTR, null);
                vm.put(ACTIVE_ATTR, null);
                vm.put(WAITING_ATTR, null);

                LOG.fine("It's null here");
                vm.put("inGamePlayerNames", "There are no players available at this time");
                vm.put("waitingPlayerNames", "There are no players available at this time");
            }
            else {
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
                    vm.put("waitingPlayerNames", "No players are not in game at the moment");
                }
                else{
                    vm.put(WAITING_ATTR, true);
                    vm.put("waitingPlayerNames", playerNamesNotInGame);
                }

            }
            Player user = httpSession.attribute("user");

            if (user.getIsInGame()) {
                if(gameCenter.getGame(user.getGameID()).playerResigned() != null ||
                        gameCenter.getGame(user.getGameID()).playerWon() != null){
                    user.setIsInGame();
                    if(!gameCenter.getGame(user.getGameID()).getWhitePlayer().getIsInGame() &&
                            !gameCenter.getGame(user.getGameID()).getRedPlayer().getIsInGame()){
                        gameCenter.endGame(user.getGameID());
                    }
                    user.setGameID(0);
                }
                else{
                    response.redirect("/game");
                }
            }
            /*
            if(user.getJustEnteredFullGame()){
                LOG.fine("Displaying game full error message");
                vm.put(MSG_ATTR, GAME_FULL_ERROR_MSG);
                user.setJustEnteredFullGame();
            }
            else{
            */
            vm.put(MSG_ATTR, WELCOME_MSG);
            //}
            GameModel[] archiveList = gameCenter.getArchives();
            vm.put("archiveGames", archiveList);
        }
        // there is not a current  player
        else {
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
                String[] inGamesList = playerServices.getLobby().getAllUsers(null, "inGame");
                int inGamesNum = inGamesList.length;
                if(inGamesList[0] == null){
                    inGamesNum = 0;
                }
                String inGameNames = "There are " + inGamesNum + " games available at this time";
                vm.put("inGamePlayerNames", inGameNames);

                String[] waitingList = playerServices.getLobby().getAllUsers(null, "notInGame");
                int waitingLen = waitingList.length;
                if(waitingList[0] == null){
                    waitingLen = 0;
                }
                String waitingNames = "There are " + waitingLen + " players available at this time";
                vm.put("waitingPlayerNames", waitingNames);
            }

            vm.put(MSG_ATTR, WELCOME_MSG);

            String finishedGamesMsg = "You must sign in before you can replay finished games.";
            vm.put("finishedGamesMsg", finishedGamesMsg);
        }

        return templateEngine.render(new ModelAndView(vm, VIEW_NAME));
    }
}
