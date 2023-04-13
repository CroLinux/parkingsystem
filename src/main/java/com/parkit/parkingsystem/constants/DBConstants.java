package com.parkit.parkingsystem.constants;

public class DBConstants {

    public static final String GET_NEXT_PARKING_SPOT = "select min(PARKING_NUMBER) from parking where AVAILABLE = true and TYPE = ?";
    public static final String UPDATE_PARKING_SPOT = "update parking set available = ? where PARKING_NUMBER = ?";

    public static final String SAVE_TICKET = "insert into ticket(PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME) values(?,?,?,?,?)";
    public static final String UPDATE_TICKET = "update ticket set PRICE=?, OUT_TIME=? where ID=?";
    
    public static final String GET_TICKET = "select t.PARKING_NUMBER, t.ID, t.PRICE, t.IN_TIME, t.OUT_TIME, p.TYPE from ticket t,parking p where p.parking_number = t.parking_number and t.VEHICLE_REG_NUMBER=? order by t.IN_TIME desc limit 1";
    //We will modify this request in order to take the last instance for the selected vehicle and actually into the parking
    //public static final String GET_TICKET = "select t.PARKING_NUMBER, t.ID, t.PRICE, t.IN_TIME, t.OUT_TIME, p.TYPE from ticket t,parking p where p.parking_number = t.parking_number and t.VEHICLE_REG_NUMBER=? and T.OUT_TIME IS NULL order by t.IN_TIME desc limit 1";
    
    //Used to check if the vehicle was already there in the past
    public static final String GET_FIDELITY = "select count(t.id) from ticket t where t.VEHICLE_REG_NUMBER=?";
    
    //Used to check if the vehicle is already parked but didn't left
    public static final String GET_PRESENCE = "select count(t.id) from ticket t where t.VEHICLE_REG_NUMBER=? and t.OUT_TIME IS NULL";
    
}
