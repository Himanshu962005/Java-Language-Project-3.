import java.io.*;
import java.util.*;

public class PlayerProfileData implements Serializable {
    private static final long serialVersionUID = 1L;
    public String name;
    public int totalXP;
    public int totalScore;
    public int gamesPlayed;
    public int totalCorrect;
    public int totalQuestions;
    public long lastPlayedTime;
    public Map<String, Integer> animeStats;

    public PlayerProfileData(String name, int totalXP, int totalScore,
            int gamesPlayed, int totalCorrect,
            int totalQuestions, long lastPlayedTime,
            Map<String, Integer> animeStats) {
        this.name = name;
        this.totalXP = totalXP;
        this.totalScore = totalScore;
        this.gamesPlayed = gamesPlayed;
        this.totalCorrect = totalCorrect;
        this.totalQuestions = totalQuestions;
        this.lastPlayedTime = lastPlayedTime;
        this.animeStats = animeStats;
    }
}