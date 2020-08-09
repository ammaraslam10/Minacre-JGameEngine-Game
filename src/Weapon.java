public class Weapon extends Item {

    public Weapon(String name, String alias, int level) {
	super.name = name;
	super.alias = alias;
	super.level = level;
    }
    
    @Override
    public void equip(CharacterPlayer p) {
	p.attack = p.base_attack + super.level * 2;
    }

    @Override
    public void unequip(CharacterPlayer p) {
	p.attack = 1;
    }    
}
