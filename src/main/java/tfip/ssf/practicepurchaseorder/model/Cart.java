package tfip.ssf.practicepurchaseorder.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Cart implements Serializable{
    private List<Item> contents = new LinkedList<>();

    public Cart(){}

    public Cart(List<Item> listOfItems){
        this.contents = listOfItems;
    }

    public List<Item> getContents() {
        return contents;
    }

    public void setContents(List<Item> contents) {
        this.contents = contents;
    }
    
    public void addToCart(Item i){
        this.contents.add(i);
    }

    @Override
    public String toString() {
        return "Cart [contents=" + contents + "]";
    }
    
}
