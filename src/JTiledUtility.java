import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class JTiledUtility {
    JGameEngine e;
    ArrayList<JGameEngine.Object> objects;
    
    String em[] = new String[4];
    public JTiledUtility(JGameEngine e) {
	this.e = e;
	objects = new ArrayList<>();
    }
    public <T extends JGameEngine.Object> ArrayList<ArrayList<JGameEngine.Object>> setGameSpace(String filename, ArrayList<Class> clas) {
	Gson gson = new Gson();
	JsonObject object = null; 
	try {
	    object = gson.fromJson(new FileReader(filename), JsonObject.class);
	    String directory = new File(filename).getAbsoluteFile().getParentFile()+"\\";
	    int width = object.get("width").getAsInt();
	    int height = object.get("height").getAsInt();
	    int tile_width = object.get("tilewidth").getAsInt();
	    int tile_height = object.get("tileheight").getAsInt();
	    e.setGameSpace(width * tile_width, height * tile_height);
	    
	    if(	object.get("orientation").getAsString().equals("orthogonal") && 
		object.get("renderorder").getAsString().equals("right-down") &&
		object.get("type").getAsString().equals("map") && 
		object.get("infinite").getAsString().equals("false")) {
		JsonArray layers = object.get("layers").getAsJsonArray();
		JTiledDraw draw = new JTiledDraw(e);
		addSprites(draw, directory, object.get("tilesets").getAsJsonArray().get(0).getAsJsonObject().get("source").getAsString());
		draw.mapwidth = width;
		e.addObject(draw);
		ArrayList<ArrayList<JGameEngine.Object>> objects = new ArrayList<>();
		int objectgroupcount = 0;
		for(int i = 0; i < layers.size(); i++) {
		    JsonObject layer = layers.get(i).getAsJsonObject();
		    if(layer.get("type").getAsString().equals("tilelayer")) {
			if(layer.get("x").getAsInt() == 0 && layer.get("y").getAsInt() == 0)
			    draw.draw_layers.add(layer.get("data").getAsJsonArray());
			else 
			    System.out.println("JTiledUtility::setGameSpace() unsupported tiled layer, skipped.");
		    } else if(layer.get("type").getAsString().equals("objectgroup")) {
			ArrayList<JGameEngine.Object> layer_objects = new ArrayList<>();
			if(clas == null)
			    solveObjects(layer, JTiledObject.class, layer_objects);
			else
			    solveObjects(layer, clas.get(objectgroupcount), layer_objects);
			objectgroupcount++;
			objects.add(layer_objects);
		    }
		}
		return objects;
	    } else { System.out.println("JTiledUtility::setGameSpace() unsupported tiled format."); }
	} catch(FileNotFoundException e) { System.out.println("JTiledUtility::setGameSpace() file not found."); }	
	return null;
    }
    private void addSprites(JTiledDraw draw, String directory, String filename) {
	BufferedImage tmp = null;
	Gson gson = new Gson();
	JsonObject object = null;
	try {
	    object = gson.fromJson(new FileReader(directory+filename), JsonObject.class);
	    String image = object.get("image").getAsString();
	    int subimages_x = object.get("columns").getAsInt();
	    int subimages_y = object.get("tilecount").getAsInt() / subimages_x;
	    int subimages_width = object.get("tilewidth").getAsInt(); int subimages_height = object.get("tileheight").getAsInt();
	    try { tmp = ImageIO.read(new File(directory+image)); } catch (IOException ex) { System.out.println("Sprite:: Unable to open tileset image " + image); }
	    if(subimages_x > 0 && subimages_y > 0) {
		draw.img =  new BufferedImage[subimages_x * subimages_y];
		for(int i = 0; i < subimages_x; i++) {
		    for(int j = 0; j < subimages_y; j++) {
			draw.img[j * subimages_x + i] = tmp.getSubimage(i * subimages_width, j * subimages_height, subimages_width, subimages_height);
		    }
		}
	    }
	    draw.tile_width = subimages_width; draw.tile_height = subimages_height; draw.columns = subimages_x; 
	} catch(FileNotFoundException e) { System.out.println("JTiledUtility::setGameSpace() tileset json "+filename+" not found"); }
    }
    public void setGameSpace(String filename) {
	setGameSpace(filename, null);
    }
    private <T extends JGameEngine.Object> void solveObjects(JsonObject layer, Class<T> clas, ArrayList<JGameEngine.Object> objects) {
	JsonArray jobjarr = layer.get("objects").getAsJsonArray();
	if(clas == null) clas = (Class<T>) JTiledObject.class;
	Constructor[] ctors = clas.getDeclaredConstructors();
	Constructor ctor = null;
	for (int i = 0; i < ctors.length; i++) {
	    ctor = ctors[i];
	    if (ctor.getGenericParameterTypes().length == 1)
		break;
	}
	if(ctor == null) { 
	    System.out.println("JTiledUtility::setGameSpace() Class needs to have a constructor that only has 1 argument that accepts a JGameEngine ("+clas.getName()+")");
	    System.exit(0);
	}
	T standardObj = null;
	try {
	    standardObj = (T) ctor.newInstance(e);
	} catch (InstantiationException ex) {
	    System.out.println("JTiledUtility::setGameSpace() Can't Instantiate object (Class "+clas.getName()+") (object creation)"); Logger.getLogger(JTiledUtility.class.getName()).log(Level.SEVERE, null, ex);
	} catch (IllegalAccessException ex) {
	    System.out.println("JTiledUtility::setGameSpace() Can't Access (Class \""+clas.getName()+"\" needs to have a PUBLIC constructor that accepts a JGameEngine object)"); Logger.getLogger(JTiledUtility.class.getName()).log(Level.SEVERE, null, ex);
	} catch (IllegalArgumentException ex) {
	    System.out.println("JTiledUtility::setGameSpace() Invalid Argument to Constructor (Class \""+clas.getName()+"\" needs to have a constructor that only has 1 argument that accepts a JGameEngine)"); Logger.getLogger(JTiledUtility.class.getName()).log(Level.SEVERE, null, ex);
	} catch (InvocationTargetException ex) {
	    System.out.println("JTiledUtility::setGameSpace() Invocation Target Exception (Class "+clas.getName()+") (object creation)"); Logger.getLogger(JTiledUtility.class.getName()).log(Level.SEVERE, null, ex);
	}
	int maskCount = 0;
	for(int i = 0; i < jobjarr.size(); i++) {
	    JsonObject jobj = jobjarr.get(i).getAsJsonObject();
	    if(jobj.get("name").getAsString().equals("")) {
		maskCount += addObjectMask(standardObj, jobj);
	    } else {
		T newObj = null;
		try {
		    newObj = (T) ctor.newInstance(e);
		} catch (InstantiationException ex) {
		    System.out.println("JTiledUtility::setGameSpace() Can't Instantiate object (Class "+clas.getName()+") (object creation)"); Logger.getLogger(JTiledUtility.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
		    System.out.println("JTiledUtility::setGameSpace() Can't Access (Class \""+clas.getName()+"\" needs to have a PUBLIC constructor that accepts a JGameEngine object)"); Logger.getLogger(JTiledUtility.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalArgumentException ex) {
		    System.out.println("JTiledUtility::setGameSpace() Invalid Argument to Constructor (Class \""+clas.getName()+"\" needs to have a constructor that only has 1 argument that accepts a JGameEngine)"); Logger.getLogger(JTiledUtility.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InvocationTargetException ex) {
		    System.out.println("JTiledUtility::setGameSpace() Invocation Target Exception (Class "+clas.getName()+") (object creation)"); Logger.getLogger(JTiledUtility.class.getName()).log(Level.SEVERE, null, ex);
		}
		addObjectMask(newObj, jobj);
		newObj.name = jobj.get("name").getAsString();
		e.addObject(newObj);
		objects.add(newObj);
	    }
	}
	if(maskCount != 0)  {
	    e.addObject(standardObj);
	    objects.add(standardObj);
	}
    }
    private <T extends JGameEngine.Object> int addObjectMask(T obj, JsonObject jobj) {
	int maskCount = 0;
	if(jobj.has("ellipse") && jobj.get("ellipse").getAsString().equals("true")) {
	    e.collisionMaskAdd(obj, jobj.get("x").getAsDouble(), 
	    jobj.get("y").getAsDouble(), (jobj.get("width").getAsDouble() + jobj.get("height").getAsDouble()) / 4);
	    maskCount++;
	} else {
	    e.collisionMaskAdd(obj, jobj.get("x").getAsDouble(), 
	    jobj.get("y").getAsDouble(), jobj.get("width").getAsDouble(), jobj.get("height").getAsDouble());
	    maskCount++;
	}
	return maskCount;
    }
}
class JTiledObject extends JGameEngine.Object implements JGameEngine.Collision {
    JGameEngine e;
    public JTiledObject(JGameEngine e) { this.e = e; }
    @Override public void start() { x = 0; y = 0; }
    @Override public void update() {}
    @Override public void collision(JGameEngine.Object with) {}
}
class JTiledDraw extends JGameEngine.Object {
    ArrayList<JsonArray> draw_layers = new ArrayList<>();
    BufferedImage[] img;
    ArrayList<JGameEngine.Sprite> map = new ArrayList<>();
    JGameEngine e;
    int columns, tile_width, tile_height, mapwidth;
    JTiledDraw(JGameEngine e) {
	this.e = e;
    }
    @Override public void start() {}
    @Override public void update() {
	for(int i = 0; i < draw_layers.size(); i++) {
	    JsonArray layer = draw_layers.get(i).getAsJsonArray();
	    int j1 = 0, k1 = 0, l = 0;
	    for(int j = 0; j < e.cameraHeight()/tile_height + 5; j++) {
		for(int k = 0; k < e.cameraWidth()/tile_width + 5; k++) {		    
		    int tox = (int) (e.cameraX()/tile_width), toy = (int) (e.cameraY()/tile_height);
		    if(tox + k < 0 || tox + k >= mapwidth) { k1++; continue; }
		    int num = tox + j1 * mapwidth + mapwidth * toy + k1;
		    if(num >= 0 && num < layer.size() && layer.get(num).getAsInt() > 0) {
			e.draw().drawImage(img[layer.get(num).getAsInt() - 1], 
			(int) Math.round(((k * tile_width) - (e.cameraX() % tile_width)) * 1/e.cameraDistance()),// + (int) ((e.cameraX()/tile_width)*tile_width), 
			(int) Math.round(((j * tile_height) - (e.cameraY() % tile_height))* 1/e.cameraDistance()),// + (int) ((e.cameraY()/tile_height)*tile_height),
			(int) Math.round(tile_width * 1/e.cameraDistance()), 
			(int) Math.round(tile_height * 1/e.cameraDistance()), null);	
		    } 
		    k1++;
		} 
		j1++; k1 = 0;
	    }
	}
    }
}