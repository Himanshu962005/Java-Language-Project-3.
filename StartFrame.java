import javax.swing.*;
import java.awt.*;

public class StartFrame extends JFrame {
    JTextField nameField;
    JComboBox<String> animeCombo;
    JComboBox<String> difficultyCombo;
    JComboBox<Integer> questionCountCombo;

    public StartFrame() {
        setTitle("Anime Quiz - Main Menu");
        setSize(500, 500);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(15, 23, 42));
        JLabel title = new JLabel("ANIME QUIZ");
        title.setBounds(130, 20, 250, 40);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        add(title);
        // Player Name.
        JLabel nameLabel = new JLabel("Player Name : ");
        nameLabel.setBounds(40, 80, 120, 30);
        nameLabel.setForeground(Color.WHITE);
        add(nameLabel);
        nameField = new JTextField();
        nameField.setBounds(160, 80, 280, 30);
        nameField.setBackground(new Color(30, 40, 60));
        nameField.setForeground(Color.WHITE);
        add(nameField);
        // Anime Selection.
        JLabel animeLabel = new JLabel("Select Anime:");
        animeLabel.setBounds(40, 130, 120, 30);
        animeLabel.setForeground(Color.WHITE);
        add(animeLabel);
        animeCombo = new JComboBox<>(new String[] {
                "Beyblade",
                "Pokemon",
                "Naruto and Boruto",
                "Miraculous",
                "One Punch Man",
                "Ben 10",
                "Jujutsu Kaisen",
                "Lookism",
                "Solo Leveling",
                "Ranma 1/2"
        });
        animeCombo.setBounds(160, 130, 280, 30);
        animeCombo.setBackground(new Color(99, 102, 241));
        animeCombo.setForeground(Color.WHITE);
        add(animeCombo);
        // Difficulty Selection.
        JLabel difficultyLabel = new JLabel("Difficulty : ");
        difficultyLabel.setBounds(40, 180, 120, 30);
        difficultyLabel.setForeground(Color.WHITE);
        add(difficultyLabel);
        difficultyCombo = new JComboBox<>(new String[] {
                "Easy (10 seconds)",
                "Medium (20 seconds)",
                "Hard (30 seconds)"
        });
        difficultyCombo.setBounds(160, 180, 280, 30);
        difficultyCombo.setBackground(new Color(99, 102, 241));
        difficultyCombo.setForeground(Color.WHITE);
        add(difficultyCombo);
        // Number of Questions.
        JLabel questionLabel = new JLabel("Questions:");
        questionLabel.setBounds(40, 230, 120, 30);
        questionLabel.setForeground(Color.WHITE);
        add(questionLabel);
        Integer[] questionCounts = new Integer[20];
        for (int i = 0; i < 20; i++) {
            questionCounts[i] = (i + 1) * 5; // 5, 10, 15, 20, ... 100
        }
        questionCountCombo = new JComboBox<>(questionCounts);
        questionCountCombo.setSelectedItem(10); // Default 10.
        questionCountCombo.setBounds(160, 230, 280, 30);
        questionCountCombo.setBackground(new Color(99, 102, 241));
        questionCountCombo.setForeground(Color.WHITE);
        add(questionCountCombo);
        // Start Button.
        JButton start = new JButton("Start Quiz");
        start.setBounds(160, 310, 280, 45);
        start.setBackground(new Color(99, 102, 241));
        start.setForeground(Color.WHITE);
        start.setFont(new Font("Arial", Font.BOLD, 16));
        start.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                start.setBackground(new Color(139, 92, 246));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                start.setBackground(new Color(99, 102, 241));
            }
        });
        start.addActionListener(e -> startQuiz());
        add(start);
        setVisible(true);
    }

    void startQuiz() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please Enter Your Name!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String anime = (String) animeCombo.getSelectedItem();
        String difficulty = (String) difficultyCombo.getSelectedItem();
        int questionCount = (Integer) questionCountCombo.getSelectedItem();
        int timePerQuestion = extractTime(difficulty);
        new QuizFrame(name, anime, timePerQuestion, questionCount);
        dispose();
    }

    int extractTime(String difficulty) {
        if (difficulty.contains("10"))
            return 10;
        if (difficulty.contains("20"))
            return 20;
        if (difficulty.contains("30"))
            return 30;
        return 15;
    }
}