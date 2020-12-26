package com.company.Elevator;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class ElevatorUIData {
    public int stage_num;
    public int passenger_num;
    public int amountOfTrips;
    public int amountOfEmptyTrips;

    public ElevatorUIData (int stage_num, int passenger_num, int amountOfTrips, int amountOfEmptyTrips){
        this.passenger_num = passenger_num;
        this.stage_num = stage_num;
        this.amountOfTrips = amountOfTrips;
        this.amountOfEmptyTrips = amountOfEmptyTrips;
    }
}