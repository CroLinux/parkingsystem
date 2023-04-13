package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

//import junit.framework.Assert;
import org.junit.Assert;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.Date;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;
	private static DataBasePrepareService dataBasePrepareService;

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	private static void setUp() throws Exception {
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
		dataBasePrepareService = new DataBasePrepareService();
	}

	@BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

	@AfterAll
	private static void tearDown() {

	}

	@Test
	public void testParkingACar() {
		// TODO: check that a ticket is actually saved in DB and Parking table is
		// updated
		// with availability
		// GIVEN
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		// Here we define a format for the next dates comparison and we get a reference
		// date
		String pattern = "yyyy-MM-dd HH:mm";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(new Date());

		// WHEN
		parkingService.processIncomingVehicle();

		// THEN
		// Here, we verify if an entry with the RegistrationNumber (from the
		// setUpPerTest()) was created
		// during the last minute and if there is a value for the ParkingSpot
		Ticket ticketTestExistingInDB = ticketDAO.getTicket("ABCDEF");
		Assert.assertNotNull(ticketTestExistingInDB); // we check if there is a row into the DB
		Assert.assertEquals("ABCDEF", ticketTestExistingInDB.getVehicleRegNumber()); // we check if we have the same
																						// Reg. Number

		String dateDB = simpleDateFormat.format(ticketTestExistingInDB.getInTime()); // we get the InTime date from the
																						// DB and convert it
		Assert.assertEquals(date, dateDB); // we compare the dates if they are the same - same minute

		Assert.assertTrue(ticketTestExistingInDB.getParkingSpot().getId() != 0); // we check if we have a ParkingSpot
	}

	@Test
	public void testParkingLotExit() {
		// GIVEN
		testParkingACar();
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processExitingVehicle();
		// TODO: check that the fare generated and out time are populated correctly in
		// the database

		// We generate a ticket with an entry time - 1 hour to be sure to get price
		// different of 0
		Ticket ticket = new Ticket();
		ticket.setInTime(new Date(System.currentTimeMillis() - 60 * 60 * 1000));
		ticket.setOutTime(null);
		ticket.setPrice(0);
		ticket.setVehicleRegNumber("ABCDEF");
		ticket.setId(1);
		ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
		ticketDAO.saveTicket(ticket);

		// WHEN
		// We process the exit for this car/ticket created, in the console we can see a
		// price
		parkingService.processExitingVehicle();

		// THEN
		// We check if outTime is not null and the price is different to 0
		Ticket testTicket = ticketDAO.getTicket("ABCDEF");
		Assert.assertNotSame(null, testTicket.getOutTime());
		Assert.assertNotSame(0, testTicket.getPrice());
		//Assert.assertSame(null, testTicket.getOutTime());
	}

}
