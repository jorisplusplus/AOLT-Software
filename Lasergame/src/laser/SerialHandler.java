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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import static laser.Main.Gui;

/**
 *
 * @author Joris
 */
public class SerialHandler implements SerialPortEventListener {

    final protected static char[]               hexArray    = "0123456789ABCDEF".toCharArray();
    public                 ArrayList<byte[]>    TransferBuffer;
    public static          SerialPort           Port;
    private                Timer                Timeout;
    private                Integer              Failed;
    public                 Boolean              DetectGuns;
    public                 ArrayList<Integer>   Guns;
    private                TimeOut              Task;
    
    public SerialHandler() {
        TransferBuffer = new ArrayList(); 
        Timeout = new Timer();
        DetectGuns = false;
        Guns = new ArrayList();
    }
    
    class TimeOut extends TimerTask {
        @Override
        public void run() {
            Gui.log("Transmission to dongle failed.");
            Failed++;
            if(Failed < 3) {
                SendPacket();
            } else {
                Gui.log("Three failed attemps. Connection lost");
                Main.Connected = false;
                try {
                    Port.closePort();
                } catch (SerialPortException ex) {
                    Gui.log(ex.toString());
                }
            }            
        }
    }
    
    class sendNext extends TimerTask {
        @Override
        public void run() {
            SendPacket();
        }
    }
    
    public void AddTransfer(byte[] Data) {
        TransferBuffer.add(Data);
        if(TransferBuffer.size() == 1){ //Added first packet to the send list, start the sending process.
            SendPacket();
        }
    }
    
    public void SendPacket() {
        if(TransferBuffer.size() > 0) {
            if(Main.Connected) {
                try {
                    Port.writeBytes(TransferBuffer.get(0));
                    if(Main.Gui.logCheckbox.isSelected()) {
                        String HexCode = bytesToHex(TransferBuffer.get(0));
                        Main.Gui.log("Sending: "+HexCode);
                    }
                    Task = new TimeOut();
                    Timeout.schedule(Task, 1000);
                } catch (SerialPortException ex) {
                    Main.Gui.log(ex.toString());
                }
                
            }
        } else if(DetectGuns) {
            DetectGuns = false;
            Gui.log("Done detecting guns...");
        }
    }
    
    public void Open(String PortName) {
        Disconnect();
        Port = new SerialPort(PortName);
        try {
            Port.openPort();        
            Port.addEventListener(this);
            Gui.log("Starting connection.");
            byte[] WriteBuffer = new byte[8];
            WriteBuffer[0] = 0x00;
            WriteBuffer[1] = 0x00;
            WriteBuffer[2] = 0x00;
            Port.writeBytes(WriteBuffer);
            TransferBuffer = new ArrayList();
        } catch (SerialPortException ex) {
            Gui.log(ex.toString());
        } 
    }
    
    public void Disconnect() {
        if (Port != null) {
            if (Port.isOpened()) {
                try {
                    Port.closePort();
                } catch (SerialPortException ex) {
                   Gui.log(ex.toString());
                }
            }
        }
    }
    
    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        if(serialPortEvent.getEventType() == SerialPortEvent.RXCHAR) {
            try {
                byte[] Payload;
                if(Task != null) Task.cancel();                
                Failed = 0;
                Payload = Port.readBytes(8);
                if(Main.Gui.logCheckbox.isSelected()) {
                String HexCode = bytesToHex(Payload);
                Main.Gui.log("Received: "+HexCode);
                }
                if(Payload[1] == (byte) 0x00 && DetectGuns) {
                    int temp = (int)(Payload[2] & 0xFF);
                    if(!Guns.contains(temp)) {
                        Guns.add(temp);
                        Main.Gui.log("Found gun with ID: "+temp);
                        Main.Players.add(new Player(temp));
                    }
                }
                if(Payload[0] != (byte) 0x00 && !DetectGuns) {
                   int id  = 0xFF & (Payload[0] >> 4);
                   HandleGun(id, Payload);
                }
                if(Payload[0] == (byte) 0x00) {
                    if(Payload[2] == (byte) 0xFF) {
                        Main.Connected = true;
                        Main.Gui.log("Device found, Connected");                        
                    } else if(Payload[1] == (byte)0xFE && Payload[2] == (byte)0x0F) { //Received TXReady 
                        Main.Connected = true;
                        if(!TransferBuffer.isEmpty()) TransferBuffer.remove(0);
                        Timeout.schedule(new sendNext(), 500); //Delay next send maybe after TXready comes another packet.
                    } else if(Payload[1] == (byte)0xFE && Payload[2] == (byte)0x1F) { //TXReady but last packet wasn't received by the gun.
                        Main.Connected = true;
                        if(!TransferBuffer.isEmpty()) TransferBuffer.remove(0);
                        Timeout.schedule(new sendNext(), 500); //Delay next send maybe after TXready comes another packet.
                    }                   
                }               
            } catch (SerialPortException ex) {
                Main.Gui.log(ex.toString());
            }
        }    
    }
    
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 3];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 3] = hexArray[v >>> 4];
            hexChars[j * 3 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    
    public static void HandleGun(int ID, byte[] Payload) {
        switch(Payload[1]) {
            case (byte) 0x00:
                Main.updatePlayerStats(ID, (int)(0xFF & Payload[5]), (int)(0xFF & Payload[6]), (int)(0xFF & Payload[4]) + ((int)(0xFF & Payload[3]))*256);
                Main.updateScoreboard();
                break;
            case (byte) 0x0D:
                Main.findPlayer(ID).increaseDeaths();
                Main.updateScoreboard();
                break;
            case (byte) 0x0E:                
                Main.findPlayer(ID).Lives = 0;
                Main.updateScoreboard();
                Main.onDeath(ID);
                break;
            case (byte) 0x0F:
                Main.findPlayer(ID).Ammo = 0;
                Main.updateScoreboard();
                Main.onOutOfAmmo(ID);
                break;
        }
    }
}
