/*
Filename: GameCenter.java
Java_Version: 8 - current
Apache_Maven_Version: 3.6.2
Spark_Version: 2.8.0
 */

package com.webcheckers.appl;

import com.webcheckers.Model.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Ashwin Gowda
 * @author Garrett Rudolfs
 * @author Joseph Becker
 * @author Julie Sojkowski
 *
 * The controller object for games stored on the server.
 * This class is an example of the Pure Fabrication principle.
 */
public class GameCenter {
    /** This is a list of the games currently being played on the Web Server */
    private ArrayList<GameModel> games;
    /** This is a list of games that have ended */
    private HashMap<Integer, GameModel> archive;
    /** This is the number of games that have been removed from the Web Server */
    private int removedGames;

    /**
     * Construct a new GameCenter.
     */
    public GameCenter() {
        this.games = new ArrayList<>();
        this.archive = new HashMap<>();
        this.removedGames = 0;
    }

    /**
     * This is used to create a new game on the server.
     * @param user the Player starting the game.
     * @param other the Player being played against.
     * @param id the identifier for this new game.
     * @return GameModel: a new game object.
     */
    public synchronized GameModel theGame(Player user, Player other, int id) {
        this.games.add(new GameModel(user, other, GameModel.Mode.PLAY, id));
        return this.games.get(id - 1 - removedGames);
    }

    /**
     * gets the current game model
     * @param id the identifier for the game.
     * @return the GameModel with the supplied identifier.
     */
    public synchronized GameModel getGame(int id){
        try {
            return this.games.get(id - 1 - removedGames);
        }
        catch(IndexOutOfBoundsException e){
            return null;
        }
    }

    /**
     * This method gets the list of finished games as a string,
     * so that they can be displayed on the home screen.
     * @return a String[] of the game's ids.
     */
    public synchronized String[] getArchiveAsString(){
        String[] gameList = new String[archive.size()];
        int index = 0;
        for(GameModel game: archive.values()){
            gameList[index] = String.valueOf(game.getGameID());
            index++;
        }
        return gameList;
    }

    /**
     * This method gets a single archived game using it's game id.
     * @param id the game's identifier.
     * @return the archived game.
     */
    public GameModel getArchiveGame(int id){
        return archive.get(id);
    }

    public GameModel[] getArchives(){
        GameModel[] gameList = new GameModel[archive.size()];
        int index = 0;
        for(GameModel game: archive.values()){
            gameList[index] = game;
            index++;
        }
        return gameList;
    }

    /**
     * This method removes the game with the given identifier.
     * @param id the identifier for the game.
     */
    public synchronized void endGame(int id){
        this.games.remove(id - 1 - removedGames);
        this.removedGames++;
    }

    public synchronized void archiveGame(int id){
        if(!archive.containsKey(id)){
            this.archive.put(id, this.games.get(id - 1 - removedGames));
        }
    }

    public int getRemovedGames(){
        return removedGames;
    }

    public GameModel unitTestSetUpGame(Player user, Player opponent, int id){
        GameModel m = new GameModel(user, opponent, GameModel.Mode.PLAY, id);
        games.add(m);
        user.setIsInGame();
        opponent.setIsInGame();
        user.setColor(Player.Color.RED);
        opponent.setColor(Player.Color.WHITE);
        user.setGameID(id);
        user.setGameID(id);

        return m;
    }
}
