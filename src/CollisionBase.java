public class CollisionBase extends JGameEngine.Object implements JGameEngine.Collision {
    JGameEngine e;
    
    public CollisionBase(JGameEngine e) {
	this.e = e;
	name = "Collision";
    }    
    @Override
    public void start() {
    }
    @Override
    public void update() {
    }
    @Override
    public void collision(JGameEngine.Object with) {
    }    
}
