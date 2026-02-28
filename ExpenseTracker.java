// ---------- Expense Tracker System ----------.
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormatSymbols;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ExpenseTracker {
    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // FallBack.
        }
        UIManager.put("control", new Color(20, 20, 20));
        UIManager.put("info", new Color(20, 20, 20));
        UIManager.put("nimbusBase", new Color(10, 10, 10));
        UIManager.put("nimbusLightBackground", new Color(25, 25, 25));
        UIManager.put("text", new Color(230, 230, 230));
        UIManager.put("nimbusDisabledText", new Color(130, 130, 130));
        UIManager.put("nimbusSelectionBackground", new Color(0, 102, 204));
        UIManager.put("nimbusFocus", new Color(0, 102, 204));
        SwingUtilities.invokeLater(() -> {
            MainFrame f = new MainFrame();
            f.setVisible(true);
        });
    }
}

// ----- Model -----.
class Transaction {
    enum Type {
        EXPENSE, INCOME
    }

    enum RecurrenceType {
        NONE, DAILY, WEEKLY, MONTHLY, YEARLY
    }

    private LocalDate date;
    private Type type;
    private String category;
    private double amount;
    private String description;
    private RecurrenceType recurrence;
    private LocalDate recurrenceEndDate;

    public Transaction(LocalDate date, Type type, String category, double amount, String description) {
        this(date, type, category, amount, description, RecurrenceType.NONE, null);
    }

    public Transaction(LocalDate date, Type type, String category, double amount, String description,
            RecurrenceType recurrence, LocalDate recurrenceEndDate) {
        this.date = date;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.description = description == null ? "" : description;
        this.recurrence = recurrence == null ? RecurrenceType.NONE : recurrence;
        this.recurrenceEndDate = recurrenceEndDate;
    }

    public LocalDate getDate() {
        return date;
    }

    public Type getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public RecurrenceType getRecurrence() {
        return recurrence;
    }

    public LocalDate getRecurrenceEndDate() {
        return recurrenceEndDate;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRecurrence(RecurrenceType recurrence) {
        this.recurrence = recurrence;
    }

    public void setRecurrenceEndDate(LocalDate recurrenceEndDate) {
        this.recurrenceEndDate = recurrenceEndDate;
    }

    // Generate Recurring Instances For A Given Date Range.
    public List<Transaction> generateInstances(LocalDate startDate, LocalDate endDate) {
        List<Transaction> instances = new ArrayList<>();
        if (recurrence == RecurrenceType.NONE) {
            if (!date.isBefore(startDate) && !date.isAfter(endDate)) {
                instances.add(this);
            }
            return instances;
        }
        LocalDate current = date;
        LocalDate limit = recurrenceEndDate != null ? recurrenceEndDate : endDate;
        while (!current.isAfter(limit) && !current.isAfter(endDate)) {
            if (!current.isBefore(startDate)) {
                instances.add(new Transaction(current, type, category, amount, description, RecurrenceType.NONE, null));
            }
            current = switch (recurrence) {
                case DAILY -> current.plusDays(1);
                case WEEKLY -> current.plusWeeks(1);
                case MONTHLY -> current.plusMonths(1);
                case YEARLY -> current.plusYears(1);
                default -> current.plusDays(1);
            };
        }
        return instances;
    }
}

class TransactionTableModel extends AbstractTableModel {
    private final String[] columns = { "Date", "Type", "Category", "Amount", "Description", "Recurrence" };
    private List<Transaction> transactions = new ArrayList<>();
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = new ArrayList<>(transactions);
        fireTableDataChanged();
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public Transaction getAt(int row) {
        return transactions.get(row);
    }

    public void add(Transaction t) {
        transactions.add(0, t); // Newest First.
        fireTableRowsInserted(0, 0);
    }

    public void remove(int row) {
        transactions.remove(row);
        fireTableRowsDeleted(row, row);
    }

    public void update(int row, Transaction t) {
        transactions.set(row, t);
        fireTableRowsUpdated(row, row);
    }

