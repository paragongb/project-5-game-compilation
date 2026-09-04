import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;

public class WordleClone extends JFrame {
    // 1. The Hash Set acting as our ultra-fast dictionary
    private HashSet<String> dictionary;
    private final String targetWord = "SMILE"; 
    
    private final int MAX_GUESSES = 6;
    private int currentGuess = 0;
    
    private JLabel[][] grid = new JLabel[MAX_GUESSES][5];
    private JTextField inputField;

    public WordleClone() {
        setTitle("Wordle - Hash Set Edition");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initializeDictionary();
        initializeUI();
        setVisible(true);
    }

    private void initializeDictionary() {
        // In a real game, this would load thousands of words from a text file.
        // We use a small hardcoded list for demonstration.
        String[] words = {"SMILE", "APPLE", "TRAIN", "HOUSE", "GHOST", "BREAD", "WATER"};
        dictionary = new HashSet<>(Arrays.asList(words));
    }

    private void initializeUI() {
        JPanel gridPanel = new JPanel(new GridLayout(MAX_GUESSES, 5, 5, 5));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gridPanel.setBackground(Color.DARK_GRAY);

        for (int r = 0; r < MAX_GUESSES; r++) {
            for (int c = 0; c < 5; c++) {
                grid[r][c] = new JLabel("", SwingConstants.CENTER);
                grid[r][c].setOpaque(true);
                grid[r][c].setBackground(Color.BLACK);
                grid[r][c].setForeground(Color.WHITE);
                grid[r][c].setFont(new Font("Arial", Font.BOLD, 30));
                grid[r][c].setBorder(BorderFactory.createLineBorder(Color.GRAY));
                gridPanel.add(grid[r][c]);
            }
        }
        add(gridPanel, BorderLayout.CENTER);

        inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.BOLD, 20));
        inputField.addActionListener(e -> processGuess());
        add(inputField, BorderLayout.SOUTH);
    }

    private void processGuess() {
        if (currentGuess >= MAX_GUESSES) return;

        String guess = inputField.getText().toUpperCase().trim();
        inputField.setText("");

        if (guess.length() != 5) {
            JOptionPane.showMessageDialog(this, "Guess must be 5 letters!");
            return;
        }

        // 2. O(1) Instant Lookup
        if (!dictionary.contains(guess)) {
            JOptionPane.showMessageDialog(this, "Not in word list!");
            return;
        }

        // Evaluate the guess and color the tiles
        boolean won = true;
        for (int i = 0; i < 5; i++) {
            char guessedChar = guess.charAt(i);
            grid[currentGuess][i].setText(String.valueOf(guessedChar));

            if (guessedChar == targetWord.charAt(i)) {
                grid[currentGuess][i].setBackground(new Color(83, 141, 78)); // Green
            } else if (targetWord.contains(String.valueOf(guessedChar))) {
                grid[currentGuess][i].setBackground(new Color(181, 159, 59)); // Yellow
                won = false;
            } else {
                grid[currentGuess][i].setBackground(new Color(58, 58, 60)); // Dark Gray
                won = false;
            }
        }

        currentGuess++;
        
        if (won) {
            JOptionPane.showMessageDialog(this, "You Won!");
            inputField.setEnabled(false);
        } else if (currentGuess == MAX_GUESSES) {
            JOptionPane.showMessageDialog(this, "Game Over! Word was " + targetWord);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WordleClone());
    }
}