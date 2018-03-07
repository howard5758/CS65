package cs65.edu.dartmouth.cs.gifto;

import java.io.Serializable;

/**
 * Created by Oliver on 2/24/2018.
 *
 * Class to store information on a single animal
 * Preferably this would be passed to MySQLiteHelper in order to make the entries
 *
 * An Animal is anything that can come to the garden
 *      animalName: the identifier of the animal (cat, dog, monkey, etc)
 *      numVisits: the number of times the animal has visited
 *      rarity: the likelihood that an animal will show up at any given point in the garden
 *      persistence: how long the animal will stay once it has showed up
 *      present: the location the animal currently occupies in the garden. -1 if not currently there
 */

class Animal implements Serializable {
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

    String getAnimalName() {
        return animalName;
    }

    void setAnimalName(String animalName) {
        this.animalName = animalName;
    }

    int getNumVisits() {
        return numVisits;
    }

    void setNumVisits(int numVisits) {
        this.numVisits = numVisits;
    }

    int getRarity() {
        return rarity;
    }

    void setRarity(int rarity) {
        this.rarity = rarity;
    }

    long getPersistence() {
        return persistence;
    }

    void setPersistence(long persistence) {
        this.persistence = persistence;
    }

    int getPresent() {
        return present;
    }

    void setPresent(int present) {
        this.present = present;
    }
}
