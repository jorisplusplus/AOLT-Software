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

package laser.enums;

/**
 *
 * @author Joris
 */
    public enum Commands {
                            Status((byte)0x00),
                            DisplayText((byte)0x01),
                            LoadText((byte)0x02),
                            SetAmmo((byte)0x03),
                            SetHealth((byte)0x04),
                            SetLives((byte)0x05),
                            SetAll((byte)0x06),
                            SetID((byte)0x07),
                            SetDeath((byte)0x08),
                            GetAmmo((byte)0x09),
                            GetHealth((byte)0x0A),
                            GetLives((byte)0x0B),
                            SetGameState((byte)0x0C),
                            Respawn((byte)0x0D),
                            Dead((byte)0x0E);
    
                            public byte value;
                            
                            private Commands(byte value) {
                                this.value = value;
                            }
    }
