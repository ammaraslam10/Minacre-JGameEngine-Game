public class Armour extends Item {

    public Armour(String name, String alias, int level) {
	super.name = name;
	super.alias = alias;
	super.level = level;
    }

    @Override
    public void equip(CharacterPlayer p) {
	p.defence = p.base_defence + super.level * 3;
    }

    @Override
    public void unequip(CharacterPlayer p) {
	p.defence = 1;
    }    
}