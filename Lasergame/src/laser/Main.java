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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import laser.enums.GameStates;
import laser.gamemodes.Deathmatch;
import laser.gamemodes.DummyGamemode;
import laser.gamemodes.TTT;

/**
 * @author Joris
 */
public class Main {
    public  static LaserInterface       Gui;
    public  static String               GamemodeFolder;
    public  static ArrayList<Player>    Players;
    private static ArrayList<IGameMode> Gamemodes;
    private static IGameMode            SelectedGamemode;
    public  static SerialHandler        Handler;
    public  static boolean              Connected;
    public  static ScoreboardInterface  Scoreboard;
    public  static Timer                PollTimer;
    private static pollGuns             Poll;
    
    static class pollGuns extends TimerTask {
        @Override
        public void run() {
            PollTimer.schedule(new pollGuns(), 10000);
            for(Player Person: Players) {
                Handler.AddTransfer(PacketHelper.getStatus(Person.ID));
            }
        }
    }
        
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Determine the location of the gamemode folder, if it doesn't exist the folder is created.
        try {
            String fileSeparator = System.getProperty("file.separator");
            GamemodeFolder = getPath() + fileSeparator + "gamemodes" + fileSeparator;
            File folder = new File(GamemodeFolder);
            if (!folder.isDirectory()) folder.mkdir();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Handler = new SerialHandler();
        Gamemodes = new ArrayList();
        Players = new ArrayList();
        //Activating GUI
        Gui = new LaserInterface();
        Gui.setVisible(true);
        Gui.disableAll();
        Handler = new SerialHandler();
        Connected = false;
        //Load the standard gamemodes and the gamemodes in the folder.
        loadGamemodes();
        PollTimer = new Timer();
        
    }
  
