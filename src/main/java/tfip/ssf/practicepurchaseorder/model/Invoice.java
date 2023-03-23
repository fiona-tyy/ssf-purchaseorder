package tfip.ssf.practicepurchaseorder.model;

import java.io.Serializable;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

public class Invoice implements Serializable{
    private String invoiceId;
    private Float totalCost;
    private Shipping shipping;
    private Cart cart;

    public Invoice(){
    }

    public String getInvoiceId() {
        return invoiceId;
    }
    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }
    
    public Shipping getShipping() {
        return shipping;
    }
    public void setShipping(Shipping shipping) {
        this.shipping = shipping;
    }

    public String getName(){
        return shipping.getName();
    }

    public String getAddress(){
        return shipping.getAddress();
    }

    public Float getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Float totalCost) {
        this.totalCost = totalCost;
    }
    
    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public JsonObject toJSON(){

        JsonObjectBuilder ob = Json.createObjectBuilder();
        for(Item item : cart.getContents()){
            ob.add(item.getItem(), item.getQuantity());
        }

        return Json.createObjectBuilder()
                    .add("invoiceId", this.getInvoiceId())
                    .add("shipping", this.getShipping().toJSON())
                    .add("total", this.getTotalCost())
                    .add("cart", ob)
                    .build();
    }

   


}
