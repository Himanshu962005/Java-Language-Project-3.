import javax.swing.*;
import java.awt.*;

public class QuizFrame extends JFrame {
    java.util.List<Question> questions;
    JLabel questionLabel;
    JRadioButton[] options = new JRadioButton[4];
    JLabel animeLabel;
    JLabel progressLabel;
    int index = 0;
    int score = 0;
    int xp = 0;
    javax.swing.Timer timer;
    int time = 15;
    int timePerQuestion = 15;
    JLabel timerLabel;
    String player;
    String anime;
    int totalQuestions;
    // Track User Answers.
    int[] userAnswers;

    public QuizFrame(String player, String anime, int timePerQuestion, int questionCount) {
        this.player = player;
        this.anime = anime;
        this.timePerQuestion = timePerQuestion;
        this.time = timePerQuestion;
        this.totalQuestions = questionCount;
        questions = QuestionBank.getQuestions(anime, questionCount);
        this.totalQuestions = questions.size(); // Update in Case Fewer Questions are Available.
        userAnswers = new int[questions.size()];
        setTitle("Anime Quiz - " + anime);
        setSize(900, 650);
        setLayout(null);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(18, 18, 18));
        // Anime Title.
        animeLabel = new JLabel(anime.toUpperCase());
        animeLabel.setBounds(50, 10, 400, 30);
        animeLabel.setForeground(new Color(99, 102, 241));
        animeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(animeLabel);
        // Progress.
        progressLabel = new JLabel("Question 1 of " + totalQuestions);
        progressLabel.setBounds(50, 45, 300, 20);
        progressLabel.setForeground(Color.CYAN);
        progressLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        add(progressLabel);
        // Question Label.
        questionLabel = new JLabel();
        questionLabel.setBounds(50, 80, 800, 50);
        questionLabel.setForeground(Color.WHITE);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(questionLabel);
        // Options.
        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < 4; i++) {
            options[i] = new JRadioButton();
            options[i].setBounds(50, 160 + (i * 50), 700, 40);
            options[i].setBackground(new Color(18, 18, 18));
            options[i].setForeground(Color.WHITE);
            options[i].setFont(new Font("Arial", Font.PLAIN, 14));
            group.add(options[i]);
            add(options[i]);
        }
        // Timer.
        timerLabel = new JLabel(timePerQuestion + "s");
        timerLabel.setBounds(800, 80, 80, 40);
        timerLabel.setForeground(Color.RED);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 32));
        add(timerLabel);
        // Next Button.
        JButton next = new JButton("Next");
        next.setBounds(350, 520, 120, 40);
        next.setBackground(new Color(99, 102, 241));
        next.setForeground(Color.WHITE);
        next.setFont(new Font("Arial", Font.BOLD, 14));
        next.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                next.setBackground(new Color(139, 92, 246));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                next.setBackground(new Color(99, 102, 241));
            }
        });
        next.addActionListener(e -> next());
        add(next);
        // Skip Button.
        JButton skip = new JButton("Skip");
        skip.setBounds(480, 520, 120, 40);
        skip.setBackground(new Color(239, 68, 68));
        skip.setForeground(Color.WHITE);
        skip.setFont(new Font("Arial", Font.BOLD, 14));
        skip.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                skip.setBackground(new Color(220, 38, 38));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                skip.setBackground(new Color(239, 68, 68));
            }
        });
        skip.addActionListener(e -> next());
        add(skip);
        timer = new javax.swing.Timer(1000, e -> {
            time--;
            timerLabel.setText(time + "s");
            if (time <= 5) {
                timerLabel.setForeground(new Color(255, 100, 100));
            }
            if (time == 0) {
                next();
            }
        });
        timer.start();
        loadQuestion();
        setVisible(true);
    }

    void loadQuestion() {
        if (index >= questions.size()) {
            timer.stop();
            new ResultFrame(player, anime, score, xp, userAnswers, questions);
            dispose();
            return;
        }
        progressLabel.setText("Question " + (index + 1) + " of " + totalQuestions);
        Question q = questions.get(index);
        questionLabel.setText((index + 1) + ". " + q.question);
        // Deselect All Options.
        for (int i = 0; i < 4; i++) {
            options[i].setSelected(false);
        }
        for (int i = 0; i < 4; i++) {
            options[i].setText(q.options[i]);
        }
        time = timePerQuestion;
        timerLabel.setText(time + "s");
        timerLabel.setForeground(Color.RED);
        timer.restart();
    }

    void next() {
        Question q = questions.get(index);
        for (int i = 0; i < 4; i++) {
            if (options[i].isSelected()) {
                userAnswers[index] = i;
                if (i == q.correct) {
                    score++;
                    xp += 10;
                }
                break;
            }
        }
        index++;
        loadQuestion();
    }
}