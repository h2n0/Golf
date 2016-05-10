package uk.fls.main.screens;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import fls.engine.main.art.SplitImage;
import fls.engine.main.screen.Screen;
import fls.engine.main.util.Renderer;
import uk.fls.main.util.TextureMap;

public class RenderScreen extends Screen {

	private Renderer r;

	private int w = 40;
	private int h = 51;
	int scale = 3;
	private int[] trixels;
	private int[] colors;
	int[][] level;
	
	private int mx, my;
	private boolean prevClick;
	
	
	private int top;
	private int dtop;
	
	private int floor;
	private int dfloor;
	
	private Ball b;

	public void postInit() {
		this.r = new Renderer(this.game.image);

		this.trixels = new int[w * h];
		this.colors = new int[8 * 2];

		int max = 255;

		for (int i = 0; i < this.colors.length; i++) {
			int a = (i & 8) > 0 ? max : (max & 0xFEFEFE) >> 1;
			int r = (i & 4) > 0 ? a : 0;
			int g = (i & 2) > 0 ? a : 0;
			int b = (i & 1) > 0 ? a : 0;

			int rgb = this.r.makeRGB(r, g, b);
			this.colors[i] = rgb;
		}

		for (int i = 0; i < w * h; i++) {
			int xx = i % w;
			int yy = i / w;
			int c = (xx & yy) % 16;
			this.trixels[i] = c;
		}
		
		this.level = genLevel("level1");
		
		this.top = this.r.makeRGB(123,255,0);
		this.dtop = (this.top & 0xFEFEFE) >> 1;
		

		this.floor = this.r.makeRGB(255,123,0);
		this.dfloor = (this.floor & 0xFEFEFE) >> 1;
		//this.b.move(-1,0);
	}

	private float pow = 0;
	
	@Override
	public void update() {
		scale = 3;
		boolean click = this.input.leftMouseButton.justClicked();
		this.mx = this.input.mouse.getX()/scale*scale/2;
		this.my = this.input.mouse.getY()/scale*scale/2;
		
		
		if(!click && this.prevClick){
			int ax = b.pos.getIX() - mx;
			int ay = b.pos.getIY() - my;
			float diff = (float)Math.atan2(ay, ax);
			int amt = 20;
			float dx = (float)Math.cos(diff);
			float dy = (float)Math.sin(diff);
			this.b.move(dx * pow, dy * pow);
			pow = 0;
		}else{
			if(click){
				pow += 0.05%1;
			}
		}
		
		this.b.update(this.r);
		
		if(this.b.hasBeenSunk()){
			
		}
		
		this.prevClick = click;
	}

	@Override
	public void render(Graphics g) {
		this.r.fill(r.makeRGB(123, 123, 123));

		boolean maker = false;
		
		if(!maker){
			int l = level.length;
			for(int i = 0; i < l; i++){
				for(int j = 0; j < l; j++){
					if(level[i][j]==1 || level[i][j] == 2)drawCube(j,3+i);//Draws ground
					if(level[i][j]==3)drawHoleCube(j,3+i);//Draw hole;
				}
			}
		}
		
		if(!b.hasBeenSunk())this.b.render(r);
		
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

		boolean flip = x % 2 == 0;
		if(y % 2 == 1)flip = !flip;
		
		if(!flip){
			drawHalf(x,y);
			drawHalf(x+1,y);
		}
	}
	
	
	private float time = 0;
	public void drawHoleCube(int x,int y){
		drawCube(x,y);
		int poleCol = this.r.makeRGB(183, 183, 183);
		int height = 20;
		
		int dx = x * scale * 2 + 5;
		int dy =  y * (scale) - 1 - height;
		
		int sqS = 6;
		for(int i = 0; i < sqS * sqS; i++){
			int xx = i % sqS;
			int yy = i / sqS;
			this.r.setPixel(dx + xx - (sqS-1)/2, dy + yy + height-1, 0);
		}
		for(int i = height; i > 0; i--){//Draws pole
			for(int j = 0; j < 2; j++)
			this.r.setPixel(dx + j, dy + i, poleCol);
		}
		
		//DrawsFlag
		int s = 6;
		int ys = s;
		for (int j = 0; j < s * 2; j++) {
			for (int i = -ys/2; i < ys/2; i++) {
				float yo = (float)Math.sin((time/2) / 180 * Math.PI) * (s-ys);
				int px = dx + j;
				int py = dy + 4 + i + (int)yo;
				boolean flip = (int)(px+time/20)/3%2==1;
				this.r.setPixel(px + 1, py-1 + (flip?1:0), flip?this.r.makeRGB(123, 0, 0):this.r.makeRGB(255, 0, 0));
			}
			if (j % 2 == 0){
				ys--;
			}
		}
		time++;
	}

	public void draw(int x, int y, int c, boolean shade) {
		boolean flip = x % 2 == 0;
		if (y % 2 == 0)
			flip = !flip;
		if (flip && shade)
			c = (c & 0xEEEEEE) >> 1;

		int ys = scale;
		for (int j = 0; j < scale * 2; j++) {
			for (int i = -ys; i < ys; i++) {
				int dx = (x * scale * 2) + j;
				int dy = (y * scale) + i;

				if (flip) {
					dx = ((x + 1) * scale * 2) - j - 1;
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
					px = x * scale * 2;
					py = y * scale;
					System.out.println("HUI");
				}else if(rgb == this.r.makeRGB(0, 0, 255)){
					res[y][x] = 3;//End of hole
				}
			}
		}
		
		if(px == -1 || py == -1)throw new RuntimeException("The level dosne't contain a start point. Make sure that it has a hex value of #00FF00");
		this.b = new Ball(px,py);
		return res;
	}
}