    @Override
    public int getRowCount() {
        return transactions.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Transaction t = transactions.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return dtf.format(t.getDate());
            case 1:
                return t.getType().toString();
            case 2:
                return t.getCategory();
            case 3:
                return String.format("%.2f", t.getAmount());
            case 4:
                return t.getDescription();
            case 5:
                return t.getRecurrence().toString();
        }
        return "";
    }
}

// ----- Persistence -----.
class DataStorage {
    private static final Path STORAGE_DIR = Path.of(System.getProperty("user.home"), ".expense_tracker");
    private static final Path STORAGE_FILE = STORAGE_DIR.resolve("transactions.csv");
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static List<Transaction> load() {
        try {
            if (!Files.exists(STORAGE_FILE))
                return new ArrayList<>();
            List<String> lines = Files.readAllLines(STORAGE_FILE);
            List<Transaction> out = new ArrayList<>();
            for (String line : lines) {
                if (line.isBlank())
                    continue;
                // CSV : Date,Type,Category,Amount,Description,Recurrence,RecurrenceEndDate.
                String[] parts = splitCsv(line);
                if (parts.length < 5)
                    continue;
                LocalDate d = LocalDate.parse(parts[0], dtf);
                Transaction.Type type = Transaction.Type.valueOf(parts[1]);
                String cat = parts[2];
                double amt = Double.parseDouble(parts[3]);
                String desc = parts[4];
                Transaction.RecurrenceType recurrence = Transaction.RecurrenceType.NONE;
                LocalDate recurrenceEndDate = null;
                if (parts.length > 5 && !parts[5].isEmpty()) {
                    recurrence = Transaction.RecurrenceType.valueOf(parts[5]);
                }
                if (parts.length > 6 && !parts[6].isEmpty()) {
                    recurrenceEndDate = LocalDate.parse(parts[6], dtf);
                }
                out.add(new Transaction(d, type, cat, amt, desc, recurrence, recurrenceEndDate));
            }
            return out;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void save(List<Transaction> transactions) {
        try {
            if (!Files.exists(STORAGE_DIR))
                Files.createDirectories(STORAGE_DIR);
            try (BufferedWriter w = Files.newBufferedWriter(STORAGE_FILE)) {
                for (Transaction t : transactions) {
                    String line = csvEscape(dtf.format(t.getDate())) + "," +
                            csvEscape(t.getType().toString()) + "," +
                            csvEscape(t.getCategory()) + "," +
                            csvEscape(String.format(Locale.ROOT, "%.2f", t.getAmount())) + "," +
                            csvEscape(t.getDescription()) + "," +
                            csvEscape(t.getRecurrence().toString()) + "," +
                            csvEscape(t.getRecurrenceEndDate() != null ? dtf.format(t.getRecurrenceEndDate()) : "");
                    w.write(line);
                    w.newLine();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void exportCsv(Component parent, List<Transaction> transactions) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Export transactions to CSV");
        fc.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));
        if (fc.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            if (!f.getName().toLowerCase().endsWith(".csv"))
                f = new File(f.getParentFile(), f.getName() + ".csv");
            try (BufferedWriter w = new BufferedWriter(new FileWriter(f))) {
                w.write("date,type,category,amount,description,recurrence,recurrenceEndDate");
                w.newLine();
                for (Transaction t : transactions) {
                    String line = csvEscape(dtf.format(t.getDate())) + "," +
                            csvEscape(t.getType().toString()) + "," +
                            csvEscape(t.getCategory()) + "," +
                            csvEscape(String.format(Locale.ROOT, "%.2f", t.getAmount())) + "," +
                            csvEscape(t.getDescription()) + "," +
                            csvEscape(t.getRecurrence().toString()) + "," +
                            csvEscape(t.getRecurrenceEndDate() != null ? dtf.format(t.getRecurrenceEndDate()) : "");
                    w.write(line);
                    w.newLine();
                }
                JOptionPane.showMessageDialog(parent, "Exported to: " + f.getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Export failed: " + ex.getMessage());
            }
        }
    }

    public static List<Transaction> importCsv(Component parent) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Import transactions from CSV");
        fc.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));
        if (fc.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            try {
                List<String> lines = Files.readAllLines(f.toPath());
                List<Transaction> out = new ArrayList<>();
                for (String line : lines) {
                    if (line.isBlank())
                        continue;
                    if (line.startsWith("date,"))
                        continue; // skip header
                    String[] parts = splitCsv(line);
                    if (parts.length < 5)
                        continue;
                    LocalDate d = LocalDate.parse(parts[0], dtf);
                    Transaction.Type type = Transaction.Type.valueOf(parts[1]);
                    String cat = parts[2];
                    double amt = Double.parseDouble(parts[3]);
                    String desc = parts[4];
                    Transaction.RecurrenceType recurrence = Transaction.RecurrenceType.NONE;
                    LocalDate recurrenceEndDate = null;
                    if (parts.length > 5 && !parts[5].isEmpty()) {
                        recurrence = Transaction.RecurrenceType.valueOf(parts[5]);
                    }
                    if (parts.length > 6 && !parts[6].isEmpty()) {
                        recurrenceEndDate = LocalDate.parse(parts[6], dtf);
                    }
                    out.add(new Transaction(d, type, cat, amt, desc, recurrence, recurrenceEndDate));
                }
                JOptionPane.showMessageDialog(parent, "Imported " + out.size() + " transactions.");
                return out;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Import failed: " + ex.getMessage());
            }
        }
        return Collections.emptyList();
    }

    // Naive CSV Split That Unquotes Values.
    private static String[] splitCsv(String line) {
        List<String> parts = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder cur = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
                continue;
            }
            if (c == ',' && !inQuotes) {
                parts.add(cur.toString());
                cur.setLength(0);
                continue;
            }
            cur.append(c);
        }
        parts.add(cur.toString());
        return parts.toArray(new String[0]);
    }

    private static String csvEscape(String v) {
        if (v == null)
            return "";
        boolean need = v.contains(",") || v.contains("\n") || v.contains("\"");
        String escaped = v.replace("\"", "\"\"");
        return need ? "\"" + escaped + "\"" : escaped;
    }
}

