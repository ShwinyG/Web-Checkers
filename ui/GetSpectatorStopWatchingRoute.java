package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.Model.Player;
import com.webcheckers.appl.GameCenter;
import com.webcheckers.appl.PlayerServices;
import spark.*;

import java.util.Objects;
import java.util.logging.Logger;

public class GetSpectatorStopWatchingRoute implements Route {

    /** This is the Logger object that is used to record server info in the terminal window */
    private static final Logger LOG = Logger.getLogger(GetSpectatorStopWatchingRoute.class.getName());
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
    public GetSpectatorStopWatchingRoute(final TemplateEngine templateEngine, final Gson gson) {
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

        // remove player from server side spectator options
        try{
            gameCenter.getGame(user.getGameID()).removeSpectator(user);
        }
        catch (NullPointerException e){
            gameCenter.getArchiveGame(user.getGameID()).removeSpectator(user);
        }

        user.setGameID(0);
        user.setViewMode(Player.Mode.NONE);
        response.redirect(WebServer.HOME_URL);
        return null;
    }

}
