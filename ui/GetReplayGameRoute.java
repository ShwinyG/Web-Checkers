package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.Model.BoardView;
import com.webcheckers.Model.GameModel;
import com.webcheckers.Model.Player;
import com.webcheckers.appl.GameCenter;
import com.webcheckers.appl.PlayerServices;
import com.webcheckers.appl.ReplayModeController;
import spark.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.TemplateEngine;

public class GetReplayGameRoute implements Route {

    /** This is the Logger object that is used  to record server info in the terminal window */
    private static final Logger LOG = Logger.getLogger(GetHomeRoute.class.getName());
    /** This is the TemplateEngine used to render the screen */
    private final TemplateEngine templateEngine;
    /** This is the PlayerServices controller used to interact with the lobby */
    private PlayerServices playerServices;
    /** This is the GameCenter controller used to interact with the game */
    private GameCenter gameCenter;

    private final Gson gson;

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
    static final String CONTROLLER_KEY = "replayController";

    public GetReplayGameRoute(final TemplateEngine templateEngine, final Gson gson) {
        this.templateEngine = Objects.requireNonNull(templateEngine, "templateEngine is required");
        this.gson = Objects.requireNonNull(gson, "gson is required");
        LOG.config("GetReplayGameRoute is initialized.");
    }

    /**
     * Render the WebCheckers Game page in REPLAY mode.
     * @param request the Request object.
     * @param response the Response object.
     * @return If successful, the rendered HTML for the Game page, otherwise null.
     */
    @Override
    public Object handle(Request request, Response response) {
        Session httpSession = request.session();
        playerServices = httpSession.attribute(GetHomeRoute.PLAYERSERVICES_KEY);
        gameCenter = httpSession.attribute(GetHomeRoute.GAMECENTER_KEY);
        Player user = httpSession.attribute("user");
        final Map<String, Object> vm = new HashMap<>();

        ReplayModeController controls;

        if(!user.getIsInGame()){
            int archivedGameNum = 0;
            Set<String> queryGameNum = request.queryParams();
            for(String string: queryGameNum){
                archivedGameNum = Integer.parseInt(string);
                break;
            }

            //Following line may not copy the game correctly. I hate pointers.
            GameModel newGame = gameCenter.getArchiveGame(archivedGameNum).copyGame(gameCenter.getArchiveGame(archivedGameNum));
            controls = new ReplayModeController(newGame);

            httpSession.attribute(CONTROLLER_KEY, controls);
            controls.getGame().setViewMode(GameModel.Mode.REPLAY);
            controls.getGame().resetGame();
            user.setIsInGame();
            user.setGameID(controls.getGame().getGameID());
        }
        else{
            controls = httpSession.attribute(CONTROLLER_KEY);
        }

        vm.put(GAME_ID, controls.getGame().getGameID());
        vm.put(TITLE_ATTR, WELCOME_MES);
        vm.put(VIEW_MODE, controls.getGame().getViewMode());
        vm.put(RED_PLAYER, controls.getGame().getRedPlayer());
        vm.put(WHITE_PLAYER, controls.getGame().getWhitePlayer());
        vm.put(ACTIVE_COLOR, controls.getGame().getActiveColor());
        vm.put(CUR_USER, user);
        if (controls.getGame().getActiveColor() == Player.Color.RED) {
            vm.put(BOARD, controls.getGame().getRedBoard());
        } else {
            vm.put(BOARD, controls.getGame().getWhiteBoard());
        }

        final Map<String, Object> modeOptions = new HashMap<>(2);
        if(controls.getTurnCounter() > 0){
            modeOptions.put("hasPrevious", true);
        }
        if(controls.getTurnCounter() < controls.getGame().getMoveHistory().size()){
            modeOptions.put("hasNext", true);
        }
        vm.put(MODE_ATTR, gson.toJson(modeOptions));

        return templateEngine.render(new ModelAndView(vm, VIEW_NAME));
    }
}

