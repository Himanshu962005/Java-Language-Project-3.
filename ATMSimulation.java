// ---------- ATM Simulation System ----------.
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.security.MessageDigest;

class Account implements Serializable {
    int accNo;
    String pinHash;
    double balance;
    int wrongAttempts = 0;
    boolean locked = false;
    java.util.List<String> history = new ArrayList<>();
    Date lastWithdrawDate;
    double dailyWithdrawn = 0;

    Account(int accNo, String pinHash, double balance) {
        this.accNo = accNo;
        this.pinHash = pinHash;
        this.balance = balance;
    }
}

public class ATMSimulation extends JFrame {
    Map<Integer, Account> accounts = new HashMap<>();
    Account currentUser = null;
    CardLayout card = new CardLayout();
    JPanel mainPanel = new JPanel(card);
    JTextField accField;
    JPasswordField pinField;
    JTextArea outputArea;
    JTextField regAccField;
    JPasswordField regPinField;
    final String ADMIN_ID = "Valtryek";
    final String ADMIN_PASS = "962005";
    Color bgMain = Color.decode("#121212");
    Color bgPanel = Color.decode("#1E1E1E");
    Color btnColor = Color.decode("#2962FF");
    Color btnHover = Color.decode("#0039CB");
    Color textWhite = Color.WHITE;
    Color textGray = Color.decode("#B0B0B0");

    public ATMSimulation() {
        loadAccounts();
        setTitle("ATM DashBoard");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        mainPanel.add(loginPanel(), "Login");
        mainPanel.add(registerPanel(), "Register");
        mainPanel.add(dashboardPanel(), "Dashboard");
        mainPanel.add(adminPanel(), "Admin");
        add(mainPanel);
        setVisible(true);
    }

