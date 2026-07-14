package gui;

import logic.visitor.Visitor;
import logic.visitor.Visit;
import rmi.RemotePrisonService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.List;

public class VisitorDashboard {
// instantiating opperations object to perform opperations 
private RemotePrisonService pmsService; 
private Visitor currentVisitor;
private TableView<Visit> table = new TableView<>();
private Label lblFeedback = new Label();

    //captures the session details passed forward from the login screen and creates an advocate object
public VisitorDashboard(Visitor visitor, RemotePrisonService pmsService) {
    this.currentVisitor = visitor;
    this.pmsService = pmsService;
}

public void show(Stage stage) {
    stage.setTitle("Prison Management System - Visitor Dashboard");

    //top-down vertical column layout
    VBox mainRoot = new VBox(25);
    mainRoot.setPadding(new Insets(30, 30, 30, 30));
    mainRoot.setAlignment(Pos.TOP_CENTER);

    Label lblTitle = new Label("Welcome, " + currentVisitor.getName());
    lblTitle.setFont(Font.font("Tahoma", FontWeight.BOLD, 22));

    Label lblSection1 = new Label("Your Booking Records:");
    lblSection1.setFont(Font.font("Tahoma", FontWeight.SEMI_BOLD, 14));
    lblSection1.setAlignment(Pos.CENTER_LEFT);

    //maps row columns directly to your database fields.
    TableColumn<Visit, Integer> colId = new TableColumn<>("Visit ID");
    colId.setCellValueFactory(new PropertyValueFactory<>("visitId"));
    colId.setMinWidth(70);

    TableColumn<Visit, String> colDate = new TableColumn<>("Scheduled Date");
    colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
    colDate.setMinWidth(110);

    TableColumn<Visit, String> colTime = new TableColumn<>("Time");
    colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
    colTime.setMinWidth(90);

    TableColumn<Visit, Integer> colPrisoner = new TableColumn<>("Prisoner ID");
    colPrisoner.setCellValueFactory(new PropertyValueFactory<>("prisonerId"));
    colPrisoner.setMinWidth(100);

    TableColumn<Visit, String> colStatus = new TableColumn<>("Status");
    colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    colStatus.setMinWidth(110);

    table.getColumns().addAll(colId, colDate, colTime, colPrisoner, colStatus);
    table.setPrefHeight(180);

    // Populate Table View with data
    refreshTableData();

    Label lblSection2 = new Label("Request a New Visit Window:");
    lblSection2.setFont(Font.font("Tahoma", FontWeight.SEMI_BOLD, 14));

    //groups input elements tightly.
    GridPane formGrid = new GridPane();
    formGrid.setHgap(10);
    formGrid.setVgap(12);
    formGrid.setAlignment(Pos.CENTER);

    Label lblDateInput = new Label("Date (YYYY-MM-DD):");
    TextField txtDate = new TextField();
    txtDate.setPromptText("e.g. 2026-06-15");

    Label lblTimeInput = new Label("Time (HH:MM:SS):");
    TextField txtTime = new TextField();
    txtTime.setPromptText("e.g. 14:30:00");

    Button btnSubmit = new Button("Submit Request");
    btnSubmit.setFont(Font.font("Tahoma", FontWeight.BOLD, 12));

    formGrid.add(lblDateInput, 0, 0);
    formGrid.add(txtDate, 1, 0);
    formGrid.add(lblTimeInput, 0, 1);
    formGrid.add(txtTime, 1, 1);
    formGrid.add(btnSubmit, 1, 2);

    lblFeedback.setFont(Font.font("Arial", FontWeight.MEDIUM, 14));

    // subbmiting request btn 
    btnSubmit.setOnAction(e -> {
        String dateVal = txtDate.getText().trim();
        String timeVal = txtTime.getText().trim();

        //event handler monitorin data submission logic, 
        if (dateVal.isEmpty() || timeVal.isEmpty()) {
            lblFeedback.setTextFill(Color.RED);
            lblFeedback.setText("Error: Input fields cannot be empty!");
            return;
        }

        try {
            // book visitor using dbopperations
            boolean success = pmsService.bookNewVisit(
                currentVisitor.getVisitorId(), 
                currentVisitor.getTargetPrisonerId(), 
                dateVal, 
                timeVal
            );

            if (success) {
                lblFeedback.setTextFill(Color.GREEN);
                lblFeedback.setText("Success: Visit request filed under PENDING status.");
                txtDate.clear();
                txtTime.clear();
                refreshTableData(); // Re-sync ui
            } else {
                lblFeedback.setTextFill(Color.RED);
                lblFeedback.setText("Error: Failed to save booking details.");
            }
        } catch (Exception ex) {
            lblFeedback.setTextFill(Color.RED);
            lblFeedback.setText("Connection Error: " + ex.getMessage());
        }
    });

    // log out btn 
    Button btnLogout = new Button("Log Out");
    btnLogout.setOnAction(e -> {
        Login login = new Login();
        login.start(stage);
    });

    HBox bottomControls = new HBox(15, btnLogout, lblFeedback);
    bottomControls.setAlignment(Pos.CENTER_LEFT);

    mainRoot.getChildren().addAll(lblTitle, lblSection1, table, lblSection2, formGrid, bottomControls);

    Scene scene = new Scene(mainRoot, 600, 520);
    scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
    stage.setScene(scene);
    stage.show();
}

// pull history using the ui 
private void refreshTableData() {
    table.getItems().clear();
    try {
        List<Visit> list = pmsService.getVisitHistoryForVisitor(currentVisitor.getVisitorId());
        table.getItems().addAll(list);
    } catch (Exception ex) {
        lblFeedback.setTextFill(Color.RED);
        lblFeedback.setText("Failed to load records from server.");
    }
}
}