package com.company.Runner;

import com.company.Building.Building;
import com.company.Elevator.ElevatorUIData;
import com.company.Building.RequestsGenerator;
import com.company.Elevator.Lift;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.Map;

public class Main extends Application {
    int num_stages=5;
    int num_lifts=1;
    int request_generator_speed=1000;
    int num_requests_per_time=1;
    int lift_capacity=30;
    int textField1Parsed;
    int textField2Parsed;
    int textField3Parsed;
    int textField4Parsed;
    TextField textFieldFlours = new TextField();
    TextField textFieldLifts = new TextField();
    TextField textFieldPeople = new TextField();
    TextField textFieldCapacity = new TextField();
    int counter;
    int emptyCounter;

    ArrayList<Slider> sliders = new ArrayList<>();
    ArrayList<Label> lift_labels = new ArrayList<>();
    ArrayList<Label> stages_labels = new ArrayList<>();
    Building building;
    Lift lift = new Lift();
    @Override
    public void init() throws Exception{
            this.building = new Building(num_stages, num_lifts, lift_capacity);
    }
    @Override
    public void start(final Stage primaryStage) {

        Button inputParamButton = new Button();
        inputParamButton.setText("Input param");
        Label labelForOutputFlours = new Label();
        Label labelForOutputLifts = new Label();
        Label labelForOutputPeople = new Label();
        Label labelForOutputCapacity = new Label();
        Label labelForFlours = new Label();
        Label labelForLifts = new Label();
        Label labelForPeople = new Label();
        Label labelForCapacity = new Label();

        textFieldFlours.setPrefColumnCount(5);
        textFieldLifts.setPrefColumnCount(5);
        textFieldPeople.setPrefColumnCount(5);
        textFieldCapacity.setPrefColumnCount(5);
        Button buttonFlours = new Button("OK");
        Button buttonLifts = new Button("OK");
        Button buttonPeople = new Button("OK");
        Button buttonCapacity = new Button("OK");
        labelForFlours.setText("Amount of flours");
        labelForLifts.setText("Amount of lifts");
        labelForPeople.setText("Amount of people");
        labelForCapacity.setText("Lift capacity");
        buttonFlours.setOnAction(eventParsed -> labelForOutputFlours.setText("Input: " + textFieldFlours.getText()));
        buttonLifts.setOnAction(eventParsed -> labelForOutputLifts.setText("Input: " + textFieldLifts.getText()));
        buttonPeople.setOnAction(eventParsed -> labelForOutputPeople.setText("Input: " + textFieldPeople.getText()));
        buttonCapacity.setOnAction(eventParsed -> labelForOutputCapacity.setText("Input: " + textFieldCapacity.getText()));
        FlowPane root = new FlowPane(Orientation.VERTICAL, 10, 10, labelForFlours, textFieldFlours, buttonFlours, labelForOutputFlours,
                labelForLifts, textFieldLifts,  buttonLifts, labelForOutputLifts,
                labelForPeople, textFieldPeople, buttonPeople, labelForOutputPeople,
                labelForCapacity, textFieldCapacity, buttonCapacity, labelForOutputCapacity);
        root.getChildren().add(inputParamButton);
        Scene scene = new Scene(root, 500, 300);
        primaryStage.setTitle("Lift");
        primaryStage.setScene(scene);
        primaryStage.show();

        inputParamButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                building.launchLifts();
                Thread rg = new Thread(new RequestsGenerator(building, request_generator_speed, num_requests_per_time));
                rg.start();
                HBox root = new HBox();
                Scene scene = new Scene(root);
                VBox stages_requests_info_container = new VBox();
                VBox info = new VBox();
                //TextField textField1 = new TextField();
                //textField1.setPrefColumnCount(5);
                textField1Parsed = Integer.parseInt(textFieldFlours.getText());
                textField2Parsed = Integer.parseInt(textFieldLifts.getText());
                textField3Parsed = Integer.parseInt(textFieldPeople.getText());
                textField4Parsed = Integer.parseInt(textFieldCapacity.getText());

                root.getChildren().addAll(stages_requests_info_container); //textField1);
                for (int i = num_stages + 1; i >= 0; i--) {
                    String s = "Количество заявок на этаже №" + i + ":  " + 0;
                    Label stage_label = new Label(s);
                    stages_labels.add(stage_label);
                    stage_label.setMinWidth(250);
                    stage_label.setFont(new Font(14));
                    stages_requests_info_container.getChildren().add(stage_label);
                }

                for (int i = 0; i < num_lifts; i++) {

                    Slider slider = new Slider();
                    sliders.add(slider);
                    root.getChildren().add(slider);
                    slider.setMin(0);
                    slider.setMax(textField1Parsed-1);
                    slider.setOrientation(Orientation.VERTICAL);
                    slider.setShowTickLabels(true);
                    slider.setMajorTickUnit(1);
                    slider.setBlockIncrement(1);
                    slider.setSnapToTicks(true);
                    slider.setMinHeight(500);
                    slider.applyCss();
                    slider.layout();

                    Pane thumb = (Pane) slider.lookup(".thumb");
                    Label label = new Label();
                    lift_labels.add(label);

                    thumb.getChildren().add(label);

                }

                Button stopButton = new Button("STOP");

                //stopButton.setOnAction(event1 -> stages_requests_info_container.getChildren().addAll(stage_label, stage_label2));
                stages_requests_info_container.getChildren().add(stopButton);

                Thread thread = new Thread(() -> {
                    try {

                        Runnable updater = this::updateUi;
                        while (true) {
                            Thread.sleep(1000);
                            Platform.runLater(updater);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                });
                thread.start();
                primaryStage.setScene(scene);
                primaryStage.show();

                stopButton.setOnAction(new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent event) {
                        HBox root2 = new HBox();
                        Scene scene2 = new Scene(root2, 250, 120);
                        VBox stats = new VBox();
                        root2.getChildren().addAll(stats);
                        String stopMessage = "Amount of Trips: ";
                        String stopMessage2 = "Amount of empty Trips: ";
                        Label stage_label = new Label(stopMessage + " " + emptyCounter);
                        Label stage_label2 = new Label(stopMessage2 + " " + counter);
                        stages_labels.add(stage_label);
                        stages_labels.add(stage_label2);
                        stage_label.setMinWidth(250);
                        stage_label.setFont(new Font(14));
                        stage_label2.setFont(new Font(14));
                        stats.getChildren().addAll(stage_label, stage_label2);
                        primaryStage.setScene(scene2);
                        primaryStage.show();
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            public void updateUi(){
                for (int i = 0; i < num_stages; i++){
                    String s = "Количество заявок на этаже №" + i + ":  " + building.getStage(i).waitersNum();
                    stages_labels.get(num_stages - i - 1).textProperty().setValue(s);
                }
                for (int i = 0; i < num_lifts; i++){
                    ElevatorUIData data = building.lifts_array.get(i).getReferencesForUI();
                    sliders.get(i).setValue(data.stage_num);
                    counter = data.amountOfTrips;

                    String trips = "Количество поездок = " + counter;

                    lift_labels.get(i).setText(String.valueOf(data.passenger_num));
                    stages_labels.get(num_lifts + 4).textProperty().setValue(trips);

                }
                for (int i = 0; i < num_lifts; i++){
                    ElevatorUIData data = building.lifts_array.get(i).getReferencesForUI();
                    emptyCounter = data.amountOfEmptyTrips;
                    String emptyTrips = "Количество <пустых> поездок = " + emptyCounter;
                    stages_labels.get(num_lifts + 5).textProperty().setValue(emptyTrips);
                }
            }
        });

    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        Application.launch();

    }

}