package gui;

import rmi.RemotePrisonService; 
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import static javafx.application.Application.launch;

public class Login extends Application {
    private RemotePrisonService pmsService; 

    //handle the RMI lookup
    private boolean connectToServer() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            pmsService = (RemotePrisonService) registry.lookup("PrisonService");
            System.out.println("Connected to RMI Server.");
            return true;
        } catch (Exception e) {
            System.err.println("Client Error: Could not find RMI Server.");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        // 3. Connect to server as soon as the app starts
        boolean isConnected = connectToServer();

        primaryStage.setTitle("Prison Management System - Login");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(20);
        grid.setPadding(new Insets(40, 40, 40, 40));

        Label sceneTitle = new Label("Prison Management System Login");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.BOLD, 30));
        grid.add(sceneTitle, 0, 0, 2, 1);

        Label lblUserId = new Label("User ID:");
        grid.add(lblUserId, 0, 1);
        TextField txtUserId = new TextField();
        grid.add(txtUserId, 1, 1);

        Label lblPassword = new Label("Password:");
        grid.add(lblPassword, 0, 2);
        PasswordField txtPassword = new PasswordField();
        grid.add(txtPassword, 1, 2);

        Button btnLogin = new Button("Sign In");
        btnLogin.setMaxWidth(Double.MAX_VALUE);
        grid.add(btnLogin, 1, 3);

        Label lblMessage = new Label();
        lblMessage.setFont(Font.font("Arial", FontWeight.MEDIUM, 16));
        grid.add(lblMessage, 1, 4);

        // 4. Disable login if server is down
        if (!isConnected) {
            lblMessage.setTextFill(Color.RED);
            lblMessage.setText("OFFLINE: Server not found.");
            btnLogin.setDisable(true);
        }

        btnLogin.setOnAction(e -> {
            String inputId = txtUserId.getText().trim();
            String inputPassword = txtPassword.getText().trim();

            if (inputId.isEmpty() || inputPassword.isEmpty()) {
                lblMessage.setTextFill(Color.RED);
                lblMessage.setText("Error: Fields cannot be blank!");
                return;
            }
            
            try {
                int authenticatedId = Integer.parseInt(inputId);
                
                // CALL THE REMOTE SERVICE across the network!
                Object userSession = pmsService.authenticateUser(authenticatedId, inputPassword);
                
                if (userSession != null) {
                    lblMessage.setTextFill(Color.GREEN);
                    lblMessage.setText("Success! Loading Dashboard...");
                    
                    // 6. ROUTING LOGIC
                    if (userSession instanceof visitor.Visitor) {
                        // Pass the pmsService to next screen 
                        VisitorDashboard dashboard = new VisitorDashboard((visitor.Visitor) userSession, pmsService);
                        dashboard.show(primaryStage);
                    } 
                    else if (userSession instanceof advocate.Advocate) {
                        AdvocateDashboard dashboard = new AdvocateDashboard((advocate.Advocate) userSession, pmsService);
                        dashboard.show(primaryStage);
                    }
                    else if (userSession instanceof systemsAdmin.SystemAdmin) {
                        AdminDashboard dashboard = new AdminDashboard((systemsAdmin.SystemAdmin) userSession, pmsService);
                        dashboard.show(primaryStage);
                    }
                    else if (userSession instanceof model.Guard) {
                        model.Guard g = (model.Guard) userSession;
                        GuardDashboard gd = new GuardDashboard(g.getGuardId(), g.getFullName(), pmsService);
                        gd.start(primaryStage);
                    }
                } else {
                    lblMessage.setTextFill(Color.RED);
                    lblMessage.setText("Access Denied: Invalid ID or Password.");
                }
                
            } catch (NumberFormatException ex) {
                lblMessage.setTextFill(Color.RED);
                lblMessage.setText("Format Error: User ID must be a number!");
            } catch (Exception ex) {
                lblMessage.setTextFill(Color.RED);
                lblMessage.setText("Network Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        Scene scene = new Scene(grid, 600, 500);
        scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}