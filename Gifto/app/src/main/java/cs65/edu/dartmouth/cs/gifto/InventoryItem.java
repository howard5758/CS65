package cs65.edu.dartmouth.cs.gifto;

/**
 * Created by Oliver on 2/25/2018.
 *
 * Another simple class to keep track of an inventory item
 */

public class InventoryItem {
    private String itemName;
    private int itemAmount;

    InventoryItem(){}

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
}
