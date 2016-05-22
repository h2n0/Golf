package uk.fls.main.screens;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fls.engine.main.art.Art;
import fls.engine.main.screen.Screen;
import fls.engine.main.util.Camera;
import fls.engine.main.util.Renderer;
import uk.fls.main.screens.entitys.Ball;
import uk.fls.main.screens.entitys.Flag;
import uk.fls.main.util.Entity;
import uk.fls.main.util.Level;
import uk.fls.main.util.TextureMap;

public class RenderScreen extends Screen {

	private Renderer r;

	private int w = 40;
	private int h = 51;
	
	private int mx, my;
	private boolean prevClick;
	
	
	private int top;
	private int dtop;
	
	private int floor;
	private int dfloor;
	
	private int maxPower;
	
	private int bgColor;
	
	private Ball ball;
	private Flag flag;
	
	private Level level;

	private Camera cam;
	private List<Entity> entitys;
	
	public class EntitySorter implements Comparator<Entity>{

		@Override
		public int compare(Entity e1, Entity e2) {
			if(e1.pos.y < e2.pos.y)return 1;
			if(e1.pos.y > e2.pos.y)return -1;
			return 0;
		}
		
	}
	
	private EntitySorter sorter;

	public void postInit() {
		this.r = new Renderer(this.game.image);
		this.r.setScale(3);
		
		this.sorter = new EntitySorter();
		
		this.level = new Level(this.r, "level2");
		genLevel("level2");
		
		this.top = this.r.makeRGB(123,255,0);
		this.dtop = (this.top & 0xFEFEFE) >> 1;
		

		this.floor = this.r.makeRGB(255,123,0);
		this.dfloor = (this.floor & 0xFEFEFE) >> 1;
		//this.b.move(-1,0);
		this.maxPower = 2;
		this.bgColor = r.makeRGB(123, 123, 123);
		this.cam = new Camera(0,0);
		this.cam.w = this.r.getWidth();
		this.cam.h = this.r.getHeight();
	}

	private float pow = 0;
	
	@Override
	public void update() {
		sortEntitys();
		maxPower = 2;
		boolean click = this.input.leftMouseButton.justClicked();
		this.mx = this.input.mouse.getX()/r.scale*r.scale/2;
		this.my = this.input.mouse.getY()/r.scale*r.scale/2;
			
		if(this.ball.canMove){
			if(!click && this.prevClick){
				int ax = ball.pos.getIX() - mx;
				int ay = ball.pos.getIY() - my;
				float diff = (float)Math.atan2(ay, ax);
				float dx = (float)Math.cos(diff);
				float dy = (float)Math.sin(diff);
				this.ball.move(dx * (pow * this.maxPower), dy * (pow * this.maxPower));
				pow = 0;
			}else{
				if(click){
					pow += 0.05;
					pow %= this.maxPower;
				}
			}
		}
		
		for(int i = 0; i < this.entitys.size(); i++)this.entitys.get(i).update(r);
		
		if(this.ball.hasBeenSunk()){
			System.out.println(this.ball.shots);
		}
		
		this.prevClick = click;
		
		if(this.input.isKeyPressed(this.input.a)){
			Art.saveScreenShot(game, true);
		}
		this.cam.center(this.ball.pos.getIX(), this.ball.pos.getIY());
		
		//System.out.println(this.totalNanos);
	}
	
	private int t = 0;

