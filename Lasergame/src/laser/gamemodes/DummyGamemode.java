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
import laser.IGameMode;

/**
 *
 * @author Joris
 */
public class DummyGamemode implements IGameMode {

    @Override
    public void init() {
    
    }

    @Override
    public void start() {
    
    }
    
    @Override
    public void starting() {
    
    }
    
    @Override
    public void playing() {
        
    }
    
    @Override
    public void quit() {
    }
    
    @Override
    public void onDeath(int id) {
    }

    @Override
    public void noAmmo(int id) {
    }

    @Override
    public String getName() {
        return "Dummy";
    }   
}