// ----- Finance Analysis -----.
class FinanceAnalyzer {
    // FEATURE 1: Income vs Expense Tracking.
    public static class IncomeExpenseData {
        public double income;
        public double expense;
        public double balance;

        public IncomeExpenseData() {
            this.income = 0;
            this.expense = 0;
            this.balance = 0;
        }
    }

    public static IncomeExpenseData calculateIncomeExpense(List<Transaction> transactions) {
        IncomeExpenseData data = new IncomeExpenseData();
        for (Transaction t : transactions) {
            if (t.getType() == Transaction.Type.INCOME) {
                data.income += t.getAmount();
            } else {
                data.expense += t.getAmount();
            }
        }
        // FEATURE 2: Balance Calculation.
        data.balance = data.income - data.expense;
        return data;
    }

    // FEATURE 3: Monthly Summary Report.
    public static Map<YearMonth, IncomeExpenseData> getMonthlySummary(List<Transaction> transactions) {
        Map<YearMonth, IncomeExpenseData> summary = new TreeMap<>();
        for (Transaction t : transactions) {
            YearMonth ym = YearMonth.from(t.getDate());
            IncomeExpenseData data = summary.computeIfAbsent(ym, k -> new IncomeExpenseData());
            if (t.getType() == Transaction.Type.INCOME) {
                data.income += t.getAmount();
            } else {
                data.expense += t.getAmount();
            }
            data.balance = data.income - data.expense;
        }
        return summary;
    }

    // FEATURE 4: Yearly Summary Report.
    public static Map<Integer, IncomeExpenseData> getYearlySummary(List<Transaction> transactions) {
        Map<Integer, IncomeExpenseData> summary = new TreeMap<>();
        for (Transaction t : transactions) {
            int year = t.getDate().getYear();
            IncomeExpenseData data = summary.computeIfAbsent(year, k -> new IncomeExpenseData());
            if (t.getType() == Transaction.Type.INCOME) {
                data.income += t.getAmount();
            } else {
                data.expense += t.getAmount();
            }
            data.balance = data.income - data.expense;
        }
        return summary;
    }

