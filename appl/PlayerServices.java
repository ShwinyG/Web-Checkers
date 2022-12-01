/*
Filename: PlayerServices.java
Java_Version: 8 - current
Apache_Maven_Version: 3.6.2
Spark_Version: 2.8.0
 */

package com.webcheckers.appl;

import com.webcheckers.Model.LobbyModel;

/**
 * @author Ashwin Gowda
 * @author Garrett Rudolfs
 * @author Joseph Becker
 * @author Julie Sojkowski
 *
 * The object to coordinate the state of the lobby in the server.
 * This class is an example of the GRASP Controller principle.
 */

public class PlayerServices {
    /** This is the model for the WebChecker's lobby */
    private LobbyModel lobby;

    /**
     * Construct a new PlayerServices object.
     */
    public PlayerServices(){
        this.lobby = null;
    }

    /**
     * Get the server's lobby. Create one if one hasn't been created yet.
     *
     * @return LobbyModel
     *    the current lobby that holds the players
     */
    public synchronized LobbyModel currentLobby() {
        if(lobby == null) {
            lobby = new LobbyModel();
        }
        return lobby;
    }

    /**
     * Get the model for the WebChecker's lobby.
     * Used for Unit Testing to check the state of the lobby.
     */
    public LobbyModel getLobby() {
        return lobby;
    }

}
