package com.company.Elevator;

import com.company.Elevator.Pointers;
import com.company.Elevator.Request;

import java.util.ArrayList;
import java.util.Iterator;

public class Stage{
    private final ArrayList<Request> requests;
    int stage_num;
    public Stage(int num){
        this.stage_num = num;
        this.requests = new ArrayList<>();
    }

    public synchronized ArrayList<Request> getNPeople(Pointers direction, int max_n){
        Iterator<Request> it = requests.iterator();
        ArrayList<Request> peopleToReturn = new ArrayList<>();

        while (it.hasNext()){
            Request r = it.next();
            if (peopleToReturn.size() >= max_n) break;
            if (r.button_pressed == direction){
                peopleToReturn.add(r);
                it.remove();
            }
        }

        return peopleToReturn;

    }

    public int waitersNum(){
        return this.requests.size();
    }

    public synchronized void addRequest(Request r){
        requests.add(r);
    }
}