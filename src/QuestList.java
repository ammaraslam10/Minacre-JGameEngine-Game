
import java.util.ArrayList;

public class QuestList {
    ArrayList<String> quests_active;
    ArrayList<String> quests_completed;
    
    public QuestList() {
	quests_active = new ArrayList<>();
	quests_completed = new ArrayList<>();
    }
    void add(String name) {
	for(int i = 0; i < quests_active.size(); i++) {
	    if(quests_active.get(i).equals(name)) return;
	}	
	quests_active.add(name);
    }
    int status(String name) {
	for(int i = 0; i < quests_active.size(); i++) {
	    if(quests_active.get(i).equals(name)) return 1;
	}
	for(int i = 0; i < quests_completed.size(); i++) {
	    if(quests_completed.get(i).equals(name)) return 2;
	}
	return 0;
    }
    void complete(String name) {
	for(int i = 0; i < quests_active.size(); i++) {
	    if(quests_active.get(i).equals(name)) {
		quests_completed.add(name);
		quests_active.remove(i);
	    }
	}	
    }
}
