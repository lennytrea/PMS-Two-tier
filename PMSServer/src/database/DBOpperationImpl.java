package database;

import model.Prisoner;
import visitor.Visit;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class DBOpperationImpl implements DBOLogin, AdvocateOperations, DBOInsertVisit,  DBOSelectVisits, DBOAdminPrisonerOperations, DBOSystemUserOperations,DBOGuardViewVisits, DBOGuardUpdateVisit {
    private DBConnection dbc = new DBConnection();

// advocate view their prisoners
    @Override
public List<Prisoner> getPrisonersForAdvocate(int advocateId) {
    List<Prisoner> list = new ArrayList<>();
    String query = "SELECT p.prisonerid, p.name, p.crime, p.sentencedurationmonths " +
                   "FROM prisoners p " +
                   "JOIN advocate_prisoner ap ON p.prisonerid = ap.prisonerid " +
                   "WHERE ap.advocateid = ?";
    try (PreparedStatement pst = dbc.con.prepareStatement(query)) {
        pst.setInt(1, advocateId);
        try (ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                // Your database natively stores months, which matches your refactored constructor slot!
                int totalMonths = rs.getInt("sentencedurationmonths");

                list.add(new Prisoner(
                    rs.getInt("prisonerid"),
                    rs.getString("name"),
                    rs.getString("crime"),
                    totalMonths
                ));
            }
        }
    } catch (SQLException e) {
        System.err.println("DB Error fetching advocate prisoners: " + e.getMessage());
    }
    return list;
}

    //visitor previous visits
    @Override
    public List<Visit> getVisitHistoryForVisitor(int visitorId) {
        List<Visit> list = new ArrayList<>();
        String query = "SELECT visitid, visitdate, visittime, prisonerid, visitorid, staffid, status " +
                       "FROM visits WHERE visitorid = ? ORDER BY visitid DESC";
        try (PreparedStatement pst = dbc.con.prepareStatement(query)) {
            pst.setInt(1, visitorId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.add(new Visit(
                        rs.getInt("visitid"),
                        rs.getString("visitdate"),
                        rs.getString("visittime"),
                        rs.getInt("prisonerid"),
                        rs.getInt("visitorid"),
                        rs.getInt("staffid"),
                        rs.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("DB Error fetching visit history: " + e.getMessage());
        }
        return list;
    }

    // visitor book visits db
    @Override
    public boolean bookNewVisit(int visitorId, int prisonerId, String date, String time) {
        String query = "INSERT INTO visits (visitdate, visittime, prisonerid, visitorid, staffid, status) " +
                       "VALUES (?, CAST(? AS time), ?, ?, NULL, 'PENDING')";
        try (PreparedStatement pst = dbc.con.prepareStatement(query)) {
            pst.setDate(1, Date.valueOf(date));
            pst.setString(2, time);
            pst.setInt(3, prisonerId);
            pst.setInt(4, visitorId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DB Error booking visit: " + e.getMessage());
            return false;
        }
    }
    @Override
    public Object authenticateUser(int userId, String password) {
        String query = "SELECT role, name FROM systemusers WHERE userid = ? AND password = ?";
        
        try (PreparedStatement pst = dbc.con.prepareStatement(query)) {
            pst.setInt(1, userId);
            pst.setString(2, password);
            
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    String databaseRole = rs.getString("role");
                    String realName = rs.getString("name"); 
                    
                    if ("VISITOR".equalsIgnoreCase(databaseRole)) {
                        int linkedPrisonerId = 1; // Default fallback
                        
                        // Visitor secondary target prisoner query
                        String lookupQuery = "SELECT prisonerid FROM visits WHERE visitorid = ? LIMIT 1";
                        try (PreparedStatement pstLookup = dbc.con.prepareStatement(lookupQuery)) {
                            pstLookup.setInt(1, userId);
                            try (ResultSet rsLookup = pstLookup.executeQuery()) {
                                if (rsLookup.next()) {
                                    linkedPrisonerId = rsLookup.getInt("prisonerid");
                                }
                            }
                        }
                        
                        return new visitor.Visitor(userId, realName, password, linkedPrisonerId);
                        
                    } else if ("ADVOCATE".equalsIgnoreCase(databaseRole)) {
                        // Instantiates and returns the exact advocate type
                        return new advocate.Advocate(userId, realName, password);
                    }
                    // Instantiates and returns the exact admin type
                    else if ("SYSTEM_ADMIN".equalsIgnoreCase(databaseRole)) {
                        return new systemsAdmin.SystemAdmin(userId, realName, password);
                    }
                    
                   else if ("GUARD".equalsIgnoreCase(databaseRole)) {
                        return new model.Guard(userId, realName, "OFFICER-ACTIVE");
}
                   
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Authentication Error: " + e.getMessage());
        }
        return null; // Denied access or exception caught
    }
    //admin functions
    // --- SYSTEM USERS CRUD

    @Override
    public java.util.List<String> viewAllSystemUsers() {
        java.util.List<String> usersList = new java.util.ArrayList<>();
        String query = "SELECT userid, name, role FROM systemusers ORDER BY userid ASC";
        
        try (PreparedStatement pst = dbc.con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                String userInfo = "ID: " + rs.getInt("userid") + 
                                  " | Name: " + rs.getString("name") + 
                                  " | Role: " + rs.getString("role");
                usersList.add(userInfo);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching system users: " + e.getMessage());
        }
        return usersList;
    }

    @Override
    public boolean addSystemUser(int userId, String name, String password, String role) {
        String query = "INSERT INTO systemusers (userid, name, password, role) VALUES (?, ?, ?, ?::user_role)";
        
        try (PreparedStatement pst = dbc.con.prepareStatement(query)) {
            pst.setInt(1, userId);
            pst.setString(2, name);
            pst.setString(3, password);
            pst.setString(4, role.toUpperCase()); // Keeps roles normalized
            
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting system user: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean alterSystemUser(int userId, String newName, String newPassword, String newRole) {
        String query = "UPDATE systemusers SET name = ?, password = ?, role = ?::user_role WHERE userid = ?";
        
        try (PreparedStatement pst = dbc.con.prepareStatement(query)) {
            pst.setString(1, newName);
            pst.setString(2, newPassword);
            pst.setString(3, newRole.toUpperCase());
            pst.setInt(4, userId);
            
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating system user: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteSystemUser(int userId) {
        String query = "DELETE FROM systemusers WHERE userid = ?";
        
        try (PreparedStatement pst = dbc.con.prepareStatement(query)) {
            pst.setInt(1, userId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting system user: " + e.getMessage());
            return false;
        }
    }
    
    
    
    // --- PRISONERS  CRUD
    @Override
    public java.util.List<String> viewAllPrisoners() {
        java.util.List<String> prisonersList = new java.util.ArrayList<>();
        String query = "SELECT prisonerid, name, crime, sentencedurationmonths FROM prisoners ORDER BY prisonerid ASC";
        
        try (PreparedStatement pst = dbc.con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                String prisonerInfo = "ID: " + rs.getInt("prisonerid") + 
                                      " | Name: " + rs.getString("name") + 
                                      " | Crime: " + rs.getString("crime") + 
                                      " | Sentence: " + rs.getInt("sentencedurationmonths") + " Months";
                prisonersList.add(prisonerInfo);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching prisoners list: " + e.getMessage());
        }
        return prisonersList;
    }

    @Override
    public boolean addPrisoner(int prisonerId, String name, String crime, int sentenceMonths) {
        String query = "INSERT INTO prisoners (prisonerid, name, dateofbirth, dateofadmittance, sentencedurationmonths, crime) " +
                       "VALUES (?, ?, '1995-01-01', CURRENT_DATE, ?, ?)";
        
        try (PreparedStatement pst = dbc.con.prepareStatement(query)) {
            pst.setInt(1, prisonerId);
            pst.setString(2, name);
            pst.setInt(3, sentenceMonths);
            pst.setString(4, crime);
            
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting prisoner record: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean alterPrisoner(int prisonerId, String newName, String newCrime, int newSentenceMonths) {
        String query = "UPDATE prisoners SET name = ?, crime = ?, sentencedurationmonths = ? WHERE prisonerid = ?";
        
        try (PreparedStatement pst = dbc.con.prepareStatement(query)) {
            pst.setString(1, newName);
            pst.setString(2, newCrime);
            pst.setInt(3, newSentenceMonths);
            pst.setInt(4, prisonerId);
            
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error altering prisoner record: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean deletePrisoner(int prisonerId) {
        String query = "DELETE FROM prisoners WHERE prisonerid = ?";
        
        try (PreparedStatement pst = dbc.con.prepareStatement(query)) {
            pst.setInt(1, prisonerId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting prisoner record: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public List<Visit> getAllVisitsForGuard() {
        List<Visit> list = new ArrayList<>();
        // Fetches all visits ordered by status so PENDING ones appear prominently
        String query = "SELECT visitid, visitdate, visittime, prisonerid, visitorid, staffid, status " +
                       "FROM visits ORDER BY CASE WHEN status = 'PENDING' THEN 1 ELSE 2 END, visitdate DESC";
        try (PreparedStatement pst = dbc.con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                list.add(new Visit(
                    rs.getInt("visitid"),
                    rs.getString("visitdate"),
                    rs.getString("visittime"),
                    rs.getInt("prisonerid"),
                    rs.getInt("visitorid"),
                    rs.getInt("staffid"), // Will be 0 if NULL in database
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("DB Error fetching guard visits: " + e.getMessage());
        }
        return list;
    }

    @Override
    public boolean updateVisitStatus(int visitId, String status, int staffId) {
        String query = "UPDATE visits SET status = ?, staffid = ? WHERE visitid = ?";
        try (PreparedStatement pst = dbc.con.prepareStatement(query)) {
            pst.setString(1, status.toUpperCase());
            pst.setInt(2, staffId);
            pst.setInt(3, visitId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DB Error updating visit status: " + e.getMessage());
            return false;
        }
    }
}