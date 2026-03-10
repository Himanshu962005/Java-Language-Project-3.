import java.util.*;

public class AchievementSystem {
    public static List<String> getAchievements(int xp) {
        List<String> list = new ArrayList<>();
        if (xp >= 10)
            list.add("Bots");
        if (xp >= 20)
            list.add("First Blood");
        if (xp >= 30)
            list.add("Getting Started");
        if (xp >= 40)
            list.add("Anime Fan");
        if (xp >= 50)
            list.add("Quiz Master");
        if (xp >= 60)
            list.add("Knowledge Expert");
        if (xp >= 70)
            list.add("Anime Encyclopedia");
        if (xp >= 80)
            list.add("Anime God");
        if (xp >= 90)
            list.add("Immortal");
        if (xp >= 100)
            list.add("Legendary Anime watcher");
        return list;
    }
}