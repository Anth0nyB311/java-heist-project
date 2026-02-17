package game;
import game.environment.environmentManager;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class newGame {

    private JFrame frame;
    private gamePanel panel;            
    private javax.swing.Timer timer;
    private game.environment.environmentManager environment;


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
        panel = new gamePanel(environment);
        frame.setContentPane(panel);
        frame.setSize(800 + 16, 600 + 39);  // extra for borders
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
        startGameLoop();
    }


    private void startGameLoop() {
        int fps = 60;
        int delay = 1000 / fps;
        timer = new javax.swing.Timer(delay, e -> {
            updateGame();
            panel.repaint();
        });
        timer.start();
    }

    private void updateGame() {
        if (environment != null) {
            environment.update();
        }
    }

}
