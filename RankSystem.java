public class RankSystem {
    public static String getRank(int xp) {
        if (xp < 10)
            return "Beginner";
        if (xp < 20)
            return "Rookie";
        if (xp < 30)
            return "Anime Fan";
        if (xp < 40)
            return "Otaku";
        if (xp < 50)
            return "Elite Otaku";
        if (xp < 60)
            return "Anime Scholar";
        if (xp < 70)
            return "Master";
        if (xp < 80)
            return "Grand Master";
        if (xp < 90)
            return "Anime King";
        return "Legendary Anime Watcher";
    }
}