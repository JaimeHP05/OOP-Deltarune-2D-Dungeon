package code.model.objects;
import code.model.exceptions.InventoryFullException;
import java.util.ArrayList;
import java.util.List;

public class Inventory<T extends code.model.objects.Item> implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private List<T> items;
    private int capacity;

    public Inventory(int capacity) {
        this.capacity = capacity;
        this.items = new ArrayList<>();
    }

    public void add(T item) throws InventoryFullException {
        if (items.size() >= capacity) {
            throw new InventoryFullException("Inventory is full! You were not able to save: " + item.getName());
        }
        items.add(item);
        System.out.println("New item in inventory: " + item.getName() + " (" + items.size() + "/" + capacity + ")");
    }

    public List<T> getItems() {
        return items;
    }
    
    public T getItem(int index) {
        if (index >= 0 && index < items.size()) {
            return items.get(index);
        }
        return null;
    }

    public void remove(T item) {
        items.remove(item);
    }
}