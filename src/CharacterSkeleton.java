
import java.util.ArrayList;

public abstract class CharacterSkeleton extends JGameEngine.Object implements JGameEngine.Collision {
    JGameEngine e;
    JGameEngine.CollisionMask m;
    JGameEngine.Sprite s;
    double width;
    double height;

    boolean moving, movement_beginning, has_moved, draw;
    String moving_direction;
    int img_cnt, last_image_index;
    double speed;
    
    public CharacterSkeleton(JGameEngine e) {
	this.e = e;
	width = 32;
	height = 32;
	draw = true;
    }   
    @Override
    public void start() {
    }
    @Override
    public void update() {
	animate_skeleton();
    }   
    @Override
    public void collision(JGameEngine.Object with) {
	if(with.name.equals("Collision")) {
	    System.out.println("HUH!! HOW");
	}
    }
    void init(String name, String sprite) {
	// Get mask of the object to find the position it should be at
	m = e.collisionMaskList(this).get(0);
	this.x = m.getX(); this.y = m.getY();
	// This is kinda a patch, the masks are relative to object position,
	// me getting mask position to set object position means that the mask position
	// is now wrong, a new mask on correct relative position of 0,0 needs to be added
	e.collisionMaskRemove(m);
	m = e.collisionMaskAdd(this, 0, 0, 32, 32);
	
	this.name = name;
	s = e.sprite(this, "sprites/" + sprite, 4, 32, 3, 32);
	if(this.getClass() == CharacterPlayer.class)
	    e.addSprite(s);
	img_cnt = 1;
	last_image_index = s.image_index;
	moving_direction = "down";
	speed = 1;
    }
    void animate_skeleton() {
	if(s == null)
	    return;
	
	s.image_speed = 0;
	if(last_image_index != s.image_index) {
	    if(img_cnt == 3) s.image_index -= 2; 
	    else if(img_cnt == 4) { s.image_index -= 2; img_cnt = 0; }
	    
	    if(s.image_index == -2) s.image_index = 10;
	    last_image_index = s.image_index;
	    img_cnt++;
	}
		
	if(!moving || movement_beginning == true) {
	    if(moving_direction.equals("right")) { last_image_index = 5; s.image_index = 7; img_cnt = 1; }
	    else if(moving_direction.equals("left")) { last_image_index = 2; s.image_index = 4; img_cnt = 1; }
	    if(moving_direction.equals("down")) { last_image_index = -1; s.image_index = 1; img_cnt = 1; }
	    else if(moving_direction.equals("up")) { last_image_index = 8; s.image_index = 10; img_cnt = 1; }
	    movement_beginning = false;
	} else {
	    if(moving_direction.equals("right")) { 
		if(!collisionTest(speed * e.deltaTime, 0))
		    x += speed * e.deltaTime;		
	    } else if(moving_direction.equals("left")) { 
		if(!collisionTest(-speed * e.deltaTime, 0))
		    x -= speed * e.deltaTime;
	    } if(moving_direction.equals("up")) { 
		if(!collisionTest(0, -speed * e.deltaTime))
		    y -= speed * e.deltaTime;
	    } else if(moving_direction.equals("down")) { 
		if(!collisionTest(0, speed * e.deltaTime))
		    y += speed * e.deltaTime;
	    }
	    s.image_speed = 0.5f;
	}
	if(this.getClass() != CharacterPlayer.class && draw)
	    e.drawSprite(s);
    }
    boolean collisionTest(double x, double y) {
	ArrayList<JGameEngine.CollisionMask> msks = e.collisionBoxTest(this.x + x, this.y + y, 32, 32);
	for(int i = 0; i < msks.size(); i++) {
	    if(msks.get(i) != m && (
		msks.get(i).o.name.equals("Collision") ||
		msks.get(i).o.getClass() == CharacterNPC.class || 
		msks.get(i).o.getClass() == CharacterPlayer.class || 
		msks.get(i).o.getClass() == CharacterNPCEnemy.class || 
		msks.get(i).o.getClass() == Interactables.class)) {
		has_moved = false;
		return true;
	    }
	}
	has_moved = true;
	return false;
    }
    void remove() {
	e.collisionMaskRemove(m);
	e.removeObject(this);
	if(this.getClass() == CharacterPlayer.class)
	    e.removeSprite(s);
    }    
}
