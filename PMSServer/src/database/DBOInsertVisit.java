package database;
//visitor to book an new visit 
public interface DBOInsertVisit {
    public boolean bookNewVisit(int visitorId, int prisonerId, String date, String time);
}