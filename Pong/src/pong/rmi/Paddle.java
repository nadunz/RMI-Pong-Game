//This class is made for the paddle and the movement of the paddle
package pong.rmi;

import java.io.Serializable;

//Crates Class Paddle, defining paddleNumber, x and y size and score

public class Paddle implements Serializable {

    public int paddleNumber;

    public int x, y, width = 50, height = 250;//50,250

    public int score;
    
    private int pongWidth, pongHeight;

    public Paddle(int pongWidth, int pongHeight, int paddleNumber)//makes two paddles one for each player
    {
        this.paddleNumber = paddleNumber;
        
        this.pongWidth = pongWidth;
        this.pongHeight = pongHeight;
        
        if (paddleNumber == 1) {
            this.x = 0;
        }

        if (paddleNumber == 2) {
            this.x = pongWidth - width;
        }

        this.y = pongHeight / 2 - this.height / 2;
    }

    public void move(boolean up)//movement of paddle
    {
        int speed = 15;

        if (up) {
            if (y - speed > 0) {
                y -= speed;
            } else {
                y = 0;
            }
        } else {
            if (y + height + speed < pongHeight) {
                y += speed;
            } else {
                y = pongHeight - height;
            }
        }
    }

}