    // FEATURE 5: Category Spending Totals.
    public static Map<String, Double> getCategoryTotals(List<Transaction> transactions, Transaction.Type type) {
        Map<String, Double> totals = new LinkedHashMap<>();
        for (Transaction t : transactions) {
            if (t.getType() == type) {
                totals.merge(t.getCategory(), t.getAmount(), Double::sum);
            }
        }
        return totals;
    }

    // FEATURE 6: Expand Recurring Transactions.
    public static List<Transaction> expandRecurringTransactions(List<Transaction> baseTransactions,
            LocalDate startDate, LocalDate endDate) {
        List<Transaction> expanded = new ArrayList<>();
        for (Transaction t : baseTransactions) {
            expanded.addAll(t.generateInstances(startDate, endDate));
        }
        return expanded;
    }
}

// ----- View / Controller -----.
class MainFrame extends JFrame {
    private final TransactionTableModel tableModel = new TransactionTableModel();
    private final JTable table = new JTable(tableModel);
    private final List<Transaction> allTransactions = new ArrayList<>();
    private final PieChartPanel piePanel = new PieChartPanel();
    private final JLabel labelIncome = new JLabel();
    private final JLabel labelExpense = new JLabel();
    private final JLabel labelBalance = new JLabel();
    private final JProgressBar budgetBar = new JProgressBar();
    private double monthlyBudget = 1000.0;
    private final JComboBox<String> monthFilter = new JComboBox<>();
    private final JComboBox<String> yearFilter = new JComboBox<>();

