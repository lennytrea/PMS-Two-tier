package database;

import java.util.List;
import visitor.Visit;

public interface DBOSelectVisits {
   public List<Visit> getVisitHistoryForVisitor(int visitorId);
}