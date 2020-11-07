package com.webstore.demo.controllers;


import com.fasterxml.jackson.databind.JsonNode;
import com.webstore.demo.services.WebStoreService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:4200"})
@RequestMapping("/webstore")
public class WebStoreController {

    @Autowired
    private WebStoreService webStoreService;

    /**
     * Get All Product
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/product")
    public List<JSONObject> getAllProduct() throws Exception {
        return webStoreService.getAllProduct();
    }

    /**
     * Get Price List Of Requested Product
     *
     * @param productParam
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/prices")
    public List<JSONObject> getPriceList(@RequestBody String productParam) throws Exception {
        return webStoreService.getPriceList(productParam);
    }

    /**
     * Get Product Selling Price to Customer
     *
     * @param cartItem
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/itemprice")
    public JSONObject getItemPrice(@RequestBody JsonNode cartItem) throws Exception {
        return webStoreService.getPriceForRequestedItem(cartItem);
    }
}
