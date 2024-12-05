package finalProj;

import javax.sound.sampled.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MusicPlayer {
    private Clip clip;
    private List<File> songs;
    private int currentSongIndex = -1;
    public long lastPausedPosition = 0; // Store the position where the music was paused

    public MusicPlayer(List<File> songs) {
        this.songs = new ArrayList<>(songs);
    }

    public void play(int index) {
        if (index < 0 || index >= songs.size()) {
            return;
        }
        stop();
        currentSongIndex = index;

        try {
            File song = songs.get(index);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(song);
            clip = AudioSystem.getClip();
            clip.open(audioStream);

            // Seek to the last paused position if it's available
            if (lastPausedPosition > 0) {
                clip.setFramePosition((int) lastPausedPosition);
            }

            // Register a LineListener to handle the song ending
            clip.addLineListener(new LineListener() {
                @Override
                public void update(LineEvent event) {
                    if (event.getType() == LineEvent.Type.STOP) {
                        // When the song finishes, automatically play the next song
                        if (clip.getFramePosition() == clip.getFrameLength()) {
                            // Skip to next song
                        }
                    }
                }
            });

            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        if (clip != null && clip.isRunning()) {
            lastPausedPosition = clip.getFramePosition(); // Store the position where it was paused
            clip.stop();
        }
    }

    public void stop() {
        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }

    public void seekTo(int progress) {
        if (clip != null && clip.isOpen()) {
            long totalFrames = clip.getFrameLength();
            int framePosition = (int) ((progress / 100.0) * totalFrames);
            clip.setFramePosition(framePosition);
            if (!clip.isRunning()) {
                clip.start();
            }
        }
    }

    public int getProgress() {
        if (clip != null && clip.isOpen()) {
            long currentFrame = clip.getFramePosition();
            long totalFrames = clip.getFrameLength();
            if (totalFrames > 0) {
                return (int) ((currentFrame * 100) / totalFrames);
            }
        }
        return 0; // Default to 0 if no clip is loaded
    }

    public void setVolume(int volume) {
        if (clip != null) {
            FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float range = volumeControl.getMaximum() - volumeControl.getMinimum();
            float gain = (range * volume / 100) + volumeControl.getMinimum();
            volumeControl.setValue(gain);
        }
    }

    public void addSong(File song) {
        songs.add(song);
    }

    public void clearSongs() {
        stop();
        songs.clear();
    }

    public boolean isPlaying() {
        return clip != null && clip.isRunning();
    }

    public void setLineListener(LineListener listener) {
        if (clip != null) {
            clip.addLineListener(listener);
        }
    }
}
