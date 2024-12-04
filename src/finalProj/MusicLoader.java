package finalProj;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MusicLoader {

    private final String folderPath;

    public MusicLoader(String folderPath) {
        this.folderPath = folderPath;
    }

    public List<File> loadSongs() {
        File folder = new File(folderPath);
        List<File> songs = new ArrayList<>();

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".wav"));
            if (files != null) {
                for (File file : files) {
                    songs.add(file);
                }
            }
        }

        return songs;
    }
}
