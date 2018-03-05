package com.techelevator.park.model.jdbc;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.park.model.Campground;
import com.techelevator.park.model.CampgroundDAO;
import com.techelevator.park.model.Park;

public class CampgroundJDBCDAO implements CampgroundDAO {
	
	private JdbcTemplate jdbcTemplate;
	
	public CampgroundJDBCDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Campground> getAllCampgrounds(Long id) {
		ArrayList<Campground> allCampgrounds = new ArrayList<>();
		String sqlFindCampgrounds = "SELECT * FROM campground WHERE park_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlFindCampgrounds, id);
		while (results.next()) {
			Campground theCampground = mapRowToCampground(results);
			allCampgrounds.add(theCampground);
		}
		return allCampgrounds;
	}
	
	@Override
	public Campground getCampgroundById(Long id) {
		Campground selectedCamp = new Campground();
		String sqlFindCampground = "SELECT * FROM campground WHERE campground_id =?";
		
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlFindCampground, id);
		while (results.next()) {
			selectedCamp = mapRowToCampground(results);
			
		}
		return selectedCamp;
	}
	
	private Campground mapRowToCampground(SqlRowSet results){
		Campground theCampground = new Campground();
		theCampground.setCampgroundId(results.getLong("campground_id"));
		theCampground.setParkId(results.getLong("park_id"));
		theCampground.setCampgroundName(results.getString("name"));
		theCampground.setOpenDate(results.getString("open_from_mm"));
		theCampground.setCloseDate(results.getString("open_to_mm"));
		theCampground.setDailyFee(results.getBigDecimal("daily_fee"));
		
		return theCampground;
	}

	

}
