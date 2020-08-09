public class Potion extends Item {
    
    String potion_type;
    int turns;

    public Potion(String name, String alias, String type, int level) {
	super.name = name;
	super.alias = alias;
	super.level = level;
	this.potion_type = type;
    }

    @Override
    public void equip(CharacterPlayer p) {
	if(potion_type.equals("health"))
	    p.health = p.health + super.level * 10;
	if(potion_type.equals("defence"))  
	    p.defence = p.defence + super.level * 3;
	if(potion_type.equals("strength"))  
	    p.attack = p.attack + super.level * 2;
	turns = 4 + level;
    }

    @Override
    public void unequip(CharacterPlayer p) {
	if(potion_type.equals("health"))
	    p.health -= super.level * 10;
	if(potion_type.equals("defence"))  
	    p.defence -= super.level * 3;
	if(potion_type.equals("strength"))  
	    p.attack -= super.level * 2;
    }  
    
    void degrade_effects(CharacterPlayer p) {
	if(turns != 0) turns--;
	if(turns == 0) unequip(p);
    }
}