    /**
     * Starts the loading process of the integrated gamemodes and gamemodes placed inside the folder.
     */
    private static void loadGamemodes() {
    Gui.log("Loading gamemodes...");
    //Loading integrated gamemodes.
    registerGamemode(new DummyGamemode());
    registerGamemode(new Deathmatch());
    registerGamemode(new TTT());
    //Loading jar files.
    FilenameFilter jarFilter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            return name.toLowerCase().endsWith(".jar");			
        }
    };
    File jars = new File(GamemodeFolder);
    for (File jar: jars.listFiles(jarFilter)) {
        System.out.println(jar.getAbsolutePath());
        loadClass(jar.getAbsolutePath());
    }
    //Updating the combobox with the gamemodes.
    //Gamemodes.stream().forEach(Gamemode -> Gui.addGamemode(Gamemode.getName()));
    for(Iterator<IGameMode> games = Gamemodes.iterator(); games.hasNext();) {
            Gui.addGamemode(games.next().getName());
        }
    Gui.log("Done loading gamemodes. "+Gamemodes.size()+" gamemodes have been loaded.");
    }
    
    
    /**
     * Used to register gamemodes.
     * @param gamemode A gamemode instance. The instance must implement IGameMode 
     */
    public static void registerGamemode(Object gamemode) {
        if(gamemode instanceof IGameMode){       
            Gamemodes.add((IGameMode) gamemode);
            Gui.log("Loaded: "+((IGameMode)gamemode).getName());
        } else {
            Gui.log("A plugin was trying to load an invalid gamemode.");  
        }
    }
    
    /**
     * Used to determine the directory of the jar. Since user.dir sometime doesn't give the right folder.
     * @return Directory of the jar.
     * @throws UnsupportedEncodingException 
     */
    public static String getPath() throws UnsupportedEncodingException {
        URL url = Main.class.getProtectionDomain().getCodeSource().getLocation();
        String jarPath = URLDecoder.decode(url.getFile(), "UTF-8");
        String parentPath = new File(jarPath).getParentFile().getPath();
        return parentPath;
    }
    
    /**
     * Loads all the classes from a jar file. From all the classes a instance is created and send to the registerGamemode
     * @param pathToJar path to the jar file including file name.
     */
    private static void loadClass(String pathToJar) {
        try {
        JarFile jarFile = new JarFile(pathToJar);
        Enumeration e = jarFile.entries();

        URL[] urls = { new URL("jar:file:" + pathToJar+"!/") };
        URLClassLoader cl = URLClassLoader.newInstance(urls);

        while (e.hasMoreElements()) {
            JarEntry je = (JarEntry) e.nextElement();
            if(je.isDirectory() || !je.getName().endsWith(".class")){
                continue;
            }
            // -6 because of .class
            String className = je.getName().substring(0,je.getName().length()-6);
            className = className.replace('/', '.');
            Class c = cl.loadClass(className);
            registerGamemode(c.newInstance());
            }
        }
        catch(IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
        Gui.log("Error: "+e.toString());
        }
    }   

    /**
     * A gamemode has been selected and the options are set by the gamemode and/or the user.
     */
    public static void initGamemode() {
        SelectedGamemode = Gamemodes.get(Gui.gamemodeBox.getSelectedIndex());
        Gui.log("Init " + SelectedGamemode.getName() + ".");
        Gui.enableAll();
        SelectedGamemode.init();
    }

    /**
     * Starts the round, sends data to the players and notifies the gamemode a round is/has started.
     */
    public static void startGamemode() {
        Gui.log("Starting " + SelectedGamemode.getName() + ".");
        SelectedGamemode.starting();
        for(Player Person: Players) {
            loadAndSend(PacketHelper.setAll(Person));
        }
        Gui.log("Start " + SelectedGamemode.getName() + ".");
        SelectedGamemode.start();
        for(Player Person: Players) {
            loadAndSend(PacketHelper.setGameState(Person.ID, GameStates.Playing));
        }
        Scoreboard = new ScoreboardInterface();
        Scoreboard.setVisible(true);
        DefaultTableModel Model = (DefaultTableModel)Scoreboard.Tabel.getModel();
       
        for(Player Person: Players) {
          Model.addRow(new Object[]{Person.Name, Person.ID, Person.getDeaths(), Person.Health, Person.Lives, Person.Ammo});  
        }
        Poll = new pollGuns();
        PollTimer.schedule(Poll, 5000);
        SelectedGamemode.playing();
    }

    /**
     * User pressed the abort button and the current gamemode should stop.
     */
    public static void abortGamemode() {
       SelectedGamemode.quit();
       Gui.disableAll();
    }
   
    /**
     * Add data to the transfer ArrayList.
     * This data will be transmitted to a connected device.
     * If no device is connected it will show in the log window.
     * @param Data 32 byte array.
     */
    public static void loadAndSend(byte[] Data) {
        if(Main.Connected) {
            Handler.AddTransfer(Data);
        } else {
            Gui.log("Something tried to send data. No device connected.");
        }
    }
    
    /**
     * Detect all guns that are online.
     */
    public static void detectGuns() {
        Handler.DetectGuns = true;
        Handler.Guns.clear();
        Players.clear();
        Gui.log("Starting network scan. Log spam incoming.");
        for(int i = 1; i<16; i++) {
            loadAndSend(PacketHelper.getStatus(i));
        }
    }
    
    /**
     * Load gui settings into player list
     */
    public static void loadPlayerSettings() {
        for(Player Person: Players) {
            Person.Ammo = (int)Gui.ammo.getValue();
            Person.MaxAmmo = (int)Gui.ammo.getValue();
            Person.Lives = (int)Gui.lives.getValue();
            Person.MaxLives = (int)Gui.lives.getValue();
            Person.Health = (int)Gui.health.getValue();
            Person.MaxHealth = (int)Gui.health.getValue();
            Person.InfiniteBullets = Gui.bulletsCheckbox.isSelected();
            Person.InfiniteLives = Gui.livesCheckbox.isSelected();
        }
    }
    
    public static void printText(String Text, int BlinkAmount, int ID) {
        loadAndSend(PacketHelper.LoadText(Text.substring(10),  ID));
        loadAndSend(PacketHelper.LoadText(Text.substring(4,10),  ID));
        loadAndSend(PacketHelper.DisplayText(Text.substring(0,4), BlinkAmount, ID));
    }
    
    public static void updatePlayerStats(int ID, int Health, int Lives, int Ammo) {
        Player Person = findPlayer(ID);
            if(Person.ID == ID) {
                Person.Health = Health;
                Person.Lives = Lives;
                Person.Ammo = Ammo;
            }
    }
    
    
    public static Player findPlayer(int ID) {
        for(Player Person: Players) {
            if(Person.ID == ID) return Person;
        }
        return new Player();
    }
    
    public static void updateScoreboard() {
        DefaultTableModel Model = (DefaultTableModel)Scoreboard.Tabel.getModel();
        while(Model.getRowCount() > 0) {
            findPlayer((int)Model.getValueAt(0, 1)).Name = (String)Model.getValueAt(0,0);
            Model.removeRow(0);
        }
        for(Player Person: Players) {
          Model.addRow(new Object[]{Person.Name, Person.ID, Person.getDeaths(), Person.Health, Person.Lives, Person.Ammo});  
        }
        
    }
    
    public static void notifyEOR () {
        Poll.cancel();
        for(Player Person: Players) {
            Handler.AddTransfer(PacketHelper.setGameState(Person.ID, GameStates.GameOver));
        }
    }
    
    public static void onDeath (int ID) {
        if(SelectedGamemode != null) {
            SelectedGamemode.onDeath(ID);
        }
    }
    
    public static void onOutOfAmmo(int ID) {
        if(SelectedGamemode != null) {
            SelectedGamemode.noAmmo(ID);
        }
    }
}