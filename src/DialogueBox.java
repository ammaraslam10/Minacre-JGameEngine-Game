
import java.util.ArrayList;

public class DialogueBox extends JGameEngine.Object {
    private ArrayList<String> arr;
    private JGameEngine e;
    private int position = 0;
    boolean draw = false, update_current_string = false;
    private String current = "";
    private int dialogue_speed = 2;
    private double waiting_time = 0;
    public DialogueBox(JGameEngine e, ArrayList<String> arr) {
	this.arr = arr;	
	this.e = e;
    }
    @Override
    public void start() {
    }
    @Override
    public void update() {
	if(draw) {
	    if(update_current_string) {
		if(current.length() != arr.get(position).length()) {
		    if(waiting_time < dialogue_speed) {
			waiting_time += 5 * e.deltaTime;
		    } else {
			current += arr.get(position).charAt(current.length());
			e.audioPlay("sounds/voice.wav", false, 1);
			waiting_time = 0;
		    }
		}
		if(current.length() == arr.get(position).length()) {
		    update_current_string = false;		    
		    position++;
		}
	    }
	    e.drawRect(e.cameraX() + 8, e.cameraY() + e.cameraHeight() - 58, e.cameraWidth() - 16, 58 - 8, e.color(0,0,0,200), true);
	    e.drawText(name, e.cameraX() + 16, e.cameraY() + e.cameraHeight() - 58 + 12, e.color(255, 255, 0));
	    int l = 0;
	    for (String line : current.split("\n")) {
		e.drawText(line, e.cameraX() + 16, e.cameraY() + e.cameraHeight() - 58 + 22 + l * 10, e.color(255, 255, 255));
		l++;
	    }
	}
    }
    void update(ArrayList<String> dialogue) {
	this.arr = dialogue;
	position = 0;
	update_current_string = false;
	current = "";
	draw = false;
    }    
    boolean runDialogue() {
	if(position == 0) {
	    draw = true;
	}
	dialogue_speed = 2;
	if(update_current_string) {
	    dialogue_speed = 0;
	    return false;
	}
	if(position >= arr.size() || arr.get(position) == null) {
	    position = 0;
	    update_current_string = false;
	    current = "";
	    draw = false;
	    return true;
	}
	update_current_string = true;
	current = "";
	return false;
    }
}
