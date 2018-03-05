package com.techelevator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.techelevator.park.model.Campground;
import com.techelevator.park.model.jdbc.CampgroundJDBCDAO;

public class CampgroundJDBCDAOTest {
	private static SingleConnectionDataSource dataSource;
	private CampgroundJDBCDAO sut;
	private Long campgroundId;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		dataSource = new SingleConnectionDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/park");
		dataSource.setUsername("postgres");
		
		dataSource.setAutoCommit(false);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		dataSource.destroy();
	}

	@Before
	public void setUp() throws Exception {
		sut = new CampgroundJDBCDAO(dataSource);
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		String newCampground= "INSERT INTO campground (park_id, name, open_from_mm, open_to_mm, daily_fee) "
				+ "VALUES (?,?,?,?,?) RETURNING campground_id";
		campgroundId = jdbcTemplate.queryForObject(newCampground, Long.class, 1, "TEST NAME", "01", "10", 20);
	}

	@After
	public void tearDown() throws Exception {
		dataSource.getConnection().rollback();
	}

	@Test
	public void testGetAllCampgrounds() {
		List<Campground> allCampgrounds = sut.getAllCampgrounds(1L);
		
		for(Campground camp : allCampgrounds) {
			if(camp.getCampgroundName().equals("TEST NAME") && camp.getCampgroundId().equals(campgroundId)) {
				assertEquals(campgroundId, camp.getCampgroundId());
				assertEquals("TEST NAME", camp.getCampgroundName());
				return;
			}
		}
		
		fail("No campgrounds found.");
	}

	@Test
	public void testGetCampgroundById() {
		Campground camp = sut.getCampgroundById(campgroundId);
		
		assertEquals("TEST NAME", camp.getCampgroundName());
		assertEquals(campgroundId, camp.getCampgroundId());
	}

}
