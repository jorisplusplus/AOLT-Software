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
public class Player {
    
    public  int      Health;
    public  int      MaxHealth;
    public  int      Lives;
    public  int      MaxLives;
    public  int      Deaths;
    public  int      Ammo;
    public  int      MaxAmmo;
    public  int      ID;
    public  boolean  InfiniteLives;
    public  boolean  InfiniteBullets;
    public  String   Name;
    
    
    /**
     * Constructs a new player with infinite lives and ammo and with 1 health.
     */
    public Player() {
        this.Health             = 1;
        this.MaxHealth          = 1;
        this.Lives              = 1;
        this.MaxLives           = 1;
        this.Ammo               = 1;
        this.MaxAmmo            = 1;
        this.InfiniteLives      = true;
        this.InfiniteBullets    = true;
        this.Name               = "dummy";
        this.ID                 = 15;
        this.Deaths             = 0;
    }
    
    /**
     * 
     * @param ID ID of the player
     * @param health Health of the player, how many hits it takes to die.
     * @param lives How many lives does this player before gameover.
     * @param ammo How many bullets does the gun have.
     * @param infLives Should the player have infinite lives.
     * @param infBullets Should the player have infinite bullets.
     * @param name Player name.
     */
    public Player(int ID, int health, int lives, int ammo, boolean infLives, boolean infBullets, String name) {
        this.Health          = health;
        this.Lives           = lives;
        this.MaxLives        = lives;
        this.Ammo            = ammo;
        this.InfiniteLives   = infLives;
        this.InfiniteBullets = infBullets;
        this.Name            = name;
        this.ID              = ID;
        this.Deaths           = 0;
    }
    
    /**
     * 
     * @param ID ID of the player
     */
    public Player(int ID) {
        this.ID = ID;
    }
    
    public int getDeaths() {
        if(InfiniteLives) return this.Deaths;
        return this.MaxLives - this.Lives;        
    }
    
    public void increaseDeaths() {
        if(InfiniteLives) this.Deaths++;
    }
}