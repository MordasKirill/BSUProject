package com.company.Elevator;

import com.company.Building.Building;
import com.company.Runner.Main;

import java.util.ArrayList;
import java.util.Iterator;

public class Lift implements Runnable {
    private int stage_num; // этаж, на котором лифт находится в данный момент
    private  int capacity; // вместимость лифта
    private  int branch_id; //id пролета, на котором установлен лифт
    private  int speed_ms;
    private Pointers state; // состояние, куда едет лифт (↑, ↓, либо 0 если стоит)
    private final ArrayList<Request> passengers = new ArrayList<>();
    public int counter;
    public int tripsCounter = 0;
    public int parsedCounter = 0;

    Building assigned_building;

    public Lift() {

    }
    public Lift(int branch_id, int initial_stage, int capacity, Building assigned_building, int speed_ms){
        this.speed_ms = speed_ms;
        this.assigned_building = assigned_building;
        this.branch_id = branch_id;
        this.stage_num = initial_stage;
        this.state = Pointers.STOP;
        this.capacity = capacity;
    }




    public ElevatorUIData getReferencesForUI(){
        return new ElevatorUIData(
                this.stage_num,
                this.passengers.size(),
                this.tripsCounter,
                this.counter
        );
    }

    // если лифт пуст, то на каждом этаже, независимо куда он вызван, будет производится пересчет маршрута к ближайшему этажу.
    private int getClosestStageNumWithRequests(){
        {
            Stage s1;
            Stage s2;
            int shift;
            while (true) {
                //проверим есть ли люди на текущем этаже
                s1 = assigned_building.getStage(this.stage_num); // 0 ... n-1
                if (s1 != null && s1.waitersNum() != 0) {
                    return s1.stage_num;
                }
                //иначе смотрим по бокам все дальше и дальше, пока не пройдем все этажи -> возврат к верхнему циклу
                shift = 1;
                do{
                    s1 = assigned_building.getStage(this.stage_num + shift);
                    s2 = assigned_building.getStage(this.stage_num - shift);
                    if (s1!=null && s1.waitersNum()!=0){
                        return s1.stage_num;
                    }else if (s2!=null && s2.waitersNum() != 0){
                        return s2.stage_num;
                    }
                    shift++;
                }while (s1!=null || s2!=null);
            }
        }
    }

    private void move(){
        try {
            Thread.sleep(this.speed_ms);
            if (this.state == Pointers.UP){
                this.stage_num++;
            }else if (this.state == Pointers.DOWN){
                this.stage_num--;
            }
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run(){
        System.out.printf("Лифт №%s запустился\n", this.branch_id);
        while (true){
            if (this.state == Pointers.STOP){ // Если лифт стоит, то

                System.out.printf("Лифт №%d на этаже: %d число пассажиров: %d state: %s \n",
                        this.branch_id, this.stage_num, this.passengers.size(), this.state.name());


                int stage_to_go = getClosestStageNumWithRequests();
                //едем к ближайшему этажу (каждый этаж пересчитываем)
                while (stage_to_go != this.stage_num){
                    if (stage_to_go > this.stage_num){
                        this.state = Pointers.UP;
                    }else{
                        this.state = Pointers.DOWN;
                    }

                    move();

                    stage_to_go = getClosestStageNumWithRequests();
                }
                this.state = Pointers.STOP;

                boolean getUpPassengersFirst = Math.random() < 0.5;
                if (getUpPassengersFirst){
                    passengers.addAll(assigned_building.getStage(this.stage_num).getNPeople(Pointers.UP, this.capacity));
                }else {
                    passengers.addAll(assigned_building.getStage(this.stage_num).getNPeople(Pointers.DOWN, this.capacity));
                }

                if (this.passengers.size() != 0){
                    this.state = this.passengers.get(0).button_pressed;
                }

            }else { //иначе лифт куда-то едет
                while (this.passengers.size() != 0){
                    System.out.printf("Лифт №%d на этаже: %d число пассажиров: %d state: %s \n",
                            this.branch_id, this.stage_num, this.passengers.size(), this.state.name());

                    Iterator<Request> it = passengers.iterator();
                    Request r;
                    while (it.hasNext()){
                        r = it.next();
                        if (r.stage_to == this.stage_num){
                            it.remove();
                        }
                    }

                    //если после выгруза пассажиров, остались те, кто еще едет в первоначальном направлении - открываем двери на этаже и двигаемся на этаж выше/ниже,
                    // иначе переходим в состояние Stop
                    if (this.passengers.size() != 0) {
                        this.passengers.addAll(assigned_building.getStage(this.stage_num).getNPeople(this.state, this.capacity - this.passengers.size()));
                        move();
                    }
                    if(this.passengers.size() == 0){
                        this.counter++;
                    }
                    this.tripsCounter++;

                }
                this.state = Pointers.STOP;
            }
            System.out.println("Empty trips: " + counter + " Amount of trips " + tripsCounter);
        }
    }
}