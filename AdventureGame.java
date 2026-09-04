import javax.swing.*;
import java.awt.*;

public class AdventureGame extends JFrame {
    
    // 1. The Tree Node class representing a single state in our story
    private static class StoryNode {
        String storyText;
        String option1Text;
        String option2Text;
        StoryNode child1;
        StoryNode child2;

        public StoryNode(String storyText, String option1Text, String option2Text) {
            this.storyText = storyText;
            this.option1Text = option1Text;
            this.option2Text = option2Text;
        }
    }

    private StoryNode root;
    private StoryNode currentNode;

    private JTextArea storyArea;
    private JButton option1Button;
    private JButton option2Button;

    public AdventureGame() {
        setTitle("Choose Your Own Adventure - 10 Tiers");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        buildStoryTree();
        currentNode = root; // Start at the root of the tree

        initializeUI();
        updateUI();
        setVisible(true);
    }

    private void buildStoryTree() {
        // Tier 10 (Leaves)
        StoryNode win = new StoryNode("You place the artifact on the pedestal. The curse lifts. You are a hero! (YOU WIN)", "Play Again", "Quit");
        StoryNode death10 = new StoryNode("The pedestal was trapped. The temple collapses on you. (GAME OVER)", "Play Again", "Quit");
        
        // Tier 9
        StoryNode t9 = new StoryNode("You reach the inner sanctum. The altar is protected by a spectral guardian.", "Use the amulet to banish it", "Try to fight it with your sword");
        t9.child1 = win; t9.child2 = death10;
        StoryNode death9 = new StoryNode("The labyrinth shifts, trapping you in a dead end forever. (GAME OVER)", "Play Again", "Quit");

        // Tier 8
        StoryNode t8 = new StoryNode("You enter a maze of mirrors. Illusions of your past try to confuse you.", "Close your eyes and trust your instincts", "Smash the mirrors");
        t8.child1 = t9; t8.child2 = death9;
        StoryNode death8 = new StoryNode("The liquid is acidic. You dissolve instantly. (GAME OVER)", "Play Again", "Quit");

        // Tier 7
        StoryNode t7 = new StoryNode("A locked stone door blocks your path. There is a basin of glowing liquid.", "Pour the liquid on the lock", "Drink the liquid");
        t7.child1 = t8; t7.child2 = death8;
        StoryNode death7 = new StoryNode("The Sphinx is insulted by your silence and devours you. (GAME OVER)", "Play Again", "Quit");

        // Tier 6
        StoryNode t6 = new StoryNode("A stone Sphinx demands an answer: 'What walks on four legs, then two, then three?'", "Man", "Stay silent and attack");
        t6.child1 = t7; t6.child2 = death7;
        StoryNode death6 = new StoryNode("The merchant was an assassin in disguise. (GAME OVER)", "Play Again", "Quit");

        // Tier 5
        StoryNode t5 = new StoryNode("You cross the chasm and find a desert oasis. A cloaked merchant offers you water.", "Politely decline and keep walking", "Accept the water");
        t5.child1 = t6; t5.child2 = death6;
        StoryNode death5 = new StoryNode("The vines were carnivorous plant tentacles. (GAME OVER)", "Play Again", "Quit");

        // Tier 4
        StoryNode t4 = new StoryNode("You arrive at a bottomless chasm. There is a fragile rope bridge.", "Walk across carefully", "Swing across using vines");
        t4.child1 = t5; t4.child2 = death5;
        StoryNode death4 = new StoryNode("You trigger a pressure plate. Poison darts end your journey. (GAME OVER)", "Play Again", "Quit");

        // Tier 3
        StoryNode t3 = new StoryNode("Inside the cave, the path splits. The left is lit by torches, the right is pitch black.", "Take the dark path", "Take the lit path");
        t3.child1 = t4; t3.child2 = death4; // Lit path is a trap
        StoryNode death3 = new StoryNode("The river current sweeps you over a massive waterfall. (GAME OVER)", "Play Again", "Quit");

        // Tier 2
        StoryNode t2 = new StoryNode("You successfully sneak past. You hear rushing water and see a dark cave entrance.", "Enter the cave", "Look for the river");
        t2.child1 = t3; t2.child2 = death3;
        StoryNode death2 = new StoryNode("The goblin wakes up and alerts the horde. You are overwhelmed. (GAME OVER)", "Play Again", "Quit");

        // Tier 1 (Root)
        root = new StoryNode("You stand at the entrance of the Whispering Woods. A sleeping goblin guards the gate.", "Sneak past the goblin", "Attack the goblin");
        root.child1 = t2;
        root.child2 = death2;
    }

    private void initializeUI() {
        storyArea = new JTextArea();
        storyArea.setEditable(false);
        storyArea.setLineWrap(true);
        storyArea.setWrapStyleWord(true);
        storyArea.setFont(new Font("Serif", Font.BOLD, 22));
        storyArea.setBackground(Color.BLACK);
        storyArea.setForeground(Color.WHITE);
        storyArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(new JScrollPane(storyArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        buttonPanel.setBackground(Color.DARK_GRAY);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        option1Button = new JButton();
        option2Button = new JButton();
        
        option1Button.setFont(new Font("SansSerif", Font.BOLD, 14));
        option2Button.setFont(new Font("SansSerif", Font.BOLD, 14));

        option1Button.addActionListener(e -> makeChoice(1));
        option2Button.addActionListener(e -> makeChoice(2));

        buttonPanel.add(option1Button);
        buttonPanel.add(option2Button);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updateUI() {
        storyArea.setText(currentNode.storyText);
        option1Button.setText(currentNode.option1Text);
        option2Button.setText(currentNode.option2Text);
    }

    private void makeChoice(int choice) {
        // Handle Game Over / Win reset
        if (currentNode.child1 == null && currentNode.child2 == null) {
            if (choice == 1) {
                currentNode = root; // Restart
                updateUI();
            } else {
                System.exit(0); // Quit
            }
            return;
        }

        // 2. Traversing the Tree
        if (choice == 1) {
            currentNode = currentNode.child1;
        } else if (choice == 2) {
            currentNode = currentNode.child2;
        }
        updateUI();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdventureGame());
    }
}