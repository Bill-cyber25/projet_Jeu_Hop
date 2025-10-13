public class Pickup {
    /** centre de l'objet */
    public final int x, y;
    public final PickupType type;

    public enum PickupType {COIN, SHIELD, SLOWDOWN, SPEEDUP};
    //moved to Field
    //private static final HashMap<PickupType, Integer> pickupInGame = new HashMap<>(); 
    //{ System.out.println("reset pickupInGame");
    //    for (PickupType p: PickupType.values()) pickupInGame.put(p, 0); }  // initialise pickupInGame

    

    public static final int COIN_VALUE = 500;

    public Pickup(int xx, int yy, PickupType pickupType) {
        x = xx;
        y = yy;
        type = pickupType;
    }

    public Pickup(int xx, int yy, Field field) {
        x = xx;
        y = yy;
        type = randomType(field);
    }

    private PickupType randomType(Field field) {
        int random = Field.seed.nextInt(6);
        PickupType p = null;
        switch (random) {
            case 0, 1: 
                System.out.print("COIN\n");
                return PickupType.COIN;
            case 2, 3: p = PickupType.SHIELD; 
                System.out.print("SHIELD");
                break;
            case 4: p = PickupType.SLOWDOWN; 
                System.out.print("SLOWDOWN");
                break;
            case 5: p = PickupType.SPEEDUP; 
                System.out.print("SPEEDUP");
                break;
            default: throw new RuntimeException("erreur");
        }
        System.out.print(" in game: ");
        System.out.println(Field.pickupInGame.get(p));
        if (!peutGénérer(p, field)) return PickupType.COIN;  // coin par défaut
        return p;
    }

    private boolean peutGénérer(PickupType type, Field field) {
        switch (type) {
            case COIN: return true;
            case SHIELD: if (Field.pickupInGame.get(type) > 0) return false; break;
            case SLOWDOWN: 
                if (Field.pickupInGame.get(type) > 0 && field.getLevel() < 4) 
                    return false; 
                break;
            case SPEEDUP:  //peut que apparaitre si on est en dessous du niveau max -1 (ici 6)
                if (Field.pickupInGame.get(type) > 0 && field.getLevel() >= 6) 
                    return false; 
                break;
        }
        return true;
    }

    public boolean enCollision(Axel axel) {
        int distCollision = GamePanel.AXEL_HEIGHT/2 + GamePanel.PICKUP_RAYON;
        int axelY = axel.getY() + GamePanel.AXEL_HEIGHT/2;  //car x,y de axel est le point inférieur du cercle et pas son centre
        double dist = Math.sqrt(Math.pow(x-axel.getX(), 2) + Math.pow(y-axelY, 2));
        return dist <= distCollision;
    }

}
