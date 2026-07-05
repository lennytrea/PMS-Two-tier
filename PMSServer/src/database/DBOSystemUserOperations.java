package database;

import java.util.List;

public interface DBOSystemUserOperations {
    // --- System Users Management Methods ---
    
    // View all system users
    List<String> viewAllSystemUsers();
    
    // Add a new system user 
    boolean addSystemUser(int userId, String name, String password, String role);
    
    // Alter/Update an existing system user's details
    boolean alterSystemUser(int userId, String newName, String newPassword, String newRole);
    
    // Delete a system user from the database
    boolean deleteSystemUser(int userId);

    
}