public class CharacterNPCEnemy extends CharacterSkeleton implements Interact {
    int health;
    int defence;
    int attack;
    CharacterPlayer p;
    int radius = 128;
    int inner_radius = 50;
    int inner_radius_time = 0;
    boolean death = false;
    boolean is_alive = true;
    public CharacterNPCEnemy(JGameEngine e) {
	super(e);
    }
    public void init(CharacterPlayer p) {
	super.init(name, name+".png");
	this.p = p;
	if(name.equals("type_1")) {
	    attack = 3;
	    defence = 0;
	    health = 5;
	    speed = 1;
	} else if(name.equals("type_2")) {
	    attack = 6;
	    defence = 3;
	    health = 30;
	    speed = 2;
	} else if(name.equals("type_3")) {
	    attack = 13;
	    defence = 5;
	    health = 50;
	    speed = 2;
	}
    }
    @Override
    public boolean interact() {
	if(p.attack - defence > 0) health -= (p.attack - defence);
	e.audioPlay("sounds/hit1.wav", false, 1);
	e.drawText(""+(p.attack - defence), x, y - 5, e.color(255, 255, 0));
	p.i.validate_potion();
	return false;
    }
    @Override
    public void update() {
	super.update();
	e.textFont("fonts/PressStart2P.ttf", 6);
	if(p == null) return;
	double dist = Math.sqrt(Math.pow(x - p.x, 2) + Math.pow(y - p.y, 2));
	if(dist < radius && health > 0) {
	    e.drawText(""+health, x + 12, y - 5, e.color(255, 255, 255));
	    moving = true;
	    String OM = moving_direction;
	    
	    if(p.x < x && (Math.abs(p.x - x) > Math.abs(p.y - y))) moving_direction = "left";
	    else if(p.x > x && (Math.abs(p.x - x) > Math.abs(p.y - y))) moving_direction = "right";
	    else if(p.y > y) moving_direction = "down";
	    else moving_direction = "up";
	    
	    if(!OM.equals(moving_direction)) super.movement_beginning = true;
	    if(dist < inner_radius) {
		p.can_interact = false;		
		moving = false;
		inner_radius_time += 10 * e.deltaTime;
	    } else {
		p.can_interact = true;
		inner_radius_time -= 4 * e.deltaTime;
	    }
	    if(inner_radius_time > 20) { 
		if(attack - p.defence > 0) p.health -= (attack - p.defence); 
		e.drawRect(e.cameraX(), e.cameraY(), e.cameraWidth(), e.cameraHeight(), e.color(0,0,0), true);
		if(p.health < 0) { 
		    p.health = 0;		
		    p.controls = false;		
		    death = true;
		    p.moving = false;
		    p.moving_direction = "down";
		    e.audioRemove("sounds/hit2.wav");
		}		
		inner_radius_time = 0; 
		if(!e.audioPlaying("sounds/hit2.wav")) e.audioPlay("sounds/hit2.wav", false, 1);
	    }
	    if(inner_radius_time < 0) { inner_radius_time = 0; }
	} else { 
	    moving = false;
	}
	if(health <= 0) {
	    e.collisionMaskRemove(m);
	    this.draw = false;
	    if(m != null) p.can_interact = true;
	    m = null;

	    if(name.equals("type_3")) {
		p.controls = false;
		p.can_interact = false;		
		e.drawRect(e.cameraX(), e.cameraY(), e.cameraWidth(), e.cameraHeight(), e.color(0,0,0), true);
		e.textFont("fonts/PressStart2P.ttf", 30);
		e.drawText("You Win!", e.cameraX() + 45, e.cameraY() + 70, e.color(255, 255, 255));
		e.textFont("fonts/PressStart2P.ttf", 15);
		e.drawText("X to exit", e.cameraX() + 100, e.cameraY() + 120, e.color(255, 255, 255));
		if(e.keyReleased("X")) {
		    p.gs.gameover = true;
		}	    
	    }
	}
	if(death) {
	    e.drawRect(e.cameraX(), e.cameraY(), e.cameraWidth(), e.cameraHeight(), e.color(0,0,0), true);
	    e.textFont("fonts/PressStart2P.ttf", 30);
	    e.drawText("You Died", e.cameraX() + 45, e.cameraY() + 70, e.color(255, 255, 255));
	    e.textFont("fonts/PressStart2P.ttf", 15);
	    e.drawText("X to exit", e.cameraX() + 100, e.cameraY() + 120, e.color(255, 255, 255));
	    if(e.keyReleased("X")) {
		p.gs.gameover = true;
	    }
	}
    }
}
