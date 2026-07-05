package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.Prisoner;
import visitor.Visit;

public interface RemotePrisonService extends Remote {
    
    public boolean bookNewVisit(int visitorId, int prisonerId, String date, String time)throws RemoteException;
    
    public List<Visit> getVisitHistoryForVisitor(int visitorId)throws RemoteException;
    
    public List<Prisoner> getPrisonersForAdvocate(int advocateId)throws RemoteException;
    
    public Object authenticateUser(int userId, String password)throws RemoteException;
    
    public List<String> viewAllSystemUsers()throws RemoteException;
    
    public boolean addSystemUser(int userId, String name, String password, String role)throws RemoteException;
    
    public boolean alterSystemUser(int userId, String newName, String newPassword, String newRole)throws RemoteException;
    
    public boolean deleteSystemUser(int userId)throws RemoteException;
    
    public List<String> viewAllPrisoners()throws RemoteException;
    
    public boolean addPrisoner(int prisonerId, String name, String crime, int sentenceMonths)throws RemoteException;
    
    public boolean alterPrisoner(int prisonerId, String newName, String newCrime, int newSentenceMonths)throws RemoteException;
    
    public boolean deletePrisoner(int prisonerId)throws RemoteException;
    
    public List<Visit> getAllVisitsForGuard()throws RemoteException;
    
    public boolean updateVisitStatus(int visitId, String status, int staffId)throws RemoteException;
}