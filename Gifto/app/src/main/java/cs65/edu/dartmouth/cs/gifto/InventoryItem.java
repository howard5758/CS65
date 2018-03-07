package cs65.edu.dartmouth.cs.gifto;

import java.io.Serializable;

/**
 * Created by Oliver on 2/25/2018.
 *
 * Another simple class to keep track of an inventory item
 *
 * an InventoryItem is anything that you currently own. This includes gifts, food, and money
 *      itemName: the unique name of the item
 *      itemAmount: how much of an item you own
 *      present: the position of the item in the garden. -1 if item is not currently in the garden
 */

public class InventoryItem implements Serializable {
    private String itemName;
    private int itemAmount;
    private int present; // location

    public InventoryItem(){
        itemName = "";
        itemAmount = 0;
        present = -1;
    }

    public InventoryItem(String itemName, int itemAmount) {
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
