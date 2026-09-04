import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener {
    private final int TILE_SIZE = 25;
    private final int GRID_WIDTH = 20;
    private final int GRID_HEIGHT = 20;
    
    // 1. The Linked List storing our snake's body segments
    private LinkedList<Point> snake;
    private Point apple;
    
    private int dirX = 1; // Moving right initially
    private int dirY = 0;
    private boolean running = false;
    private Timer timer;

    public SnakeGame() {
        setPreferredSize(new Dimension(GRID_WIDTH * TILE_SIZE, GRID_HEIGHT * TILE_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:  if (dirX != 1)  { dirX = -1; dirY = 0; } break;
                    case KeyEvent.VK_RIGHT: if (dirX != -1) { dirX = 1;  dirY = 0; } break;
                    case KeyEvent.VK_UP:    if (dirY != 1)  { dirX = 0;  dirY = -1; } break;
                    case KeyEvent.VK_DOWN:  if (dirY != -1) { dirX = 0;  dirY = 1; } break;
                }
            }
        });

        startGame();
    }

    private void startGame() {
        snake = new LinkedList<>();
        // Create initial snake with 3 segments
        snake.add(new Point(5, 5)); // Head
        snake.add(new Point(4, 5)); // Body
        snake.add(new Point(3, 5)); // Tail
        
        spawnApple();
        running = true;
        timer = new Timer(100, this); // Game loop runs every 100ms
        timer.start();
    }

    private void spawnApple() {
        Random random = new Random();
        int x, y;
        boolean onSnake;
        do {
            onSnake = false;
            x = random.nextInt(GRID_WIDTH);
            y = random.nextInt(GRID_HEIGHT);
            for (Point p : snake) {
                if (p.x == x && p.y == y) onSnake = true;
            }
        } while (onSnake); // Ensure apple doesn't spawn on the snake
        
        apple = new Point(x, y);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkCollision();
        }
        repaint();
    }

    private void move() {
        // Read the current head position
        Point head = snake.getFirst();
        
        // Calculate the new head position based on direction
        Point newHead = new Point(head.x + dirX, head.y + dirY);
        
        // Add the new head to the FRONT of the Linked List
        snake.addFirst(newHead);
        
        // If we ate the apple, keep the tail (snake grows). Otherwise, remove the tail.
        if (newHead.x == apple.x && newHead.y == apple.y) {
            spawnApple();
        } else {
            // Remove the LAST node to simulate forward movement
            snake.removeLast(); 
        }
    }

    private void checkCollision() {
        Point head = snake.getFirst();
        
        // Check wall collision
        if (head.x < 0 || head.x >= GRID_WIDTH || head.y < 0 || head.y >= GRID_HEIGHT) {
            running = false;
        }
        
        // Check self collision (skip the head itself, which is index 0)
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                running = false;
            }
        }
        
        if (!running) timer.stop();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (running) {
            // Draw Apple
            g.setColor(Color.RED);
            g.fillOval(apple.x * TILE_SIZE, apple.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

            // Draw Snake
            for (int i = 0; i < snake.size(); i++) {
                g.setColor(i == 0 ? Color.GREEN : new Color(45, 180, 0)); // Head is brighter
                Point p = snake.get(i);
                g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        } else {
            g.setColor(Color.WHITE);
            g.drawString("Game Over", (GRID_WIDTH * TILE_SIZE) / 2 - 30, (GRID_HEIGHT * TILE_SIZE) / 2);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(new SnakeGame());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}