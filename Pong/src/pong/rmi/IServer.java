
package pong.rmi;

import java.awt.Dimension;
import pong.rmi.gameobjects.GameState;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IServer extends Remote {

    public GameState gameState() throws RemoteException;

    public void updateGameState(GameState state) throws RemoteException;

    public void start() throws RemoteException;

    public Dimension getPongSize() throws RemoteException;

    public boolean isBotActive() throws RemoteException;

    public void setBotActive(boolean active) throws RemoteException;

    public void update() throws RemoteException;

    public void keyPressed(int id) throws RemoteException;

    public void keyReleased(int id) throws RemoteException;

    public Paddle getPlayer(int number) throws RemoteException;

    public Ball getBall() throws RemoteException;

    public boolean isSelectingDifficulty() throws RemoteException;

    public void setSelectingDifficulty(boolean value) throws RemoteException;

    public void setScoreLimit(int score) throws RemoteException;
    
    public int getScoreLimit() throws RemoteException;
    
    public int getBotDifficulty() throws RemoteException;
    
    public int getPlayerWon() throws RemoteException;

}
