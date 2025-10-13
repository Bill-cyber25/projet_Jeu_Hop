import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Field {
    public static final int ALTITUDE_GAP = 80;
    public static final int START_ALTITUDE = 40;

    /** espace entre chaque changement de niveau */
    public static final int[] LEVEL_THRESHOLD = {80, 80*3, 720*3, 1200*3, 1200, 1600, 3600};
    //TODO à remettre à la normale après les tests
    public static final int[] MIN_BLOCK_WIDTH = {50, 45, 40, 35, 30, 25, 20};
    public static final int[] MAX_BLOCK_WIDTH = {100, 90, 80, 70, 60, 50, 40};

    static final HashMap<Pickup.PickupType, Integer> pickupInGame = new HashMap<>(); 
    { for (Pickup.PickupType p: Pickup.PickupType.values()) pickupInGame.put(p, 0); }  // initialise pickupInGame

    public static final Random seed = new Random();  //pr éviter d'en définir 1 à chaque nouveau bloc
    public static final int PICKUP_RATE = 5; //tt les 5 blocs au minimum

    public final int width, height;
    private int bottom, top; // bottom and top altitude
    protected ArrayList<Block> blocs;
    protected ArrayList<Pickup> pickups;

    private boolean hasStarted;
    /** le niveau de difficulté actuel, augmente au cours de la partie */
    private int difficultyLevel;

    public Field(int width, int height) {
        this.width = width;
        this.height = height;
        bottom = 0;
        top = height;
        hasStarted = false;
        blocs = randomBlocksList();
        pickups = new ArrayList<>();
        difficultyLevel = 0;
    }

    public int getBottom() {
        return bottom;
    }
    public int getTop() {
        return top;
    }
    public int getLevel() {
        return difficultyLevel;
    }


    /** créé une liste de blocs avec une taille et une abscisse aléatoire
      * <p> les blocs sont espacés régulièrement en fonction de ALTITUDE_GAP, 
      * START_ALTITUDE et height */
    private ArrayList<Block> randomBlocksList() {
        ArrayList<Block> bList = new ArrayList<>();
        bList.add(new Block(width/2 - width/8, START_ALTITUDE, width/4));  // 1er bloc fixe
        bList.addAll(randomBlocksList(START_ALTITUDE + ALTITUDE_GAP));  // blocs suivants aléatoires
        return bList;
    }
    /** créé une liste de blocs avec une taille et une abscisse aléatoire
     *  <p> les blocs sont espacés régulièrement en fonction de ALTITUDE_GAP, 
     *  START_ALTITUDE et height
     * @param startY l'altitude du 1er bloc de la liste générée */
    private ArrayList<Block> randomBlocksList(int startY) {
        ArrayList<Block> bList = new ArrayList<>();
        int y = startY;
        for (int i=0; i<=height/ALTITUDE_GAP; i++) {
            int index = difficultyLevel;
            if (index < MIN_BLOCK_WIDTH.length) index = MIN_BLOCK_WIDTH.length-1;
            bList.add(new Block(y, width, MIN_BLOCK_WIDTH[index], MAX_BLOCK_WIDTH[index]));
            y += ALTITUDE_GAP;
        }
        return bList;
    }

    private ArrayList<Pickup> RandomPickupList(ArrayList<Block> blocs) {
        ArrayList<Pickup> pickups = new ArrayList<>();
        int timeout = 0;
        for (Block b : blocs) { 
            if (b.y > top && (seed.nextInt(PICKUP_RATE) == PICKUP_RATE-1 || timeout >= PICKUP_RATE)) {
                int x = b.x + b.width/2;
                int y = b.y + GamePanel.PICKUP_RAYON + GamePanel.PICKUP_RAYON/2;
                Pickup newPickup = new Pickup(x, y, this);
                pickups.add(newPickup);
                pickupAddedToGame(newPickup.type);
                timeout = 0;
            } 
            else timeout++;
        }
        return pickups;
    }


    public void increaseLevel() {
        difficultyLevel++;
    }
    public void decreaseLevel() {
        if (difficultyLevel > 1) difficultyLevel--;
    }
    public void startGame() {
        hasStarted = true;
    }
    
    public void update() {
        if (hasStarted) {
            bottom += difficultyLevel;  // scroll up
            top += difficultyLevel;
            if (blocs.get(blocs.size()-1).y < top) {  
                // si il n'y a plus assez de blocs
                removeOldBlocks();
                removeOldPickups();
                ArrayList<Block> newBlocks = randomBlocksList(blocs.get(blocs.size()-1).y + ALTITUDE_GAP);
                blocs.addAll(newBlocks);
                pickups.addAll(RandomPickupList(newBlocks));
            }
            //if (blocs.get(0).y < bottom) blocs.remove(0);
            //pas nécessaire pour l'instant
        }
    }

    private void removeOldBlocks() {
        for (int i=0; i<blocs.size(); i++) {
            if (blocs.get(i).y < bottom) {
                blocs.remove(i);
                i--;
            }
            else return;
        }
    }
    private void removeOldPickups() {
        for (int i=0; i<pickups.size(); i++) {
            if (pickups.get(i).y < bottom) {
                pickupRemovedFromGame(pickups.get(i).type);
                pickups.remove(i);
                i--;
            }
            else return;
        }
    }

    public static void pickupRemovedFromGame(Pickup.PickupType type) {
        int newValue = pickupInGame.get(type)-1;
        if (newValue >= 0) pickupInGame.put(type, newValue);
        else pickupInGame.put(type, 0);
    }

    public static void pickupAddedToGame(Pickup.PickupType type) {
        int newValue = pickupInGame.get(type)+1;
        if (newValue > 0) pickupInGame.put(type, newValue);
        else throw new RuntimeException("valeur négative");
    }
    
}
