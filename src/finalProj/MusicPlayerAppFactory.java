package finalProj;

import javax.swing.*;
import java.io.File;
import java.util.List;

public class MusicPlayerAppFactory {

    private static final String MUSIC_FOLDER = "music";

    public static void createAndRun() {
        // Ensure the music folder exists
        File folder = new File(MUSIC_FOLDER);
        if (!folder.exists() || !folder.isDirectory()) {
            JOptionPane.showMessageDialog(null, "The music folder does not exist: " + MUSIC_FOLDER,
                                          "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Load songs from the folder
        MusicLoader loader = new MusicLoader(MUSIC_FOLDER);
        List<File> songs = loader.loadSongs();

        if (songs.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No music files found in: " + MUSIC_FOLDER,
                                          "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Launch the Swing music player
        SwingMusicPlayer player = new SwingMusicPlayer(songs);
        player.setVisible(true);
    }
}
