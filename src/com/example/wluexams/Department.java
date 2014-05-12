package com.example.wluexams;

public class Department {
	
	private String name;
	private int ID;
	
	public Department(int ID, String name){
		
		this.ID = ID;
		this.name = name;
		
	}
	
	public String getName() {
		return name;
	}

	public int getID() {
		return ID;
	}
	
	public String toString(){
		
		return this.name;
		
	}


}
