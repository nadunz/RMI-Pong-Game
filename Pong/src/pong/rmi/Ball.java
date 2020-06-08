//This class is made for the ball and the movement of the ball
package pong.rmi;

import java.io.Serializable;
import java.util.Random;

public class Ball implements Serializable {

    public int x, y, width = 25, height = 25;

    private int motionX, motionY;

    private Random random;

    private int pongWidth, pongHeight;

    private int amountOfHits;

    public Ball(int pongWidth, int pongHeight)//class for ball
    {
        this.pongWidth = pongWidth;
        this.pongHeight = pongHeight;

        this.random = new Random();

        spawn();
    }

    public void update(Paddle paddle1, Paddle paddle2)//updates position and movement of ball
    {
        int speed = 5;//5

        this.x += motionX * speed;
        this.y += motionY * speed;

        if (this.y + height - motionY > pongHeight || this.y + motionY < 0) {
            if (this.motionY < 0) {
                this.y = 0;
                this.motionY = random.nextInt(4);

                if (motionY == 0) {
                    motionY = 1;
                }
            } else {
                this.motionY = -random.nextInt(4);
                this.y = pongHeight - height;

                if (motionY == 0) {
                    motionY = -1;
                }
            }
        }

        if (checkCollision(paddle1) == 1) {
            this.motionX = 1 + (amountOfHits / 5);
            this.motionY = -2 + random.nextInt(4);

            if (motionY == 0) {
                motionY = 1;
            }

            amountOfHits++;
        } else if (checkCollision(paddle2) == 1) {
            this.motionX = -1 - (amountOfHits / 5);
            this.motionY = -2 + random.nextInt(4);

            if (motionY == 0) {
                motionY = 1;
            }

            amountOfHits++;
        }

        if (checkCollision(paddle1) == 2) {
            paddle2.score++;
            spawn();
        } else if (checkCollision(paddle2) == 2) {
            paddle1.score++;
            spawn();
        }
    }

    public void spawn()//spawns ball with movement and random direction
    {
        this.amountOfHits = 0;
        this.x = pongWidth / 2 - this.width / 2;
        this.y = pongHeight / 2 - this.height / 2;

        this.motionY = -2 + random.nextInt(4);

        if (motionY == 0) {
            motionY = 1;
        }

        if (random.nextBoolean()) {
            motionX = 1;
        } else {
            motionX = -1;
        }
    }

    public int checkCollision(Paddle paddle)//check collision with paddles to make chose to bounce of or score
    {
        if (this.x < paddle.x + paddle.width && this.x + width > paddle.x && this.y < paddle.y + paddle.height && this.y + height > paddle.y) {
            return 1; //bounce
        } else if ((paddle.x > x && paddle.paddleNumber == 1) || (paddle.x < x - width && paddle.paddleNumber == 2)) {
            return 2; //score
        }

        return 0; //nothing
    }

}
