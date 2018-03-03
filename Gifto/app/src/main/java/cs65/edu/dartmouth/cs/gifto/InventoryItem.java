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
    private int itemType;

    InventoryItem(){}

    InventoryItem(String itemName, int itemType, int itemAmount) {
        this.itemName = itemName;
        this.itemType = itemType;
        this.itemAmount = itemAmount;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public int getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(int itemAmount) {
        this.itemAmount = itemAmount;
    }

    public String getItemName() { return itemName; }

    public void setItemName(String itemName) { this.itemName = itemName; }
}
