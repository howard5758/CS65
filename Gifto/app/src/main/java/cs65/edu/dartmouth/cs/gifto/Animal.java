package cs65.edu.dartmouth.cs.gifto;

import java.io.Serializable;

/**
 * Created by Oliver on 2/24/2018.
 *
 * Class to store information on a single animal
 * Preferably this would be passed to MySQLiteHelper in order to make the entries
 *
 * 2 possibilities for storing data:
 *      1: store animals one by one as they are seen and keep track of all animal data, seen and
 *          unseen, as a static variable somewhere, probably a list or struct, that also keeps track
 *          of if the animals have been seen
 *      2: store all animals in the SQLite database right off the bat, and the only data that is
 *          changed is the column saying if te cat has been seen or not
 *
 * haven't decided which I'm going to use; will discuss
 */

public class Animal implements Serializable {
    private String animalName;
    private int numVisits;
    private int rarity;
    private long persistence;
    private int present;

    Animal(){
        animalName = "";
        numVisits = 0;
        rarity = -1;
        persistence = -1;
        present = -1;
    }

    Animal(String animalName, int numVisits, int rarity, long persistence, int present) {
        this.animalName = animalName;
        this.numVisits = numVisits;
        this.rarity = rarity;
        this.persistence = persistence;
        this.present = present;
    }

    public String getAnimalName() {
        return animalName;
    }

    public void setAnimalName(String animalName) {
        this.animalName = animalName;
    }

    public int getNumVisits() {
        return numVisits;
    }

    public void setNumVisits(int numVisits) {
        this.numVisits = numVisits;
    }

    public int getRarity() {
        return rarity;
    }

    public void setRarity(int rarity) {
        this.rarity = rarity;
    }

    public long getPersistence() {
        return persistence;
    }

    public void setPersistence(long persistence) {
        this.persistence = persistence;
    }

    public int getPresent() {
        return present;
    }

    public void setPresent(int present) {
        this.present = present;
    }
}
