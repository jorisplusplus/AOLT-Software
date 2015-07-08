/*
 * Copyright (C) 2014 Joris
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package laser;

/**
 *
 * @author Joris
 */
public interface IGameMode {
    
    /**
     * Method called when this gamemode is activated.
     */    
    public void init();
    
    /**
     * Method just before the start of a round.
     * Final changes to the player list should be made now.
     */
    public void starting();
    
    /**
     * Method called when the round is starting.
     */
    public void start();
    
     /**
     * The round has started. Need to show anything to your players do it now.
     */
    public void playing();
    
    /**
     * Method called when the round is aborted. Should you have to do any cleanup
     * before the end of round notify do it now.
     */
    public void quit();
    
    /**
     * Method called when a player dies
     * @param id the id of the died player.
     */
    public void onDeath(int id);
    
    /**
     * Method called when a player has no ammo left.
     * @param id the id from the player that has no ammo left.
     */
    public void noAmmo(int id);
    
    /**
     * @return the name of this gamemode.
     */
    public String getName();
    

}
