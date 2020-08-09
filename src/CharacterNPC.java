
import java.util.ArrayList;

public class CharacterNPC extends CharacterSkeleton implements Interact {
    ArrayList<String> dialogue = new ArrayList<>();
    double steps, wait_time;
    DialogueBox box;
    CharacterPlayer p;
    
    public CharacterNPC(JGameEngine e) {
	super(e);
	steps = wait_time = 0;
	box = new DialogueBox(e, dialogue);
    }
    public void init(CharacterPlayer p) {
	super.init(name, name+".png");
	this.p = p;
	moving = true;
	moving_direction = "right";
	e.addObject(box);
	dialogue_update();
	box.name = name + ":";
    }
    @Override
    public void update() {
	super.update();	
	if(steps < 100) {	    
	    if(has_moved && box.draw == false) steps += e.deltaTime * 1.75;
	} else {
	    if(wait_time < 32) {
		moving = false;
		wait_time += e.deltaTime * 1.75;
		return;
	    } else wait_time = 0;
	    if(moving_direction.equals("right")) moving_direction = "down";
	    else if(moving_direction.equals("down")) moving_direction = "left";
	    else if(moving_direction.equals("left")) moving_direction = "up";
	    else moving_direction = "right";
	    super.movement_beginning = true;
	    if(box.draw == false) moving = true;
	    steps = 0;
	}
    }
    @Override
    public boolean interact() {
	moving = false;
	if(box.runDialogue()) {
	    moving = true;
	    
	    dialogue_update();
	    //System.out.println("interact " + name);
	    if(name.equals("Amu")) { p.q.add("meet_shizo"); }
	    if(name.equals("Shizo") && p.q.status("meet_shizo") == 1) { p.q.complete("meet_shizo"); p.q.add("find_sword"); }
	    return false;
	}
	return true;
    }
    void dialogue_update() {
	dialogue.clear();
	//System.out.println(p.q.status("meet_shizo"));
    	if(name.equals("Amu")) dialogue_amu();
	else if(name.equals("Shizo")) dialogue_shizo();
	box.update(dialogue);
    }
    void dialogue_amu() {
	if(p.q.status("meet_shizo") == 0) {
	    dialogue.add("How's it going?");
	    dialogue.add("WAIT! your name is Iiawak?! OMG! you're\n"
		    + "the legendary hero!");
	    dialogue.add("Do me a favour and find a person called Shizo.\n"
		    + "She should be in this town");
	    dialogue.add(null);
	} else if(p.q.status("meet_shizo") == 1) {
	    dialogue.add("Find Shizo..");
	    dialogue.add(null);	    
	} else if(p.q.status("meet_shizo") == 2) {
	    dialogue.add("Nice! Shizo will now guide you....");
	    dialogue.add(null);	    
	}    
    }
    void dialogue_shizo() {
	if(p.q.status("meet_shizo") == 0) {
	    dialogue.add("Hmm.. who are you?");
	    dialogue.add(null);
	} else if(p.q.status("meet_shizo") == 1) {
	    dialogue.add("What? Amu sent you here? Tch..\n"
		    + "You look nothing like the legengs!");
	    dialogue.add("I don't care about your name..");
	    dialogue.add("Hmm.. perhaps you're the one..");
	    dialogue.add("Prove yourself! find a sword");
	    dialogue.add(null);	    
	} else if(p.q.status("find_sword") == 1) {
	    dialogue.add("Prove yourself! find a sword");
	    dialogue.add("Head north into the forest...");
	    dialogue.add(null);	    
	}  else if(p.q.status("find_sword") == 2) {
	    dialogue.add("You are ready!");
	    dialogue.add("End the evil...");
	    dialogue.add(null);	    
	}    
    }
}
