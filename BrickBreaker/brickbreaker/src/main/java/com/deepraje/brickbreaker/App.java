package com.deepraje.brickbreaker;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;

public class App extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private Circle ball;
    private Rectangle paddle;

    private Text scoreText;
    private Text livesText;
    private Text messageText;

    private ArrayList<Rectangle> bricks;

    private double ballDX = 4;
    private double ballDY = -4;

    private int score = 0;
    private int lives = 3;

    private boolean leftPressed = false;
    private boolean rightPressed = false;

    private Pane root;
    private AnimationTimer timer;

    @Override
    public void start(Stage stage) {

        root = new Pane();
        root.setFocusTraversable(true);

        createGameObjects();

        Scene scene = new Scene(root, WIDTH, HEIGHT);

        scene.setOnKeyPressed(e -> {

            if (e.getCode() == KeyCode.LEFT)
                leftPressed = true;

            if (e.getCode() == KeyCode.RIGHT)
                rightPressed = true;

            if (e.getCode() == KeyCode.R)
                restartGame();
        });

        scene.setOnKeyReleased(e -> {

            if (e.getCode() == KeyCode.LEFT)
                leftPressed = false;

            if (e.getCode() == KeyCode.RIGHT)
                rightPressed = false;
        });

        startGameLoop();

        stage.setTitle("Brick Breaker");
        stage.setScene(scene);
        stage.show();

        root.requestFocus();
    }

    private void createGameObjects() {

        root.getChildren().clear();

        paddle = new Rectangle(120, 15);
        paddle.setFill(Color.DODGERBLUE);
        paddle.setX((WIDTH - 120) / 2);
        paddle.setY(HEIGHT - 50);

        ball = new Circle(10);
        ball.setFill(Color.RED);
        ball.setCenterX(WIDTH / 2);
        ball.setCenterY(HEIGHT / 2);

        scoreText = new Text("Score: " + score);
        scoreText.setX(20);
        scoreText.setY(30);
        scoreText.setStyle("-fx-font-size:20px;");

        livesText = new Text("Lives: " + lives);
        livesText.setX(680);
        livesText.setY(30);
        livesText.setStyle("-fx-font-size:20px;");

        messageText = new Text("");
        messageText.setX(220);
        messageText.setY(320);
        messageText.setStyle("-fx-font-size:40px;");

        bricks = new ArrayList<>();

        for (int row = 0; row < 3; row++) {

            for (int col = 0; col < 8; col++) {

                Rectangle brick = new Rectangle(90, 30);

                brick.setX(20 + col * 95);
                brick.setY(60 + row * 40);

                if (row == 0)
                    brick.setFill(Color.RED);
                else if (row == 1)
                    brick.setFill(Color.ORANGE);
                else
                    brick.setFill(Color.LIMEGREEN);

                bricks.add(brick);
                root.getChildren().add(brick);
            }
        }

        root.getChildren().addAll(
                scoreText,
                livesText,
                messageText,
                paddle,
                ball
        );
    }

    private void startGameLoop() {

        timer = new AnimationTimer() {

            @Override
            public void handle(long now) {

                movePaddle();
                moveBall();
                checkWallCollision();
                checkPaddleCollision();
                checkBrickCollision();
                checkLoseLife();
                checkWin();
            }
        };

        timer.start();
    }

    private void movePaddle() {

        if (leftPressed && paddle.getX() > 0)
            paddle.setX(paddle.getX() - 8);

        if (rightPressed &&
                paddle.getX() < WIDTH - paddle.getWidth())
            paddle.setX(paddle.getX() + 8);
    }

    private void moveBall() {

        ball.setCenterX(ball.getCenterX() + ballDX);
        ball.setCenterY(ball.getCenterY() + ballDY);
    }

    private void checkWallCollision() {

        if (ball.getCenterX() <= 10 ||
                ball.getCenterX() >= WIDTH - 10)
            ballDX *= -1;

        if (ball.getCenterY() <= 10)
            ballDY *= -1;
    }

    private void checkPaddleCollision() {

        if (ball.getBoundsInParent()
                .intersects(paddle.getBoundsInParent())) {

            double hitPosition =
                    (ball.getCenterX() - paddle.getX())
                            / paddle.getWidth();

            ballDX = (hitPosition - 0.5) * 10;
            ballDY = -Math.abs(ballDY);
        }
    }

    private void checkBrickCollision() {

        for (int i = 0; i < bricks.size(); i++) {

            Rectangle brick = bricks.get(i);

            if (ball.getBoundsInParent()
                    .intersects(brick.getBoundsInParent())) {

                if (brick.getFill().equals(Color.RED))
                    score += 30;
                else if (brick.getFill().equals(Color.ORANGE))
                    score += 20;
                else
                    score += 10;

                scoreText.setText("Score: " + score);

                root.getChildren().remove(brick);
                bricks.remove(i);

                ballDY *= -1;

                break;
            }
        }
    }

    private void checkLoseLife() {

        if (ball.getCenterY() > HEIGHT) {

            lives--;

            livesText.setText("Lives: " + lives);

            ball.setCenterX(WIDTH / 2);
            ball.setCenterY(HEIGHT / 2);

            ballDX = 4;
            ballDY = -4;

            if (lives <= 0) {

                messageText.setText("GAME OVER");

                timer.stop();
            }
        }
    }

    private void checkWin() {

        if (bricks.isEmpty()) {

            messageText.setText("YOU WIN!");

            timer.stop();
        }
    }

    private void restartGame() {

        score = 0;
        lives = 3;

        ballDX = 4;
        ballDY = -4;

        if (timer != null)
            timer.stop();

        createGameObjects();
        startGameLoop();

        root.requestFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }
}