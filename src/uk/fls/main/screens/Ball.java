package uk.fls.main.screens;

import fls.engine.main.util.Point;
import fls.engine.main.util.Renderer;
import uk.fls.main.util.TextureMap;

public class Ball {
	
	public Point pos;
	public Point respawn;
	private int fallTime;
	private boolean sunk;
	private int r;
	
	public boolean falling, canMove;
	public int color;
	private TextureMap texmap;
	
	public float vx, vy;
	public int shots;
	
	public Ball(int x,int y){
		this.pos = new Point(x,y);
		r = 4;
		this.vx = 0;
		this.vy = 0;
		this.falling = false;
		this.canMove = true;
		this.texmap = new TextureMap("/assets/BallMap.png");
		this.fallTime = 0;
		this.sunk = false;
		
		genColor();
	}
	
	public void render(Renderer r){
		
		int dx = this.pos.getIX();
		int dy = this.pos.getIY();
		drawCircle(dx, dy - this.r, this.r, r);
	}
	
	public void move(float mx,float my){
		if(!canMove)return;
		this.vx += mx;
		this.vy += my;
		canMove = false;
		shots++;
	}
	
	public void fall(){
		this.vy += 0.15f;
	}
	
	public void update(Renderer r){
		this.r = 4;
		if(falling){
			fallTime++;
			fall();
		}else{
			fallTime = 0;
		}
		
		int c = r.getColor(this.pos.getIX(), this.pos.getIY());
		int tc = r.makeRGB(123, 255, 0);
		int tc2 = (tc & 0xFEFEFE) >> 1;
		
		if(c ==  tc || c == tc2){
			falling = false;
		}else{
			falling = true;
		}
		
		this.pos.x += this.vx;
		this.pos.y += this.vy;
		
		if(falling){
			this.vx *= 0.99;
			if(fallTime > 60 * 2){
				resetPosition();
			}
		}else{
			this.vx *= 0.98;
		}
		
		this.vy *= 0.98;
		
		if(this.vx != 0f || this.vy != 0f){
			if(Math.abs(this.vx) < 0.05)this.vx = 0f;
			if(Math.abs(this.vy) < 0.05)this.vy = 0f;
		}else if(this.vx == 0f && this.vy == 0f){
			this.canMove = true;
			setRespawnPos();
		}
		
		if(c == 0){//Hole
			this.sunk = true;
		}
	}
	
	public void drawCircle(int x,int y,int r, Renderer re){
		for(int xx = -r; xx < r; xx++){
			for(int yy = -r; yy < r; yy++){
				
				
				int x2 = xx * xx;
				int y2 = yy * yy;
				
				if(x2 + y2 < r * r){
					int dx = x + xx;
					int dy = y + yy;
					float finalCol = (this.texmap.getPixel(dx, dy));
					re.setPixel(dx, dy, (int)finalCol & this.color);
				}
			}
		}
	}
	
	public void resetPosition(){
		this.pos = new Point(this.respawn.x,this.respawn.y);;
		this.vx = 0;
		this.vy = 0;
		this.falling = false;
		//genColor();
	}
	
	public void setRespawnPos(){
		this.respawn = new Point(this.pos.x,this.pos.y);;
	}
	
	public void genColor(){
		this.color = (int)Math.floor(Math.random() * 0xFFFFFF);
	}
	
	public boolean hasBeenSunk(){
		return this.sunk;
	}
}
