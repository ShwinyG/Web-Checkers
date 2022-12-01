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
import java.util.Set;

public class PostReplayNextTurn implements Route {
    private Gson gson;


    public PostReplayNextTurn(final Gson gson) {
        this.gson = Objects.requireNonNull(gson, "gson is required");
    }

    @Override
    public Object handle(Request request, Response response) {
        final Session httpSession = request.session();
        ReplayModeController controls = httpSession.attribute(GetReplayGameRoute.CONTROLLER_KEY);

        if(controls.getTurnCounter() < controls.getGame().getMoveHistory().size()){
            controls.redoMove(controls.getGame().getActiveColor());
            return this.gson.toJson(Message.info("true"));
        }
        else{
            return this.gson.toJson(Message.info("false"));
        }
    }

}
