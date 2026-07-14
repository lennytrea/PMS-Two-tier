package database;

import java.util.List;
import logic.prisoner.Prisoner;

public interface AdvocateOperations {
    public List<Prisoner> getPrisonersForAdvocate(int advocateId);
}