/*
Filename: GetGameRoute.java
Java_Version: 8 - current
Apache_Maven_Version: 3.6.2
Spark_Version: 2.8.0
 */

package com.webcheckers.ui;

import static spark.Spark.halt;

import com.google.gson.Gson;
import com.webcheckers.Model.GameModel;
import com.webcheckers.Model.Player;
import com.webcheckers.appl.GameCenter;
import com.webcheckers.appl.PlayerServices;
import spark.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author Ashwin Gowda
 * @author Garrett Rudolfs
 * @author Joseph Becker
 * @author Julie Sojkowski
 *
 * This is the Route used to get the game and render it on the server.
 * It is used as a GET route in this program.
 */
public class GetGameRoute implements Route{
    /** This is the Logger object that is used to record server info in the terminal window */
    private static final Logger LOG = Logger.getLogger(GetGameRoute.class.getName());
    /** This keeps track of the number of games */
    static int numGames = 0;
    /** This is the TemplateEngine used to render the screen */
    private final TemplateEngine templateEngine;
    /** This is the java to json string converter */
    private final Gson gson;

    // These are a bunch of attribute names and a couple String messages.
    static final String VIEW_NAME = "game.ftl";
    static final String GAME_ID = "gameID";
    static final String TITLE_ATTR = "title";
    static final String WELCOME_MES = "Welcome!";
    static final String CUR_USER = "currentUser";
    static final String VIEW_MODE = "viewMode";
    static final String RED_PLAYER = "redPlayer";
    static final String WHITE_PLAYER = "whitePlayer";
    static final String ACTIVE_COLOR = "activeColor";
    static final String BOARD = "board";
    static final String MODE_ATTR = "modeOptionsAsJSON";
    static final String GAME_OVER_ATTR = "isGameOver";
    static final String GAME_OVER_MES = "gameOverMessage";

    /**
     * This is the Constructor for GetGameRoute.
     * @param templateEngine the HTML template rendering engine.
     * @param gson the java to json string converter.
     */
    public GetGameRoute(final TemplateEngine templateEngine, final Gson gson) {
        this.templateEngine = Objects.requireNonNull(templateEngine, "templateEngine is required");
        this.gson = Objects.requireNonNull(gson, "gson is required");
        LOG.config("GetGameRoute is initialized.");
    }

