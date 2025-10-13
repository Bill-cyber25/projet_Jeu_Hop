import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.security.InvalidParameterException;
import java.util.ArrayList;

public class GamePanel extends JPanel implements KeyListener {
    private static final Color[] RAINBOW = {Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN,
                                    Color.BLUE, Color.MAGENTA, Color.PINK, Color.BLACK};
    private static final Color COIN_COLOR = new Color(255, 200, 0);
    private static final Color SHIELD_COLOR = new Color(0, 255, 200);

    protected static final int BLOCK_HEIGHT = 10;
    protected static final int AXEL_WIDTH = 10;
    protected static final int AXEL_HEIGHT = 10;
    protected static final int PICKUP_RAYON = 7;

    private final ArrayList<Axel> axels;
    private final Field field;
    private final JLabel score;

    private int timerAnimation = 0;

    public GamePanel(Field field, ArrayList<Axel> axels) {
        this.field = field;
        this.axels = axels;
        score = new JLabel("Start");
        this.add(score);
        setPreferredSize(new Dimension(field.width, field.height));
        setBackground(Color.WHITE);
    }

    public void paintComponent(Graphics g) {
        // IMPORTANT: le point (0,0) dans graphics est en HAUT à gauche, 
        //  alors que le point (0,0) dans le jeu   est en BAS  à gauche
        //      donc conversion nécessaire de y pr passer de l'un à l'autre
        super.paintComponent(g);
        afficheBlocs(g);
        for (Axel a : axels) afficheAxel(g, a);
        affichePickups(g);
        afficheScore();
    }

    private void afficheBlocs(Graphics g) {
        g.setColor(Color.BLACK);
        for (Block b : field.blocs) {
        if (b.y > field.getBottom() && b.y-BLOCK_HEIGHT < field.getTop()) // si le bloc est à l'écran
            g.fillRect(b.x, field.height-b.y+field.getBottom(), b.width, BLOCK_HEIGHT);
        }
    }
    private void afficheAxel(Graphics g, Axel axel) {
        g.setColor(couleurAxel(axel));
        g.fillOval(axel.getX()-AXEL_WIDTH/2, 
                   field.height-(axel.getY()+AXEL_HEIGHT-field.getBottom()), 
                   AXEL_WIDTH, 
                   AXEL_HEIGHT);
    }
    private void affichePickups(Graphics g) {
        for (Pickup p : field.pickups) {
            g.setColor(couleurPickup(p));
            if (p.y + PICKUP_RAYON > field.getBottom() && p.y - PICKUP_RAYON < field.getTop()) // si le bloc est à l'écran
                g.fillOval(p.x-PICKUP_RAYON, field.height-(p.y+PICKUP_RAYON-field.getBottom()), 
                            PICKUP_RAYON*2, PICKUP_RAYON*2);
        }
    }
    private void afficheScore() {
        String txt = "";
        for (int i=0; i<axels.size(); i++) {
            txt += "J"+(i+1)+": " + axels.get(i).getScore() + " ";
        }
        txt += " Level: " + field.getLevel();
        score.setText(txt);
    }

    private Color couleurPickup(Pickup p) {
        switch (p.type) {
            case SHIELD: return SHIELD_COLOR;
            case SLOWDOWN: return Color.BLUE;
            case COIN: return COIN_COLOR;
            case SPEEDUP: return Color.RED;
        }
        throw new InvalidParameterException("pickup a un type inconnu/null");
    }
    private Color couleurAxel(Axel axel) {
        switch (axel.getCurrentPowerUp()) {
            case null: return Color.RED;
            case SHIELD: return SHIELD_COLOR;
            case SPEEDUP:
            case SLOWDOWN: 
            case COIN: throw new InvalidParameterException("axel a un powerup impossible");
        }
    }

    private void keyHappened(KeyEvent e, boolean setTo) {
        boolean hasStarted = true;
        /* marche pas pcq switch case veut des constantes 
        int[][] controlsList = {
            {KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT},
            {KeyEvent.VK_Z, KeyEvent.VK_S, KeyEvent.VK_Q, KeyEvent.VK_D}}; */
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP: 
                axels.get(0).jumping = setTo; break;
            case KeyEvent.VK_DOWN: 
                axels.get(0).diving = setTo; break;
            case KeyEvent.VK_LEFT: 
                axels.get(0).left = setTo; break;
            case KeyEvent.VK_RIGHT: 
                axels.get(0).right = setTo; break;
            default: hasStarted = false;
        }
        if (axels.size() > 1) {
            hasStarted = true;
            switch (e.getKeyCode()) {
                case KeyEvent.VK_Z: 
                    axels.get(1).jumping = setTo; break;
                case KeyEvent.VK_S: 
                    axels.get(1).diving = setTo; break;
                case KeyEvent.VK_Q: 
                    axels.get(1).left = setTo; break;
                case KeyEvent.VK_D: 
                    axels.get(1).right = setTo; break;
                default: hasStarted = false;
            }
        }
        if (hasStarted) field.startGame();  //démarre seulement si 1 des joueurs a bougé
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
    @Override
    public void keyPressed(KeyEvent e) {
        keyHappened(e, true);
    }
    @Override
    public void keyReleased(KeyEvent e) {
        keyHappened(e, false);
    }
}
