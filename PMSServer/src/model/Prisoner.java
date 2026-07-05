package model;

import java.io.Serializable;

public class Prisoner implements Serializable { 
    private static final long serialVersionUID = 1L;
    private int prisonerId;
    private String fullName;
    private String crime;
    private int sentenceMonths;

    public Prisoner(int prisonerId, String fullName, String crime, int sentenceMonths) {
        this.prisonerId = prisonerId;
        this.fullName = fullName;
        this.crime = crime;
        this.sentenceMonths = sentenceMonths;
    }

    public int getPrisonerId() { return prisonerId; }
    public String getFullName() { return fullName; }
    public String getCrime() { return crime; }
    public int getSentenceMonths() { return sentenceMonths; }

    public void setCrime(String crime) { this.crime = crime; }
    public void setSentenceMonths(int sentenceMonths) { this.sentenceMonths = sentenceMonths; }
}