package finalProj;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Random;

public class SwingMusicPlayer extends JFrame {

    private JList<String> songList;
    private JButton playButton, pauseButton, nextButton, previousButton, shuffleButton, loadFolderButton;
    private JLabel currentSongLabel, currentTimeLabel, totalTimeLabel;
    private JSlider volumeSlider;
    private JSlider progressSlider; // Interactive progress slider
    private MusicPlayer musicPlayer;
    private boolean isShuffle = false; // Track shuffle mode
    private Random random = new Random();
    private DefaultListModel<String> listModel; // For dynamically updating the song list
    private Timer progressTimer; // Timer for progress updates
    private boolean isAdjustingProgress = false; // Track user interaction with slider

    public SwingMusicPlayer(List<File> songs) {
        setTitle("Modern Music Player");
        setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);  // Set layout to null for manual positioning of components

        // Disable resizing of the JFrame
        setResizable(false);

        // Set dark theme colors
        Color backgroundColor = new Color(40, 30, 40); // Dark background color
        Color labelColor = Color.WHITE; // White text for labels

        // Top panel for current song display
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());
        currentSongLabel = new JLabel("No song selected");
        currentSongLabel.setFont(new Font("Arial", Font.BOLD, 18));
        currentSongLabel.setForeground(labelColor);
        topPanel.setBackground(backgroundColor);
        topPanel.setBounds(300, 500, 550, 40); // Adjust the position of the top panel
        topPanel.add(currentSongLabel);
        add(topPanel);

        // Song List Panel
        listModel = new DefaultListModel<>();
        for (File song : songs) {
            listModel.addElement(song.getName());
        }
        songList = new JList<>(listModel);
        songList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        songList.setBackground(new Color(60, 60, 60)); // Dark background for song list
        songList.setForeground(Color.WHITE); // White text for song list
        songList.setSelectionBackground(new Color(80, 80, 80)); // Dark selection background
        JScrollPane scrollPane = new JScrollPane(songList);
        scrollPane.setBounds(10, 60, 200, 500);  // Adjust the position and size of the song list
        scrollPane.getVerticalScrollBar().setBackground(backgroundColor); // Set dark background for the scroll bar
        add(scrollPane);

        // Control panel for buttons and sliders
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(null);  // Use null layout for control panel to manually position components
        controlPanel.setBounds(250, 60, 960, 800);  // Adjust control panel bounds to fit the bottom of the frame
        controlPanel.setBackground(backgroundColor); // Set background color of control panel

        // Initialize buttons with PNG images
        playButton = new JButton(loadImageIcon("pb.png"));
        pauseButton = new JButton(loadImageIcon("pausebutton.png"));
        nextButton = new JButton(loadImageIcon("nb.png"));
        previousButton = new JButton(loadImageIcon("pp.png"));
        shuffleButton = new JButton(loadImageIcon("shuffle.png"));
        loadFolderButton = new JButton(loadImageIcon("music-file.png"));

        // Make buttons invisible but keep icons visible
        JButton[] buttons = {playButton, pauseButton, nextButton, previousButton, shuffleButton, loadFolderButton};
        for (JButton button : buttons) {
            button.setBorderPainted(false); // Remove the border
            button.setContentAreaFilled(false); // Make the background invisible
            button.setFocusPainted(false); // Disable focus paint
            button.setOpaque(false); // Ensure transparency
        }

        // Set tooltips for better user experience
        playButton.setToolTipText("Play");
        pauseButton.setToolTipText("Pause");
        nextButton.setToolTipText("Next");
        previousButton.setToolTipText("Previous");
        shuffleButton.setToolTipText("Shuffle");
        loadFolderButton.setToolTipText("Load Folder");

        // Manually set bounds for each button to adjust layout
        playButton.setBounds(230, 545, 45, 45);
        pauseButton.setBounds(130, 545, 45, 45);
        nextButton.setBounds(310, 545, 45, 45);
        previousButton.setBounds(40, 545, 45, 45);
        shuffleButton.setBounds(410, 545, 45, 45);
        loadFolderButton.setBounds(500, 545, 45, 45);

        // Add buttons to the control panel
        controlPanel.add(playButton);
        controlPanel.add(pauseButton);
        controlPanel.add(nextButton);
        controlPanel.add(previousButton);
        controlPanel.add(shuffleButton);
        controlPanel.add(loadFolderButton);

        // Volume Panel
        JPanel volumePanel = new JPanel();
        volumePanel.setLayout(new FlowLayout());
        volumePanel.setBackground(backgroundColor);
        JLabel volumeLabel = new JLabel("Volume:");
        volumeLabel.setForeground(labelColor);
        volumeSlider = new JSlider(0, 100, 50);
        volumeSlider.setBackground(backgroundColor);
        volumePanel.add(volumeLabel);
        volumePanel.add(volumeSlider);
        volumePanel.setBounds(500, 550, 400, 50);  // Position of volume panel in control panel
        controlPanel.add(volumePanel);

        // Progress slider
        progressSlider = new JSlider(0, 100, 0);
        progressSlider.setBackground(backgroundColor);
        progressSlider.setEnabled(false);
        progressSlider.setBounds(125, 500, 400, 30);  // Position of progress slider in control panel
        progressSlider.addChangeListener(e -> {
            if (progressSlider.getValueIsAdjusting()) {
                isAdjustingProgress = true;
            } else {
                if (isAdjustingProgress) {    
                    int progress = progressSlider.getValue();
                    musicPlayer.seekTo(progress); // Seek to new position
                    isAdjustingProgress = false;
                }
            }
        });
        controlPanel.add(progressSlider);

        // Time labels for progress slider
        currentTimeLabel = new JLabel("0:00");
        currentTimeLabel.setForeground(labelColor);
        currentTimeLabel.setBounds(75, 500, 50, 30); // Position of current time label
        controlPanel.add(currentTimeLabel);

        totalTimeLabel = new JLabel("0:00");
        totalTimeLabel.setForeground(labelColor);
        totalTimeLabel.setBounds(100, 500, 50, 30); // Position of total time label
        controlPanel.add(totalTimeLabel);

        // Add control panel to the main frame
        add(controlPanel);

        // Initialize MusicPlayer
        musicPlayer = new MusicPlayer(songs);

        // Add Action Listeners for buttons
        playButton.addActionListener(e -> playSong());
        pauseButton.addActionListener(e -> pauseSong());
        nextButton.addActionListener(e -> playNext());
        previousButton.addActionListener(e -> playPrevious());
        shuffleButton.addActionListener(e -> toggleShuffle());
        loadFolderButton.addActionListener(e -> loadFolder());

        // Volume slider listener
        volumeSlider.addChangeListener(e -> {
            int volume = volumeSlider.getValue();
            musicPlayer.setVolume(volume);
        });

        // Set the main window background color
        getContentPane().setBackground(backgroundColor);
    }

    // Method to load PNG images for buttons
    private ImageIcon loadImageIcon(String fileName) {
        try {
            java.net.URL resource = getClass().getClassLoader().getResource("assets/" + fileName);
            if (resource == null) {
                System.err.println("Error: Icon not found: " + fileName);
                return new ImageIcon(); // Return an empty icon to prevent layout issues
            }
            return new ImageIcon(resource);
        } catch (Exception e) {
            System.err.println("Error loading icon: " + fileName + " - " + e.getMessage());
            return new ImageIcon(); // Return an empty icon to prevent layout issues
        }
    }

    private void playSong() {
        int selectedIndex = songList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Please select a song to play!", "No Song Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String selectedSong = songList.getModel().getElementAt(selectedIndex);
        currentSongLabel.setText("Playing: " + selectedSong);
        musicPlayer.play(selectedIndex);

        // Apply the current volume setting
        int currentVolume = volumeSlider.getValue();
        musicPlayer.setVolume(currentVolume);

        progressSlider.setEnabled(true); // Enable progress slider
        progressTimer = new Timer(200, e -> {
            if (!isAdjustingProgress) {
                int progress = musicPlayer.getProgress();
                progressSlider.setValue(progress);

                // Update the current time label
                currentTimeLabel.setText(formatTime(progress));

                // Update the total time label (song duration)
                int totalDuration = musicPlayer.getTotalDuration();
                totalTimeLabel.setText(formatTime(totalDuration));
            }
        });
        progressTimer.start();

        playButton.setEnabled(false);
        pauseButton.setEnabled(true);
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%d:%02d", minutes, remainingSeconds);
    }

    private void pauseSong() {
        musicPlayer.pause();
        currentSongLabel.setText("Paused");
        playButton.setEnabled(true);
        pauseButton.setEnabled(false);
        if (progressTimer != null) progressTimer.stop(); // Stop updating progress
    }

    private void playNext() {
        musicPlayer.lastPausedPosition = 0;
        int nextIndex = isShuffle
                ? random.nextInt(songList.getModel().getSize())
                : (songList.getSelectedIndex() + 1) % songList.getModel().getSize();

        songList.setSelectedIndex(nextIndex);
        playSong();
    }

    private void playPrevious() {
        int currentIndex = songList.getSelectedIndex();
        int previousIndex = (currentIndex - 1 + songList.getModel().getSize()) % songList.getModel().getSize();

        // Ensure the song starts from the beginning
        musicPlayer.lastPausedPosition = 0;

        songList.setSelectedIndex(previousIndex);
        musicPlayer.stop(); // Stop the current clip to reset playback position
        playSong();
    }

    private void toggleShuffle() {
        isShuffle = !isShuffle;
        JOptionPane.showMessageDialog(this, isShuffle ? "Shuffle ON" : "Shuffle OFF", "Shuffle", JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadFolder() {
        JFileChooser folderChooser = new JFileChooser();
        folderChooser.setDialogTitle("Select a Folder");
        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = folderChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = folderChooser.getSelectedFile();
            File[] files = selectedFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".wav"));

            if (files == null || files.length == 0) {
                JOptionPane.showMessageDialog(this, "No WAV files found in the selected folder.", 
                                              "Folder Empty", JOptionPane.WARNING_MESSAGE);
                return;
            }

            listModel.clear();
            musicPlayer.clearSongs();

            for (File file : files) {
                listModel.addElement(file.getName());
                musicPlayer.addSong(file);
            }

            JOptionPane.showMessageDialog(this, "Folder loaded successfully with " + files.length + " WAV file(s).", 
                                          "Folder Loaded", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
