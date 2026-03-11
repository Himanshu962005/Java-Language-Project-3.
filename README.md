# Java-Language-Project-3.
1.**Project Title : ATM Simulation System (Core Java).**

**Project Description :**
The ATM Simulation System is a desktop-based banking application developed using Core Java and Java Swing. It simulates the working of a real Automated Teller Machine by allowing users to perform common banking operations such as account creation, login authentication, balance inquiry, and cash withdrawal.
The system provides a structured and secure environment for managing ATM operations while demonstrating the practical use of Object-Oriented Programming, GUI development, and file-based data persistence in Java. It is designed primarily for educational purposes and helps students understand how real ATM systems manage user authentication and transactions.

**Key Features :**

1. **Account Management :**
  * Create new bank accounts with user details.
  * Each account is assigned a unique Account Number and PIN.
  * Secure login system using PIN authentication.
  * User data stored persistently using file handling.

2. **ATM Operations :**
  * Balance Inquiry to check current account balance.
  * Cash Withdrawal with validation to prevent overdraft.
  * Deposit Money into the account.
  * Transaction updates reflected immediately in stored data.

3. **Security System :**
  * PIN-based authentication for secure access.
  * Limited login attempts to prevent unauthorized usage.
  * Secure handling of user data.

4. **Graphical User Interface :**
  * Built using Java Swing components such as JFrame, JButton, JLabel, JTextField, and Panels.
  * Interactive and easy-to-use interface simulating a real ATM dashboard.

5. **Core Functional Modules :**
  * User Authentication Module.
  * Handles login using account number and PIN.
  * Verifies user credentials before granting access.
  * Transaction Module.
  * Performs deposit, withdrawal, and balance inquiry operations.
  * Ensures correct balance calculation and validation.
  * Data Management Module.
  * Stores account data using file handling / serialization.
  * Ensures persistent storage of user information and transactions.

6. **Technologies Used :**
  * Programming Language : Java (Core Java).
  * GUI Framework : Java Swing.
  * Concepts Used : Object-Oriented Programming (OOP).
  * Data Storage : File Handling / Serialization.
  * Platform : Desktop Application.

7. **Learning Outcomes :**
  * Implementation of GUI-based desktop applications using Java Swing.
  * Understanding of banking system workflow and authentication logic.
  * Application of OOP concepts such as classes, objects, encapsulation, and modular design.
  * Practical experience with file handling and persistent data storage in Java.

8. **Conclusion :**
The ATM Simulation System demonstrates how a real-world ATM operates through a simplified software implementation. The project integrates GUI design, authentication mechanisms, and transaction management into a single application, making it a valuable educational project for students learning Java programming and desktop application development.

2.**Project Title : Expense Tracker System (Core Java)**.

**Project Description :**
The Expense Tracker System is a Java desktop application developed using the Swing GUI framework to help users manage their personal finances. The program allows users to record, track, analyze, and visualize their income and expenses in an organized manner. It provides a graphical interface where users can add, edit, delete, import, and export financial transactions.

**Key Features :**

1. **Purpose of the System**
**The main goal of the Expense Tracker is to help users :**
* Monitor daily income and expenses.
* Maintain a financial record.
* Analyze spending habits.
* Manage budgets effectively.
**It simplifies financial management by storing transaction data and generating reports.**

2. **Main Features**
2.1 **Transaction Management :**
**Users can perform the following operations :**
* Add transaction – Record a new income or expense.
* Edit transaction – Modify existing records.
* Delete transaction – Remove unwanted entries.

** Each transaction includes :**
* Date.
* Type (Income / Expense).
* Category.
* Amount.
* Description.
* Recurrence option.
**These details are stored using the Transaction class.**

2.2 **Recurring Transactions**
**The system supports recurring payments such as :**
* Daily.
* Weekly.
* Monthly.
* Yearly.

**Examples :**
* Monthly rent
* Weekly grocery
* Salary
**Recurring transactions are automatically generated for the selected date range.**

2.3 **Data Storage**
The program stores transaction data in a CSV file.
**Storage location :**
**User Home Directory :**
* → .expense_tracker
* → transactions.csv

**Functions provided :**
* Load transactions when the program starts.
* Save transactions automatically.
* Import data from CSV.
* Export data to CSV.
**This functionality is handled by the DataStorage class.**

3. **Financial Analysis Features**
3.1 **Income vs Expense Tracking :**
**The system calculates :**
* Total Income.
* Total Expense.
* Remaining Balance.
* Formula : Balance = Income − Expense.

