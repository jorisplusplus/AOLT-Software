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

import laser.enums.Commands;
import laser.enums.GameStates;

/**
 *
 * @author Joris
 */
public class PacketHelper {    
  
    //PacketIDs
    public static final int Target          = 0x00;
    public static final int Command         = 0x01;
    
    public static byte[]    Payload;
    
    
    
    static byte[] LoadText(String Text, int ID) {
        Payload = new byte[8];
        Payload[Target] = (byte)(ID & 0xFF);       
        Payload[Command] = Commands.LoadText.value;
        for(int i = 0; i<Text.length(); i++) {
            Payload[2+i] = (byte) Text.charAt(i);
        }
        return Payload;
    }
    
    static byte[] DisplayText(String Text, int BlinkAmount, int ID) {
        Payload = new byte[8];
        Payload[Target] = (byte)(ID & 0xFF);       
        Payload[Command] = Commands.DisplayText.value;
        Payload[2] = (byte)(BlinkAmount & 0xFF);
        for(int i = 0; i<Text.length(); i++) {
            Payload[3+i] = (byte) Text.charAt(i);
        }
        return Payload;
    }
    
    static byte[] setHealth(int ID, int Health, int MaxHealth) {
        Payload = new byte[8];
        Payload[Target] = (byte)(ID & 0xFF);
        Payload[1] = Commands.SetHealth.value;
        Payload[2] = (byte)(Health & 0xFF);
        Payload[3] = (byte)(MaxHealth & 0xFF);
        return Payload;
    }
    
    static byte[] setAmmo(int ID, int Ammo, int MaxAmmo, boolean InfAmmo) {
        Payload = new byte[8];
        Payload[Target] = (byte)(ID & 0xFF);
        Payload[1] = Commands.SetAmmo.value;
        Payload[2] = (byte)((Ammo << 8) & 0xFF);
        Payload[3] = (byte)(Ammo & 0xFF);
        Payload[4] = (byte)((MaxAmmo << 8) & 0xFF);
        Payload[5] = (byte)(MaxAmmo & 0xFF);
        Payload[6] = toByte(InfAmmo);
        return Payload;
    }
    
    static byte[] setLives(int ID, int Lives, boolean InfLives) {
        Payload = new byte[8];
        Payload[Target] = (byte)(ID & 0xFF);
        Payload[1] = Commands.SetLives.value;
        Payload[2] = (byte)(Lives & 0xFF);
        Payload[3] = toByte(InfLives);
        return Payload;        
    }
    
    static byte[] setAll(int ID, int Lives, int MaxHealth, int MaxAmmo, boolean InfAmmo, boolean InfLives) {
        Payload = new byte[8];
        Payload[Target] = (byte)(ID & 0x0F);
        Payload[1] = Commands.SetAll.value;
        Payload[2] = (byte)((MaxAmmo >> 8) & 0xFF);
        Payload[3] = (byte)(MaxAmmo & 0xFF);
        Payload[4] = toByte(InfAmmo);
        Payload[5] = (byte)(MaxHealth & 0xFF);
        Payload[6] = (byte)(Lives & 0xFF);
        Payload[7] = toByte(InfLives);
        return Payload;
    }
    
    static byte[] setAll(Player Person) {
        return setAll(Person.ID, Person.Lives, Person.MaxHealth, Person.MaxAmmo, Person.InfiniteBullets, Person.InfiniteLives);
    }
    
    static byte[] setID(int ID, int NewID) {
        Payload = new byte[8];
        Payload[Target] = (byte)(ID & 0xFF);
        Payload[1] = Commands.SetID.value;
        Payload[2] = (byte)(NewID & 0xFF);
        return Payload;
    }
    
    static byte[] setDeath(int ID) {
        Payload = new byte[8];
        Payload[0] = (byte)(ID & 0xFF);
        Payload[1] = Commands.SetDeath.value;
        return Payload;
    }
    
    static byte[] setGameState(int ID, GameStates NewState ) {
        Payload = new byte[8];
        Payload[Target] = (byte)(ID & 0xFF);
        Payload[1] = Commands.SetGameState.value;
        Payload[2] = NewState.value;
        return Payload;
    }
    
    static byte[] getHealth(int ID) {
        Payload = new byte[8];
        Payload[Target] = (byte)(ID & 0xFF);
        Payload[1] = Commands.GetHealth.value;
        return Payload;
    }
    
    static byte[] getAmmo(int ID) {
        Payload = new byte[8];
        Payload[Target] = (byte)(ID & 0xFF);
        Payload[1] = Commands.GetAmmo.value;
        return Payload;
    }
    
    static byte[] getLives(int ID) {
        Payload = new byte[8];
        Payload[Target] = (byte)(ID & 0xFF);
        Payload[1] = Commands.GetLives.value;
        return Payload;
    }
    
    static byte[] getStatus(int ID) {
        Payload = new byte[8];
        Payload[Target] = (byte)(ID & 0xFF);
        Payload[1] = Commands.Status.value;
        return Payload;
    }
    
    static byte toByte(boolean bool){
        if(bool) return 0x01;            
        return 0x00;
    }
    
}
