package com.parkit.parkingsystem.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        //https://docs.oracle.com/javase/8/docs/api/java/time/Instant.html
        //We convert the Date object to an Instant for the Entry and Exit values
        Instant inHour = ticket.getInTime().toInstant();
        Instant outHour = ticket.getOutTime().toInstant();

        //TODO: Some tests are failing here. Need to check if this logic is correct
        //Now, using those 2 Instant values, we calculate the difference between and convert the result in Hour
        
        double duration = (ChronoUnit.SECONDS.between(inHour, outHour)) /3600.0;
        // other method for the same calculation:
        //double duration = inHour.until(outHour, ChronoUnit.SECONDS);

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}