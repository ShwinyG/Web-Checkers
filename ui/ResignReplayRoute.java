package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.Model.Player;
import com.webcheckers.appl.GameCenter;
import com.webcheckers.appl.PlayerServices;
import com.webcheckers.appl.ReplayModeController;
import com.webcheckers.util.Message;
import spark.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ResignReplayRoute implements Route {

    private final Gson gson;

    private final TemplateEngine templateEngine;

    /**
     * This is the Constructor for ResignPlayerRoute
     * @param gson the java to json string converter.
     */
    public ResignReplayRoute(TemplateEngine templateEngine, final Gson gson) {
        this.templateEngine = Objects.requireNonNull(templateEngine, "templateEngine is required");
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
        //final ReplayModeController controls = httpSession.attribute(GetReplayGameRoute.CONTROLLER_KEY);
        Player user = httpSession.attribute("user");

        user.setIsInGame();
        user.setGameID(0);

        response.redirect("/");

        return null;
    }

}
