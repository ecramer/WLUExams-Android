package com.example.wluexams;

import java.io.Serializable;
import java.util.Date;

public class Exam implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private int ID;
	private String depID;
	private String courseCode;
	private String section;
	private Date date;
	private String room;
	private String locationName;
	private Float latitude;
	private Float longitude;
	
	
	public Exam(int iD, String depID, String courseCode, String section,
			Date date,  String room, String locationName,
			Float latitude, Float longitude) {
		super();
		ID = iD;
		this.depID = depID;
		this.courseCode = courseCode;
		this.section = section;
		this.date = date;
		this.room = room;
		this.locationName = locationName;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	
	public int getID() {
		return ID;
	}
	public String getDepID() {
		return depID;
	}
	public String getCourseCode() {
		return courseCode;
	}
	public String getSection() {
		return section;
	}
	public Date getDate() {
		return date;
	}

	public String getRoom() {
		return room;
	}
	public String getLocationName() {
		return locationName;
	}
	public Float getLatitude() {
		return latitude;
	}
	public Float getLongitude() {
		return longitude;
	}
	
	public String toString(){
		
		return this.courseCode;
	}
	
	
}