    /**
     * Render the WebCheckers Game page.
     * @param request the Request object.
     * @param response the Response object.
     * @return If successful, the rendered HTML for the Game page, otherwise null.
     */
    @Override
    public Object handle(Request request, Response response) {
        // retrieve the game object and start one if no game is in progress
        final Session httpSession = request.session();
        final PlayerServices playerServices = httpSession.attribute(GetHomeRoute.PLAYERSERVICES_KEY);
        final GameCenter gameCenter = httpSession.attribute(GetHomeRoute.GAMECENTER_KEY);
        Player user = httpSession.attribute("user");

        final Map<String, Object> vm = new HashMap<>();
        if(playerServices != null) {
            if (user.getGameID() == 0) {
                if (user.getIsInGame()) {
                    user.setIsInGame();
                    response.redirect("/");
                    halt();
                    return null;
                }

                Set<String> querySet = request.queryParams();
                Player opPlayer = null;
                for (String name : querySet) {
                    String[] namesParam = name.split(" vs. ");
                    opPlayer = playerServices.currentLobby().getPlayer(namesParam[0]);
                    if(opPlayer.getIsInGame()){
                        user.setJustEnteredFullGame();
                        user.setGameID(opPlayer.getGameID());

                        response.redirect(WebServer.SPECTATEGAME_URL);
                        halt();
                    }
                    opPlayer.setIsInGame();
                    break;
                }

                if(opPlayer == null) {
                    response.redirect("/");
                    halt();
                    return null;
                }

                user.setColor(Player.Color.RED);
                opPlayer.setColor(Player.Color.WHITE);

                numGames = numGames + 1;

                GameModel game = gameCenter.theGame(user, opPlayer, numGames);

                user.setIsInGame();

                user.setGameID(game.getGameID());
                opPlayer.setGameID(game.getGameID());

                vm.put(GAME_ID, game.getGameID());
                vm.put(TITLE_ATTR, WELCOME_MES);
                vm.put(CUR_USER, user);
                vm.put(VIEW_MODE, gameCenter.getGame(game.getGameID()).getViewMode());
                vm.put(RED_PLAYER, user);
                vm.put(WHITE_PLAYER, opPlayer);
                vm.put(ACTIVE_COLOR, Player.Color.RED);
                vm.put(BOARD, gameCenter.getGame(game.getGameID()).getRedBoard());
            } else {
                if (!gameCenter.getGame(user.getGameID()).getWhitePlayer().equals(user) &&
                        !gameCenter.getGame(user.getGameID()).getRedPlayer().equals(user)) {
                    LOG.fine("Both the white and red players are not equal to this user");
                    user.setJustEnteredFullGame();
                    //change so you enter spectator mode
                    user.setGameID(gameCenter.getGame(user.getGameID()).getWhitePlayer().getGameID());
                    response.redirect(WebServer.SPECTATEGAME_URL);
                }

                if(!user.getIsInGame()){
                    user.setIsInGame();
                }

                vm.put(GAME_ID, user.getGameID());
                vm.put(TITLE_ATTR, WELCOME_MES);
                vm.put(CUR_USER, user);
                vm.put(VIEW_MODE, gameCenter.getGame(user.getGameID()).getViewMode());
                vm.put(RED_PLAYER, gameCenter.getGame(user.getGameID()).getRedPlayer());
                vm.put(WHITE_PLAYER, gameCenter.getGame(user.getGameID()).getWhitePlayer());
                vm.put(ACTIVE_COLOR, gameCenter.getGame(user.getGameID()).getActiveColor());
                if (user.getColor() == Player.Color.RED) {
                    vm.put(BOARD, gameCenter.getGame(user.getGameID()).getRedBoard());
                } else {
                    vm.put(BOARD, gameCenter.getGame(user.getGameID()).getWhiteBoard());
                }
            }
            // This is where the end game functionality starts.
            if (gameCenter.getGame(user.getGameID()).playerWon() != null) {
                LOG.fine("get game route: player won = " + gameCenter.getGame(user.getGameID()).playerWon());

                final Map<String, Object> modeOptions = new HashMap<>(2);
                String winner = gameCenter.getGame(user.getGameID()).playerWon();
                String endGameMsg = "player " + winner + " has captured all pieces!";
                gameCenter.archiveGame(user.getGameID());
                modeOptions.put(GAME_OVER_ATTR, true);
                modeOptions.put(GAME_OVER_MES, endGameMsg);
                vm.put(MODE_ATTR, gson.toJson(modeOptions));
            }
            else if(gameCenter.getGame(user.getGameID()).playerResigned() != null){

                final Map<String, Object> modeOptions = new HashMap<>(2);
                String loser;
                if(gameCenter.getGame(user.getGameID()).playerResigned() == Player.Color.RED){
                    loser = gameCenter.getGame(user.getGameID()).getRedPlayer().getName();
                }
                else{
                    loser = gameCenter.getGame(user.getGameID()).getWhitePlayer().getName();
                }
                String endGameMsg = "player " + loser + " has resigned!";
                gameCenter.archiveGame(user.getGameID());
                modeOptions.put(GAME_OVER_ATTR, true);
                modeOptions.put(GAME_OVER_MES, endGameMsg);
                vm.put(MODE_ATTR, gson.toJson(modeOptions));
            }
            return templateEngine.render(new ModelAndView(vm, VIEW_NAME));
        }
        else{
            response.redirect(WebServer.HOME_URL);
            halt();
            return null;
        }
    }
}
