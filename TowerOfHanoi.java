import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Stack;

public class TowerOfHanoi extends JPanel {
    // 1. The Stacks storing the disks for each of the 3 pegs
    private Stack<Integer>[] pegs;
    private int selectedPeg = -1; // -1 means no peg is currently selected
    private final int NUM_DISKS = 4;

    @SuppressWarnings("unchecked")
    public TowerOfHanoi() {
        pegs = new Stack[3];
        for (int i = 0; i < 3; i++) {
            pegs[i] = new Stack<>();
        }

        // Push disks onto the first peg (largest disk first)
        for (int i = NUM_DISKS; i >= 1; i--) {
            pegs[0].push(i);
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int colWidth = getWidth() / 3;
                int clickedPeg = e.getX() / colWidth; // Determine which peg was clicked (0, 1, or 2)

                if (selectedPeg == -1) {
                    // Step 1: Selecting a peg to pick up a disk
                    if (!pegs[clickedPeg].isEmpty()) {
                        selectedPeg = clickedPeg;
                    }
                } else {
                    // Step 2: Selecting a destination peg to drop the disk
                    if (selectedPeg != clickedPeg) {
                        // Game Rule: Can only place on an empty peg OR on top of a larger disk
                        if (pegs[clickedPeg].isEmpty() || pegs[clickedPeg].peek() > pegs[selectedPeg].peek()) {
                            // Pop from source, push to destination
                            int disk = pegs[selectedPeg].pop();
                            pegs[clickedPeg].push(disk);
                        }
                    }
                    selectedPeg = -1; // Deselect after a move attempt
                }
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.WHITE);
        int colWidth = getWidth() / 3;
        int pegBottom = getHeight() - 50;

        for (int p = 0; p < 3; p++) {
            // Draw Pegs
            g.setColor(p == selectedPeg ? Color.RED : Color.DARK_GRAY);
            g.fillRect(p * colWidth + colWidth / 2 - 5, 100, 10, getHeight() - 150);
            g.fillRect(p * colWidth + colWidth / 4, pegBottom, colWidth / 2, 10);

            // Draw Disks based on Stack contents
            Stack<Integer> currentPeg = pegs[p];
            for (int i = 0; i < currentPeg.size(); i++) {
                int diskSize = currentPeg.get(i);
                int diskWidth = 40 + (diskSize * 20);
                int diskHeight = 20;
                int x = p * colWidth + colWidth / 2 - diskWidth / 2;
                int y = pegBottom - ((i + 1) * diskHeight);

                g.setColor(new Color(100, 150 + (diskSize * 20) % 105, 200));
                g.fillRoundRect(x, y, diskWidth, diskHeight, 10, 10);
                g.setColor(Color.BLACK);
                g.drawRoundRect(x, y, diskWidth, diskHeight, 10, 10);
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tower of Hanoi");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.add(new TowerOfHanoi());
        frame.setVisible(true);
    }
}