package tfip.ssf.practicepurchaseorder.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import tfip.ssf.practicepurchaseorder.model.Cart;
import tfip.ssf.practicepurchaseorder.model.Invoice;
import tfip.ssf.practicepurchaseorder.model.Item;
import tfip.ssf.practicepurchaseorder.model.Quotation;
import tfip.ssf.practicepurchaseorder.model.Shipping;
import tfip.ssf.practicepurchaseorder.service.PurchaseService;

@Controller
@RequestMapping("/")
public class PurchaseOrderController {

    @Autowired
    private PurchaseService pSvc;
    
    @GetMapping
    public String getCart(Model model, HttpSession session){
        // Get existing cart session
        Cart c = (Cart) session.getAttribute("cart");
        if (null == c){
            c = new Cart();
            session.setAttribute("cart", c);
            model.addAttribute("cart", new Cart());
        }
        // if there is existing cart session, add cart to model
        model.addAttribute("cart", c);
        // input field to be a new Item() each time
        model.addAttribute("item", new Item());
        return "view1";
    }

    @PostMapping
    public String postCart(Model model, HttpSession session, @ModelAttribute @Valid Item item, BindingResult result ){

        // Cart already exists as it will be created by the GetMapping
        Cart c = (Cart) session.getAttribute("cart");

        if(result.hasErrors()){
            model.addAttribute("cart", c);
            return "view1";
        }
        
        // adding field errors based on backend validation
        List<ObjectError> errors = pSvc.validateItem(item);
        if(!errors.isEmpty()){
            for(ObjectError e : errors){
                result.addError(e);
            }
        }
        //iterate through cart content, and get name of item, if name matches item.getName, quantity += item.getQuantity
        Boolean existsInCart = false;
        List<Item> listOfItemsInCart = c.getContents();
        for(int i=0; i < listOfItemsInCart.size(); i++){
            if(listOfItemsInCart.get(i).getItem().equalsIgnoreCase(item.getItem())){
                listOfItemsInCart.get(i).setQuantity(listOfItemsInCart.get(i).getQuantity() + item.getQuantity());
                existsInCart = true;
            }
        }
        // if item does not exist in cart, add item to cart
        if(!existsInCart && errors.isEmpty()){
            c.addToCart(item);
        }
        session.setAttribute("cart", c);
        model.addAttribute("cart", c);
        model.addAttribute("item", item);

        return "view1";
    }

    @PostMapping("/shipping")
    public String getShipping(Model model, HttpSession sessions){
        model.addAttribute("shipping", new Shipping());
        return "view2";
    }

    // @PostMapping("/invoice")
    // public String getInvoice(Model model, HttpSession session, @ModelAttribute @Valid Shipping shipping, BindingResult result){
    //     if(result.hasErrors()){
    //         return "view2";
    //     }
    //     Cart c = (Cart) session.getAttribute("cart");
    //     Invoice invoice = pSvc.saveInvoice(c, shipping);
    //     model.addAttribute("invoice", invoice);
    //     return "view3";
    // }

    // //GetMapping by ID

    @GetMapping("/invoice/{invoiceId}")
    public String getById(Model model, @PathVariable String invoiceId){
        Optional<Invoice> inv = pSvc.retrieveById(invoiceId);
        model.addAttribute("invoice", inv.get());
        return "view4";
    }

    // checkout, call API to get unit cost and show invoice
    @PostMapping("/checkout")
    public String checkoutCart(HttpSession session, Model model, @ModelAttribute @Valid Shipping shipping, BindingResult result){
        if(result.hasErrors()){
            return "view2";
        }
        Cart c = (Cart) session.getAttribute("cart");
        Quotation q = pSvc.getQuotations(c);

        Invoice invoice = pSvc.saveInvoice(c, shipping, q);
        model.addAttribute("invoice", invoice);
        session.invalidate();
    
        return "view3";
    }
}
