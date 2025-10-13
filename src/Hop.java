import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class Hop {
    public static final int WIDTH = 400;
    public static final int HEIGHT = 600;
    public static final int DELAY = 40;

    private final JFrame frame;
    private final Field field;
    private final ArrayList<Axel> axels;
    private Timer timer;
    private GamePanel gamePanel;

    private final int NOMBRE_JOUEURS = 2;

    public Hop() {
        this.field = new Field(WIDTH, HEIGHT);
        axels = new ArrayList<>();
        for (int i=0; i<NOMBRE_JOUEURS; i++) {
            axels.add(new Axel(field, WIDTH/2, Field.START_ALTITUDE));
        }
        this.gamePanel = new GamePanel(field, axels);
        this.frame = new JFrame("Hop!");
        frame.add(gamePanel);
        frame.addKeyListener(gamePanel);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void round() {
        for (Axel axel:axels) axel.update();
        field.update();
        frame.repaint();
    }

    public boolean over() {
        for (Axel a:axels) {
            if (a.isSurviving()) return false;
        }
        return true;
    }

    public static void main(String[] args) {
        Hop game = new Hop();

        game.timer = new Timer(DELAY, (ActionEvent e) -> {
            game.round();
            if (game.over()) {
                game.timer.stop();
                game.frame.remove(game.gamePanel);
            }
        });
        game.timer.start();
    }
}
