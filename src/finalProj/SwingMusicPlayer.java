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
    private MusicPlayer musicPlayer;
    private boolean isShuffle = false; // Track shuffle mode
    private Random random = new Random();
    private DefaultListModel<String> listModel; // For dynamically updating the song list

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
        controlPanel.setLayout(new GridLayout(2, 1, 10, 10));
        controlPanel.setBackground(backgroundColor);

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 6, 10, 10));
        buttonPanel.setBackground(backgroundColor);

        previousButton = new JButton("Previous");
        playButton = new JButton("Play");
        pauseButton = new JButton("Pause");
        nextButton = new JButton("Next");
        shuffleButton = new JButton("Shuffle");
        loadFolderButton = new JButton("Load Folder");

        // Set button colors
        previousButton.setBackground(buttonColor);
        playButton.setBackground(buttonColor);
        pauseButton.setBackground(buttonColor);
        nextButton.setBackground(buttonColor);
        shuffleButton.setBackground(buttonColor);
        loadFolderButton.setBackground(buttonColor);

        previousButton.setForeground(buttonTextColor);
        playButton.setForeground(buttonTextColor);
        pauseButton.setForeground(buttonTextColor);
        nextButton.setForeground(buttonTextColor);
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
        volumeSlider.setMajorTickSpacing(25);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        volumeSlider.setBackground(backgroundColor);
        volumeSlider.setForeground(labelColor);

        volumePanel.add(volumeLabel);
        volumePanel.add(volumeSlider);

        controlPanel.add(buttonPanel);
        controlPanel.add(volumePanel);

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
                // Enable the Play button and reset Pause button when a new song is selected
                playButton.setEnabled(true);
                pauseButton.setEnabled(false);
                currentSongLabel.setText("No song selected");
            }
        });

        // Set the main window background color
        getContentPane().setBackground(backgroundColor);
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

        playButton.setEnabled(false); // Disable Play when playing
        pauseButton.setEnabled(true); // Enable Pause when playing
    }

    private void pauseSong() {
        musicPlayer.pause();
        currentSongLabel.setText("Paused");
        playButton.setEnabled(true); // Enable Play after pausing
        pauseButton.setEnabled(false); // Disable Pause after pausing
    }

    private void playNext() {
        int nextIndex;

        if (isShuffle) {
            // Pick a random index
            nextIndex = random.nextInt(songList.getModel().getSize());
        } else {
            // Play the next song in sequence
            int currentIndex = songList.getSelectedIndex();
            nextIndex = (currentIndex + 1) % songList.getModel().getSize();
        }

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
        String status = isShuffle ? "Shuffle ON" : "Shuffle OFF";
        JOptionPane.showMessageDialog(this, status, "Shuffle", JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadFolder() {
        JFileChooser folderChooser = new JFileChooser();
        folderChooser.setDialogTitle("Select a Folder");
        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = folderChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = folderChooser.getSelectedFile();

            // Get all valid .wav files in the folder
            File[] files = selectedFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".wav"));

            if (files == null || files.length == 0) {
                JOptionPane.showMessageDialog(this, "No WAV files found in the selected folder.", 
                                              "Folder Empty", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Clear the current list and reload
            listModel.clear(); // Clear the JList
            musicPlayer.clearSongs(); // Clear the MusicPlayer song list

            for (File file : files) {
                listModel.addElement(file.getName()); // Update JList
                musicPlayer.addSong(file); // Update MusicPlayer
            }

            JOptionPane.showMessageDialog(this, "Folder loaded successfully with " + files.length + " WAV file(s).", 
                                          "Folder Loaded", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
