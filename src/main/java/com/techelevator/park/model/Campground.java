package com.techelevator.park.model;

import java.math.BigDecimal;
import java.text.DateFormatSymbols;
import java.time.LocalDate;

public class Campground {
	
	private Long campgroundId;
	private Long parkId;
	private String campgroundName;
	private String openDate;
	private String closeDate;
	private BigDecimal dailyFee;
	
	
	public Long getCampgroundId() {
		return campgroundId;
	}
	public void setCampgroundId(Long campgroundId) {
		this.campgroundId = campgroundId;
	}
	public Long getParkId() {
		return parkId;
	}
	public void setParkId(Long parkId) {
		this.parkId = parkId;
	}
	public String getCampgroundName() {
		return campgroundName;
	}
	public void setCampgroundName(String campgroundName) {
		this.campgroundName = campgroundName;
	}
	public String getOpenDate() {
		return openDate;
	}
	public void setOpenDate(String openDate) {
		this.openDate = openDate;
	}
	public String getCloseDate() {
		return closeDate;
	}
	public void setCloseDate(String closeDate) {
		this.closeDate = closeDate;
	}
	public BigDecimal getDailyFee() {
		return dailyFee;
	}
	public void setDailyFee(BigDecimal dailyFee) {
		this.dailyFee = dailyFee;
	}
	
	public String toString() {
		return this.campgroundName;
	}
	
	public String toMonth(String date) {
		int start = Integer.parseInt(date);
		String month = "";
		
		DateFormatSymbols dfs = new DateFormatSymbols();
		String[] months = dfs.getMonths();
		if(start >= 0 && start <= 12) {
			month = months[start - 1];
		}
		return month;
	}
	
}
