package com.driver.controllers;

import com.driver.model.Airport;
import com.driver.model.City;
import com.driver.model.Flight;
import com.driver.model.Passenger;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.*;

@Repository
public class AirportRepository{

    Map<String, Airport> airportMap = new HashMap<>();
    Map<City, Airport> cityAirportMap = new HashMap<>();
    Map<Integer, Flight> flightMap = new HashMap<>();
    Map<Integer, Passenger> passengerMap = new HashMap<>();
    Map<Flight, Integer> currBookingInFlight = new HashMap<>();
    Map<Flight, List<Passenger>> flightPassengerListMap = new HashMap<>();
    public void addAirport(Airport airport) {
        airportMap.put(airport.getAirportName(), airport);
        cityAirportMap.put(airport.getCity(), airport);
    }

    public String getLargestAirportName() {
        List<Airport> airports = new ArrayList<>();
        String ans = null;
        for(String s : airportMap.keySet()){
            airports.add(airportMap.get(s));
        }
        Collections.sort(airports, new Comp());

        for(int i=airports.size()-1; i>=0; i--){
            if(airports.get(airports.size()-1).getNoOfTerminals()
                    == airports.get(i).getNoOfTerminals()){

                ans = airports.get(i).getAirportName();
            }
        }
        return  ans;
    }

    public void addFlight(Flight flight) {
        flightMap.put(flight.getFlightId(), flight);
        List<Passenger> passengerList = new ArrayList<>();
        flightPassengerListMap.put(flight, passengerList);
        currBookingInFlight.put(flight, 0);

    }

    public double getShortestDurationOfPossibleBetweenTwoCities(City fromCity, City toCity) {
        double shortestDuration = Integer.MAX_VALUE;
        for(Integer i : flightMap.keySet()){
            Flight flight = flightMap.get(i);
            City from = flight.getFromCity();
            City to = flight.getToCity();

            if(from.equals(fromCity) && to.equals(toCity)){
                shortestDuration = Math.min(shortestDuration, flight.getDuration());
            }
        }
        return shortestDuration;
    }

    public String addPassenger(Passenger passenger){
        passengerMap.put(passenger.getPassengerId(), passenger);
        return "SUCCESS";
    }

    public String bookATicket(Integer flightId, Integer passengerId) {
        Flight flight;
        int currBooking;
        if(flightMap.containsKey(flightId)){
            flight = flightMap.get(flightId);
        } else{
            return "FAILURE";
        }
        if(currBookingInFlight.containsKey(flight)){
            currBooking = currBookingInFlight.get(flight);
        } else{
            return "FAILURE";
        }

        int maxCap = flight.getMaxCapacity();

        if(currBooking >= maxCap){return "FAILURE";}

        // checking  passenger already  booked flight or not
        List<Passenger> passengerList = flightPassengerListMap.get(flight);
        for(int i=0; i<passengerList.size(); i++){
            if(passengerList.get(i).getPassengerId() == passengerId){
                return "FAILURE";
            }
        }
        passengerList.add(passengerMap.get(passengerId));
        flightPassengerListMap.put(flight, passengerList);
        currBooking++;
        currBookingInFlight.put(flight, currBooking);
        return "SUCCESS";
    }

    public String cancelATicket(Integer flightId, Integer passengerId) {
        Flight flight = flightMap.get(flightId);
        if(!flightMap.containsKey(flightId)){
            return "FAILURE";
        }
        List<Passenger> passengerList = flightPassengerListMap.get(flight);
        for(int i=0; i<passengerList.size(); i++){
            if(passengerList.get(i).getPassengerId() == passengerId){
                passengerList.remove(i);
                flightPassengerListMap.put(flight, passengerList);
                int currBooking = currBookingInFlight.get(flight);
                currBooking--;
                currBookingInFlight.put(flight, currBooking);
                return "SUCCESS";
            }
        }
        return "FAILURE";
    }

    public int getNumberOfPeopleOn(Date date, String airportName) {
        int noOfPassenger = 0;

        for(Integer id : flightMap.keySet()){
            Flight flight = flightMap.get(id);
            String takeOffAirport = cityAirportMap.get(flight.getFromCity()).getAirportName();
            String landAirport = cityAirportMap.get(flight.getToCity()).getAirportName();
            if(flight.getFlightDate().equals(date) &&
                    (airportName.equals(takeOffAirport) || airportName.equals(landAirport))){
                noOfPassenger += flightPassengerListMap.get(flight).size();
            }
        }
        return noOfPassenger;
    }

    public int calculateFlightFare(Integer flightId) {
        Flight flight = flightMap.get(flightId);
        return 3000 + currBookingInFlight.get(flight) * 50;
    }

    public int calculateRevenueOfAFlight(Integer flightId) {
        if(flightMap.containsKey(flightId)){
            Flight flight = flightMap.get(flightId);
            int currNoOfPessenger = currBookingInFlight.get(flight);
            return calculateRevenue(currNoOfPessenger);
        }
        return 0;
    }

    private int calculateRevenue(int currNoOfPessenger) {
        if(currNoOfPessenger == 1){
            return 3000 + 0 * 50;
        }
        int sAns = calculateRevenue(currNoOfPessenger-1);
        return sAns += 3000 + (currNoOfPessenger-1) * 50;
    }

    public String getAirportNameFromFlightId(Integer flightId) {
        if(flightMap.containsKey(flightId)){
            Flight flight = flightMap.get(flightId);
            City takeOffCity = flight.getFromCity();
            return cityAirportMap.get(takeOffCity).getAirportName();
        }
        else{
            return null;
        }
    }

    public int countOfBookingsDoneByPassengerAllCombined(Integer passengerId) {
        int noOfBookings = 0;
        Passenger passenger = passengerMap.get(passengerId);
        for(Flight flight : flightPassengerListMap.keySet()){
            List<Passenger> passengerList = flightPassengerListMap.get(flight);
            if(passengerList.contains(passenger)){
                noOfBookings++;
            }
        }
        return noOfBookings;
    }
}
