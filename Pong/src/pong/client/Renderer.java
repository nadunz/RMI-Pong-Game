//This class is made for the jpanel rendering process
package pong.client;

import java.awt.Graphics;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;

public class Renderer extends JPanel//repaints(renders)
{

    private static final long serialVersionUID = 1L;
    
    private PongClient pong;

    public Renderer(PongClient pong) {
        this.pong = pong;
    }

    @Override
    public void paintComponent(Graphics g) {
           super.paintComponent(g);

        try {
            pong.render(g);
            
        } catch (RemoteException ex) {
            Logger.getLogger(Renderer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

}
