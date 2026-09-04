import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class DinerDash extends JFrame {
    
    // 1. The Queue storing the incoming customer orders
    private Queue<String> orderQueue;
    private final String[] MENU = {"Burger", "Pizza", "Salad"};
    
    private int score = 0;
    private boolean gameOver = false;
    private Timer timer;
    private GamePanel gamePanel;

    public DinerDash() {
        setTitle("Diner Dash - Queue Edition");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        orderQueue = new LinkedList<>();
        gamePanel = new GamePanel();
        add(gamePanel, BorderLayout.CENTER);

        // Control Panel with Buttons
        JPanel buttonPanel = new JPanel();
        for (String food : MENU) {
            JButton btn = new JButton("Serve " + food);
            btn.setFont(new Font("Arial", Font.BOLD, 16));
            btn.addActionListener(e -> serveOrder(food));
            buttonPanel.add(btn);
        }
        add(buttonPanel, BorderLayout.SOUTH);

        startGame();
        setVisible(true);
    }

    private void startGame() {
        // Timer adds a new random order to the queue every 2 seconds
        timer = new Timer(2000, e -> {
            if (gameOver) return;
            
            Random rand = new Random();
            String newOrder = MENU[rand.nextInt(MENU.length)];
            
            // Add the new order to the back of the queue
            orderQueue.offer(newOrder); 
            
            // If the line gets too long (10 orders), game over!
            if (orderQueue.size() > 10) {
                gameOver = true;
                timer.stop();
            }
            gamePanel.repaint();
        });
        timer.start();
    }

    private void serveOrder(String servedFood) {
        if (gameOver || orderQueue.isEmpty()) return;

        // Look at the first order in line without removing it
        String currentOrder = orderQueue.peek(); 

        if (servedFood.equals(currentOrder)) {
            // Correct order! Remove it from the front of the queue
            orderQueue.poll(); 
            score += 10;
        } else {
            // Wrong order penalty
            score -= 5; 
        }
        gamePanel.repaint();
    }

    // Inner class to handle drawing the game state
    private class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(Color.DARK_GRAY);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            
            if (gameOver) {
                g.setColor(Color.RED);
                g.drawString("GAME OVER! Kitchen Overwhelmed. Score: " + score, 80, 150);
                return;
            }

            g.drawString("Score: " + score, 20, 30);
            g.drawString("Current Orders (Serve left to right!):", 20, 80);

            // Draw the Queue visually
            int x = 20;
            for (String order : orderQueue) {
                // Color code the tickets
                if (order.equals("Burger")) g.setColor(new Color(200, 150, 50));
                else if (order.equals("Pizza")) g.setColor(new Color(220, 80, 80));
                else g.setColor(new Color(100, 200, 100)); // Salad

                g.fillRect(x, 100, 80, 100);
                g.setColor(Color.BLACK);
                g.drawRect(x, 100, 80, 100);
                
                g.setColor(Color.WHITE);
                g.drawString(order, x + 10, 155);
                x += 90; // Move right for the next order ticket
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DinerDash());
    }
}