package tfip.ssf.practicepurchaseorder.model;

import java.io.Serializable;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class Item implements Serializable{

    @NotBlank(message="Item cannot be blank")
    private String item;

    @Min(value = 1, message="You must add at least 1 item")
    private int quantity;

    public String getItem() {
        return item;
    }
    public void setItem(String item) {
        this.item = item;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Item [item=" + item + ", quantity=" + quantity + "]";
    }

    public JsonObjectBuilder toJSON(){
        return Json.createObjectBuilder()
                    .add("item", this.getItem())
                    .add("quantity", this.getQuantity());
    }
    
    public static Item createFromJson(JsonObject o){
        Item item = new Item();
        item.setItem(o.getString("item"));
        item.setQuantity(o.getJsonNumber("quantity").intValue());
        return item;
    }
}
