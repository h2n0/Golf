package uk.fls.main;

import java.math.BigInteger;

import fls.engine.main.Init;
import fls.engine.main.input.Input;
import uk.fls.main.screens.RenderScreen;

public class Caster extends Init{

	private static int w = 320;
	private static int h = 200;
	private static int s = 2;
	
	public Caster(){
		super("Golf V0.1", w * s, h * s);
		useCustomBufferedImage(w, h, false);
		setInput(new Input(this, Input.KEYS, Input.MOUSE));
		skipInit();
		setScreen(new RenderScreen());
		showFPS();
	}
	
	public static void main(String[] args){
		new Caster().start();
	}
}