3.2 **Monthly Summary Report**
**Shows financial data for each month :**
**Example output :**
* 2026-01
* Income : ₹50000
* Expense : ₹30000
* Balance : ₹20000

3.3 **Yearly Summary Report**
Displays financial totals for each year.
**Example:**
* 2026
* Income : ₹600000
* Expense : ₹420000
* Balance : ₹180000

3.4 **Category Spending Report**
**Shows how much money is spent in each category :**
**Example :**
* Food : ₹4500
* Transport : ₹1500
* Shopping : ₹2000
**This helps identify spending habits.**

4. **Graphical User Interface (GUI)**
The application uses Java Swing components.
**Main interface sections :**
**Top Panel**
**Contains control buttons :**
* Add.
* Edit.
* Delete.
* Import CSV.
* Export CSV.
* Set Budget.
* Monthly Summary.
* Yearly Summary.
* Category Report.
**Also includes Month and Year filters.**

**Center Panel**
Displays transactions in a table format using JTable.

**Columns :**
* Date | Type | Category | Amount | Description | Recurrence

**Right Panel**
**Shows financial statistics :**
* Total Income.
* Total Expense.
* Balance.
* Budget progress bar.
* Pie chart visualization.

5. **Budget Management**
Users can set a monthly budget.
**The system displays :**
* Percentage of budget used.
* Visual progress bar.

**Example:**
* Expense : ₹8000
* Budget : ₹10000
* Used : 80%

6. **Data Visualization**
A Pie Chart displays spending distribution by category.
**Example :**
* Food        → 40%
* Transport   → 20%
* Shopping    → 25%
* Bills       → 15%
**This helps users quickly understand where money is being spent.**

7. **Technologies Used**
**Technology	Purpose :**
* Java	Core programming language.
* Swing	Graphical User Interface.
* CSV File	Data storage.
* JTable	Transaction table.
* Graphics2D	Pie chart visualization.
* Java Time API	Date and time management.

8. **Advantages of the System**
* Easy financial tracking.
* User-friendly GUI.
* Data import/export support.
* Budget management.
* Visual spending analysis.
* Recurring transaction automation.

9. **Conclusion**
The Expense Tracker System is a complete personal finance management application built using Java Swing. It allows users to record transactions, analyze financial data, visualize spending patterns, and maintain a budget efficiently. The system improves financial awareness and helps users manage their money in a structured and organized way.

3.**Project Title : Anime Quiz Application System (Core Java).**

**Project Description :**
A Java Swing-based desktop quiz application that tests your knowledge across 10 popular anime series — including Naruto, Pokémon, Jujutsu Kaisen, Solo Leveling, One Punch Man, and more!.

**🚀 Features**
* 10 Anime Categories — Beyblade, Pokémon, Naruto and Boruto, Miraculous, One Punch Man, Ben 10, Jujutsu Kaisen, Lookism, Solo Leveling, Ranma 1/2.
* Randomized Questions — Answer options are shuffled every game for a fresh experience.
* Difficulty Modes — Choose between Easy (10s), Medium (20s), and Hard (30s) per question.
* Flexible Question Count — Select anywhere from 5 to 100 questions per session.
* Live Countdown Timer — A per-question timer adds pressure and excitement.
* XP & Leveling System — Earn XP for every correct answer and level up over time.
* Rank Progression — Climb from Beginner all the way to Legendary Anime Watcher.
* Achievement System — Unlock 10 unique achievements as your XP grows.
* Persistent Player Profiles — Stats are saved locally and loaded automatically on return.
* Detailed Results Screen — Review every answer after the quiz with correct/incorrect breakdowns.
* Profile Stats Viewer — See your best scores per anime, total accuracy, games played, and more.

**🛠️ Tech Stack**
* Language: Java.
* GUI Framework: Java Swing.
* Data Persistence: Java Object Serialization (.dat files).
* Architecture: Multi-frame MVC-style layout.

**📁 Project Structure :**
FileRoleMain.javaEntry pointStartFrame.javaMain menu UIQuizFrame.javaQuiz gameplay UIResultFrame.javaResults & review screenProfileStatsFrame.javaPlayer stats viewerPlayerProfile.javaProfile logic & file I/OPlayerProfileData.javaSerializable profile dataQuestionBank.javaAll quiz questionsQuestion.javaQuestion model with shufflingRankSystem.javaXP-to-rank mappingAchievementSystem.javaXP-based achievement unlocks

**▶️ How to Run :**
* bashjavac *.java
* java Main
* No external dependencies required — runs on any machine with Java 8+.

**👤 Author**
Built with ❤️ for anime fans who love a good challenge.
