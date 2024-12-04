package finalProj;

import javax.sound.sampled.*;
import javax.swing.JOptionPane;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class MusicPlayer {

    private final List<File> songs;
    private Clip audioClip;
    private int currentFrame = 0;
    private boolean isPaused = false;
    private int currentIndex = -1;

    public MusicPlayer(List<File> songs) {
        this.songs = songs;
    }

    public void play(int index) {
        try {
            if (isPaused && index == currentIndex) {
                audioClip.setFramePosition(currentFrame);
                audioClip.start();
                isPaused = false;
                return;
            }

            stop();
            currentIndex = index;
            File song = songs.get(index);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(song);
            audioClip = AudioSystem.getClip();
            audioClip.open(audioStream);

            currentFrame = 0;
            isPaused = false;

            audioClip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error playing file: " + e.getMessage(), "Playback Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void pause() {
        if (audioClip != null && audioClip.isRunning()) {
            currentFrame = audioClip.getFramePosition();
            audioClip.stop();
            isPaused = true;
        }
    }

    public void stop() {
        if (audioClip != null) {
            audioClip.stop();
            audioClip.close();
        }
        currentFrame = 0;
        isPaused = false;
        currentIndex = -1;
    }

    public void setVolume(int volume) {
        if (audioClip != null) {
            try {
                FloatControl gainControl = (FloatControl) audioClip.getControl(FloatControl.Type.MASTER_GAIN);
                float min = gainControl.getMinimum();
                float max = gainControl.getMaximum();
                float newVolume = min + (max - min) * (volume / 100.0f);
                gainControl.setValue(newVolume);
            } catch (IllegalArgumentException e) {
                System.out.println("Volume control is not supported for this audio clip.");
            }
        }
    }

    public void addSong(File song) {
        if (song != null && song.exists() && song.getName().toLowerCase().endsWith(".wav")) {
            songs.add(song);
        } else {
            System.out.println("Invalid or non-existent file: " + song);
        }
    }

    public void clearSongs() {
        songs.clear();
        currentFrame = 0;
        isPaused = false;
        currentIndex = -1;
    }
}
