package com.techelevator.park.model.jdbc;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.park.model.Park;
import com.techelevator.park.model.ParkDAO;

public class ParkJDBCDAO implements ParkDAO {
	
	private JdbcTemplate jdbcTemplate;
	
	public ParkJDBCDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	// Returns a List of Park objects
	public List<Park> getAllParks() {
		ArrayList<Park> allParks = new ArrayList<>();
		String sqlFindParks = "SELECT * FROM park";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlFindParks);
		while (results.next()) {
			Park thePark = mapRowToPark(results);
			allParks.add(thePark);
		}
		return allParks;
	}
	
	private Park mapRowToPark(SqlRowSet results) {
		Park thePark = new Park();
		thePark.setParkId(results.getLong("park_id"));
		thePark.setArea(results.getInt("area"));
		thePark.setDescription(results.getString("description"));
		thePark.setEstblDate(results.getDate("establish_date").toLocalDate());
		thePark.setParkLocation(results.getString("location"));
		thePark.setParkName(results.getString("name"));
		thePark.setVisitors(results.getInt("visitors"));
		
		return thePark;
	}

}
