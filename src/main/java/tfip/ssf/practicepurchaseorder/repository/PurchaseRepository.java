package tfip.ssf.practicepurchaseorder.repository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import tfip.ssf.practicepurchaseorder.model.Invoice;

@Repository
public class PurchaseRepository {
    
    @Autowired @Qualifier("purchase")
    private RedisTemplate<String, String> redisTemplate;

    public void saveToRedis(Invoice invoice){
        redisTemplate.opsForValue().set(invoice.getInvoiceId(), invoice.toJSON().toString());
    }

    public Optional<Invoice> retrieveById(String invoiceId){
        String jsonString = redisTemplate.opsForValue().get(invoiceId);
        if(null == jsonString || jsonString.trim().length() <=0){
            return Optional.empty();
        }
        Invoice inv = new Invoice();
        // Invoice inv = Invoice.createFromJsonString(jsonString); // to work on it
        return Optional.of(inv);
    }

}
