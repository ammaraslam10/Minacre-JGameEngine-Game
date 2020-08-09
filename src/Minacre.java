import java.util.ArrayList;

public class Minacre extends JGameEngine {
    Minacre() {
	this.setWindow("Minacre RPG");
	this.windowIcon("sprites/icon.png");

	// Set zoom level
	this.cameraDistance(0.4);
	
	GameState gs = new GameState(this);
	
	// Set up the map, add the objects to map
	// gs.loadGame();
	gs.splash();
	gs.waitAction();
    }    
    
    public static void main(String[] args) {
	Minacre d = new Minacre();
    }
}
