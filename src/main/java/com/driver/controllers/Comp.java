package com.driver.controllers;

import com.driver.model.Airport;

import java.util.Comparator;

public class Comp implements Comparator<Airport> {
    public int compare(Airport a, Airport b){
        if(a.getNoOfTerminals() - b.getNoOfTerminals() == 0){
            return a.getAirportName().compareTo(b.getAirportName());
        }
        else{
            return a.getNoOfTerminals() - b.getNoOfTerminals();
        }
    }
}