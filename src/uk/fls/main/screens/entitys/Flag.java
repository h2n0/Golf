package uk.fls.main.screens.entitys;

import fls.engine.main.util.Point;
import fls.engine.main.util.Renderer;
import uk.fls.main.util.Entity;

public class Flag extends Entity{

	
	private float offset;
	
	public Flag(int x,int y){
		super(x,y);
		this.offset = 0;
	}
	
	public void render(Renderer r){
		int poleCol = r.makeRGB(183, 183, 183);
		int height = 20;
		
		int dx = pos.getIX() + 5;
		int dy =  pos.getIY() - 1 - height + (int)(height/3);
		
		int sqS = 6;
		int w = 6;
		int h = 4;
		for(int i = 0; i < w * h; i++){
			int xx = i % w;
			int yy = i / w;
			r.setPixel(dx + xx - (sqS-1)/2, dy + yy + height-1, 0);
		}
		for(int i = height; i > 0; i--){//Draws pole
			for(int j = 0; j < 2; j++)
				r.setPixel(dx + j, dy + i, poleCol);
		}
		
		int s = 6;
		int ys = s;
		for (int j = 0; j < s * 2; j++) {
			for (int i = -ys/2; i < ys/2; i++) {
				float yo = (float)Math.sin((offset) / 180 * Math.PI) * (s-ys)/2;
				int px = dx + j;
				int py = dy + 4 + i + (int)yo;
				boolean flip = (int)(px+offset/25)/3%2==1;
				r.setPixel(px + 1, py-1 + (flip?1:0), flip?r.makeRGB(123, 0, 0):r.makeRGB(255, 0, 0));
			}
			if (j % 2 == 0){
				ys--;
			}
		}
	}
	
	public void update(Renderer r){
		this.offset += 4;
		this.offset %= 720;
	}
}
