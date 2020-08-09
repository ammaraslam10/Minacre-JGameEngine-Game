public class WelcomeScreen extends JGameEngine.Object {
    JGameEngine e;
    GameState gs;
    double splashscreen = 0;
    int rclr = 100, tclr = 130, r0, r1, r2;
    
    public WelcomeScreen(JGameEngine e, GameState gs) {
	this.e = e;
	this.gs = gs;
    }
    @Override
    public void start() {
	e.audioPlay("sounds/intro.wav", true, 1);
    }

    @Override
    public void update() {
	if(splashscreen < 255) {
	    e.drawRect(e.cameraX(), e.cameraY(), e.cameraWidth(), e.cameraHeight(), e.color(255 - (int) splashscreen,0,0), true);
	    splashscreen += 7 * e.deltaTime;
	    e.textFont("fonts/PressStart2P.ttf", 50);
	    e.drawText("HEY", e.cameraX() + 90, e.cameraY() + 85, e.color(255, 255, 255));
	    e.textFont("fonts/PressStart2P.ttf", 15);
	    e.drawText("JGameEngine Demo", e.cameraX() + 35, e.cameraY() + 120, e.color(255, 255, 255));	
	    return;
	}
	if(rclr + (5.5 * e.deltaTime) < 150) rclr += 5.5 * e.deltaTime; else {
	    rclr = 100;
	    r0 = (int) (Math.random() * 40 - 20);
	    r1 = (int) (Math.random() * 40 - 20);
	    r2 = (int) (Math.random() * 40 - 20);
	}
	if(tclr + 8.5 * e.deltaTime < 255) tclr += 8.5 * e.deltaTime;
	e.drawRect(e.cameraX(), e.cameraY(), e.cameraWidth(), e.cameraHeight(), e.color((rclr + r2) % 255, (rclr + r1) % 255, (rclr + r2) % 255), true);
	e.textFont("fonts/PressStart2P.ttf", 30);
	e.drawText("Minacre", e.cameraX() + 60, e.cameraY() + 70, e.color(tclr, tclr, tclr));
	e.textFont("fonts/PressStart2P.ttf", 15);
	e.drawText("Z to start", e.cameraX() + 87, e.cameraY() + 120, e.color(tclr, tclr, tclr));	
	e.textFont("fonts/PressStart2P.ttf", 9);
	e.drawText("Controls: Arrow keys and Z", e.cameraX() + 40, e.cameraY() + 140, e.color(tclr, tclr, tclr));	
	if(e.keyReleased("Z")) {
	    e.removeObject(this);
	    gs.room = "";
	    gs.load_room = true;
	    e.audioRemove("sounds/intro.wav");
	}
    }    
}
