package com.techelevator.park.model;

import java.time.LocalDate;

public class Park {
	
	private Long parkId;
	private String parkName;
	private String parkLocation;
	private LocalDate estblDate;
	private int area;
	private int visitors;
	private String description;
	
	
	public Long getParkId() {
		return parkId;
	}
	public void setParkId(Long parkId) {
		this.parkId = parkId;
	}
	public String getParkName() {
		return parkName;
	}
	public void setParkName(String parkName) {
		this.parkName = parkName;
	}
	public String getParkLocation() {
		return parkLocation;
	}
	public void setParkLocation(String parkLocation) {
		this.parkLocation = parkLocation;
	}
	public LocalDate getEstblDate() {
		return estblDate;
	}
	public void setEstblDate(LocalDate estblDate) {
		this.estblDate = estblDate;
	}
	public int getArea() {
		return area;
	}
	public void setArea(int area) {
		this.area = area;
	}
	public int getVisitors() {
		return visitors;
	}
	public void setVisitors(int visitors) {
		this.visitors = visitors;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String toString() {
		return this.parkName;
	}
}
