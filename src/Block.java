public class Block { 
    /** coordonnées du point supérieur gauche de la plateforme
     * <p> x abscisse sur __ width
     * <p> y ordonnée sur | height, pas dans la fenêtre
    */
    protected final int x, y;
    protected final int width;

    public enum BlockType {NORMAL, BOUNCY, STICKY, MOVING};
    //TODO rien n'est implémenté jusqu'à présent, c'est juste des idées

    public Block(int x, int y, int width) {
        this.x = x;
        this.y = y;
        this.width = width;
    }

    /** créé un bloc de taille et abscisse aléatoire
     * <p> taille max et min déterminées par des constantes au sein de la fonction, dépendent de fieldWidth
     * @param y l'ordonnée du bloc
     * @param fieldWidth la largeur du volcan
     */
    public Block(int y, int fieldWidth) {
        final int maxW = fieldWidth/3, minW = fieldWidth/10;  // taille max & min d'un bloc
        width = Field.seed.nextInt(maxW - minW) + minW;
        x = Field.seed.nextInt(fieldWidth-width);
        this.y = y;
    }
    
    /** créé un bloc de taille et abscisse aléatoire
     * @param y l'ordonnée du bloc
     * @param fieldWidth la largeur du volcan
     * @param minW la largeur minimale d'une plateforme
     * @param maxW la largeur maximale d'une plateforme
     */
    public Block(int y, int fieldWidth, int minW, int maxW) {
        width = Field.seed.nextInt(maxW - minW) + minW;
        x = Field.seed.nextInt(fieldWidth-width);
        this.y = y;
    }

}
