package database;

import visitor.Visit;
import java.util.List;

public interface DBOGuardViewVisits {
    List<Visit> getAllVisitsForGuard();
}