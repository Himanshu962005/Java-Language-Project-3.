import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;

public class ResultFrame extends JFrame {
    JPanel detailsPanel;
    JPanel answersPanel;
    JScrollPane scrollPane;

    public ResultFrame(String name, String anime, int score, int xp, int[] userAnswers, List<Question> questions) {
        setTitle("Quiz Result - " + anime);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(15, 23, 42));
        // ✅ FIX: Load Player Profile from Saved Data.
        PlayerProfile profile = PlayerProfile.load(name);
        if (profile == null) {
            // New player - Create Profile.
            profile = new PlayerProfile(name);
        }

        // ✅ FIX: Update Player Stats with Quiz Results.
        profile.updateStats(score, questions.size(), xp, anime);

        // ✅ FIX: Use TOTAL XP from Profile (Not Just Session XP).
        int level = profile.getLevel();
        String rank = profile.getRank();
        java.util.List<String> achievements = AchievementSystem.getAchievements(profile.totalXP);

        // Top Panel - Results Summary.
        detailsPanel = new JPanel();
        detailsPanel.setBounds(20, 20, 850, 150);
        detailsPanel.setBackground(new Color(30, 40, 60));
        detailsPanel.setBorder(new LineBorder(new Color(99, 102, 241), 2));
        detailsPanel.setLayout(null);

        JLabel playerLabel = new JLabel("Player : " + name);
        playerLabel.setBounds(20, 10, 300, 25);
        playerLabel.setForeground(Color.WHITE);
        playerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        detailsPanel.add(playerLabel);

        JLabel animeLabel = new JLabel("Anime : " + anime);
        animeLabel.setBounds(20, 40, 300, 25);
        animeLabel.setForeground(Color.CYAN);
        animeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        detailsPanel.add(animeLabel);

        JLabel scoreLabel = new JLabel("Score : " + score + "/" + questions.size());
        scoreLabel.setBounds(20, 70, 300, 25);
        scoreLabel.setForeground(new Color(34, 197, 94));
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 14));
        detailsPanel.add(scoreLabel);

        double percentage = (double) score / questions.size() * 100;
        JLabel percentLabel = new JLabel(String.format("Accuracy: %.1f%%", percentage));
        percentLabel.setBounds(20, 100, 300, 25);
        percentLabel.setForeground(new Color(251, 146, 60));
        percentLabel.setFont(new Font("Arial", Font.BOLD, 14));
        detailsPanel.add(percentLabel);

        // ✅ FIX: Show TOTAL XP From Profile.
        JLabel xpLabel = new JLabel("Total XP : " + profile.totalXP);
        xpLabel.setBounds(420, 10, 300, 25);
        xpLabel.setForeground(new Color(168, 85, 247));
        xpLabel.setFont(new Font("Arial", Font.BOLD, 14));
        detailsPanel.add(xpLabel);

        JLabel levelLabel = new JLabel("Level : " + level);
        levelLabel.setBounds(420, 40, 300, 25);
        levelLabel.setForeground(Color.YELLOW);
        levelLabel.setFont(new Font("Arial", Font.BOLD, 14));
        detailsPanel.add(levelLabel);

        JLabel rankLabel = new JLabel("Rank : " + rank);
        rankLabel.setBounds(420, 70, 300, 25);
        rankLabel.setForeground(new Color(255, 165, 0));
        rankLabel.setFont(new Font("Arial", Font.BOLD, 14));
        detailsPanel.add(rankLabel);

        String achievementText = achievements.isEmpty() ? "None" : String.join(", ", achievements);
        JLabel achievementLabel = new JLabel("Achievements: " + achievementText);
        achievementLabel.setBounds(420, 100, 400, 25);
        achievementLabel.setForeground(new Color(59, 130, 246));
        achievementLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        detailsPanel.add(achievementLabel);

        add(detailsPanel);

        // Answers Review Panel.
        answersPanel = new JPanel();
        answersPanel.setLayout(new BoxLayout(answersPanel, BoxLayout.Y_AXIS));
        answersPanel.setBackground(new Color(15, 23, 42));

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            int userAnswer = userAnswers[i];
            int correctAnswer = q.correct;
            boolean isCorrect = (userAnswer == correctAnswer);

            JPanel questionPanel = new JPanel();
            questionPanel.setLayout(null);
            questionPanel.setPreferredSize(new Dimension(800, 120));
            questionPanel.setBackground(isCorrect ? new Color(20, 40, 25) : new Color(40, 20, 20));
            questionPanel.setBorder(new LineBorder(isCorrect ? new Color(34, 197, 94) : new Color(239, 68, 68), 2));

            JLabel qLabel = new JLabel((i + 1) + ". " + q.question);
            qLabel.setBounds(15, 10, 770, 25);
            qLabel.setForeground(Color.WHITE);
            qLabel.setFont(new Font("Arial", Font.BOLD, 13));
            questionPanel.add(qLabel);

            JLabel userAnswerLabel = new JLabel("Your Answer : "
                    + (userAnswer >= 0 && userAnswer < q.options.length ? q.options[userAnswer] : "Not answered"));
            userAnswerLabel.setBounds(15, 40, 770, 20);
            userAnswerLabel.setForeground(new Color(255, 200, 124));
            userAnswerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            questionPanel.add(userAnswerLabel);

            if (!isCorrect) {
                JLabel correctAnswerLabel = new JLabel("Correct Answer : " + q.options[correctAnswer]);
                correctAnswerLabel.setBounds(15, 65, 770, 20);
                correctAnswerLabel.setForeground(new Color(34, 197, 94));
                correctAnswerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
                questionPanel.add(correctAnswerLabel);
            }

            String resultText = isCorrect ? "✓ Correct" : "✗ Wrong";
            Color resultColor = isCorrect ? new Color(34, 197, 94) : new Color(239, 68, 68);
            JLabel resultLabel = new JLabel(resultText);
            resultLabel.setBounds(700, 40, 100, 40);
            resultLabel.setForeground(resultColor);
            resultLabel.setFont(new Font("Arial", Font.BOLD, 14));
            questionPanel.add(resultLabel);

            answersPanel.add(questionPanel);
            answersPanel.add(Box.createVerticalStrut(10));
        }

        scrollPane = new JScrollPane(answersPanel);
        scrollPane.setBounds(20, 180, 850, 390);
        scrollPane.setBackground(new Color(15, 23, 42));
        scrollPane.getViewport().setBackground(new Color(15, 23, 42));
        add(scrollPane);

        // Buttons.
        JButton playAgain = new JButton("Play Again");
        playAgain.setBounds(250, 600, 140, 40);
        playAgain.setBackground(new Color(99, 102, 241));
        playAgain.setForeground(Color.WHITE);
        playAgain.setFont(new Font("Arial", Font.BOLD, 14));

        playAgain.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                playAgain.setBackground(new Color(139, 92, 246));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                playAgain.setBackground(new Color(99, 102, 241));
            }
        });

        playAgain.addActionListener(e -> {
            new StartFrame();
            dispose();
        });

        add(playAgain);

        JButton menu = new JButton("Main Menu");
        menu.setBounds(510, 600, 140, 40);
        menu.setBackground(new Color(139, 92, 246));
        menu.setForeground(Color.WHITE);
        menu.setFont(new Font("Arial", Font.BOLD, 14));

        menu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                menu.setBackground(new Color(168, 85, 247));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                menu.setBackground(new Color(139, 92, 246));
            }
        });

        menu.addActionListener(e -> {
            new StartFrame();
            dispose();
        });

        add(menu);

        setVisible(true);
    }
}