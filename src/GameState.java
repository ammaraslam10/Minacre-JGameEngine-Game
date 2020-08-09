
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

// Runs in a separate thread, helps with things that the engine can't do itself
public class GameState {
    String room;
    double player_position_x;
    double player_position_y;
    CharacterPlayer player_reference;
    Inventory inventory;
    QuestList questlist;
    JGameEngine e;
    
    volatile boolean load_room = false, gameover = false;
    ArrayList<JGameEngine.Object> npc_list;
    ArrayList<JGameEngine.Object> npc_enemy_list;
    String music = "";
    boolean music_play = true;
    public GameState(JGameEngine e) {
	this.e = e;
	inventory = new Inventory(e);
	questlist = new QuestList();
    }
    void waitAction() {
	while(true) {
	    if(load_room) { 
		if(!room.equals(""))
		    loadRoom(true);
		else 
		    loadGame();
		manageMusic();
		load_room = false; 
	    }
	    if(gameover) { System.exit(0); }
	    try {
		Thread.sleep(1000);
	    } catch (InterruptedException ex) {
		Logger.getLogger(GameState.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}
    }    
    boolean loadGame() {
	Gson gson = new Gson();
	boolean loadSuccess = true;
	try {
	    JsonObject state = gson.fromJson(new FileReader("GameState.json"), JsonObject.class);
	    room = state.get("room").getAsString();
	    e.cameraDistance(state.get("settings").getAsJsonObject().get("camera_distance").getAsDouble());
	    music_play = state.get("settings").getAsJsonObject().get("music").getAsBoolean();
	    JsonArray inv = state.getAsJsonArray("inventory");
	    for(int i = 0; i < inv.size(); i++)
		inventory.items.add(ItemFactory.getItem(inv.get(i).getAsString()));
	    JsonArray qus_act = state.getAsJsonArray("quests_active");
	    for(int i = 0; i < qus_act.size(); i++)
		questlist.quests_active.add(qus_act.get(i).getAsString());
	    JsonArray qus_cmp = state.getAsJsonArray("quests_completed");
	    for(int i = 0; i < qus_cmp.size(); i++)
		questlist.quests_completed.add(qus_cmp.get(i).getAsString());
	    loadRoom(false);
	    player_reference.x = state.get("x").getAsDouble();
	    player_reference.y = state.get("y").getAsDouble();
	    player_reference.base_attack = state.get("base_attack").getAsInt();
	    player_reference.base_defence = state.get("base_defence").getAsInt();
	    player_reference.base_health = state.get("base_health").getAsInt();
	    player_reference.health = state.get("health").getAsInt();
	    ItemFactory.getItem(state.get("weapon_equipped").getAsString()).equip(player_reference);
	    inventory.weapon_equipped = (Weapon) ItemFactory.getItem(state.get("weapon_equipped").getAsString());
	    ItemFactory.getItem(state.get("armour_equipped").getAsString()).equip(player_reference);
	    inventory.armour_equipped = (Armour) ItemFactory.getItem(state.get("armour_equipped").getAsString());
	} catch (FileNotFoundException ex) {
	    loadSuccess = false;
	}
	if(loadSuccess == false) {
	    room = "Laitini City";
	    inventory.items.add(ItemFactory.getItem("Stick"));
	    loadRoom(false);
	}
	return loadSuccess;
    }
    void manageMusic() {
	if(!music.equals("")) { e.audioRemove(music); music = ""; }
	if(music_play) { 
	    if(room.equals("Laitini City")) { e.audioPlay("sounds/initial.wav", true, 1); music = "sounds/initial.wav"; }
	    if(room.equals("Forest")) { e.audioPlay("sounds/Autumn.wav", true, 1); music = "sounds/Autumn.wav"; }
	    if(room.equals("Final")) { e.audioPlay("sounds/final.wav", true, 1); music = "sounds/final.wav"; } 
	}
    }
    void saveGame() {
	Gson out = new GsonBuilder().setPrettyPrinting().create();
	JsonObject obj = new JsonObject();
	obj.add("room", new JsonPrimitive(room));
	obj.add("x", new JsonPrimitive(player_reference.x));
	obj.add("y", new JsonPrimitive(player_reference.y));
	obj.add("base_attack", new JsonPrimitive(player_reference.base_attack));
	obj.add("base_defence", new JsonPrimitive(player_reference.base_defence));
	obj.add("base_health", new JsonPrimitive(player_reference.base_health));
	obj.add("health", new JsonPrimitive(player_reference.health));
	obj.add("weapon_equipped", new JsonPrimitive(inventory.weapon_equipped.alias));
	obj.add("armour_equipped", new JsonPrimitive(inventory.armour_equipped.alias));
	if(inventory.potion_equipped != null) obj.add("potion_equipped", new JsonPrimitive(inventory.potion_equipped.alias));
	// settings
	JsonObject settings = new JsonObject();
	settings.add("camera_distance", new JsonPrimitive(e.cameraDistance()));
	settings.add("music", new JsonPrimitive(music_play));
	obj.add("settings", settings);
	// inventory
	JsonArray inv = new JsonArray();
	for(int i = 0; i < inventory.items.size(); i++)
	    inv.add(new JsonPrimitive(inventory.items.get(i).alias));
	obj.add("inventory", inv);
	// quests
	JsonArray qus_act = new JsonArray();
	for(int i = 0; i < questlist.quests_active.size(); i++)
	    qus_act.add(new JsonPrimitive(questlist.quests_active.get(i)));
	JsonArray qus_cmp = new JsonArray();
	for(int i = 0; i < questlist.quests_completed.size(); i++)
	    qus_cmp.add(new JsonPrimitive(questlist.quests_completed.get(i)));
	obj.add("quests_active", qus_act);
	obj.add("quests_completed", qus_cmp);
	try (FileWriter writer = new FileWriter("Gamestate.json")) {
	    out.toJson(obj, writer);
	} catch (Exception e) {
	    System.out.println("Gamestate:: Failed to save " + e);
	}
    }
    void splash() {	
	e.addObject(new WelcomeScreen(e, this));
    }
    void loadRoom(boolean newroom) {
	if(newroom) {
//
	}
	JTiledUtility u = new JTiledUtility(e);
	ArrayList<Class> objectClassList = new ArrayList<>();
	objectClassList.add(CollisionBase.class);	    // base collision for walls
	objectClassList.add(Interactables.class);	    // interactables class
	objectClassList.add(Teleport.class);		    // teleport class
	objectClassList.add(CharacterNPC.class);	    // npc class
	objectClassList.add(CharacterNPCEnemy.class);	    // npc enemy class
	objectClassList.add(CharacterPlayer.class);	    // player class
	ArrayList<ArrayList<JGameEngine.Object>> objects_list = u.setGameSpace("maps/" + this.room + ".json", objectClassList);
	
	if(objects_list.size() != 6  || objects_list.get(5).isEmpty()) {
	    System.out.println("GameState:: Initialization failure: objects could not be created");
	    System.exit(0);
	}
	
	// Add player
	CharacterPlayer player = (CharacterPlayer) objects_list.get(5).get(0);
	this.player_reference = player;
	inventory.setPlayer(player);
	player.init("Iiawak","player.png", inventory, questlist, this);	
	// Add NPC & Enemies
	npc_list = objects_list.get(3);
	for(int i = 0; i < npc_list.size(); i++) {
	    if(((CharacterNPC) npc_list.get(i)).name.equals("player_return")) {
		
	    } else ((CharacterNPC) npc_list.get(i)).init(player);
	}
	npc_enemy_list = objects_list.get(4);
	for(int i = 0; i < npc_enemy_list.size(); i++) {
	    ((CharacterNPCEnemy) npc_enemy_list.get(i)).init(player);
	}
	// Add teleport
	for(int i = 0; i < objects_list.get(2).size(); i++) {
	    ((Teleport) objects_list.get(2).get(i)).init(this);
	}
	// Add interactables
	for(int i = 0; i < objects_list.get(1).size(); i++) {
	    ((Interactables) objects_list.get(1).get(i)).init(player);
	}
	e.cameraFollow(player, -(e.cameraWidth() / 2 - 16), -(e.cameraHeight() / 2 - 16));
    }
}