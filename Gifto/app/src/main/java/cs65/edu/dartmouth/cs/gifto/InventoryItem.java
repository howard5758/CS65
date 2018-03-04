package cs65.edu.dartmouth.cs.gifto;

import java.io.Serializable;

/**
 * Created by Oliver on 2/25/2018.
 *
 * Another simple class to keep track of an inventory item
 */

public class InventoryItem implements Serializable {
    private String itemName;
    private int itemAmount;
    private int present;

    InventoryItem(){
        itemName = "";
        itemAmount = 0;
        present = -1;
    }

    InventoryItem(String itemName, int itemAmount) {
        this.itemName = itemName;
        this.itemAmount = itemAmount;
    }

    public int getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(int itemAmount) {
        this.itemAmount = itemAmount;
    }

    public String getItemName() { return itemName; }

    public void setItemName(String itemName) { this.itemName = itemName; }

    public int getPresent() {
        return present;
    }

    public void setPresent(int present) {
        this.present = present;
    }
}
