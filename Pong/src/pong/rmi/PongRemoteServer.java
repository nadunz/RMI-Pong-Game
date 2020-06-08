package pong.rmi;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import pong.rmi.gameobjects.GameState;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class PongRemoteServer extends UnicastRemoteObject implements IServer {

    private static final long serialVersionUID = 1L;

    private GameState gameState;

    public Paddle player1, player2;

    private final int pongWidth = 700, pongHeight = 700;

    private boolean bot = false, selectingDifficulty;

    private Ball ball;

    private boolean w, s, up, down;

    private int scoreLimit = 11;

    private int playerWon;

    private int botDifficulty, botMoves, botCooldown = 0;

    public PongRemoteServer() throws RemoteException {

    }

    @Override
    public GameState gameState() throws RemoteException {
        return this.gameState;
    }

    @Override
    public void updateGameState(GameState state) throws RemoteException {
        this.gameState = state;
    }

    @Override
    public void start() throws RemoteException {
        gameState = GameState.Playing;
        player1 = new Paddle(pongWidth, pongHeight, 1);
        player2 = new Paddle(pongWidth, pongHeight, 2);
        ball = new Ball(pongWidth, pongHeight);
    }

    @Override
    public Dimension getPongSize() throws RemoteException {
        return new Dimension(pongWidth, pongHeight);
    }

    @Override
    public boolean isBotActive() throws RemoteException {
        return bot;
    }

    @Override
    public void setBotActive(boolean active) throws RemoteException {
        bot = active;
    }

    //players or bot movement update, W and S, UP and DOWN buttons
    @Override
    public void update() throws RemoteException {

        if (gameState != GameState.Playing) {
            return;
        }

        if (player1.score >= scoreLimit) {
            playerWon = 1;
            gameState = GameState.Over;
        }

        if (player2.score >= scoreLimit) {
            playerWon = 2;
            gameState = GameState.Over;
        }

        if (w) {
            player1.move(true);
        }
        if (s) {
            player1.move(false);
        }

        if (!bot) {
            if (up) {
                player2.move(true);
            }
            if (down) {
                player2.move(false);
            }
        } else {
            if (botCooldown > 0) {
                botCooldown--;

                if (botCooldown == 0) {
                    botMoves = 0;
                }
            }

            if (botMoves < 10) {
                if (player2.y + player2.height / 2 < ball.y) {
                    player2.move(false);
                    botMoves++;
                }

                if (player2.y + player2.height / 2 > ball.y) {
                    player2.move(true);
                    botMoves++;
                }

                if (botDifficulty == 0) {
                    botCooldown = 20;
                }
                if (botDifficulty == 1) {
                    botCooldown = 15;
                }
                if (botDifficulty == 2) {
                    botCooldown = 10;
                }
            }
        }

        ball.update(player1, player2);
    }

    @Override
    public void keyPressed(int id) throws RemoteException {

        if (id == KeyEvent.VK_W) {
            w = true;
        } else if (id == KeyEvent.VK_S) {
            s = true;
        } else if (id == KeyEvent.VK_UP) {
            up = true;
        } else if (id == KeyEvent.VK_DOWN) {
            down = true;
        } else if (id == KeyEvent.VK_RIGHT) {
            if (selectingDifficulty) {
                if (botDifficulty < 2) {
                    botDifficulty++;
                } else {
                    botDifficulty = 0;
                }
            } else if (gameState == GameState.Menu) {
                scoreLimit++;
            }
        } else if (id == KeyEvent.VK_LEFT) {
            if (selectingDifficulty) {
                if (botDifficulty > 0) {
                    botDifficulty--;
                } else {
                    botDifficulty = 2;
                }
            } else if (gameState == GameState.Menu && scoreLimit > 1) {
                scoreLimit--;
            }
        } else if (id == KeyEvent.VK_ESCAPE && (gameState == GameState.Playing
                || gameState == GameState.Over)) {

            gameState = GameState.Menu;

        } else if (id == KeyEvent.VK_SHIFT && gameState == GameState.Menu) {
            bot = true;
            selectingDifficulty = true;
        } else if (id == KeyEvent.VK_SPACE) {
            if (gameState == GameState.Menu) {
                if (!selectingDifficulty) {
                    bot = false;
                } else {
                    selectingDifficulty = false;
                }
                
                start();
            } else if (gameState == GameState.Over) {

                start();
            } else if (gameState == GameState.Paused) {
                gameState = GameState.Playing;
            } else if (gameState == GameState.Playing) {
                gameState = GameState.Paused;
            }
        }
    }

    @Override
    public void keyReleased(int id) throws RemoteException {

        if (id == KeyEvent.VK_W) {
            w = false;
        } else if (id == KeyEvent.VK_S) {
            s = false;
        } else if (id == KeyEvent.VK_UP) {
            up = false;
        } else if (id == KeyEvent.VK_DOWN) {
            down = false;
        }
    }

    @Override
    public boolean isSelectingDifficulty() throws RemoteException {
        return selectingDifficulty;
    }

    @Override
    public void setSelectingDifficulty(boolean value) throws RemoteException {
        this.selectingDifficulty = value;
    }

    @Override
    public void setScoreLimit(int score) throws RemoteException {
        scoreLimit = score;
    }

    @Override
    public int getScoreLimit() throws RemoteException {
        return scoreLimit;
    }

    @Override
    public Paddle getPlayer(int number) throws RemoteException {
        if (number == 1) {
            return player1;
        } else if (number == 2) {
            return player2;
        }
        return null;
    }

    @Override
    public Ball getBall() throws RemoteException {
        return ball;
    }

    @Override
    public int getBotDifficulty() throws RemoteException {
        return botDifficulty;
    }

    @Override
    public int getPlayerWon() throws RemoteException {
        return playerWon;
    }

}
