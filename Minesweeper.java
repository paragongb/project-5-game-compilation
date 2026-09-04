import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class Minesweeper extends JFrame {
    private final int ROWS = 10;
    private final int COLS = 10;
    private final int MINES = 10;
    private final int WIN_CONDITION = (ROWS * COLS) - MINES;
    
    // 1. The Nodes of our Graph
    private Cell[][] grid = new Cell[ROWS][COLS];
    private boolean gameOver = false;
    private int cellsRevealed = 0;

    // Encapsulated Cell object acting as a Graph Node
    private class Cell extends JButton {
        int row, col;
        boolean isMine = false;
        boolean isRevealed = false;
        boolean isFlagged = false;
        int adjacentMines = 0;

        public Cell(int r, int c) {
            this.row = r;
            this.col = c;
            setFont(new Font("Arial", Font.BOLD, 12));
            setMargin(new Insets(0,0,0,0));
            setBackground(new Color(200, 200, 200));
        }
    }

    public Minesweeper() {
        setTitle("Minesweeper - Graph & DFS Edition");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(ROWS, COLS));

        initializeGrid();
        placeMines();
        calculateAdjacentMines();
        setVisible(true);
    }

    private void initializeGrid() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Cell cell = new Cell(r, c);
                grid[r][c] = cell;
                add(cell);

                cell.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameOver) return;
                        if (SwingUtilities.isRightMouseButton(e)) {
                            toggleFlag(cell);
                        } else if (SwingUtilities.isLeftMouseButton(e)) {
                            if (!cell.isFlagged) revealCell(cell.row, cell.col);
                        }
                        checkWinCondition();
                    }
                });
            }
        }
    }

    private void placeMines() {
        Random rand = new Random();
        int minesPlaced = 0;
        while (minesPlaced < MINES) {
            int r = rand.nextInt(ROWS);
            int c = rand.nextInt(COLS);
            if (!grid[r][c].isMine) {
                grid[r][c].isMine = true;
                minesPlaced++;
            }
        }
    }

    private void calculateAdjacentMines() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (grid[r][c].isMine) continue;
                
                int count = 0;
                // Check all 8 adjacent directions (The Edges of our Graph)
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        int nr = r + i;
                        int nc = c + j;
                        if (nr >= 0 && nr < ROWS && nc >= 0 && nc < COLS && grid[nr][nc].isMine) {
                            count++;
                        }
                    }
                }
                grid[r][c].adjacentMines = count;
            }
        }
    }

    private void toggleFlag(Cell cell) {
        if (cell.isRevealed) return;
        cell.isFlagged = !cell.isFlagged;
        cell.setText(cell.isFlagged ? "F" : "");
        cell.setForeground(Color.RED);
    }

    // 2. Traversing the Graph using Depth-First Search (DFS)
    private void revealCell(int r, int c) {
        // Base Cases: Out of bounds, already revealed, or flagged
        if (r < 0 || r >= ROWS || c < 0 || c >= COLS) return;
        Cell cell = grid[r][c];
        if (cell.isRevealed || cell.isFlagged) return;

        cell.isRevealed = true;
        cell.setBackground(Color.WHITE);

        if (cell.isMine) {
            cell.setText("M");
            cell.setBackground(Color.RED);
            gameOver = true;
            JOptionPane.showMessageDialog(this, "Boom! Game Over.");
            return;
        }

        cellsRevealed++;
        
        if (cell.adjacentMines > 0) {
            // It has adjacent mines, so we display the number and stop traversing here.
            cell.setText(String.valueOf(cell.adjacentMines));
        } else {
            // It is an empty cell (0 adjacent mines), so we traverse its neighbors (DFS)
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    revealCell(r + i, c + j);
                }
            }
        }
    }

    private void checkWinCondition() {
        if (!gameOver && cellsRevealed == WIN_CONDITION) {
            gameOver = true;
            JOptionPane.showMessageDialog(this, "You cleared the minefield!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Minesweeper());
    }
}