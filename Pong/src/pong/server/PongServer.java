package pong.server;

import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.logging.Level;
import java.util.logging.Logger;
import pong.rmi.PongRemoteServer;

public class PongServer {

    public static void main(String args[]) throws Exception {

        try {
            LocateRegistry.createRegistry(4000);
            Remote remote = new PongRemoteServer();
            Naming.rebind("rmi://localhost:4000/pong", remote);
            System.out.println("Pong server started..");
            
        } catch (RemoteException ex) {
            Logger.getLogger(PongServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