	@Override
	public void render(Graphics g) {
		//this.r.setOffset(-(int)this.cam.pos.x, -(int)this.cam.pos.y);
		this.r.fill(this.bgColor);
		
		for(int i = 0; i < this.level.w; i++){
			for(int j = 0; j < this.level.h; j++){
				if(this.level.levelData[i][j]==1 || this.level.levelData[i][j] == 2)drawCube(j,3+i);//Draws ground
				if(this.level.levelData[i][j]==3){ // The hole
					drawCube(j,3+i);
					
					int w = 6;
					int h = 4;
					for(int l = 0; l < w * h; l++){
						int dx = l % w;
						int dy = l / w;
						r.setPixel((j * r.scale * 2 * 2) + dx + w - 3, (i * r.scale) + r.scale * 2 + dy + 1, 0);
					}
				}
			}
		}
		

		for(int i = 0; i < this.entitys.size(); i++)this.entitys.get(i).render(r);
		
		
		//World has been rendered, do all of the GUI here
		this.r.setOffset(0, 0);
		
		int length = 100;
		int xo = (320-length)/2;
		int yo = 150;
		float barPos = (pow/this.maxPower)*length;
		if(pow == 0.0f)barPos = -1;
		for(int i = 0; i < length; i++){
			for(int j = 0; j < 5; j++){
				this.r.setPixel(xo + i, yo + j, !(i>(int)barPos)?this.ball.color:this.r.makeRGB(64, 64, 64));
			}
		}
		
		this.r.finaliseRender();
	}

	public void draw(int x, int y, int c) {
		draw(x, y, c, false);
	}
	
	public void drawHalf(int x,int y){
		
		boolean flip = x % 2 == 0;
		if(y % 2 == 1)flip = !flip;
		
		int t = top;
		int f = floor;
		
		if(!flip)f = dfloor;
		if(y%2==0)t = dtop;
		
		draw(x,y,t);
		draw(x,y+1,f);
		draw(x,y+2,f);
	}
	
	public void drawCube(int x,int y){
		x *= 2;
		y *= 3;
		boolean flip = x % 2 == 0;
		if(y % 2 == 1)flip = !flip;
		
		y/=3;
		
		if(flip && x % 2 == 0){
			x++;
			flip = !flip;
		}
		
		if(!flip){
			drawHalf(x,y);
			drawHalf(x+1,y);
		}
	}

	public void draw(int x, int y, int c, boolean shade) {
		boolean flip = x % 2 == 0;
		if (y % 2 == 0)
			flip = !flip;
		if (flip && shade)
			c = (c & 0xEEEEEE) >> 1;

		int ys = r.scale;
		for (int j = 0; j < r.scale * 2; j++) {
			for (int i = -ys; i < ys; i++) {
				int dx = (x * r.scale * 2) + j;
				int dy = (y * r.scale) + i;

				if (flip) {
					dx = ((x + 1) * r.scale * 2) - j - 1;
				}
				this.r.setPixel(dx, dy, c);
			}
			if (j % 2 == 0)
				ys--;
		}
	}
	
	private int[][] genLevel(String name){
		int[][] d = TextureMap.getPixles("/levels/"+name+".png");
		this.w = d[0][0];
		this.h = d[0][1];
		int[][] res = new int[this.h][this.w];
		int[] pixels = d[1];
		
		int px = -1,py = -1;
		
		for(int y = 0; y < h; y++){
			for(int x = 0; x < w; x++){
				int rgb = pixels[x + y * w];
				if(rgb == this.r.makeRGB(0, 0, 0)){
					res[y][x] = 1;//Ground
				}else if(rgb == this.r.makeRGB(0, 255, 0)){
					res[y][x] = 2;//Start of hole
					px = x * r.scale * 2 * 2;
					py = y * r.scale;
				}else if(rgb == this.r.makeRGB(0, 0, 255)){
					res[y][x] = 3;//End of hole
					this.flag = new Flag(x * r.scale * 2 * 2, y * r.scale + r.scale);
				}
			}
		}
		
		if(px == -1 || py == -1)throw new RuntimeException("The level dosne't contain a start point. Make sure that it has a hex value of #00FF00");
		this.ball = new Ball(px,py);
		
		this.entitys = new ArrayList<Entity>();
		this.entitys.add(this.ball);
		this.entitys.add(this.flag);
		return res;
	}
	
	private void sortEntitys(){
		Collections.sort(this.entitys, this.sorter);
	}
}
