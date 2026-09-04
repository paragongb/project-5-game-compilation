import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConnectFour extends JFrame {
    private final int ROWS = 6;
    private final int COLS = 7;
    
    // 1. The 2D Arrays storing our data and our UI components
    private int[][] board = new int[ROWS][COLS]; // 0 = empty, 1 = red, 2 = yellow
    private JButton[][] buttons = new JButton[ROWS][COLS];
    
    private int currentPlayer = 1; // Player 1 starts
    private boolean gameWon = false;

    public ConnectFour() {
        setTitle("Connect Four");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(ROWS, COLS));

        initializeBoard();
        setVisible(true);
    }

    private void initializeBoard() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                buttons[r][c] = new JButton();
                buttons[r][c].setBackground(Color.WHITE);
                buttons[r][c].setOpaque(true);
                buttons[r][c].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                
                final int col = c; // capture column for the click listener
                buttons[r][c].addActionListener(e -> dropPiece(col));
                
                add(buttons[r][c]);
                board[r][c] = 0; // Initialize logic board to empty
            }
        }
    }

    private void dropPiece(int col) {
        if (gameWon) return;

        // Find the lowest empty row in the clicked column
        for (int r = ROWS - 1; r >= 0; r--) {
            if (board[r][col] == 0) {
                // Update the logical 2D array
                board[r][col] = currentPlayer;
                
                // Update the visual 2D array
                buttons[r][col].setBackground(currentPlayer == 1 ? Color.RED : Color.YELLOW);
                
                if (checkWin(r, col, currentPlayer)) {
                    gameWon = true;
                    JOptionPane.showMessageDialog(this, "Player " + currentPlayer + " Wins!");
                } else {
                    currentPlayer = (currentPlayer == 1) ? 2 : 1; // Swap turns
                }
                return; // Piece dropped, exit the method
            }
        }
        JOptionPane.showMessageDialog(this, "Column is full!");
    }

    private boolean checkWin(int row, int col, int player) {
        // Check all 4 directions from the newly dropped piece
        return countConsecutive(row, col, 1, 0, player) + countConsecutive(row, col, -1, 0, player) >= 3 || // Vertical
               countConsecutive(row, col, 0, 1, player) + countConsecutive(row, col, 0, -1, player) >= 3 || // Horizontal
               countConsecutive(row, col, 1, 1, player) + countConsecutive(row, col, -1, -1, player) >= 3 || // Diagonal \
               countConsecutive(row, col, 1, -1, player) + countConsecutive(row, col, -1, 1, player) >= 3;   // Diagonal /
    }

    private int countConsecutive(int r, int c, int rowDir, int colDir, int player) {
        int count = 0;
        for (int i = 1; i <= 3; i++) {
            int newRow = r + (rowDir * i);
            int newCol = c + (colDir * i);
            
            // Check array boundaries before accessing to prevent errors
            if (newRow >= 0 && newRow < ROWS && newCol >= 0 && newCol < COLS && board[newRow][newCol] == player) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        // Run GUI construction on the Event-Dispatching Thread for thread safety
        SwingUtilities.invokeLater(() -> new ConnectFour());
    }
}