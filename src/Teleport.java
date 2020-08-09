public class Teleport extends JGameEngine.Object implements JGameEngine.Collision {
    JGameEngine e;
    GameState s;
    
    public Teleport(JGameEngine e) {
	this.e = e;
    }
    void init(GameState s) {
	this.s = s;
    }
    @Override
    public void start() {
    }
    @Override
    public void update() {
    }
    @Override
    public void collision(JGameEngine.Object with) {
	if(with.getClass() == CharacterPlayer.class) {
	    switch(name) {
		case "teleport_forest_enter": { 
		    s.room = "Forest";
		    s.load_room = true;	
		    break; 
		}
		case "teleport_dnoces": { 
		    s.room = "Final";
		    s.load_room = true;	
		    break; 
		}
	    }	    
	}
    }    
}
