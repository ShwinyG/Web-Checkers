/*
Filename: LobbyModel.java
Java_Version: 8 - current
Apache_Maven_Version: 3.6.2
Spark_Version: 2.8.0
 */

package com.webcheckers.Model;

import java.util.ArrayList;
/**
 * @author Ashwin Gowda
 * @author Garrett Rudolfs
 * @author Joseph Becker
 * @author Julie Sojkowski
 *
 * This class holds all the game-state data for a WebChecker's lobby.
 */
public class LobbyModel {
    /** This is the list of Player's who are in the lobby */
    private static ArrayList<Player> playerList = new ArrayList<>(10);

    /**
     * This is the Constructor for LobbyModel. It currently has nothing in it
     * since playerList is static. It probably should hold playerList.
     */
    public LobbyModel(){
    }

    /**
     * checks if a the given players name is valid or not
     * @param player the player to check
     * @return true if the player is added, false if not
     */
    public static boolean addPlayer(Player player){
        // return false if it is already a player
        if(isPlayer(player)) {
            return false;
        }
        // return false if the length of the string is less than one
        if(player.getName().length() <1) {
            return false;}
        // return false if it does not have an alphanumeric character
        if(!player.getName().matches(".*\\w.*")){
            return false;
        }
        // if the name contains double quotes
        if(player.getName().contains("\"")) return false;
        if(player.getName().contains("vs.")) return false;

        // name has passed the requirements, add them to the list and return true
        playerList.add(player);
        return true;
    }

    /**
     * gets the player list
     * @return the list of players
     */
    public ArrayList<Player> getPlayerList(){
        return playerList;
    }

    /**
     * gets the current number of players
     * @return int: the size of the  player
     */
    public static int getNumberPlayers(){
        return playerList.size();
    }

    /**
     * checks if a player with that name exists
     * @param player the player to check if they exist
     * @return true if they are a player in the lobby, false if not
     */
    public static boolean isPlayer(Player player){
        for( Player p : playerList ){
            if(p.getName().equals(player.getName())){
                return true;
            }
        }
        return false;
    }

    /**
     * Removes all player from the list
     * Used for unit testing
     */
    public static void removeAllPlayers(){
        playerList.clear();
    }

    /**
     * removes a player from the list
     * @param player the player to remove
     * @return true if the player was removed, false if not
     */
    public static boolean removePlayer(Player player){
            return playerList.remove(player);
    }

    /**
     * gets the plaer from their name and removes the player from the player lobby list
     * @param name the name of the player to remove
     * @return true if the player was removed, false if not
     */
    public static boolean removePlayerByName(String name){
        boolean condition = false;
        for(int i = 0; i < playerList.size(); i++){
            if(playerList.get(i).getName().compareTo(name) == 0){
                playerList.remove(i);
                condition = true;
                break;
            }
        }
        return condition;
    }

    /**
     * gets a string name of all the users
     * used to get the player  list on the hime page
     * @param player the player not to include in the  list
     * @return the list of players
     */
    public static String[] getAllUsers(Player player, String s) {
        String[] nameList = new String[playerList.size() - 1];
        int indexOf = playerList.indexOf(player);
        // get users in a game
        if (s.compareTo("inGame") == 0){

            ArrayList<String> playerInGameList = new ArrayList<>();
            ArrayList<Player> tempPlayerList = new ArrayList<>();
            for (int i = 0; i < playerList.size(); i++) {
                if (indexOf != i) { // is not the current player
                    if (playerList.get(i).getGameID() != 0) {
                        tempPlayerList.add(playerList.get(i));
                    }
                }
            }

            for (int n = 0; n < playerList.size(); n++) {
                if(playerList.get(n).getGameID() != 0 && playerList.get(n).getViewMode() == Player.Mode.PLAY) {
                    for (int m = 0; m < tempPlayerList.size(); m++) {
                        if(tempPlayerList.get(m).getGameID() != 0 && tempPlayerList.get(m).getViewMode() == Player.Mode.PLAY) {
                            if (playerList.get(n).getGameID() == tempPlayerList.get(m).getGameID() &&
                                    playerList.get(n).getName().compareTo(tempPlayerList.get(m).getName()) != 0) {
                                String a = playerList.get(n).getName() + " vs. " + tempPlayerList.get(m).getName();
                                tempPlayerList.remove(tempPlayerList.get(m));
                                tempPlayerList.remove(playerList.get(n));

                                playerInGameList.add(a);
                            }
                        }
                    }
                }
            }
            if(playerInGameList.size() == 0){
                return new String[1];
            }
            nameList = new String[playerInGameList.size()];
            int j = 0;
            for (int n = 0; n < playerInGameList.size(); n++) {
                nameList[j] = playerInGameList.get(n);
                j++;
            }
            return nameList;
        }
        // get users not in game
        else if (s.compareTo("notInGame") == 0){

            ArrayList<Player> playerInGameList = new ArrayList<>(playerList.size());
            for (int n = 0; n < playerList.size(); n++) {
                if ((indexOf != n) && (playerList.get(n).getGameID() == 0)) { // is not the current player
                    playerInGameList.add(playerList.get(n));
                }
            }
            if(playerInGameList.size() == 0){
                return new String[1];
            }
            nameList = new String[playerInGameList.size()];
            int j = 0;
            for (int i = 0; i < playerInGameList.size(); i++) {
                nameList[j] = playerInGameList.get(i).getName();
                j++;
            }
            return nameList;
        }
        // get all users
        int j = 0;
        for (int i = 0; i < playerList.size(); i++) {
            if ((indexOf != i) && (j < nameList.length)) { // is not the current player
                nameList[j] = playerList.get(i).getName();
                j++;
            }
        }
        return nameList;
    }

    /**
     * get if a player is in the list
     * @param name the name of the player to get
     * @return the player or null if they do not exist
     */
    public Player getPlayer(String name){
        for(Player player: playerList){
            if(0 == name.compareTo(player.getName())){
                return player;
            }
        }
        return null;
    }
}
