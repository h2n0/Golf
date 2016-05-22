package uk.fls.main.util;

import java.util.Arrays;

import fls.engine.main.util.Renderer;
import uk.fls.main.screens.entitys.Ball;
import uk.fls.main.screens.entitys.Flag;

public class Level {

	public final int w, h;
	public int ww, wh;
	
	public int[] worldImage;
	public int[][] levelData;
	
	public Flag flag;
	public Ball ball;
	
	private int top;
	private int dtop;
	private int floor;
	private int dfloor;
	
	public Level(Renderer r, String name){
		int[][] d = TextureMap.getPixles("/levels/"+name+".png");
		this.w = d[0][0];
		this.h = d[0][1];
		int[][] res = new int[this.w][this.h];
		int[] pixels = d[1];
		
		int px = -1,py = -1,fx = -1, fy = -1;
		
		for(int y = 0; y < this.h; y++){
			for(int x = 0; x < this.w; x++){
				int rgb = pixels[x + y * this.w];
				if(rgb == r.makeRGB(0, 0, 0)){
					res[y][x] = 1;//Ground
				}else if(rgb == r.makeRGB(0, 255, 0)){
					res[y][x] = 2;//Start of hole
					px = x * r.scale * 2 * 2;
					py = y * r.scale;
					System.out.println("HUI");
				}else if(rgb == r.makeRGB(0, 0, 255)){
					res[y][x] = 3;//End of hole
					//this.flag = new Flag(x * r.scale * 2 * 2, y * r.scale + r.scale);
					fx = x * r.scale * 2 * 2;
					fy = y * r.scale + r.scale;
				}
			}
		}
		
		if(px == -1 || py == -1)throw new RuntimeException("The level doesn't contain a start point. Make sure that it has a hex value of #00FF00");
		if(fx == -1 || fy == -1)throw new RuntimeException("The level doesn't contain an end point. Make sure that it has a hex value of #0000FF");
		//this.ball = new Ball(px,py);
		
		this.top = r.makeRGB(123, 255, 0);
		this.floor = r.makeRGB(255, 123, 0);
		
		this.dtop = (this.top & 0xFEFEFE) >> 1;
		this.dfloor = (this.floor & 0xFEFEFE) >> 1;
		
		this.flag = new Flag(fx, fy);
		this.ball = new Ball(px, py);
		
		this.levelData = res;
		
		renderPass(r);
	}
	
	private void renderPass(Renderer r){
		this.ww = this.w * r.scale * 2 * 2;
		this.wh = this.h * r.scale + r.scale;
		
		this.worldImage = new int[ww * wh];
		Arrays.fill(worldImage, r.makeRGB(123, 123, 123));
		
		for(int x = 0; x < this.w; x++){
			for(int y = 0; y < this.h; y++){
				int p = levelData[y][x];
				if(p != 0)drawCube(r, x, 3 + y);
			}
		}
	}
	
	private void drawCube(Renderer r, int x,int y){
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
			drawHalf(r, x,y);
			drawHalf(r, x+1,y);
		}
	}
	
	private void drawHalf(Renderer r, int x,int y){	
		boolean flip = x % 2 == 0;
		if(y % 2 == 1)flip = !flip;
		
		int t = top;
		int f = floor;
		
		if(!flip)f = dfloor;
		if(y%2==0)t = dtop;
		
		draw(r, x,y,t);
		draw(r, x,y+1,f);
		draw(r, x,y+2,f);
	}
	
	public void draw(Renderer r, int x, int y, int c) {
		boolean flip = x % 2 == 0;
		if (y % 2 == 0)
			flip = !flip;
		if (flip)
			c = (c & 0xEEEEEE) >> 1;

		int ys = r.scale;
		for (int j = 0; j < r.scale * 2; j++) {
			for (int i = -ys; i < ys; i++) {
				int dx = (x * r.scale * 2) + j;
				int dy = (y * r.scale) + i;

				if (flip) {
					dx = ((x + 1) * r.scale * 2) - j - 1;
				}
				//r.setPixel(dx, dy, c);
				if(this.worldImage[dx + dy * this.ww] == c || c < 0)continue;
				this.worldImage[dx + dy * this.ww] = c;
			}
			if (j % 2 == 0)
				ys--;
		}
	}
}
