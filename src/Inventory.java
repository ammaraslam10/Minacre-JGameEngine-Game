import java.awt.Color;
import java.util.ArrayList;

public class Inventory extends JGameEngine.Object {
    private JGameEngine e;
    private CharacterPlayer p;
    Weapon weapon_equipped;
    Armour armour_equipped;
    Potion potion_equipped;
    
    ArrayList<Item> items;
    private ArrayList<String> menu;
    boolean is_opened;
    private int menu_pos, inv_pos, selected;
    private String menu_s = "sounds/menu.wav";
    public Inventory(JGameEngine e) {
	this.e = e;
	items = new ArrayList<>();
	menu = new ArrayList<>();
	is_opened = false;
	
	weapon_equipped = (Weapon) ItemFactory.getItem("No Weapon");
	armour_equipped = (Armour) ItemFactory.getItem("No Armour");	

	menu.add("Resume");
	menu.add("Save");
	menu.add("Sound On");
	menu.add("Zoom 0");
	menu.add("Exit");
    }
    public void setPlayer(CharacterPlayer p) {
	this.p = p;
    }

    @Override
    public void start() {
    }

    @Override
    public void update() {
	if(p == null || !p.can_interact)
	    return;
	if(e.keyReleased("z")) {
	    e.audioPlay(menu_s, false, 1f);
	    if(!is_opened) {
		is_opened = true;
		p.controls = false;
		p.moving_direction = "down";
		menu_pos = 0;
		inv_pos = 0;
		selected = 0;
	    } else {
		if(selected == 0) {
		    if(menu_pos == 0) { is_opened = false; p.controls = true; }
		    else if(menu_pos == 1) {	ArrayList<String> sc = new ArrayList(); sc.add("Game Saved!"); p.generic_dialogue.update(sc);
						p.gs.saveGame(); is_opened = false; p.can_interact = false; p.generic_dialogue.name = ""; p.dialogue_run = true; }
		    else if(menu_pos == 2) {	if(menu.get(2).equals("Sound On")) { menu.set(2, "Sound Off"); p.gs.music_play = false; p.gs.manageMusic(); }
						else { menu.set(2, "Sound On"); p.gs.music_play = true; p.gs.manageMusic(); } }
		    else if(menu_pos == 3) {	if(menu.get(3).equals("Zoom 0")) { menu.set(3, "Zoom 1"); e.cameraDistance(0.41111); }
						else if(menu.get(3).equals("Zoom 1")) { menu.set(3, "Zoom 2"); e.cameraDistance(0.44444); }
						else if(menu.get(3).equals("Zoom 2")) { menu.set(3, "Zoom -1"); e.cameraDistance(0.39999); }
						else if(menu.get(3).equals("Zoom -1")) { menu.set(3, "Zoom 0"); e.cameraDistance(0.4); }
						e.cameraFollow(p, -(e.cameraWidth() / 2 - 16), -(e.cameraHeight() / 2 - 16));
					    }
		    else if(menu_pos == 4) System.exit(0);
		} else {
		    if(items.size() == 0) return;
		    if(items.get(inv_pos) == weapon_equipped) { items.get(inv_pos).unequip(p); weapon_equipped = (Weapon) ItemFactory.getItem("No Weapon"); }
		    else if(items.get(inv_pos) == armour_equipped) { items.get(inv_pos).unequip(p); armour_equipped = (Armour) ItemFactory.getItem("No Armour"); }
		    else {
			if(items.get(inv_pos).getClass() == Weapon.class || items.get(inv_pos).getClass() == Armour.class
			|| items.get(inv_pos).getClass() == Potion.class)
			    items.get(inv_pos).equip(p);
			
			if(items.get(inv_pos).getClass() == Weapon.class) weapon_equipped = (Weapon) items.get(inv_pos);
			else if(items.get(inv_pos).getClass() == Armour.class) armour_equipped = (Armour) items.get(inv_pos);
			else if(items.get(inv_pos).getClass() == Potion.class) { potion_equipped = (Potion) items.get(inv_pos); items.remove(inv_pos); inv_pos = 0; }
		    }
		}
	    }
	}
	if(e.keyReleased("x")) {
	    e.audioPlay(menu_s, false, 1f);
	    if(is_opened) {
		is_opened = false;
		p.controls = true;		
	    } 
	}
	if(is_opened)
	    draw();
    }
    void validate_potion() {
	if(potion_equipped != null) {
	    if(potion_equipped.turns > 0) potion_equipped.turns--;
	    else { potion_equipped.unequip(p); potion_equipped = null; }
	}
    }
    void draw() {
	e.drawRect(e.cameraX() + 8, e.cameraY() + 25, e.cameraWidth() - 16, 
		e.cameraHeight() - 34, e.color(0, 0, 0, 200), true);
	e.drawText("Iiawak", e.cameraX() + 20, e.cameraY() + 40, e.color(200, 200, 200));
	e.drawText("Strength: " + p.attack, e.cameraX() + 20, e.cameraY() + 50, e.color(255, 255, 255));
	e.drawText("Weapon: " + weapon_equipped.name, e.cameraX() + 100, e.cameraY() + 50, e.color(255, 255, 255));
	e.drawText("Defence: " + p.defence, e.cameraX() + 20, e.cameraY() + 60, e.color(255, 255, 255));
	e.drawText("Armour: " + armour_equipped.name, e.cameraX() + 100, e.cameraY() + 60, e.color(255, 255, 255));
	
	for(int i = 0; i < items.size(); i++) {
	    Color c = e.color(255,255,255);
	    if(items.get(i) == weapon_equipped || items.get(i) == armour_equipped) { c = e.color(200, 200, 200); }
	    if(i == inv_pos && selected == 1) { c = e.color(255, 255, 0); }
	    
	    e.drawText(items.get(i).name, e.cameraX() + 220, e.cameraY() + 82 + 10 * i, c);
	}
	for(int i = 0; i < menu.size(); i++) {
	    Color c = e.color(255,255,255);
	    if(i == menu_pos && selected == 0) { c = e.color(255, 255, 0); }	    
	    e.drawText(menu.get(i), e.cameraX() + 40, e.cameraY() + 82 + 10 * i, c);
	}
	
	if(e.keyReleased("up") || e.keyReleased("down")) e.audioPlay(menu_s, false, 1f);
	if(selected == 1 && e.keyReleased("down")) { if(inv_pos < items.size() - 1) inv_pos++; else inv_pos = 0; }
	if(selected == 1 && e.keyReleased("up")) { if(inv_pos > 0) inv_pos--; else inv_pos =  items.size() - 1; }
	if(selected == 0 && e.keyReleased("down")) { if(menu_pos < menu.size() - 1) menu_pos++; else menu_pos = 0; }
	if(selected == 0 && e.keyReleased("up")) { if(menu_pos > 0) menu_pos--; else menu_pos =  menu.size() - 1; }
	if(e.keyReleased("right") || e.keyReleased("left")) { 
	    if(selected == 0) selected = 1; else selected = 0;
	    if(selected == 0) { if(menu.size() > inv_pos) menu_pos = inv_pos; else menu_pos = menu.size() - 1; }
	    else if(selected == 1) { if(items.size() > menu_pos) inv_pos = menu_pos; else inv_pos = items.size() - 1; }
	    e.audioPlay(menu_s, false, 1f);
	}
    }
}
