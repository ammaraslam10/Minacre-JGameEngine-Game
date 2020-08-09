
import java.util.ArrayList;

public class CharacterPlayer extends CharacterSkeleton {
    int base_attack, attack;
    int base_defence, defence;
    int base_health, health;
    Inventory i;
    QuestList q;
    GameState gs;
    boolean controls;
    boolean can_interact, key_skip, dialogue_run;
    public  DialogueBox generic_dialogue;
    private JGameEngine.Sprite heart_sprite;
    public CharacterPlayer(JGameEngine e) {
	super(e);
	base_attack = attack = 1;
	base_defence = defence = 1;
	base_health = health = 30;
	controls = true;
	can_interact = true;
	heart_sprite = e.sprite(this, "sprites/heart.png");
	heart_sprite.width = 16;
	heart_sprite.height = 16;
	generic_dialogue = new DialogueBox(e, null);
	dialogue_run = false;
    }    
    public void init(String name, String type, Inventory i, QuestList q, GameState gs) {
	super.init(name, type);
	e.addObject(i);
	e.addObject(generic_dialogue);
	speed = 4;
	this.i = i;
	this.q = q;
	this.gs = gs;	
    }
    @Override
    public void start() {
    }
    @Override
    public void update() {
	super.update();
		
	// Player is added before so setting this font here should be reliable enough
	e.textFont("fonts/PressStart2P.ttf", 6);
	
	// Display hearts
	int tmp = health;
	heart_sprite.y = -(e.cameraHeight() / 2 - 20);
	heart_sprite.x = -(e.cameraWidth() / 2 - 24);
	while(tmp > 0){
	    e.drawSprite(heart_sprite);
	    heart_sprite.x += 20; tmp -= 10;
	}
	e.drawText("HP: " + health, e.cameraX() + 10, e.cameraY() + 30, e.color(255, 255, 255));
	
	// If something can be interacted with, do it
	interact_check();
	
	if(!controls)
	    return;
	// First keypress, setting movement_beginning to true will fix the direction of the image
	if(e.keyPressed("left") || e.keyPressed("right") || e.keyPressed("up") || e.keyPressed("down")) {
	    super.movement_beginning = true;
	    if (e.keyPressed("left")) { moving_direction = "left"; }
	    else if (e.keyPressed("right")) { moving_direction = "right"; }
	    else if (e.keyPressed("up")) { moving_direction = "up"; }
	    else if(e.keyPressed("down")) { moving_direction = "down"; }
	}
	// Afterwards, while key is being held down, setting moving variable will 
	// animate character in that direction appropirately
	if(e.keyPressing("left") && moving_direction.equals("left")) {
	    moving = true;
	} else if(e.keyPressing("right") && moving_direction.equals("right")) {
	    moving = true;
	} else if(e.keyPressing("up") && moving_direction.equals("up")) {
	    moving = true;
	} else if(e.keyPressing("down") && moving_direction.equals("down")) {
	    moving = true;
	} else {
	    moving = false;
	}
    }    
    void interact_check() {
	// Can interact with menus, and controls are set to false
	// probably a menu is opened so no need to interact with anything	
	if(can_interact == true && controls == false)
	    return;
	if(e.keyReleased("z")) {
	    if(key_skip) {
		key_skip = false;
		return;
	    }
	    ArrayList<JGameEngine.CollisionMask> msks;
	    if(moving_direction.equals("left")) msks = e.collisionBoxTest(this.x - 12, this.y, 12, 32);
	    else if(moving_direction.equals("right")) msks = e.collisionBoxTest(this.x + 12 + 32, this.y, 12, 32);
	    else if(moving_direction.equals("up")) msks = e.collisionBoxTest(this.x, this.y - 12, 32, 12);
	    else msks = e.collisionBoxTest(this.x, this.y + 12 + 32, 32, 12);
	    can_interact = true;
	    for(int i = 0; i < msks.size(); i++) {
		if( msks.get(i) != m && 
		    (msks.get(i).o.getClass() == CharacterNPC.class || 
		     msks.get(i).o.getClass() == CharacterNPCEnemy.class || 
		     msks.get(i).o.getClass() == Interactables.class)) {
			Interact in = (Interact) msks.get(i).o;
			if(!in.interact()) {
			    controls = true;	
			} else {
			    controls = false;
			}
			can_interact = false;
		}
	    }
	}
	// Generic messages
	if(generic_dialogue.name.equals("")) { 
	    can_interact = false;
	    if((dialogue_run || e.keyReleased("Z")) && generic_dialogue.runDialogue()) {
		controls = true;
		generic_dialogue.name = " ";
	    }
	    dialogue_run = false;
	}
    }
}
