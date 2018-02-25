package cs65.edu.dartmouth.cs.gifto;

/**
 * Created by Oliver on 2/25/2018.
 *
 * Another simple class to keep track of an inventory item
 */

public class InventoryItem {
    private String itemType;
    private int itemAmount;

    InventoryItem(){}

    InventoryItem(String itemType, int itemAmount) {
        this.itemType = itemType;
        this.itemAmount = itemAmount;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public int getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(int itemAmount) {
        this.itemAmount = itemAmount;
    }
}
