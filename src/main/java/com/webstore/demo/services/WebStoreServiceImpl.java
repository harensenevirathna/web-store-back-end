package com.webstore.demo.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.webstore.demo.models.Product;
import com.webstore.demo.repository.ProductRepository;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class WebStoreServiceImpl implements WebStoreService {


    private final ProductRepository productRepository;

    @Autowired
    public WebStoreServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Get All Product
     *
     * @return
     * @throws Exception
     */
    @Override
    public List<JSONObject> getAllProduct() throws Exception {
        Iterable<Product> products = productRepository.findAll();
        List<JSONObject> objectList = new ArrayList<>();
        for (Product product : products) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", product.getId());
            jsonObject.put("productName", product.getProductName());
            jsonObject.put("unitsPerCarton", product.getUnitsPerCarton());
            jsonObject.put("price", product.getPrice());
            jsonObject.put("imgPath", product.getImgPath());
            jsonObject.put("description", product.getDescription());
            jsonObject.put("requestQty", "");
            jsonObject.put("discountAfterprice", "");
            objectList.add(jsonObject);
        }
        return objectList;
    }

    /**
     * Get Price List Of Requested Product
     *
     * @param product
     * @return
     * @throws Exception
     */
    @Override
    public List<JSONObject> getPriceList(String product) throws Exception {

        Product requestproduct = productRepository.findById(Integer.parseInt(product)).get();
        List<JSONObject> itemList = new ArrayList<>();
        for (int i = 1; i < 51; i++) {
            JSONObject item = new JSONObject();
            item.put("id", i);
            item.put("name", requestproduct.getProductName());
            item.put("unit", i);
            item.put("price", this.getTotalPriceForQuantity(requestproduct, i));
            itemList.add(item);
        }
        return itemList;
    }

    private static BigDecimal getTotalPriceForQuantity(Product requestproduct, Integer i) {
        double price = 0d;
        int extraUnit = i % requestproduct.getUnitsPerCarton();
        if (extraUnit != 0) {

            // New Carton Price For If Customer Request Extra Units (Quantity). It increase 30% of Cartoon price
            double cartonNewPrice = (requestproduct.getPrice() * 30 / 100) + requestproduct.getPrice();

            // New Price of One Unit If Customer Request Extra Units (Quantity) After the increase 30% of Cartoon price
            double oneUnitPrice = (cartonNewPrice / requestproduct.getUnitsPerCarton());

            //Get Carton count
            int cartonCount = (i - extraUnit) / requestproduct.getUnitsPerCarton();

            //Calculate price for carton Quantity
            double priceForCarton = cartonCount * requestproduct.getPrice();

            //Calculate Price For Extra Unit
            double priceForExtraUnit = extraUnit * oneUnitPrice;

            // Total Price for requested Units
            double totalPriceForQuantity = priceForCarton + priceForExtraUnit;

            // Last Price
            price = totalPriceForQuantity;


        } else if (extraUnit == 0) {

            double totalCartonPrice = (requestproduct.getPrice()) / requestproduct.getUnitsPerCarton() * i;

            // Last Price
            price = totalCartonPrice;

        }

        return new BigDecimal(price).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Get Product Selling Price to Customer
     *
     * @param cartItem
     * @return
     * @throws Exception
     */
    @Override
    public JSONObject getPriceForRequestedItem(JsonNode cartItem) throws Exception {
        int i = cartItem.get("requestQty").asInt();
        Product requestproduct = productRepository.findById(cartItem.get("id").asInt()).get();
        JSONObject response = new JSONObject();
        double price = 0d;
        double discount = 0d;
        int extraUnit = i % requestproduct.getUnitsPerCarton();
        if (extraUnit != 0) {

            // New Carton Price For If Customer Request Extra Units (Quantity). It increase 30% of Cartoon price
            double cartonNewPrice = (requestproduct.getPrice() * 30 / 100) + requestproduct.getPrice();

            // New Price of One Unit If Customer Request Extra Units (Quantity) After the increase 30% of Cartoon price
            double oneUnitPrice = (cartonNewPrice / requestproduct.getUnitsPerCarton());

            //Get Carton count
            int cartonCount = (i - extraUnit) / requestproduct.getUnitsPerCarton();

            // Calculate 10% Discount for All cartons If customer purchase 3 cartons or more
            if (cartonCount >= 3) {
                discount = (requestproduct.getPrice() * 10 / 100) * cartonCount;
                response.put("discountDescription", "10% Discount");
            }

            //Calculate price for carton Quantity
            double priceForCarton = cartonCount * requestproduct.getPrice();

            //Calculate Price For Extra Unit
            double priceForExtraUnit = extraUnit * oneUnitPrice;

            // Total Price for requested Units and Discount
            double totalPriceForQuantity = (priceForCarton + priceForExtraUnit) - discount;

            // Last Price Send to customer
            price = totalPriceForQuantity;


        } else if (extraUnit == 0) {

            //Get Carton count
            int cartonCount = (i - extraUnit) / requestproduct.getUnitsPerCarton();

            // Calculate 10% Discount for All cartons If customer purchase 3 cartons or more
            if (cartonCount >= 3) {
                discount = (requestproduct.getPrice() * 10 / 100) * cartonCount;
                response.put("discountDescription", "10% Discount");
            }

            // Total Price for requested Units and Discount
            double totalCartonPrice = ((requestproduct.getPrice() / requestproduct.getUnitsPerCarton()) * i) - discount;

            // Last Price Send to customer
            price = totalCartonPrice;

        }

        response.put("discountAfterprice", new BigDecimal(price).setScale(2, RoundingMode.HALF_UP));
        response.put("discount", discount);
        return response;
    }
}
