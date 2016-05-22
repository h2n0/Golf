package uk.fls.main.util;

import fls.engine.main.util.Point;
import fls.engine.main.util.Renderer;

public class Entity {

	
	public Point pos;
	
	public Entity(int x,int y){
		this.pos = new Point(x, y);
	}
	
	public void render(Renderer r){
		
	}
	
	public void update(Renderer r){
		
	}
}
