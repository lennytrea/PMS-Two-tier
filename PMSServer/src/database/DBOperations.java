package database;

import model.Prisoner;
import visitor.Visit;
import java.util.List;


public class DBOperations implements DBOLogin, AdvocateOperations, DBOInsertVisit, DBOSelectVisits, DBOAdminPrisonerOperations, DBOSystemUserOperations, DBOGuardViewVisits, DBOGuardUpdateVisit {

    private final DBOpperationImpl implementation;

    public DBOperations() {
        this.implementation = new DBOpperationImpl();
    }

    // implements visitor booking  interface logic
   @Override
    public boolean bookNewVisit(int visitorId, int prisonerId, String date, String time) {
        return implementation.bookNewVisit(visitorId, prisonerId, date, time);
    }

    // implements select all operations from interface
   @Override
    public List<Visit> getVisitHistoryForVisitor(int visitorId) {
        return implementation.getVisitHistoryForVisitor(visitorId);
    }

    // interface impl that getting prisoners logic from other classes 
   @Override
    public List<Prisoner> getPrisonersForAdvocate(int advocateId) {
        return implementation.getPrisonersForAdvocate(advocateId);
    }  
 // interface impl authenticate logic from impl
    @Override
    public Object authenticateUser(int userId, String password) {
        return implementation.authenticateUser(userId, password);
    }
     // interface impl for admin view system users logic 
    @Override
    public List<String> viewAllSystemUsers() {
        return implementation.viewAllSystemUsers();
    }
 // interface impl for addmin for adding a user logic 
    @Override
    public boolean addSystemUser(int userId, String name, String password, String role) {
        return implementation.addSystemUser(userId, name, password, role);
    }
 // interface impl for updtating user logic 
    @Override
    public boolean alterSystemUser(int userId, String newName, String newPassword, String newRole) {
        return implementation.alterSystemUser(userId, newName, newPassword, newRole);
    }
 // interface impl for deleting user logic 
    @Override
    public boolean deleteSystemUser(int userId) {
        return implementation.deleteSystemUser(userId);
    }
 // interface impl for admin viewing logic 
    @Override
    public List<String> viewAllPrisoners() {
        return implementation.viewAllPrisoners();
    }
 // interface impl for admin adding prisoners 
    @Override
    public boolean addPrisoner(int prisonerId, String name, String crime, int sentenceMonths) {
        return implementation.addPrisoner(prisonerId, name, crime, sentenceMonths);
    }

    @Override
    public boolean alterPrisoner(int prisonerId, String newName, String newCrime, int newSentenceMonths) {
        return implementation.alterPrisoner(prisonerId, newName, newCrime, newSentenceMonths);
    }

    @Override
    public boolean deletePrisoner(int prisonerId) {
        return implementation.deletePrisoner(prisonerId);
    }
    
    @Override
    public List<Visit> getAllVisitsForGuard() {
        return implementation.getAllVisitsForGuard();
    }
 // interface impl guard for viewing all visitors logic 
    @Override
    public boolean updateVisitStatus(int visitId, String status, int staffId) {
        return implementation.updateVisitStatus(visitId, status, staffId);
    }
}