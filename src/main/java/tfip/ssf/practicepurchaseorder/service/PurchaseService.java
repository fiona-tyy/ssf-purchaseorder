package tfip.ssf.practicepurchaseorder.service;

import java.io.StringReader;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.stream.JsonCollectors;
import tfip.ssf.practicepurchaseorder.model.Cart;
import tfip.ssf.practicepurchaseorder.model.Invoice;
import tfip.ssf.practicepurchaseorder.model.Item;
import tfip.ssf.practicepurchaseorder.model.Quotation;
import tfip.ssf.practicepurchaseorder.model.Shipping;
import tfip.ssf.practicepurchaseorder.repository.PurchaseRepository;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseRepository pRepo;
    
    public static final String[] ITEM_NAMES = {"apple", "orange", "bread", "cheese", "chicken","mineral_water", "instant_noodles"};

    private final Set<String> itemNames;

    public PurchaseService(){
        itemNames = new HashSet<>(Arrays.asList(ITEM_NAMES));
    }

    public List<ObjectError> validateItem(Item item){
        List<ObjectError> errors  = new LinkedList<>();
        FieldError error;

        if(!itemNames.contains(item.getItem().toLowerCase())){
            error = new FieldError("item", "item", "We do not stock %s".formatted(item.getItem()));
            errors.add(error);
        }
        
        return errors;

    }




    // public void saveToRedis(Invoice inv){
    //     pRepo.saveToRedis(inv);
    // }

    // generating random ID
    private synchronized String generateId(int numOfChar){
        SecureRandom sr = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        while(sb.length() < numOfChar){
            sb.append(Integer.toHexString(sr.nextInt()));
        }
        return sb.toString().substring(0, numOfChar);
    }

    public Optional<Invoice> retrieveById(String invoiceId){
        return pRepo.retrieveById(invoiceId);
    }

    public List<String> getItemList (Cart cart){
        List<String> itemList = new LinkedList<>();
        for(Item item : cart.getContents()){
            itemList.add(item.getItem());
        }
        return itemList;
    }

    public Quotation getQuotations(Cart cart){
        List<String> itemList = getItemList(cart);
        JsonArrayBuilder arrBuilder = Json.createArrayBuilder(itemList);
        
        
        RequestEntity<String> req = RequestEntity.post("https://quotationsys-production.up.railway.app/quotation")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Accept", MediaType.APPLICATION_JSON_VALUE)
        .body(arrBuilder.build().toString(),String.class);
        
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> resp = template.exchange(req, String.class);
        String payload = resp.getBody();
        JsonReader reader = Json.createReader(new StringReader(payload));
        JsonObject object = reader.readObject();
        Quotation q = new Quotation();
        q.setQuoteId(object.getString("quoteId"));
        
        // get JsonArray, loop through, add to map
        JsonObject quotationObj = object.getJsonObject("quotations");
        Set<String> itemKeys = quotationObj.keySet();
        for (String item : itemKeys){
            Float value = (float) quotationObj.getJsonNumber(item).doubleValue();
            q.addQuotation(item, value);
        }

        return q;
    }

    public Invoice createInvoice(Cart c, Shipping s, Quotation q){
        Invoice invoice = new Invoice();
        invoice.setInvoiceId(q.getQuoteId());
        invoice.setShipping(s);

        Float totalCost = 0f;

        for(Item item: c.getContents()){
            totalCost+= item.getQuantity() * q.getQuotation(item.getItem());
        }
        invoice.setTotalCost(totalCost);
        invoice.setCart(c);

        return invoice;
    }

    public Invoice saveInvoice(Cart c, Shipping s, Quotation q){
        // create invoice;
        Invoice inv = createInvoice(c, s,q);
        // save to Repo
        pRepo.saveToRedis(inv);

        return inv;
        
        

    }
    
}
