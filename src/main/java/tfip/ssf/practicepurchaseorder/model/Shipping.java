package tfip.ssf.practicepurchaseorder.model;

import java.io.Serializable;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.validation.constraints.NotBlank;

public class Shipping implements Serializable{
    @NotBlank(message="Please provide your name")
    private String name;

    @NotBlank(message="Please provide your address")
    private String address;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    @Override
    public String toString() {
        return "Shipping [name=" + name + ", address=" + address + "]";
    }

    public JsonObjectBuilder toJSON(){
        return Json.createObjectBuilder()
                    .add("name", this.getName())
                    .add("address", this.getAddress());
    }

    public static Shipping createFromJson(JsonObject o){
        Shipping shipping = new Shipping();
        shipping.setName(o.getString("name"));
        shipping.setAddress(o.getString("address"));

        return shipping;
    }
    
}
