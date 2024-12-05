package finalProj;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Random;

public class SwingMusicPlayer extends JFrame {

    private JList<String> songList;
    private JButton playButton, pauseButton, nextButton, previousButton, shuffleButton, loadFolderButton;
    private JLabel currentSongLabel;
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
        setLayout(new BorderLayout());

        // Set dark theme colors
        Color backgroundColor = new Color(40, 40, 40);
        Color buttonColor = new Color(60, 60, 60);
        Color buttonTextColor = Color.WHITE;
        Color labelColor = Color.WHITE;

        // Top panel for current song display
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());
        currentSongLabel = new JLabel("No song selected");
        currentSongLabel.setFont(new Font("Arial", Font.BOLD, 18));
        currentSongLabel.setForeground(labelColor);
        topPanel.setBackground(backgroundColor);
        topPanel.add(currentSongLabel);
        add(topPanel, BorderLayout.NORTH);

        // Right panel for song list and controls
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setBackground(backgroundColor);

        // Song List Panel
        listModel = new DefaultListModel<>();
        for (File song : songs) {
            listModel.addElement(song.getName());
        }
        songList = new JList<>(listModel);
        songList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        songList.setBackground(new Color(60, 60, 60));
        songList.setForeground(Color.WHITE);
        songList.setSelectionBackground(new Color(80, 80, 80));
        JScrollPane scrollPane = new JScrollPane(songList);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel for controls
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(3, 1, 10, 10));
        controlPanel.setBackground(backgroundColor);

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 6, 10, 10));
        buttonPanel.setBackground(backgroundColor);

        // Initialize buttons with PNG images
        playButton = new JButton(loadImageIcon("pb.png"));
        pauseButton = new JButton(loadImageIcon("pausebutton.png"));
        nextButton = new JButton(loadImageIcon("nb.png"));
        previousButton = new JButton(loadImageIcon("pp.png"));
        shuffleButton = new JButton(loadImageIcon("shuffle.png"));
        loadFolderButton = new JButton(loadImageIcon("music-file.png"));

        // Set tooltips for better user experience
        playButton.setToolTipText("Play");
        pauseButton.setToolTipText("Pause");
        nextButton.setToolTipText("Next");
        previousButton.setToolTipText("Previous");
        shuffleButton.setToolTipText("Shuffle");
        loadFolderButton.setToolTipText("Load Folder");

        // Set button colors
        playButton.setBackground(buttonColor);
        pauseButton.setBackground(buttonColor);
        nextButton.setBackground(buttonColor);
        previousButton.setBackground(buttonColor);
        shuffleButton.setBackground(buttonColor);
        loadFolderButton.setBackground(buttonColor);

        // Set button text colors
        playButton.setForeground(buttonTextColor);
        pauseButton.setForeground(buttonTextColor);
        nextButton.setForeground(buttonTextColor);
        previousButton.setForeground(buttonTextColor);
        shuffleButton.setForeground(buttonTextColor);
        loadFolderButton.setForeground(buttonTextColor);

        pauseButton.setEnabled(false); // Pause button starts as disabled

        buttonPanel.add(previousButton);
        buttonPanel.add(playButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(shuffleButton);
        buttonPanel.add(loadFolderButton);

        // Volume panel
        JPanel volumePanel = new JPanel();
        volumePanel.setLayout(new FlowLayout());
        volumePanel.setBackground(backgroundColor);
        JLabel volumeLabel = new JLabel("Volume:");
        volumeLabel.setForeground(labelColor);
        volumeSlider = new JSlider(0, 100, 50);
        volumeSlider.setBackground(backgroundColor);
        volumePanel.add(volumeLabel);
        volumePanel.add(volumeSlider);

        // Progress slider
        progressSlider = new JSlider(0, 100, 0);
        progressSlider.setBackground(backgroundColor);
        progressSlider.setEnabled(false);
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

        controlPanel.add(buttonPanel);
        controlPanel.add(volumePanel);
        controlPanel.add(progressSlider);

        rightPanel.add(controlPanel, BorderLayout.SOUTH);
        add(rightPanel, BorderLayout.EAST);

        // Initialize MusicPlayer
        musicPlayer = new MusicPlayer(songs);

        // Add Action Listeners
        playButton.addActionListener(e -> playSong());
        pauseButton.addActionListener(e -> pauseSong());
        nextButton.addActionListener(e -> playNext());
        previousButton.addActionListener(e -> playPrevious());
        shuffleButton.addActionListener(e -> toggleShuffle());
        loadFolderButton.addActionListener(e -> loadFolder());

        // Add ListSelectionListener to the song list
        songList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                playButton.setEnabled(true);
                pauseButton.setEnabled(false);
                currentSongLabel.setText("No song selected");
            }
        });

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

        progressSlider.setEnabled(true); // Enable progress slider
        progressTimer = new Timer(200, e -> {
            if (!isAdjustingProgress) {
                int progress = musicPlayer.getProgress();
                progressSlider.setValue(progress);
            }
        });
        progressTimer.start();

        playButton.setEnabled(false);
        pauseButton.setEnabled(true);
    }

    private void pauseSong() {
        musicPlayer.pause();
        currentSongLabel.setText("Paused");
        playButton.setEnabled(true);
        pauseButton.setEnabled(false);
        if (progressTimer != null) progressTimer.stop(); // Stop updating progress
    }

    private void playNext() {
        // Clear the last paused position to ensure the next song starts from the beginning
        musicPlayer.lastPausedPosition = 0;

        // If shuffle is enabled, pick a random song; otherwise, go to the next one sequentially
        int nextIndex = isShuffle
                ? random.nextInt(songList.getModel().getSize())
                : (songList.getSelectedIndex() + 1) % songList.getModel().getSize();

        songList.setSelectedIndex(nextIndex);
        playSong();
    }

    private void playPrevious() {
        int currentIndex = songList.getSelectedIndex();
        int previousIndex = (currentIndex - 1 + songList.getModel().getSize()) % songList.getModel().getSize();
        songList.setSelectedIndex(previousIndex);
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
