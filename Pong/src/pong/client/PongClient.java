//This class is made for the main, actions, updates and keys
package pong.client;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.Timer;
import pong.rmi.Ball;
import pong.rmi.IServer;
import pong.rmi.gameobjects.GameState;
import pong.rmi.Paddle;

public class PongClient implements ActionListener, KeyListener {

    public Renderer renderer;

    public int width, height;

    public Random random;

    public JFrame jframe;

    public int score;

    private IServer remote;

    public PongClient(IServer remote) throws RemoteException//Sets up jframe options
    {
        this.remote = remote;

        Dimension pongSize = remote.getPongSize();
        width = pongSize.width;
        height = pongSize.height;

        remote.updateGameState(GameState.Menu);

        Timer timer = new Timer(20, this);
        random = new Random();

        renderer = new Renderer(this);

        JFrame jf = new JFrame("Pong");

        jf.setSize(width + 15, height + 35);
        jf.setVisible(true);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.add(renderer);
        jf.addKeyListener(this);

        //JMenuBar//
        JMenuBar jmb = new JMenuBar();
        jf.setJMenuBar(jmb);

        JMenu file = new JMenu("File");
        jmb.add(file);
        JMenuItem exit = new JMenuItem("Exit");
        file.add(exit);

        class exitaction implements ActionListener {

            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }

        }
        exit.addActionListener(new exitaction());
        timer.start();
    }

    public void render(Graphics g0) throws RemoteException//Start menu, select mode(2 players, 1 player 1 bot(set difficulty))
    {
        Graphics2D g = (Graphics2D) g0;

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GameState gameState = remote.gameState();

        if (gameState == GameState.Menu) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", 1, 50));

            g.drawString("PONG", width / 2 - 75, 50);

            if (!remote.isSelectingDifficulty()) {
                g.setFont(new Font("Arial", 1, 30));

                g.drawString("Press Space to Play", width / 2 - 150, height / 2 - 25);
                g.drawString("Press Shift to Play with Bot", width / 2 - 200, height / 2 + 25);
                g.drawString("<< Score Limit: " + remote.getScoreLimit() + " >>", width / 2 - 150, height / 2 + 75);
            }
        }

        if (remote.isSelectingDifficulty()) {
            String string = remote.getBotDifficulty() == 0 ? "Easy" : (remote.getBotDifficulty() == 1 ? "Medium" : "Hard");

            g.setFont(new Font("Arial", 1, 30));

            g.drawString("<< Bot Difficulty: " + string + " >>", width / 2 - 180, height / 2 - 25);
            g.drawString("Press Space to Play", width / 2 - 150, height / 2 + 25);
        }

        if (gameState == GameState.Paused) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", 1, 50));
            g.drawString("PAUSED", width / 2 - 103, height / 2 - 25);
        }

        //Graphic for circle and line for game status 1(paused) or 2(game is played)
        if (gameState == GameState.Paused || gameState == GameState.Playing) {

            g.setColor(Color.WHITE);

            g.setStroke(new BasicStroke(5f));

            g.drawLine(width / 2, 0, width / 2, height);

            g.setStroke(new BasicStroke(2f));

            g.drawOval(width / 2 - 150, height / 2 - 150, 300, 300);

            g.setFont(new Font("Arial", 1, 50));

            Paddle p1 = remote.getPlayer(1);
            Paddle p2 = remote.getPlayer(2);

            g.drawString(String.valueOf(p1.score), width / 2 - 90, 50);
            g.drawString(String.valueOf(p2.score), width / 2 + 65, 50);

            // render player 1
            g.setColor(Color.WHITE);
            g.fillRect(p1.x, p1.y, p1.width, p1.height);

            // render player 2
            g.fillRect(p2.x, p2.y, p2.width, p2.height);

            // render ball
            Ball ball = remote.getBall();
            g.fillOval(ball.x, ball.y, ball.width, ball.height);
        }

        if (gameState == GameState.Over)//finish of game(shows winner),lets to choose next action(play again,go to main menu)
        {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", 1, 50));

            g.drawString("PONG", width / 2 - 75, 50);

            if (remote.isBotActive() && remote.getPlayerWon() == 2) {
                g.drawString("The Bot Wins!", width / 2 - 170, 200);
            } else {
                g.drawString("Player " + remote.getPlayerWon() + " Wins!", width / 2 - 165, 200);
            }

            g.setFont(new Font("Arial", 1, 30));

            g.drawString("Press Space to Play Again", width / 2 - 185, height / 2 - 25);
            g.drawString("Press ESC for Menu", width / 2 - 140, height / 2 + 25);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) //repaints and updates(paddle,ball)
    {
        try //repaints and updates(paddle,ball)
        {
            remote.update();
            renderer.repaint();

        } catch (RemoteException ex) {
            Logger.getLogger(PongClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void keyPressed(KeyEvent e)//key listeners for all necessary keys when it is pressed - w,s,up,down,right,left,shift,space,esc
    {
        
        try {
            remote.keyPressed(e.getKeyCode());
            
        } catch (RemoteException ex) {
            Logger.getLogger(PongClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void keyReleased(KeyEvent e)//key listeners for all necessary keys when it is released
    {
        try {
            remote.keyReleased(e.getKeyCode());

        } catch (RemoteException ex) {
            Logger.getLogger(PongClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    public static void main(String[] args)//main
    {

        try {

            IServer remoteServer = (IServer) Naming.lookup("rmi://localhost:4000/pong");
            new PongClient(remoteServer);

        } catch (NotBoundException | MalformedURLException | RemoteException ex) {
            Logger.getLogger(PongClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out))) {
            bw.write("This is test file");
            bw.newLine();
            bw.write("This isnew test line");
            bw.newLine();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("Score.txt"))) {
            bw.write("Player 1");
            bw.newLine();
            bw.write("Player 2");
            bw.newLine();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String inputString = br.readLine();
            while (!inputString.equals("EXIT")) {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter("Sample.txt", true))) {
                    bw.write(inputString);
                    bw.newLine();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                inputString = br.readLine();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
