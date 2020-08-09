public class ItemFactory {
    public static Item getItem(String type) {
        Item i = null;
        switch(type.toLowerCase()) {
            case "no weapon":{
		i = new Weapon("No Weapon", "no weapon", 0);
                break;
	    } case "no armour":{
		i = new Armour("No Armour", "no armour", 0);
                break;
	    } case "sword":{
		i = new Weapon("Sword", "sword", 2);
                break;
	    } case "stick":{
		i = new Weapon("Stick", "stick", 1);
                break;
	    } case "axe":{
		i = new Weapon("Axe", "axe", 3);
                break;
	    } case "wood_armour":{
		i = new Armour("Wood Armour", "wood_armour", 1);
                break;
	    } case "iron_armour":{
		i = new Armour("Iron Armour", "iron_armour", 2);
                break;
	    } case "weak_health_potion":{
		i = new Potion("HP Potion I", "weak_health_potion", "health", 1);
                break;
	    } case "weak_strength_potion":{
		i = new Potion("Str Potion I", "weak_strength_potion", "strength", 1);
                break;
	    } case "weak_defence_potion":{
		i = new Potion("Def Potion I", "weak_defence_potion", "defence", 1);
                break;
	    } default: {
		System.out.println("ItemFactory:: invalid item ("+type.toLowerCase()+")");
	    }
        }
        return i;        
    }
}
