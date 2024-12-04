package finalProj;

import javax.swing.SwingUtilities;

public class MusicUI {

	public static void main(String[] args) {
        // Delegates to the factory for initialization
        SwingUtilities.invokeLater(() -> MusicPlayerAppFactory.createAndRun());

	}

}
