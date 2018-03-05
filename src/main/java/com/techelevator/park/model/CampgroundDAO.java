package com.techelevator.park.model;

import java.util.List;

public interface CampgroundDAO {
	
	public List<Campground> getAllCampgrounds(Long id);
	public Campground getCampgroundById(Long id);

}
