package gui;

import rmi.RemotePrisonService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import visitor.Visit;
import java.util.List;
import javafx.beans.value.ChangeListener;

public class GuardDashboard {

    private final int loggedInGuardId;
    private final String guardName;
    private RemotePrisonService pmsService;

    private ListView<String> visitListView;
    private ObservableList<String> visitObservableList;
    private List<Visit> currentVisitsList;

    private Label lblSelectedVisit;
    private Button btnApprove;
    private Button btnReject;
    private Label lblStatusMessage;
    private ChangeListener<Number> selectionListener;

    public GuardDashboard(int guardId, String guardName, RemotePrisonService pmsService) {
        this.loggedInGuardId = guardId;
        this.guardName = guardName;
        this.pmsService = pmsService;
    }

    public void start(Stage stage) {
        stage.setTitle("Prison Management System - Guard Dashboard");

        BorderPane mainLayout = new BorderPane();
        mainLayout.setId("main-layout");

        HBox topBar = new HBox();
        topBar.setId("top-bar");
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label lblWelcome = new Label("Welcome, Officer " + guardName + " (ID: " + loggedInGuardId + ")");
        lblWelcome.setId("welcome-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnLogout = new Button("Log Out");
        btnLogout.setId("logout-button");
        btnLogout.setOnAction(e -> {
            Login login = new Login();
            login.start(stage);
            System.out.println("Guard session ended safely. Logged out.");
        });

        topBar.getChildren().addAll(lblWelcome, spacer, btnLogout);
        mainLayout.setTop(topBar);

        VBox leftBox = new VBox();
        leftBox.setId("left-box");
        HBox.setHgrow(leftBox, Priority.ALWAYS);

        Label lblListHeader = new Label("Incoming & Historical Visit Requests:");
        lblListHeader.getStyleClass().add("section-header");

        visitListView = new ListView<>();
        visitObservableList = FXCollections.observableArrayList();
        visitListView.setItems(visitObservableList);
        visitListView.setId("visit-list-view");
        VBox.setVgrow(visitListView, Priority.ALWAYS);

        Button btnRefresh = new Button("🔄 Refresh Bookings");
        btnRefresh.setId("refresh-button");
        btnRefresh.setMaxWidth(Double.MAX_VALUE);
        btnRefresh.setOnAction(e -> populateVisitsList());

        leftBox.getChildren().addAll(lblListHeader, visitListView, btnRefresh);

        VBox rightBox = new VBox();
        rightBox.setId("right-box");
        rightBox.setAlignment(Pos.TOP_CENTER);

        Label lblActionsHeader = new Label("Review Selection");
        lblActionsHeader.getStyleClass().add("section-header");

        lblSelectedVisit = new Label("No visit selected.\nSelect a pending record from the left panel.");
        lblSelectedVisit.setId("selected-visit-label");
        lblSelectedVisit.setWrapText(true);
        lblSelectedVisit.setAlignment(Pos.CENTER);
        lblSelectedVisit.setMaxWidth(Double.MAX_VALUE);

        btnApprove = new Button("✔ Approve Visit");
        btnApprove.setId("approve-button");
        btnApprove.setMaxWidth(Double.MAX_VALUE);
        btnApprove.setDisable(true);
        btnApprove.setOnAction(e -> handleStatusUpdate("APPROVED"));

        btnReject = new Button("❌ Reject Visit");
        btnReject.setId("reject-button");
        btnReject.setMaxWidth(Double.MAX_VALUE);
        btnReject.setDisable(true);
        btnReject.setOnAction(e -> handleStatusUpdate("REJECTED"));

        lblStatusMessage = new Label();
        lblStatusMessage.setId("status-message-label");
        lblStatusMessage.setWrapText(true);

        rightBox.getChildren().addAll(lblActionsHeader, lblSelectedVisit, btnApprove, btnReject, lblStatusMessage);

        HBox splitLayout = new HBox();
        splitLayout.setId("split-layout");
        splitLayout.getChildren().addAll(leftBox, rightBox);
        mainLayout.setCenter(splitLayout);

        selectionListener = (observable, oldValue, newValue) -> {
            int index = newValue.intValue();
            if (index >= 0 && currentVisitsList != null && index < currentVisitsList.size()) {
                Visit selectedVisit = currentVisitsList.get(index);
                displaySelectedVisit(selectedVisit);
            } else {
                resetActionPanel();
            }
        };

        visitListView.getSelectionModel().selectedIndexProperty().addListener(selectionListener);

        populateVisitsList();

        Scene scene = new Scene(mainLayout, 900, 550);
        scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    private void populateVisitsList() {
        if (visitListView != null && selectionListener != null) {
            visitListView.getSelectionModel().selectedIndexProperty().removeListener(selectionListener);
        }

        visitObservableList.clear();
        try {
            currentVisitsList = pmsService.getAllVisitsForGuard();

            if (currentVisitsList == null || currentVisitsList.isEmpty()) {
                visitObservableList.add("No visit logs found in the database.");
                resetActionPanel();
                return;
            }

            for (Visit v : currentVisitsList) {
                String formatRow = String.format("ID: %02d | Date: %s @ %s | Prisoner: #%02d | Visitor: #%02d | [%s]",
                        v.getVisitId(), v.getDate(), v.getTime(), v.getPrisonerId(), v.getVisitorId(), v.getStatus().toUpperCase());
                visitObservableList.add(formatRow);
            }
        } catch (Exception ex) {
            System.err.println("Error rendering PostgreSQL data rows: " + ex.getMessage());
            visitObservableList.add("Error loading data from database connection.");
        } finally {
            if (visitListView != null && selectionListener != null) {
                visitListView.getSelectionModel().selectedIndexProperty().addListener(selectionListener);
            }
        }
    }

    private void displaySelectedVisit(Visit visit) {
        if (visit == null) {
            resetActionPanel();
            return;
        }

        String info = String.format("Visit Record Details:\n\n• Visit ID: %d\n• Schedule Date: %s\n• Schedule Time: %s\n• Target Prisoner ID: %d\n• Submitting Visitor ID: %d\n\nStatus Context: %s",
                visit.getVisitId(), visit.getDate(), visit.getTime(), visit.getPrisonerId(), visit.getVisitorId(), visit.getStatus().toUpperCase());
        lblSelectedVisit.setText(info);
        lblSelectedVisit.getStyleClass().remove("placeholder-text");
        lblSelectedVisit.getStyleClass().add("active-text");

        lblStatusMessage.getStyleClass().removeAll("status-pending", "status-finalized");

        if ("PENDING".equalsIgnoreCase(visit.getStatus())) {
            btnApprove.setDisable(false);
            btnReject.setDisable(false);
            lblStatusMessage.setText("Action Required: Awaiting decision...");
            lblStatusMessage.getStyleClass().add("status-pending");
        } else {
            btnApprove.setDisable(true);
            btnReject.setDisable(true);
            lblStatusMessage.setText("Finalized Record (Signed by Staff ID: " + visit.getStaffId() + ").");
            lblStatusMessage.getStyleClass().add("status-finalized");
        }
    }

    private void handleStatusUpdate(String targetStatus) {
        int selectedIndex = visitListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            return;
        }

        Visit selectedVisit = currentVisitsList.get(selectedIndex);

        try {
            boolean success = pmsService.updateVisitStatus(selectedVisit.getVisitId(), targetStatus, loggedInGuardId);

            lblStatusMessage.getStyleClass().removeAll("status-success", "status-error");

            if (success) {
                populateVisitsList();
                lblStatusMessage.setText("Success! Status updated to " + targetStatus);
                lblStatusMessage.getStyleClass().add("status-success");
            } else {
                lblStatusMessage.setText("Database Exception: Error processing update status write.");
                lblStatusMessage.getStyleClass().add("status-error");
            }
        } catch (Exception ex) {
            lblStatusMessage.setText("Network Error: " + ex.getMessage());
            lblStatusMessage.getStyleClass().add("status-error");
        }
    }

    private void resetActionPanel() {
        lblSelectedVisit.setText("No visit selected.\nSelect a pending record from the left panel.");
        lblSelectedVisit.getStyleClass().remove("active-text");
        lblSelectedVisit.getStyleClass().add("placeholder-text");
        btnApprove.setDisable(true);
        btnReject.setDisable(true);
        lblStatusMessage.setText("");
        lblStatusMessage.getStyleClass().removeAll("status-pending", "status-finalized", "status-success", "status-error");
    }

}
