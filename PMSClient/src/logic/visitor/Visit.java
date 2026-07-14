package logic.visitor;

import java.io.Serializable;

public class Visit implements Serializable { 
    private static final long serialVersionUID = 1L;
    private int visitId;     
    private String date;     
    private String time;     
    private int prisonerId;  
    private int visitorId;   
    private int staffId;      
    private String status;   

    // Constructor for when a Visitor creates a NEW request
    public Visit(String date, String time, int prisonerId, int visitorId) {
        this.date = date;
        this.time = time;
        this.prisonerId = prisonerId;
        this.visitorId = visitorId;
        this.status = "PENDING"; 
        this.staffId = 0; // no guard has picked it up yet
    }

    // Constructor for when existing Visit out of PostgreSQL
    public Visit(int visitId, String date, String time, int prisonerId, int visitorId, int staffId, String status) {
        this.visitId = visitId;
        this.date = date;
        this.time = time;
        this.prisonerId = prisonerId;
        this.visitorId = visitorId;
        this.staffId = staffId;
        this.status = status;
    }

    // Getters and Setters
    public int getVisitId() { return visitId; }
    public void setVisitId(int visitId) { this.visitId = visitId; }

    public int getStaffId() { return staffId; }
    public void setStaffId(int staffId) { this.staffId = staffId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getPrisonerId() { return prisonerId; }
    public int getVisitorId() { return visitorId; }
    
    public String getDate() {return date;}
    public String getTime() {return time;}
}