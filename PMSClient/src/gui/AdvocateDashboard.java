package gui;

import logic.prisoner.Prisoner;
import logic.advocate.Advocate;
import rmi.RemotePrisonService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.List;

public class AdvocateDashboard {

    private RemotePrisonService pmsService;
    private Advocate currentAdvocate;

    public AdvocateDashboard(Advocate advocate, RemotePrisonService pmsService) {
        this.currentAdvocate = advocate;
        this.pmsService = pmsService;
    }

    public void show(Stage stage) {
        stage.setTitle("Prison Management System - Advocate Dashboard");

        VBox root = new VBox();
        root.setSpacing(20);
        root.setPadding(new Insets(30, 30, 30, 30));
        root.setAlignment(Pos.TOP_CENTER);

        Label lblTitle = new Label("Prison Management System");
        lblTitle.setFont(Font.font("Tahoma", FontWeight.BOLD, 24));
        TableView<Prisoner> table = new TableView<>();

        TableColumn<Prisoner, Integer> colId = new TableColumn<>("Prisoner ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("prisonerId"));
        colId.setMinWidth(100);

        TableColumn<Prisoner, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colName.setMinWidth(150);

        TableColumn<Prisoner, String> colCrime = new TableColumn<>("Charge");
        colCrime.setCellValueFactory(new PropertyValueFactory<>("crime")); // FIXED: Changed from "offence"
        colCrime.setMinWidth(150);

        TableColumn<Prisoner, Integer> colSentence = new TableColumn<>("Sentence Duration (Months)");
        colSentence.setCellValueFactory(new PropertyValueFactory<>("sentenceMonths")); // FIXED: Changed from "sentenceYears"
        colSentence.setMinWidth(150);

        table.getColumns().addAll(colId, colName, colCrime, colSentence);
        table.setPrefHeight(300);

        try {

            List<Prisoner> prisoners = pmsService.getPrisonersForAdvocate(currentAdvocate.getAdvocateId());

            for (Prisoner p : prisoners) {
                currentAdvocate.loadPrisoner(p);
            }
        } catch (Exception ex) {
            System.err.println("RMI Error: " + ex.getMessage());
        }

        table.getItems().clear();
        table.getItems().addAll(currentAdvocate.getAssignedPrisoners());

        Button btnLogout = new Button("Log Out");
        btnLogout.setPrefSize(120, 35);
        btnLogout.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));
        btnLogout.setOnAction(e -> {
            Login login = new Login();
            login.start(stage);
        });

        root.getChildren().addAll(lblTitle, table, btnLogout);

        Scene scene = new Scene(root, 650, 500);

        if (getClass().getResource("/style/style.css") != null) {
            scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
        }

        stage.setScene(scene);
        stage.show();
    }

}
