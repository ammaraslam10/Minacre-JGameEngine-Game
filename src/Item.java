public abstract class Item {
    String name;
    String alias;
    int level;
    public abstract void equip(CharacterPlayer p);
    public abstract void unequip(CharacterPlayer p);
}