    @SuppressWarnings("unchecked")
    void loadAccounts() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("Accounts.Dat"))) {
            Object obj = in.readObject();
            if (obj instanceof HashMap<?, ?>) {
                accounts = (HashMap<Integer, Account>) obj;
            } else {
                accounts = new HashMap<>();
            }
        } catch (Exception e) {
            accounts = new HashMap<>();
        }
    }

    void saveAccounts() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("Accounts.Dat"))) {
            out.writeObject(accounts);
        } catch (Exception e) {
            System.out.println("Save Error");
        }
    }

    JPanel loginPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(bgMain);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(15, 10, 15, 10);
        JLabel title = new JLabel("ATM LOGIN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(textWhite);
        accField = new JTextField(15);
        pinField = new JPasswordField(15);
        JButton loginBtn = createButton("Login");
        JButton regBtn = createButton("Create Account");
        JButton adminBtn = createButton("Admin Login");
        loginBtn.setPreferredSize(new Dimension(160, 40));
        g.gridx = 0;
        g.gridy = 0;
        p.add(title, g);
        g.gridy++;
        p.add(label("Account Number"), g);
        g.gridy++;
        p.add(accField, g);
        g.gridy++;
        p.add(label("PIN"), g);
        g.gridy++;
        p.add(pinField, g);
        g.gridy++;
        p.add(loginBtn, g);
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(bgMain);
        bottomPanel.add(regBtn);
        bottomPanel.add(adminBtn);
        g.gridy++;
        p.add(bottomPanel, g);
        loginBtn.addActionListener(e -> login());
        regBtn.addActionListener(e -> card.show(mainPanel, "Register"));
        adminBtn.addActionListener(e -> adminLogin());
        return p;
    }

    JPanel registerPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(bgMain);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 10, 10, 10);
        JLabel title = new JLabel("New Account Registration");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(textWhite);
        regAccField = new JTextField(15);
        regPinField = new JPasswordField(15);
        JButton createBtn = createButton("Register");
        JButton backBtn = createButton("Back to Login");
        g.gridx = 0;
        g.gridy = 0;
        p.add(title, g);
        g.gridy++;
        p.add(label("New Account Number"), g);
        g.gridy++;
        p.add(regAccField, g);
        g.gridy++;
        p.add(label("New PIN"), g);
        g.gridy++;
        p.add(regPinField, g);
        g.gridy++;
        p.add(createBtn, g);
        g.gridy++;
        p.add(backBtn, g);
        createBtn.addActionListener(e -> registerAccount());
        backBtn.addActionListener(e -> card.show(mainPanel, "Login"));
        return p;
    }

    JPanel dashboardPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(bgMain);
        JLabel title = new JLabel("ATM DASHBOARD", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(textWhite);
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        p.add(title, BorderLayout.NORTH);
        JPanel btnPanel = new JPanel(new GridLayout(2, 4, 15, 15));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        btnPanel.setBackground(bgPanel);
        JButton bal = createButton("Balance");
        JButton dep = createButton("Deposit");
        JButton wit = createButton("Withdraw");
        JButton fastCash = createButton("Fast Cash");
        JButton mini = createButton("Mini Statement");
        JButton hist = createButton("History");
        JButton changePin = createButton("Change PIN");
        JButton logout = createButton("Logout");
        btnPanel.add(bal);
        btnPanel.add(dep);
        btnPanel.add(wit);
        btnPanel.add(fastCash);
        btnPanel.add(mini);
        btnPanel.add(hist);
        btnPanel.add(changePin);
        btnPanel.add(logout);
        p.add(btnPanel, BorderLayout.CENTER);
        outputArea = new JTextArea(6, 40);
        outputArea.setEditable(false);
        outputArea.setBackground(bgPanel);
        outputArea.setForeground(textWhite);
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        p.add(new JScrollPane(outputArea), BorderLayout.SOUTH);
        bal.addActionListener(e -> showBalance());
        dep.addActionListener(e -> deposit());
        wit.addActionListener(e -> withdraw());
        fastCash.addActionListener(e -> fastCashMenu());
        mini.addActionListener(e -> mini());
        hist.addActionListener(e -> history());
        changePin.addActionListener(e -> changePIN());
        logout.addActionListener(e -> logout());
        return p;
    }

    void fastCashMenu() {
        String[] options = { "50", "100", "200", "500", "1000", "5000", "10000" };
        String choice = (String) JOptionPane.showInputDialog(
                this,
                "Select Fast Cash Amount",
                "Fast Cash",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);
        if (choice != null) {
            double amt = Double.parseDouble(choice);
            Date today = new Date();
            if (currentUser.lastWithdrawDate == null ||
                    !isSameDay(currentUser.lastWithdrawDate, today)) {
                currentUser.dailyWithdrawn = 0;
                currentUser.lastWithdrawDate = today;
            }
            if (currentUser.dailyWithdrawn + amt > 50000) {
                outputArea.setText("Daily withdrawal limit ₹50,000 exceeded.");
                return;
            }
            if (amt > currentUser.balance) {
                outputArea.setText("Insufficient Balance");
                return;
            }
            currentUser.balance -= amt;
            currentUser.dailyWithdrawn += amt;
            currentUser.lastWithdrawDate = today;
            currentUser.history.add("Fast Cash ₹" + amt + "  " + new Date());
            saveAccounts();
            outputArea.setText("Fast Cash Withdrawn ₹" + amt);
        }
    }

    JPanel adminPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(bgMain);
        JTextArea area = new JTextArea();
        area.setEditable(false);
        JButton view = createButton("View All Accounts");
        JButton delete = createButton("Delete Account");
        JButton unlock = createButton("Unlock Account");
        JButton back = createButton("Back to Login");
        JPanel top = new JPanel();
        top.add(view);
        top.add(delete);
        top.add(unlock);
        top.add(back);
        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(area), BorderLayout.CENTER);
        view.addActionListener(e -> {
            StringBuilder sb = new StringBuilder();
            for (Account a : accounts.values()) {
                sb.append("Acc : ").append(a.accNo)
                        .append(" | Balance : ₹").append(a.balance)
                        .append(a.locked ? " | LOCKED" : "")
                        .append("\n");
            }
            area.setText(sb.length() == 0 ? "No Accounts Found" : sb.toString());
        });
        delete.addActionListener(e -> {
            String s = JOptionPane.showInputDialog(this, "Enter Account to Delete : ");
            try {
                int acc = Integer.parseInt(s);
                if (accounts.containsKey(acc)) {
                    accounts.remove(acc);
                    saveAccounts();
                    JOptionPane.showMessageDialog(this, "Account deleted");
                } else {
                    JOptionPane.showMessageDialog(this, "Account not found");
                }
                saveAccounts();
                area.setText("Account Deleted If Existed.");
            } catch (Exception ex) {
                area.setText("Invalid Account Number");
            }
        });
        unlock.addActionListener(e -> {
            String s = JOptionPane.showInputDialog(this, "Enter Account to Unlock : ");
            try {
                int acc = Integer.parseInt(s);
                Account a = accounts.get(acc);
                if (a != null) {
                    a.locked = false;
                    a.wrongAttempts = 0;
                    saveAccounts();
                    area.setText("Account Unlocked.");
                }
            } catch (Exception ex) {
                area.setText("Invalid Account Number");
            }
        });
        back.addActionListener(e -> card.show(mainPanel, "Login"));
        return p;
    }

    JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(textGray);
        return l;
    }

    JButton createButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBackground(btnColor);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                b.setBackground(btnHover);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                b.setBackground(btnColor);
            }
        });
        return b;
    }

    void login() {
        try {
            int acc = Integer.parseInt(accField.getText());
            String pin = new String(pinField.getPassword());
            String hash = hashPin(pin);
            Account u = accounts.get(acc);
            if (u == null) {
                JOptionPane.showMessageDialog(this, "Account Not Found");
                return;
            }
            if (u.locked) {
                JOptionPane.showMessageDialog(this, "Account is Locked. Contact admin.");
                return;
            }
            if (u.pinHash.equals(hash)) {
                u.wrongAttempts = 0;
                currentUser = u;
                outputArea.setText("Login Successful");
                card.show(mainPanel, "Dashboard");
            } else {
                u.wrongAttempts++;
                saveAccounts();
                if (u.wrongAttempts >= 3) {
                    u.locked = true;
                    saveAccounts();
                    JOptionPane.showMessageDialog(this, "Account Locked After 3 Wrong Attempts");
                } else {
                    JOptionPane.showMessageDialog(this, "Wrong PIN. Attempts : " + u.wrongAttempts);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Enter Valid Details");
        }
    }

    void adminLogin() {
        String id = JOptionPane.showInputDialog(this, "Enter Admin ID : ");
        String pass = JOptionPane.showInputDialog(this, "Enter Admin Password : ");

        if (ADMIN_ID.equals(id) && ADMIN_PASS.equals(pass)) {
            card.show(mainPanel, "Admin");
        } else {
            JOptionPane.showMessageDialog(this, "Wrong Admin Credentials");
        }
    }

    void registerAccount() {
        try {
            int acc = Integer.parseInt(regAccField.getText());
            String pin = new String(regPinField.getPassword());
            String hash = hashPin(pin);
            if (accounts.containsKey(acc)) {
                JOptionPane.showMessageDialog(this, "Account Already Exists");
                return;
            }
            accounts.put(acc, new Account(acc, hash, 0));
            saveAccounts();
            JOptionPane.showMessageDialog(this, "Account Created Successfully");
            regAccField.setText("");
            regPinField.setText("");
            card.show(mainPanel, "login");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Enter Valid Account Details");
        }
    }

    void changePIN() {
        String oldPinStr = JOptionPane.showInputDialog(this, "Enter Current PIN : ");
        String newPinStr = JOptionPane.showInputDialog(this, "Enter New PIN : ");
        try {
            String oldHash = hashPin(oldPinStr);
            if (!currentUser.pinHash.equals(oldHash)) {
                outputArea.setText("Wrong Current PIN");
                return;
            }
            currentUser.pinHash = hashPin(newPinStr);
            saveAccounts();
            outputArea.setText("PIN Changed Successfully");
        } catch (Exception e) {
            outputArea.setText("Invalid PIN Input");
        }
    }

    void showBalance() {
        outputArea.setText("Current Balance : ₹" + currentUser.balance);
    }

    void deposit() {
        String s = JOptionPane.showInputDialog(this, "Enter Amount : ");
        try {
            double amt = Double.parseDouble(s);
            if (amt <= 0) {
                JOptionPane.showMessageDialog(this, "Invalid deposit amount");
                return;
            }
            currentUser.balance += amt;
            saveAccounts();
            currentUser.history.add("Deposit ₹" + amt + "  " + new Date());
            saveAccounts();
            outputArea.setText("Deposit Successful");
        } catch (Exception e) {
            outputArea.setText("Invalid Amount");
        }
    }

    // ---------- DAILY LIMIT LOGIC (₹10,000) ----------.
    void withdraw() {
        String s = JOptionPane.showInputDialog(this, "Enter Amount : ");
        try {
            double amt = Double.parseDouble(s);
            if (amt <= 0) {
                JOptionPane.showMessageDialog(this, "Invalid withdrawal amount");
                return;
            }
            Date today = new Date();
            if (currentUser.lastWithdrawDate == null ||
                    !isSameDay(currentUser.lastWithdrawDate, today)) {
                currentUser.dailyWithdrawn = 0;
                currentUser.lastWithdrawDate = today;
            }
            if (currentUser.dailyWithdrawn + amt > 50000) {
                outputArea.setText("Daily Withdrawal Limit ₹50,000 Exceeded");
                return;
            }
            if (amt > currentUser.balance) {
                outputArea.setText("Insufficient Balance");
                return;
            }
            currentUser.balance -= amt;
            currentUser.dailyWithdrawn += amt;
            currentUser.lastWithdrawDate = today;
            currentUser.history.add("Withdraw ₹" + amt + "  " + new Date());
            saveAccounts();
            outputArea.setText("Withdraw Successful");
        } catch (Exception e) {
            outputArea.setText("Invalid Amount");
        }
    }

    void mini() {
        if (currentUser.history == null || currentUser.history.size() == 0) {
            outputArea.setText("No Transactions Available");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("---------- MINI STATEMENT ----------\n");
        int size = currentUser.history.size();
        int start = Math.max(0, size - 5); // Last 5 Transactions.
        for (int i = start; i < size; i++) {
            sb.append(currentUser.history.get(i)).append("\n");
        }
        sb.append("------------------------------\n");
        sb.append("Balance : ₹").append(currentUser.balance);
        sb.append("\n------------------------------");
        outputArea.setText(sb.toString());
    }

    void history() {
        StringBuilder sb = new StringBuilder();
        for (String h : currentUser.history)
            sb.append(h).append("\n");
        outputArea.setText(sb.length() == 0 ? "No History" : sb.toString());
    }

    void logout() {
        currentUser = null;
        accField.setText("");
        pinField.setText("");
        outputArea.setText("");
        card.show(mainPanel, "Login");
    }

    // ---------- HELPER METHOD ----------.
    boolean isSameDay(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }

    String hashPin(String pin) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(pin.getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                String s = Integer.toHexString(0xff & b);
                if (s.length() == 1)
                    hex.append('0');
                hex.append(s);
            }
            return hex.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public static void main(String[] args) {
        new ATMSimulation();
    }
}