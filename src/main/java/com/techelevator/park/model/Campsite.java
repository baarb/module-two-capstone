package com.techelevator.park.model;

public class Campsite {
	
	private Long siteId;
	private Long campgroundId;
	private int siteNumber;
	private int maxOccupancy;
	private boolean isAccessible;
	private int maxRvLength;
	private boolean hasUtilities;
	
	
	public Long getSiteId() {
		return siteId;
	}
	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}
	public Long getCampgroundId() {
		return campgroundId;
	}
	public void setCampgroundId(Long campgroundId) {
		this.campgroundId = campgroundId;
	}
	public int getSiteNumber() {
		return siteNumber;
	}
	public void setSiteNumber(int siteNumber) {
		this.siteNumber = siteNumber;
	}
	public int getMaxOccupancy() {
		return maxOccupancy;
	}
	public void setMaxOccupancy(int maxOccupancy) {
		this.maxOccupancy = maxOccupancy;
	}
	public boolean isAccessible() {
		return isAccessible;
	}
	public void setAccessible(boolean isAccessible) {
		this.isAccessible = isAccessible;
	}
	public int getMaxRvLength() {
		return maxRvLength;
	}
	public void setMaxRvLength(int maxRvLength) {
		this.maxRvLength = maxRvLength;
	}
	public boolean isHasUtilities() {
		return hasUtilities;
	}
	public void setHasUtilities(boolean hasUtilities) {
		this.hasUtilities = hasUtilities;
	}
	
	public String convertBooleanForAccess(boolean boo) {
		if(boo == true) {
			return "Yes";
		}
		return "No";
	}
	
	public String convertBooleanForUtil(boolean boo) {
		if(boo == true) {
			return "Yes";
		}
		return "N/A";
	}
	
	public String convertMaxRVLength(int length) {
		if(length == 0) {
			return "N/A";
		}
		return " " + length;
	}
	
}
