package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.Model.Player;
import com.webcheckers.appl.GameCenter;
import com.webcheckers.appl.PlayerServices;
import spark.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import static com.webcheckers.ui.GetGameRoute.*;

public class GetSpectatorGameRoute implements Route {
    /** This is the Logger object that is used to record server info in the terminal window */
    private static final Logger LOG = Logger.getLogger(GetSpectatorGameRoute.class.getName());
    /** This is the TemplateEngine used to render the screen */
    private final TemplateEngine templateEngine;
    /** This is the java to json string converter */
    private final Gson gson;
    static final String VIEW_NAME = "game.ftl";
    static final String VIEW_MODE = "viewMode";

    /**
     * This is the Constructor for GetGameRoute.
     * @param templateEngine the HTML template rendering engine.
     * @param gson the java to json string converter.
     */
    public GetSpectatorGameRoute(final TemplateEngine templateEngine, final Gson gson) {
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
        if (playerServices != null) {
            user.setViewMode(Player.Mode.SPECTATOR);
            if(!gameCenter.getGame(user.getGameID()).addSpectator(user)){
                LOG.fine("player: " + user.getName() + " could not be added to spectator list");
            }

            vm.put(GAME_ID, user.getGameID());
            vm.put(TITLE_ATTR, WELCOME_MES);
            vm.put(CUR_USER, user);
            vm.put(VIEW_MODE, user.getViewMode());
            vm.put(RED_PLAYER, gameCenter.getGame(user.getGameID()).getRedPlayer());
            vm.put(WHITE_PLAYER, gameCenter.getGame(user.getGameID()).getWhitePlayer());
            vm.put(ACTIVE_COLOR, gameCenter.getGame(user.getGameID()).getActiveColor());
            vm.put(BOARD, gameCenter.getGame(user.getGameID()).getRedBoard());

            // end game if player won / lost / resigned
            if (gameCenter.getGame(user.getGameID()).playerWon() != null) {
                LOG.fine("get game route: player won = " + gameCenter.getGame(user.getGameID()).playerWon());

                final Map<String, Object> modeOptions = new HashMap<>(2);
                String winner = gameCenter.getGame(user.getGameID()).playerWon();
                String endGameMsg = "player " + winner + " has captured all pieces!";
                modeOptions.put(GAME_OVER_ATTR, true);
                modeOptions.put(GAME_OVER_MES, endGameMsg);
                vm.put(MODE_ATTR, gson.toJson(modeOptions));
            }
            else if (gameCenter.getGame(user.getGameID()).playerResigned() != null) {

                final Map<String, Object> modeOptions = new HashMap<>(2);
                String loser;
                if (gameCenter.getGame(user.getGameID()).playerResigned() == Player.Color.RED) {
                    loser = gameCenter.getGame(user.getGameID()).getRedPlayer().getName();
                } else {
                    loser = gameCenter.getGame(user.getGameID()).getWhitePlayer().getName();
                }
                String endGameMsg = "player " + loser + " has resigned!";
                modeOptions.put(GAME_OVER_ATTR, true);
                modeOptions.put(GAME_OVER_MES, endGameMsg);
                vm.put(MODE_ATTR, gson.toJson(modeOptions));
            }
        }
        user.setNewMove(false);
        return templateEngine.render(new ModelAndView(vm, VIEW_NAME));
    }
}
