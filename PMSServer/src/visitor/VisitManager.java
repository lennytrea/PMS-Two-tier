package visitor;

public interface VisitManager {//logic for creating a visit
    public static Visit bookVisit(int visitorId, int prisonerId, String date, String time) {
        
        if (date.isEmpty() || time.isEmpty()) {
            System.out.println("Error: Date and time cannot be empty.");
            return null;
        }
        
        Visit newVisit = new Visit(date, time, prisonerId , visitorId);
        System.out.println("Visit successfully created for Prisoner ID: " + prisonerId);
        
        return newVisit;
    }
    public static void viewVisitDetails(Visit visit) {
        if (visit == null) {
            System.out.println("No visit record found.");
            return;
        }

        System.out.println("~~~ VISIT DETAILS ~~~");
        System.out.println("Date: " + visit.getDate());
        System.out.println("Time: " + visit.getTime());
        System.out.println("Prisoner ID: " + visit.getPrisonerId());
        System.out.println("Status: [" + visit.getStatus() + "]"); 
        
        if (visit.getStaffId() != 0) {
            System.out.println("Reviewed By Staff ID: " + visit.getStaffId());
        } else {
            System.out.println("Awaiting Guard Review.");
        }
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~");
    }
}

