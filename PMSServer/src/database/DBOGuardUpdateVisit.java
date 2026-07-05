package database;

public interface DBOGuardUpdateVisit {
    boolean updateVisitStatus(int visitId, String status, int staffId);
}