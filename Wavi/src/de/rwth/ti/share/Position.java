package de.rwth.ti.share;

public class Position {
	private int x, y;
	private int map_id;
	private int building_id;
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public int getMap_id(){
		return map_id;
	}
	
	public int getBuilding_id(){
		return building_id;
	}
	
	public void setX(int x){
		this.x=x;
	}
	
	public void setY(int y){
		this.y=y;
	}
	
	public void setMap_id(int map_id){
		this.map_id=map_id;
	}
	
	public void setBuilding_id(int building_id){
		this.building_id=building_id;
	}
	
	
	
}