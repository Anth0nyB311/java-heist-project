package game;

import game.environment.environmentManager;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class newGame {

    private JFrame frame;
    private gamePanel panel;
    private environmentManager environment;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            newGame game = new newGame();
            game.createShowWindow();
        });
    }

    private void createShowWindow() {
        frame = new JFrame("CCTV Heist");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        environment = new environmentManager();
        panel = new gamePanel(environment, this);
        frame.setContentPane(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
        environment.setView(panel);
        environment.start(); // City Engine physics loop
    }

    public void restartGame() {
        frame.dispose(); // close window
        createShowWindow(); // make a new environment
    }

}
