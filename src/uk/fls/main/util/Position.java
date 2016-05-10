package uk.fls.main.util;

public class Position {

	
	private float x,y,z;
	
	public Position(float x, float y, float z){
		setPosition(x, y, z);
	}
	
	public float getX(){
		return this.x;
	}
	
	public float getY(){
		return this.y;
	}
	
	public float getZ(){
		return this.z;
	}
	
	public void setX(float nx){
		this.x = nx;
	}
	
	public void setY(float ny){
		this.y = ny;
	}
	
	public void setZ(float nz){
		this.z = nz;
	}
	
	public void setPosition(float x,float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
