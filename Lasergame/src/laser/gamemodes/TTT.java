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

package laser.gamemodes;

import java.util.Random;
import laser.IGameMode;
import laser.Main;
import laser.Player;

public class TTT implements IGameMode {
    
    private int TerroristID;    
    
    @Override
    public void init() {
        //TTT only has one life
        Main.Gui.lives.setValue((int) 1);
        Main.Gui.livesCheckbox.setSelected(false);
        Main.Gui.lives.setEnabled(false);
        Main.Gui.livesCheckbox.setEnabled(false);
    }

    @Override
    public void starting() {
        Main.loadPlayerSettings();        
    }

    @Override
    public void start() {
        
    }
    
    @Override
    public void playing() {
        Random Rand = new Random();

        TerroristID = Main.Players.get(Rand.nextInt(Main.Players.size())).ID;
        for(Player Person: Main.Players) {
            if(Person.ID == TerroristID) {
                Main.printText("Traitor        ", 5, Person.ID);
            } else {
                Main.printText("Innocent       ", 5, Person.ID);
            }
        }
    }

    @Override
    public void quit() {
        
    }

    @Override
    public void onDeath(int id) {
        if(id == TerroristID) {
            Main.notifyEOR();
            for(Player Person: Main.Players) {
                Main.printText("Innocents win.  ", 4, Person.ID);
            }
            Main.Gui.log("Innocents win.");
            return;
        }
        int Alive;
        Alive = 0;
        for(Player Person: Main.Players) {
            if(Person.Lives > 0) {
                Alive++;
            }
        }
        if(Alive == 1) {
            Main.notifyEOR();
            for(Player Person: Main.Players) {
                Main.printText("Traitors win.   ", 4, Person.ID);
            }
            Main.Gui.log("Traitors win.");
        }
    }

    @Override
    public void noAmmo(int id) {
       
    }

    @Override
    public String getName() {
        return "Trouble in terrorist town by AOLT";
    }   
}