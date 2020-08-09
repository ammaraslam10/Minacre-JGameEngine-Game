
import java.util.ArrayList;

public class Interactables extends JGameEngine.Object implements JGameEngine.Collision, Interact {
    JGameEngine e;
    DialogueBox box;
    CharacterPlayer p;
    ArrayList<String> dialogue = null;
    boolean status;
    
    public Interactables(JGameEngine e) {
	this.e = e;
	status = false;
    }
    public void init(CharacterPlayer p) {
	this.p = p;
	dialogue = new ArrayList<>();
	if(name.equals("chest_forest")) {
	    dialogue.add("Found Sword");
	    dialogue.add("Found Wood Armour");
	    dialogue.add("Open Inventory Using Z to Equip");
	    dialogue.add(null);
	} else if(name.equals("chest_forest_2")) {
	    dialogue.add("Found Axe, Iron Armour");
	    dialogue.add("Found Weak Health Potion");
	    dialogue.add("Open Inventory Using Z to Equip");
	    dialogue.add(null);
	}
	box = new DialogueBox(e, dialogue);
	e.addObject(box);
	box.name = "Chest:";
    }    
    @Override
    public void collision(JGameEngine.Object with) {
    }    
    @Override
    public boolean interact() {
	if(name.equals("chest_forest") && status == false) {
	    if(box.runDialogue()) {		
		p.i.items.add(ItemFactory.getItem("sword"));
		p.i.items.add(ItemFactory.getItem("wood_armour"));
		p.q.complete("find_sword");
		e.audioPlay("sounds/chest.wav", false, 1f);
		status = true;
		return false;
	    } else p.can_interact = true;
	} else if(name.equals("chest_forest_2") && status == false) {
	    if(box.runDialogue()) {	
		p.i.items.add(ItemFactory.getItem("axe"));
		p.i.items.add(ItemFactory.getItem("iron_armour"));
		p.i.items.add(ItemFactory.getItem("weak_health_potion"));
		e.audioPlay("sounds/chest.wav", false, 1f);
		status = true;
		return false;
	    } else p.can_interact = true;
	}
	return true;
    }
    @Override
    public void start() {
    }
    @Override
    public void update() {
    }
}
