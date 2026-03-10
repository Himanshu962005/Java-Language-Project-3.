import java.io.*;
import java.util.*;

public class PlayerProfile {
    public String name;
    public int totalXP;
    public int totalScore;
    public int gamesPlayed;
    public int totalCorrect;
    public int totalQuestions;
    public long lastPlayedTime;
    public Map<String, Integer> animeStats; // Anime Name -> Best Score.

    public PlayerProfile(String name) {
        this.name = name;
        this.totalXP = 0;
        this.totalScore = 0;
        this.gamesPlayed = 0;
        this.totalCorrect = 0;
        this.totalQuestions = 0;
        this.lastPlayedTime = System.currentTimeMillis();
        this.animeStats = new HashMap<>();
    }

    // Save Player Profile to File.
    public void save() {
        try {
            File dir = new File("Profiles");
            if (!dir.exists()) {
                dir.mkdir();
            }
            String filename = "Profiles/" + sanitizeFilename(name) + ".dat";
            FileOutputStream fos = new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            // Create a Serializable Profile Object.
            PlayerProfileData data = new PlayerProfileData(
                    name, totalXP, totalScore, gamesPlayed,
                    totalCorrect, totalQuestions, lastPlayedTime,
                    new HashMap<>(animeStats));
            oos.writeObject(data);
            oos.close();
            fos.close();
            System.out.println("✓ Profile Saved : " + name);
        } catch (Exception e) {
            System.out.println("✗ Error Saving Profile : " + e.getMessage());
        }
    }

    // Load Player Profile from File.
    public static PlayerProfile load(String playerName) {
        try {
            String filename = "Profiles/" + sanitizeFilename(playerName) + ".dat";
            File file = new File(filename);
            if (!file.exists()) {
                return null; // Profile Doesn't Exist.
            }
            FileInputStream fis = new FileInputStream(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            PlayerProfileData data = (PlayerProfileData) ois.readObject();
            ois.close();
            fis.close();
            // Reconstruct PlayerProfile from Loaded Data.
            PlayerProfile profile = new PlayerProfile(data.name);
            profile.totalXP = data.totalXP;
            profile.totalScore = data.totalScore;
            profile.gamesPlayed = data.gamesPlayed;
            profile.totalCorrect = data.totalCorrect;
            profile.totalQuestions = data.totalQuestions;
            profile.lastPlayedTime = data.lastPlayedTime;
            profile.animeStats = new HashMap<>(data.animeStats);
            System.out.println("✓ Profile Loaded : " + playerName);
            return profile;
        } catch (Exception e) {
            System.out.println("✗ Error Loading Profile : " + e.getMessage());
            return null;
        }
    }

    // Get All Saved Player Names.
    public static List<String> getAllPlayers() {
        List<String> players = new ArrayList<>();
        try {
            File dir = new File("Profiles");
            if (!dir.exists()) {
                return players;
            }
            File[] files = dir.listFiles((d, name) -> name.endsWith(".dat"));
            if (files != null) {
                for (File file : files) {
                    String playerName = file.getName().replace(".dat", "");
                    players.add(desanitizeFilename(playerName));
                }
            }
        } catch (Exception e) {
            System.out.println("Error Loading Player List : " + e.getMessage());
        }
        return players;
    }

    // Delete Player Profile.
    public static boolean deleteProfile(String playerName) {
        try {
            String filename = "Profiles/" + sanitizeFilename(playerName) + ".dat";
            File file = new File(filename);
            if (file.exists()) {
                file.delete();
                System.out.println("✓ Profile Deleted : " + playerName);
                return true;
            }
        } catch (Exception e) {
            System.out.println("Error Deleting Profile : " + e.getMessage());
        }
        return false;
    }

    // Make FileName Safe by Replacing Special Characters.
    private static String sanitizeFilename(String name) {
        return name.replaceAll("[^a-zA-Z0-9_]", "_");
    }

    private static String desanitizeFilename(String filename) {
        return filename;
    }

    // Update Stats After Quiz.
    public void updateStats(int score, int totalQuestions, int xpEarned, String anime) {
        this.gamesPlayed++;
        this.totalScore += score;
        this.totalCorrect += score;
        this.totalQuestions += totalQuestions;
        this.totalXP += xpEarned;
        this.lastPlayedTime = System.currentTimeMillis();
        // Update Anime-Specific Stats.
        int currentBest = animeStats.getOrDefault(anime, 0);
        if (score > currentBest) {
            animeStats.put(anime, score);
        }
        // Auto-Save After Each Quiz.
        save();
    }

    public int getLevel() {
        return totalXP / 100;
    }

    public String getRank() {
        return RankSystem.getRank(totalXP);
    }

    public double getAccuracy() {
        if (totalQuestions == 0)
            return 0;
        return (double) totalCorrect / totalQuestions * 100;
    }
}