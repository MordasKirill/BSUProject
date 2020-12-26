package com.company.Building;

import com.company.Elevator.Lift;
import com.company.Elevator.Stage;

import java.util.ArrayList;

public class Building {
    public final ArrayList<Lift> lifts_array = new ArrayList<>();
    private final Stage[] stages;
    private final int num_stages;
    public Building(int num_stages, int num_lifts, int lift_capacity){
        //добавляем лифты
        for (int i = 0; i < num_lifts; i++) {
            int initial_stage = (int)(Math.random() * num_stages);
            lifts_array.add(new Lift(i, initial_stage, lift_capacity, this, 1000));
        }

        this.num_stages = num_stages;
        this.stages = new Stage[num_stages];
        for (int i = 0; i < num_stages; i++){
            this.stages[i] = new Stage(i);
        }
    }

    public void launchLifts(){
        for (Runnable lift : lifts_array){
            new Thread(lift).start();
        }
    }

    public int getNumStages(){
        return this.num_stages;
    }

    public Stage getStage(int stage_num) {
        if (stage_num >= this.stages.length || stage_num < 0){
            return null;
        }

        return this.stages[stage_num];
    }
}