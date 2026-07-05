package database;

import java.util.List;

public interface DBOAdminPrisonerOperations {
    // --- Prisoners Management Methods ---
    
    // View all prisoners currently tracked in the system
    List<String> viewAllPrisoners();
    
    // Add a brand new prisoner record
    boolean addPrisoner(int prisonerId, String name, String crime, int sentenceMonths);
    
    // Alter/Update an existing prisoner record
    boolean alterPrisoner(int prisonerId, String newName, String newCrime, int newSentenceMonths);
    
    // Delete a prisoner record permanently from db
    boolean deletePrisoner(int prisonerId);
}