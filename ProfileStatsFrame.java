import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class ProfileStatsFrame extends JFrame {
    public ProfileStatsFrame(PlayerProfile profile) {
        setTitle("Profile Stats - " + profile.name);
        setSize(700, 650);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(15, 23, 42));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // ===== PLAYER NAME HEADER =====.
        JLabel playerNameLabel = new JLabel(profile.name.toUpperCase());
        playerNameLabel.setBounds(50, 20, 600, 35);
        playerNameLabel.setForeground(new Color(99, 102, 241));
        playerNameLabel.setFont(new Font("Arial", Font.BOLD, 26));
        add(playerNameLabel);
        // ===== MAIN STATS PANEL =====.
        JPanel statsPanel = new JPanel();
        statsPanel.setBounds(50, 65, 600, 150);
        statsPanel.setBackground(new Color(30, 40, 60));
        statsPanel.setBorder(new LineBorder(new Color(99, 102, 241), 2));
        statsPanel.setLayout(null);
        // Level.
        JLabel levelLabel = new JLabel("Level: " + profile.getLevel());
        levelLabel.setBounds(20, 20, 150, 25);
        levelLabel.setForeground(Color.YELLOW);
        levelLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statsPanel.add(levelLabel);
        // Rank.
        JLabel rankLabel = new JLabel("Rank: " + profile.getRank());
        rankLabel.setBounds(20, 50, 250, 25);
        rankLabel.setForeground(new Color(255, 165, 0));
        rankLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statsPanel.add(rankLabel);
        // Total XP.
        JLabel xpLabel = new JLabel("Total XP: " + profile.totalXP);
        xpLabel.setBounds(20, 80, 250, 25);
        xpLabel.setForeground(new Color(168, 85, 247));
        xpLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statsPanel.add(xpLabel);
        // Games Played.
        JLabel gamesLabel = new JLabel("Games Played: " + profile.gamesPlayed);
        gamesLabel.setBounds(350, 20, 200, 25);
        gamesLabel.setForeground(Color.WHITE);
        gamesLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statsPanel.add(gamesLabel);
        // Total Score.
        JLabel scoreLabel = new JLabel("Total Score: " + profile.totalScore);
        scoreLabel.setBounds(350, 50, 200, 25);
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statsPanel.add(scoreLabel);
        // Accuracy.
        JLabel accuracyLabel = new JLabel(String.format("Accuracy: %.1f%%", profile.getAccuracy()));
        accuracyLabel.setBounds(350, 80, 200, 25);
        accuracyLabel.setForeground(new Color(34, 197, 94));
        accuracyLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statsPanel.add(accuracyLabel);
        add(statsPanel);
        // ===== ANIME STATS SECTION =====.
        JLabel animeStatsLabel = new JLabel("BEST SCORES BY ANIME");
        animeStatsLabel.setBounds(50, 225, 300, 25);
        animeStatsLabel.setForeground(new Color(99, 102, 241));
        animeStatsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(animeStatsLabel);
        // Create Anime Stats Panel.
        JPanel animePanel = new JPanel();
        animePanel.setLayout(new BoxLayout(animePanel, BoxLayout.Y_AXIS));
        animePanel.setBackground(new Color(15, 23, 42));
        String[] animes = {
                "Beyblade", "Pokemon", "Naruto and Boruto", "Miraculous",
                "One Punch Man", "Ben 10", "Jujutsu Kaisen", "Lookism",
                "Solo Leveling", "Ranma 1/2"
        };
        for (String anime : animes) {
            int bestScore = profile.animeStats.getOrDefault(anime, 0);
            JPanel animeItemPanel = new JPanel();
            animeItemPanel.setLayout(null);
            animeItemPanel.setPreferredSize(new Dimension(550, 35));
            animeItemPanel.setBackground(bestScore > 0 ? new Color(25, 35, 55) : new Color(20, 30, 50));
            animeItemPanel.setBorder(new LineBorder(new Color(50, 60, 80), 1));
            JLabel animeNameLabel = new JLabel(anime);
            animeNameLabel.setBounds(15, 5, 300, 25);
            animeNameLabel.setForeground(Color.WHITE);
            animeNameLabel.setFont(new Font("Arial", Font.PLAIN, 13));
            animeItemPanel.add(animeNameLabel);
            String scoreText = bestScore > 0 ? "Best: " + bestScore + "/10" : "Not played";
            JLabel scoreTextLabel = new JLabel(scoreText);
            scoreTextLabel.setBounds(450, 5, 100, 25);
            scoreTextLabel.setForeground(bestScore > 0 ? new Color(34, 197, 94) : new Color(150, 150, 150));
            scoreTextLabel.setFont(new Font("Arial", Font.BOLD, 12));
            scoreTextLabel.setHorizontalAlignment(JLabel.RIGHT);
            animeItemPanel.add(scoreTextLabel);
            animePanel.add(animeItemPanel);
            animePanel.add(Box.createVerticalStrut(5));
        }
        JScrollPane animeScrollPane = new JScrollPane(animePanel);
        animeScrollPane.setBounds(50, 255, 600, 280);
        animeScrollPane.setBackground(new Color(15, 23, 42));
        animeScrollPane.getViewport().setBackground(new Color(15, 23, 42));
        animeScrollPane.setBorder(new LineBorder(new Color(50, 60, 80), 1));
        add(animeScrollPane);
        // ===== CLOSE BUTTON =====.
        JButton closeBtn = new JButton("Close");
        closeBtn.setBounds(300, 550, 100, 40);
        closeBtn.setBackground(new Color(139, 92, 246));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFont(new Font("Arial", Font.BOLD, 14));
        closeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                closeBtn.setBackground(new Color(168, 85, 247));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                closeBtn.setBackground(new Color(139, 92, 246));
            }
        });
        closeBtn.addActionListener(e -> dispose());
        add(closeBtn);
        setVisible(true);
    }
}