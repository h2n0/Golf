package uk.fls.main.screens;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import fls.engine.main.art.SplitImage;
import fls.engine.main.screen.Screen;
import fls.engine.main.util.Renderer;

public class RenderScreen extends Screen {

	private Renderer r;

	private int w = 40;
	private int h = 51;
	int scale = 3;
	private int[] trixels;
	private int[] colors;
	int[][] level;
	
	private int mx, my;
	
	
	private int top;
	private int dtop;
	
	private int floor;
	private int dfloor;

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
	}

	@Override
	public void update() {
		scale = 3;
		
		this.mx = this.input.mouse.getX()/(scale*2)/2;
		this.my = this.input.mouse.getY()/(scale)/2;
	}

	@Override
	public void render(Graphics g) {
		this.r.fill(r.makeRGB(123, 123, 123));

		boolean maker = false;
		
		if(!maker){
			int l = level.length;
			for(int i = 0; i < l; i++){
				for(int j = 0; j < l; j++){
					if(level[i][j]==1)drawCube(j,3+i);
				}
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

		boolean flip = x % 2 == 0;
		if(y % 2 == 1)flip = !flip;
		
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
		BufferedImage img = new SplitImage("/levels/"+name+".png").load();
		int w = img.getWidth();
		int h = img.getHeight();
		int[][] res = new int[h][w];
		int[] pixels = new int[w * h];
		img.getRGB(0, 0, w, h, pixels, 0, w);
		
		for(int y = 0; y < h; y++){
			for(int x = 0; x < w; x++){
				int rgb = ~pixels[x + y * w];
				if(rgb == this.r.makeRGB(255, 255, 255)){
					res[y][x] = 1;
				}
			}
		}
		
		return res;
	}
}
