public class Axel {
    public static final double MAX_FALL_SPEED = -20;
    public static final double JUMP_SPEED = 20;
    public static final double GRAVITY = 1;
    public static final double DIVE_SPEED = 3 * GRAVITY;
    public static final double LATERAL_SPEED = 8;

    /** coordonnées du point le plus bas de l'axel */
    private int x, y;
    private Pickup.PickupType currentPowerUp;


    //TODO à modifier pour que commun à plusieurs joueurs => à mettre dans Field
    /** décompte jusqu'à la prochaine augmentation de difficulté, basé sur Field.LEVEL_THRESHOLD */
    private int untilNextLevel; 
    /** correspond au max Y atteint */
    private int maxHeightReached;

    /** le score affiché (!= maxHeightReached pcq -height de départ et +points bonus via pickup) */
    private int score;

    boolean falling;
    boolean jumping;
    boolean diving;
    boolean left;
    boolean right;
    private double vitesse;
    private boolean surviving;

    private final Field field;

    public Axel(Field f, int x, int y) {
        currentPowerUp = null;
        this.field = f;
        this.x = x;
        this.y = y;
        score = 0;
        untilNextLevel = Field.LEVEL_THRESHOLD[0];
        maxHeightReached = y;
        this.surviving = true;
    }

    public Pickup.PickupType getCurrentPowerUp() {
        return currentPowerUp;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getScore() {
        return score;
    }

    public boolean isSurviving() {
        return surviving;
    }





    public void update() { 
        computeMove();
        checkCollision();
        updateScore();
    }

    public void updateScore() {
        if (y > maxHeightReached) {
            score += y - maxHeightReached;
            untilNextLevel -= y - maxHeightReached;
            maxHeightReached = y;
        }
    }

    // modifie la position de Axel en fonction de son mouvement
    public void computeMove() {
        if (left) x -= LATERAL_SPEED;
        if (right) x += LATERAL_SPEED;
        updateSpeed();
        applySpeed();
    }
    /** met la vitesse à jour en fonction de falling, jumping & diving */
    private void updateSpeed() {
        if (!falling) {  //si on est sur une plateforme
            vitesse = 0;
            if (jumping) vitesse = JUMP_SPEED;
        }
        else {
            if (vitesse > MAX_FALL_SPEED) { 
                if (diving) vitesse -= DIVE_SPEED;
                vitesse -= GRAVITY;
                if (vitesse < MAX_FALL_SPEED) vitesse = MAX_FALL_SPEED;
            }
            if (!jumping && vitesse > 0) vitesse = vitesse/2; //vitesse -= GRAVITY; // un saut s'arrête + vite si on saute pas
        }
    }
    /** met à jour y en fonction de la vitesse (et falling si on tombe sur un bloc) */
    private void applySpeed() {
        if (vitesse < 0) {  // pr pas traverser de bloc
            Block b = blocSurLeChemin();
            if (b != null) {
                y = b.y;
                falling = false;
                return;
            }
        }
        y += vitesse;
    }

    /** s'il y a un bloc sur le chemin de axel, renvoie ce bloc
     * <p> sinon, renvoie null
     * <p> ne modifie pas axel
     */
    private Block blocSurLeChemin() {
        Block f = null;
        for (Block b : field.blocs) {
            if (b.y >= y) break;
            if (b.y > field.getBottom()) {
                if ((b.x -1 <= x && x <= b.x + b.width +1) //si Axel est sur la plateforme sur l'axe _ avec une petite marge
                && (y + vitesse <= b.y && b.y < y)) //et si la plateforme est entre Axel et sa position finale sur l'axe |
                    { f = b; }
            }
        }
        return f;
    }





    /* _____________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
     * fonction checkCollision */

    public void checkCollision() {
        checkSurvie();
        if (!surviving) return; 
        checkCollisionBords();
        checkCollisionBlocks();
        checkCollisionLevelThreshold();
        checkCollisionPickups();
    }

    /** set surviving à true si axel survie (car est pas en bas de l'écran ou a un bouclier), false sinon
     *  <p> si axel a un bouclier, l'enlève et remet axel en haut de l'écran avec une vitesse nulle */
    private void checkSurvie() {
        if (y < field.getBottom()) {
            if (currentPowerUp == Pickup.PickupType.SHIELD) {
                Field.pickupRemovedFromGame(currentPowerUp);
                currentPowerUp = null;
                y = field.getTop();
                vitesse = field.getLevel(); // pr être stable pendant quelques frames
            }
            else {
                surviving = false;
            }
        }
    }

    private void checkCollisionBords() {
        if (x > field.width) x = field.width;
        else if (x < 0) x = 0;
        if (y > field.getTop() - GamePanel.AXEL_HEIGHT) // si le haut de axel atteind le haut de la fenetre
            y = field.getTop()- GamePanel.AXEL_HEIGHT;
    }
    /** regarde si axel est sur un bloc, si oui met falling à false (axel n'est plus soumis à la gravité) 
     *  <p> si axel est dans une platforme et ne tombe pas, axel est remis dessus
    */
    private void checkCollisionBlocks() {
        if (diving || vitesse>0) {  
            // si le joueur choisit de tomber, il tombe (peu importe qu'il y ait un bloc ou non)
            // si le joueur monte, pas besoin de check la collision
            falling = true;
            return;
        }
        for (Block b : field.blocs) {
            if (b.y > field.getTop()) break;
            if (b.y > field.getBottom())  // si le bloc est affiché (probablement une meilleure manière de faire)
                if (b.x -1 <= x && x <= b.x + b.width +1) {
                    //si Axel est sur la plateforme sur l'axe _ avec une petite marge
                    if (b.y == y) {  //si Axel est sur la plateforme sur l'axe |
                        falling = false;
                        return;
                    }
                }
        }
        falling = true;
    }
    /** met à jour le niveau de difficulté */
    private void checkCollisionLevelThreshold() {
        // si on est pas déjà au niveau max
        if (field.getLevel() < Field.LEVEL_THRESHOLD.length-1 && untilNextLevel <= 0) {
            field.increaseLevel();
            untilNextLevel += Field.LEVEL_THRESHOLD[field.getLevel()]; 
        }
        /** 
        for (int i=field.getLevel() +1; i < Field.LEVEL_THRESHOLD.length; i++) {
            if (Field.LEVEL_THRESHOLD[i] <= score) field.increaseLevel();
            else return;
        } 
        */
    }
    private void checkCollisionPickups() {
        for (Pickup p : field.pickups) {
            if (p.enCollision(this)) {
                switch (p.type) {
                    case SHIELD: currentPowerUp = p.type; break;
                    case SLOWDOWN:  field.decreaseLevel(); 
                            untilNextLevel = Field.LEVEL_THRESHOLD[field.getLevel()]/2;
                            break;
                    case SPEEDUP:   field.increaseLevel(); 
                            if (field.getLevel() < Field.LEVEL_THRESHOLD.length)
                                untilNextLevel = Field.LEVEL_THRESHOLD[field.getLevel()] + Field.LEVEL_THRESHOLD[field.getLevel()]/2;
                            break;
                    case COIN: score += Pickup.COIN_VALUE; break;
                }
                field.pickups.remove(p);
                return;  // => peut toucher qu'un pickup par frame, probablement pas un problème ici
            }
        }
    }

}
