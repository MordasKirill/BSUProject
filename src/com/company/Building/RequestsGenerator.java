package com.company.Building;

import com.company.Building.Building;
import com.company.Elevator.Pointers;
import com.company.Elevator.Request;

public class RequestsGenerator implements Runnable{
    private final Building building;
    private final int speed;
    private final int num_requests_per_time;
    public RequestsGenerator(Building building, int speed, int num_requests_per_time){
        this.num_requests_per_time = num_requests_per_time;
        this.speed = speed;
        this.building = building;
    }
    @Override
    public void run() {
        try {
            int stage_num;
            int num_stages = building.getNumStages();
            System.out.println(num_stages);
            Request r;
            while (true){
                for (int i = 0; i < num_requests_per_time; i++){
                    stage_num = (int) ( (Math.random() * (num_stages)) );
                    r = generateRandomRequest(num_stages - 1, stage_num);
                    building.getStage(stage_num).addRequest(r);
                }
                Thread.sleep(this.speed);
            }
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

    private static Request generateRandomRequest(int max_stage_num, int for_stage){
        int destination_stage = for_stage;

        while (destination_stage == for_stage){
            destination_stage = (int) ( (Math.random() * (max_stage_num + 1) ) );
        }

        return new Request(destination_stage, destination_stage > for_stage ? Pointers.UP : Pointers.DOWN);
    }
}
