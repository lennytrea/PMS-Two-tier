package database;

import java.util.List;
import model.Prisoner;

public interface AdvocateOperations {
    public List<Prisoner> getPrisonersForAdvocate(int advocateId);
}