    public MainFrame() {
        setTitle("Expense Tracker - Enhanced with Core Finance Features");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        allTransactions.addAll(DataStorage.load());
        initTopBar();
        initCenter();
        initRight();
        refreshFilters();
        applyFiltersAndUpdate();
        // Autosave On Close.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                DataStorage.save(allTransactions);
            }
        });
    }

    private void initTopBar() {
        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.setBorder(new EmptyBorder(8, 8, 8, 8));
        top.setOpaque(false);
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);
        JButton btnAdd = createButton("Add");
        JButton btnEdit = createButton("Edit");
        JButton btnDelete = createButton("Delete");
        left.add(btnAdd);
        left.add(btnEdit);
        left.add(btnDelete);
        JButton btnImport = createButton("Import CSV");
        JButton btnExport = createButton("Export CSV");
        left.add(btnImport);
        left.add(btnExport);
        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER));
        center.setOpaque(false);
        center.add(new JLabel("Filter:"));
        monthFilter.setPreferredSize(new Dimension(120, 28));
        yearFilter.setPreferredSize(new Dimension(90, 28));
        center.add(monthFilter);
        center.add(yearFilter);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        JButton btnBudgetSet = createButton("Set Budget");
        JButton btnMonthlySummary = createButton("Monthly Summary");
        JButton btnYearlySummary = createButton("Yearly Summary");
        JButton btnCategoryReport = createButton("Category Report");
        right.add(btnBudgetSet);
        right.add(btnMonthlySummary);
        right.add(btnYearlySummary);
        right.add(btnCategoryReport);
        top.add(left, BorderLayout.WEST);
        top.add(center, BorderLayout.CENTER);
        top.add(right, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);
        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());
        btnImport.addActionListener(e -> onImport());
        btnExport.addActionListener(e -> onExport());
        btnBudgetSet.addActionListener(e -> onSetBudget());
        btnMonthlySummary.addActionListener(e -> onMonthlySummary());
        btnYearlySummary.addActionListener(e -> onYearlySummary());
        btnCategoryReport.addActionListener(e -> onCategoryReport());
        monthFilter.addActionListener(e -> applyFiltersAndUpdate());
        yearFilter.addActionListener(e -> applyFiltersAndUpdate());
    }

    private void initCenter() {
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(28);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(new EmptyBorder(8, 8, 8, 8));
        add(sp, BorderLayout.CENTER);
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2)
                    onEdit();
            }
        });
    }

    private void initRight() {
        JPanel right = new JPanel(new BorderLayout(8, 8));
        right.setPreferredSize(new Dimension(350, getHeight()));
        right.setBorder(new EmptyBorder(8, 8, 8, 8));
        right.setOpaque(false);
        JPanel stats = new JPanel();
        stats.setLayout(new BoxLayout(stats, BoxLayout.Y_AXIS));
        stats.setOpaque(false);
        labelIncome.setFont(new Font("SansSerif", Font.BOLD, 16));
        labelExpense.setFont(new Font("SansSerif", Font.BOLD, 16));
        labelBalance.setFont(new Font("SansSerif", Font.BOLD, 18));
        labelIncome.setForeground(new Color(144, 238, 144));
        labelExpense.setForeground(new Color(255, 99, 71));
        labelBalance.setForeground(new Color(173, 216, 230));
        stats.add(labelIncome);
        stats.add(Box.createVerticalStrut(6));
        stats.add(labelExpense);
        stats.add(Box.createVerticalStrut(6));
        stats.add(labelBalance);
        stats.add(Box.createVerticalStrut(12));
        budgetBar.setStringPainted(true);
        budgetBar.setMaximum(100);
        budgetBar.setValue(0);
        budgetBar.setPreferredSize(new Dimension(300, 22));
        stats.add(budgetBar);
        stats.add(Box.createVerticalStrut(12));
        piePanel.setPreferredSize(new Dimension(320, 300));
        right.add(stats, BorderLayout.NORTH);
        right.add(piePanel, BorderLayout.CENTER);
        add(right, BorderLayout.EAST);
    }

    private JButton createButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBackground(new Color(0, 102, 204));
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setFont(new Font("SansSerif", Font.PLAIN, 11));
        return b;
    }

    private void onAdd() {
        AddTransactionDialog dlg = new AddTransactionDialog(this, null);
        dlg.setVisible(true);
        Transaction t = dlg.getResult();
        if (t != null) {
            allTransactions.add(0, t);
            DataStorage.save(allTransactions);
            applyFiltersAndUpdate();
        }
    }

    private void onEdit() {
        int sel = table.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Select a Transaction to Edit.");
            return;
        }
        Transaction current = tableModel.getAt(sel);
        AddTransactionDialog dlg = new AddTransactionDialog(this, current);
        dlg.setVisible(true);
        Transaction updated = dlg.getResult();
        if (updated != null) {
            OptionalInt idxOpt = findTransactionIndex(current);
            if (idxOpt.isPresent()) {
                allTransactions.set(idxOpt.getAsInt(), updated);
                DataStorage.save(allTransactions);
                applyFiltersAndUpdate();
            }
        }
    }

    private void onDelete() {
        int sel = table.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Select a Transaction to Delete.");
            return;
        }
        int confirmed = JOptionPane.showConfirmDialog(this, "Delete Selected Transaction?", "Confirm",
                JOptionPane.YES_NO_OPTION);
        if (confirmed == JOptionPane.YES_OPTION) {
            Transaction t = tableModel.getAt(sel);
            OptionalInt idxOpt = findTransactionIndex(t);
            if (idxOpt.isPresent()) {
                allTransactions.remove(idxOpt.getAsInt());
                DataStorage.save(allTransactions);
                applyFiltersAndUpdate();
            }
        }
    }

    private void onImport() {
        List<Transaction> imported = DataStorage.importCsv(this);
        if (!imported.isEmpty()) {
            allTransactions.addAll(0, imported);
            DataStorage.save(allTransactions);
            refreshFilters();
            applyFiltersAndUpdate();
        }
    }

    private void onExport() {
        DataStorage.exportCsv(this, allTransactions);
    }

    private void onSetBudget() {
        String s = JOptionPane.showInputDialog(this, "Set Monthly Budget (Numeric)",
                String.format(Locale.ROOT, "%.2f", monthlyBudget));
        if (s == null)
            return;
        try {
            double v = Double.parseDouble(s);
            if (v <= 0)
                throw new NumberFormatException();
            monthlyBudget = v;
            applyFiltersAndUpdate();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Number");
        }
    }

    private void onMonthlySummary() {
        Map<YearMonth, FinanceAnalyzer.IncomeExpenseData> summary = FinanceAnalyzer.getMonthlySummary(allTransactions);
        StringBuilder sb = new StringBuilder("=== MONTHLY SUMMARY ===\n\n");
        for (Map.Entry<YearMonth, FinanceAnalyzer.IncomeExpenseData> entry : summary.entrySet()) {
            FinanceAnalyzer.IncomeExpenseData data = entry.getValue();
            sb.append(String.format("%s:\n", entry.getKey()))
                    .append(String.format("  Income : ₹%.2f\n", data.income))
                    .append(String.format("  Expense : ₹%.2f\n", data.expense))
                    .append(String.format("  Balance : ₹%.2f\n\n", data.balance));
        }
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        JOptionPane.showMessageDialog(this, scrollPane, "Monthly Summary", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onYearlySummary() {
        Map<Integer, FinanceAnalyzer.IncomeExpenseData> summary = FinanceAnalyzer.getYearlySummary(allTransactions);
        StringBuilder sb = new StringBuilder("=== YEARLY SUMMARY ===\n\n");
        for (Map.Entry<Integer, FinanceAnalyzer.IncomeExpenseData> entry : summary.entrySet()) {
            FinanceAnalyzer.IncomeExpenseData data = entry.getValue();
            sb.append(String.format("%d:\n", entry.getKey()))
                    .append(String.format("  Income : ₹%.2f\n", data.income))
                    .append(String.format("  Expense : ₹%.2f\n", data.expense))
                    .append(String.format("  Balance : ₹%.2f\n\n", data.balance));
        }
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        JOptionPane.showMessageDialog(this, scrollPane, "Yearly Summary", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onCategoryReport() {
        StringBuilder sb = new StringBuilder("=== CATEGORY SPENDING REPORT ===\n\n");
        Map<String, Double> expenseByCategory = FinanceAnalyzer.getCategoryTotals(allTransactions,
                Transaction.Type.EXPENSE);
        sb.append("EXPENSES BY CATEGORY:\n");
        for (Map.Entry<String, Double> entry : expenseByCategory.entrySet()) {
            sb.append(String.format("  %s: ₹%.2f\n", entry.getKey(), entry.getValue()));
        }
        sb.append("\nINCOME BY CATEGORY:\n");
        Map<String, Double> incomeByCategory = FinanceAnalyzer.getCategoryTotals(allTransactions,
                Transaction.Type.INCOME);
        for (Map.Entry<String, Double> entry : incomeByCategory.entrySet()) {
            sb.append(String.format("  %s: ₹%.2f\n", entry.getKey(), entry.getValue()));
        }
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        JOptionPane.showMessageDialog(this, scrollPane, "Category Report", JOptionPane.INFORMATION_MESSAGE);
    }

    private OptionalInt findTransactionIndex(Transaction t) {
        for (int i = 0; i < allTransactions.size(); i++) {
            Transaction a = allTransactions.get(i);
            if (a.getDate().equals(t.getDate()) && Math.abs(a.getAmount() - t.getAmount()) < 0.001
                    && a.getDescription().equals(t.getDescription()))
                return OptionalInt.of(i);
        }
        return OptionalInt.empty();
    }

    private void refreshFilters() {
        monthFilter.removeAllItems();
        yearFilter.removeAllItems();
        monthFilter.addItem("All months");
        String[] months = new DateFormatSymbols().getMonths();
        for (int i = 0; i < 12; i++)
            monthFilter.addItem((i + 1) + " - " + months[i]);

        Set<Integer> years = allTransactions.stream().map(t -> t.getDate().getYear())
                .collect(Collectors.toCollection(TreeSet::new));
        yearFilter.addItem("All years");
        int currentYear = LocalDate.now().getYear();
        for (int y = currentYear + 1; y >= currentYear - 5; y--) {
            yearFilter.addItem(String.valueOf(y));
        }
        for (int y : years) {
            boolean present = false;
            for (int i = 0; i < yearFilter.getItemCount(); i++)
                if (yearFilter.getItemAt(i).equals(String.valueOf(y)))
                    present = true;
            if (!present)
                yearFilter.addItem(String.valueOf(y));
        }
        monthFilter.setSelectedIndex(0);
        yearFilter.setSelectedIndex(0);
    }

    private void applyFiltersAndUpdate() {
        String monthSel = (String) monthFilter.getSelectedItem();
        String yearSel = (String) yearFilter.getSelectedItem();
        final Integer month;
        final Integer year;
        if (monthSel != null && !monthSel.equals("All months")) {
            month = Integer.parseInt(monthSel.split(" - ")[0]);
        } else {
            month = null;
        }
        if (yearSel != null && !yearSel.equals("All years")) {
            year = Integer.parseInt(yearSel);
        } else {
            year = null;
        }
        List<Transaction> filtered = allTransactions.stream().filter(t -> {
            if (month != null && t.getDate().getMonthValue() != month)
                return false;
            if (year != null && t.getDate().getYear() != year)
                return false;
            return true;
        }).collect(Collectors.toList());
        tableModel.setTransactions(filtered);
        updateStats(filtered);
        piePanel.setTransactions(filtered);
    }

    private void updateStats(List<Transaction> filtered) {
        FinanceAnalyzer.IncomeExpenseData data = FinanceAnalyzer.calculateIncomeExpense(filtered);
        labelIncome.setText(String.format("Income : ₹%.2f", data.income));
        labelExpense.setText(String.format("Expense : ₹%.2f", data.expense));
        labelBalance.setText(String.format("Balance : ₹%.2f", data.balance));
        int progress = (int) Math.min(100, (monthlyBudget <= 0 ? 0 : (data.expense / monthlyBudget) * 100));
        budgetBar.setValue(progress);
        budgetBar.setString(String.format("Budget: ₹%.2f / ₹%.2f (%.0f%%)", data.expense, monthlyBudget,
                (monthlyBudget <= 0 ? 0 : (data.expense / monthlyBudget * 100))));
    }
}

class AddTransactionDialog extends JDialog {
    private Transaction result;
    private final JComboBox<String> typeBox = new JComboBox<>(new String[] { "EXPENSE", "INCOME" });
    private final JTextField amountField = new JTextField();
    private final JComboBox<String> categoryBox = new JComboBox<>();
    private final JSpinner dateSpinner;
    private final JTextField descField = new JTextField();
    private final JComboBox<String> recurrenceBox = new JComboBox<>();
    private final JSpinner recurrenceEndSpinner;
    private static final String[] defaultCategories = { "Food", "Transport", "Bills", "Shopping", "Salary", "Rent",
            "Utilities", "Others" };

    public AddTransactionDialog(Window owner, Transaction existing) {
        super(owner, "Transaction", ModalityType.APPLICATION_MODAL);
        setSize(480, 450);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(8, 8));
        JPanel body = new JPanel();
        body.setLayout(new GridBagLayout());
        body.setBorder(new EmptyBorder(12, 12, 12, 12));
        body.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        body.add(new JLabel("Type"), gbc);
        gbc.gridx = 1;
        body.add(typeBox, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        body.add(new JLabel("Amount"), gbc);
        gbc.gridx = 1;
        body.add(amountField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        body.add(new JLabel("Category"), gbc);
        for (String c : defaultCategories)
            categoryBox.addItem(c);
        gbc.gridx = 1;
        body.add(categoryBox, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        body.add(new JLabel("Date"), gbc);
        dateSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor de = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(de);
        gbc.gridx = 1;
        body.add(dateSpinner, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        body.add(new JLabel("Description"), gbc);
        gbc.gridx = 1;
        body.add(descField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        body.add(new JLabel("Recurrence"), gbc);
        for (Transaction.RecurrenceType rt : Transaction.RecurrenceType.values()) {
            recurrenceBox.addItem(rt.toString());
        }
        gbc.gridx = 1;
        body.add(recurrenceBox, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        body.add(new JLabel("Recurrence End Date"), gbc);
        recurrenceEndSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor de2 = new JSpinner.DateEditor(recurrenceEndSpinner, "yyyy-MM-dd");
        recurrenceEndSpinner.setEditor(de2);
        gbc.gridx = 1;
        body.add(recurrenceEndSpinner, gbc);
        add(body, BorderLayout.CENTER);
        JPanel foot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        foot.add(ok);
        foot.add(cancel);
        add(foot, BorderLayout.SOUTH);
        ok.addActionListener(e -> onOk(existing));
        cancel.addActionListener(e -> {
            result = null;
            dispose();
        });
        if (existing != null)
            loadExisting(existing);
    }

    private void loadExisting(Transaction t) {
        typeBox.setSelectedItem(t.getType().toString());
        amountField.setText(String.format(Locale.ROOT, "%.2f", t.getAmount()));
        categoryBox.setSelectedItem(t.getCategory());
        Date d = Date.from(t.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
        dateSpinner.setValue(d);
        descField.setText(t.getDescription());
        recurrenceBox.setSelectedItem(t.getRecurrence().toString());
        if (t.getRecurrenceEndDate() != null) {
            Date recEnd = Date.from(t.getRecurrenceEndDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
            recurrenceEndSpinner.setValue(recEnd);
        }
    }

    private void onOk(Transaction existing) {
        try {
            String typeS = (String) typeBox.getSelectedItem();
            Transaction.Type type = Transaction.Type.valueOf(typeS);
            double amt = Double.parseDouble(amountField.getText().trim());
            String cat = ((String) categoryBox.getSelectedItem()).trim();
            Date d = (Date) dateSpinner.getValue();
            LocalDate ld = Instant.ofEpochMilli(d.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
            String desc = descField.getText().trim();
            String recS = (String) recurrenceBox.getSelectedItem();
            Transaction.RecurrenceType recurrence = Transaction.RecurrenceType.valueOf(recS);
            LocalDate recurrenceEndDate = null;
            if (recurrence != Transaction.RecurrenceType.NONE) {
                Date recEndD = (Date) recurrenceEndSpinner.getValue();
                recurrenceEndDate = Instant.ofEpochMilli(recEndD.getTime()).atZone(ZoneId.systemDefault())
                        .toLocalDate();
            }
            if (amt < 0)
                throw new NumberFormatException("Amount must be positive");
            result = new Transaction(ld, type, cat, amt, desc, recurrence, recurrenceEndDate);
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid data : " + ex.getMessage());
        }
    }

    public Transaction getResult() {
        return result;
    }
}

class PieChartPanel extends JPanel {
    private List<Transaction> transactions = Collections.emptyList();
    private final Color[] palette = new Color[] { new Color(111, 76, 255), new Color(75, 110, 175),
            new Color(255, 99, 71), new Color(60, 179, 113), new Color(255, 165, 0), new Color(199, 21, 133) };

    public void setTransactions(List<Transaction> t) {
        this.transactions = new ArrayList<>(t);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        g2.setColor(getBackground());
        g2.fillRect(0, 0, w, h);
        Map<String, Double> totals = new LinkedHashMap<>();
        for (Transaction t : transactions) {
            if (t.getType() == Transaction.Type.EXPENSE) {
                totals.merge(t.getCategory(), t.getAmount(), Double::sum);
            }
        }
        double sum = totals.values().stream().mapToDouble(Double::doubleValue).sum();
        if (sum <= 0) {
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawString("No expense data for chart", 10, 20);
            g2.dispose();
            return;
        }
        int size = Math.min(w, h) - 60;
        int x = (w - size) / 2;
        int y = 20;
        double start = 0;
        int i = 0;
        for (Map.Entry<String, Double> en : totals.entrySet()) {
            double val = en.getValue();
            double angle = val / sum * 360.0;
            g2.setColor(palette[i % palette.length]);
            g2.fillArc(x, y, size, size, (int) Math.round(start), (int) Math.round(angle));
            start += angle;
            i++;
        }
        // Legend.
        int lx = 10;
        int ly = y + size + 10;
        i = 0;
        Font small = g2.getFont().deriveFont(12f);
        g2.setFont(small);
        for (Map.Entry<String, Double> en : totals.entrySet()) {
            g2.setColor(palette[i % palette.length]);
            g2.fillRect(lx, ly - 12, 12, 12);
            g2.setColor(Color.WHITE);
            String label = String.format("%s - ₹%.2f (%.0f%%)", en.getKey(), en.getValue(),
                    (en.getValue() / sum * 100));
            g2.drawString(label, lx + 18, ly);
            ly += 18;
            i++;
        }
        g2.dispose();
    }
}