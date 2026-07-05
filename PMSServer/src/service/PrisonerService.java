package service;

import model.Prisoner;
import java.util.ArrayList;

public class PrisonerService {

    private ArrayList<Prisoner> prisoners = new ArrayList<>();

    public void addPrisoner(Prisoner prisoner) {
        prisoners.add(prisoner);
        System.out.println("Prisoner added successfully: " + prisoner.getFullName());
    }

    public void viewAllPrisoners() {
    if (prisoners.isEmpty()) {
        System.out.println("No prisoners found.");
    } else {
        for (Prisoner prisoner : prisoners) {
            System.out.println("----------------------");
            System.out.println("ID: " + prisoner.getPrisonerId());
            System.out.println("Name: " + prisoner.getFullName());
            System.out.println("Crime: " + prisoner.getCrime());
            System.out.println("Sentence: " + prisoner.getSentenceMonths() + " Months");
        }
    }
}

public void updatePrisonerDetails(int prisonerId, String newCrime, int newSentenceMonths) {
    for (Prisoner prisoner : prisoners) {
        if (prisoner.getPrisonerId() == prisonerId) {
            prisoner.setCrime(newCrime);
            prisoner.setSentenceMonths(newSentenceMonths);
            System.out.println("Prisoner details updated successfully.");
            return;
        }
    }

    System.out.println("Prisoner not found.");
}
}