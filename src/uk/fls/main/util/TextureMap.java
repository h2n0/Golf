package uk.fls.main.util;

import java.awt.image.BufferedImage;

import fls.engine.main.art.SplitImage;

public class TextureMap {

	private int[] data;
	private final int w,h;
	
	public TextureMap(String place){
		int[][] d = getPixles(place);
		this.data = d[1];
		this.w = d[0][0];
		this.h = d[0][1];
	}
	
	public int getPixel(int x,int y){
		int dx = (x % w);
		int dy = (y / w) % h;
		int c = this.data[dx + dy * w];
		return c;
	}
	
	public static int[][] getPixles(String place){
		BufferedImage img = new SplitImage(place).load();
		int w = img.getWidth();
		int h = img.getHeight();
		
		int[] data = new int[w * h];
		
		int[] current = new int[w * h];
		img.getRGB(0, 0, w, h, current, 0, w);
		
		int ss = w * h;
		int[] res = new int[w * h];
		
		if(img.getType() == BufferedImage.TYPE_INT_RGB){// No transparency
			for(int i = 0; i < ss; i++){
				int c = current[i];
				int r = (c >> 16) & 0xFF;
				int g = (c >> 8) & 0xFF;
				int b = (c) & 0xFF;
				int rgb = (r << 16) | (g << 8) | b;
				res[i] = rgb;
			}
		}else{
			for(int i = 0; i < ss; i++){
				int c = current[i];
				int r = (c >> 16) & 0xFF;
				int g = (c >> 8) & 0xFF;
				int b = (c) & 0xFF;
				int rgb = (r << 16) | (g << 8) | b;
				res[i] = rgb;
			}
		}
		
		return new int[][]{{w,h},res};
	}
}
