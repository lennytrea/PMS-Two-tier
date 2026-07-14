package server; 

import rmi.RemotePrisonService; 
import database.DBOpperationImpl;
import logic.prisoner.Prisoner;
import logic.visitor.Visit;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class PrisonServiceProvider extends UnicastRemoteObject implements RemotePrisonService {

    
    private final DBOpperationImpl implementation;

    public PrisonServiceProvider() throws RemoteException {
        super(); 
        this.implementation = new DBOpperationImpl(); 
    }

    // --- VISITOR OPERATIONS ---
    @Override
    public boolean bookNewVisit(int visitorId, int prisonerId, String date, String time) throws RemoteException {
        return implementation.bookNewVisit(visitorId, prisonerId, date, time);
    }

    @Override
    public List<Visit> getVisitHistoryForVisitor(int visitorId) throws RemoteException {
        return implementation.getVisitHistoryForVisitor(visitorId);
    }

    // --- ADVOCATE OPERATIONS ---
    @Override
    public List<Prisoner> getPrisonersForAdvocate(int advocateId) throws RemoteException {
        return implementation.getPrisonersForAdvocate(advocateId);
    }  

    // --- AUTHENTICATION ---
    @Override
    public Object authenticateUser(int userId, String password) throws RemoteException {
        return implementation.authenticateUser(userId, password);
    }

    // --- SYSTEM ADMIN USERS CRUD ---
    @Override
    public List<String> viewAllSystemUsers() throws RemoteException {
        return implementation.viewAllSystemUsers();
    }

    @Override
    public boolean addSystemUser(int userId, String name, String password, String role) throws RemoteException {
        return implementation.addSystemUser(userId, name, password, role);
    }

    @Override
    public boolean alterSystemUser(int userId, String newName, String newPassword, String newRole) throws RemoteException {
        return implementation.alterSystemUser(userId, newName, newPassword, newRole);
    }

    @Override
    public boolean deleteSystemUser(int userId) throws RemoteException {
        return implementation.deleteSystemUser(userId);
    }

    // --- SYSTEM ADMIN PRISONERS CRUD ---
    @Override
    public List<String> viewAllPrisoners() throws RemoteException {
        return implementation.viewAllPrisoners();
    }

    @Override
    public boolean addPrisoner(int prisonerId, String name, String crime, int sentenceMonths) throws RemoteException {
        return implementation.addPrisoner(prisonerId, name, crime, sentenceMonths);
    }

    @Override
    public boolean alterPrisoner(int prisonerId, String newName, String newCrime, int newSentenceMonths) throws RemoteException {
        return implementation.alterPrisoner(prisonerId, newName, newCrime, newSentenceMonths);
    }

    @Override
    public boolean deletePrisoner(int prisonerId) throws RemoteException {
        return implementation.deletePrisoner(prisonerId);
    }

    // --- GUARD OPERATIONS ---
    @Override
    public List<Visit> getAllVisitsForGuard() throws RemoteException {
        return implementation.getAllVisitsForGuard();
    }

    @Override
    public boolean updateVisitStatus(int visitId, String status, int staffId) throws RemoteException {
        return implementation.updateVisitStatus(visitId, status, staffId);
    }
}