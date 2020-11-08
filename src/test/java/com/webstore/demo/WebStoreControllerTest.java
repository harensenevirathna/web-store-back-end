package com.webstore.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webstore.demo.controllers.WebStoreController;
import com.webstore.demo.models.Product;
import com.webstore.demo.services.WebStoreService;
import net.minidev.json.JSONObject;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@WebMvcTest(WebStoreController.class)
public class WebStoreControllerTest {

    @MockBean
    private WebStoreService webStoreService;

    @Autowired
    private WebStoreController webStoreController;

    /**
     * Test get All Product
     *
     * @throws Exception
     */
    @Test
    public void getAllProduct() throws Exception {
        List<JSONObject> expectitemresult = new ArrayList<>();

        JSONObject product1 = new JSONObject();
        product1.put("id", 1);
        product1.put("productName", "Penguin-ears");
        product1.put("unitsPerCarton", 20);
        product1.put("price", 175);
        product1.put("imgPath", "/assets/images/im1.jpg");
        product1.put("description", "test");
        product1.put("requestQty", "");
        product1.put("discountAfterprice", "");

        JSONObject product2 = new JSONObject();
        product2.put("id", 2);
        product2.put("productName", "Horseshoe");
        product2.put("unitsPerCarton", 5);
        product2.put("price", 825);
        product2.put("imgPath", "/assets/images/im2.jpg");
        product2.put("description", "test");
        product2.put("requestQty", "");
        product2.put("discountAfterprice", "");

        expectitemresult.add(product1);
        expectitemresult.add(product2);

        Mockito.when(webStoreService.getAllProduct()).thenReturn(expectitemresult);
        List<JSONObject> actualitemresult = webStoreController.getAllProduct();
        assertEquals(expectitemresult, actualitemresult);
    }

    /**
     * Test Get Price List
     *
     * @throws Exception
     */
    @Test
    public void getPriceList() throws Exception {

        Product requestproduct = new Product();
        requestproduct.setId(1);
        requestproduct.setProductName("Penguin-ears");
        requestproduct.setUnitsPerCarton(20);
        requestproduct.setPrice(175d);
        requestproduct.setImgPath("/assets/images/im1.jpg");
        requestproduct.setDescription("test");

        List<JSONObject> expectitemresult = new ArrayList<>();

        for (int i = 1; i < 51; i++) {
            JSONObject item = new JSONObject();
            item.put("id", i);
            item.put("name", requestproduct.getProductName());
            item.put("unit", i);
            item.put("price", this.getTotalPriceForQuantity(requestproduct, i));
            expectitemresult.add(item);
        }

        Mockito.when(webStoreService.getPriceList("1")).thenReturn(expectitemresult);
        List<JSONObject> actualitemresult = webStoreController.getPriceList("1");
        assertEquals(expectitemresult.size(), actualitemresult.size());
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

            price = totalPriceForQuantity;


        } else if (extraUnit == 0) {

            double totalCartonPrice = (requestproduct.getPrice()) / requestproduct.getUnitsPerCarton() * i;

            price = totalCartonPrice;
        }
        return new BigDecimal(price).setScale(2, RoundingMode.HALF_UP);
    }


    /**
     * Test Get Item Price
     *
     * @throws Exception
     */
    @Test
    public void getItemPrice() throws Exception {

        Product requestproduct = new Product();
        requestproduct.setId(1);
        requestproduct.setProductName("Penguin-ears");
        requestproduct.setUnitsPerCarton(20);
        requestproduct.setPrice(175d);
        requestproduct.setImgPath("/assets/images/im1.jpg");
        requestproduct.setDescription("test");

        JSONObject expectitemresult = new JSONObject();
        int i = 12;


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
                expectitemresult.put("discountDescription", "10% Discount");
            }

            //Calculate price for carton Quantity
            double priceForCarton = cartonCount * requestproduct.getPrice();

            //Calculate Price For Extra Unit
            double priceForExtraUnit = extraUnit * oneUnitPrice;

            // Total Price for requested Units and Discount
            double totalPriceForQuantity = (priceForCarton + priceForExtraUnit) - discount;

            price = totalPriceForQuantity;


        } else if (extraUnit == 0) {

            //Get Carton count
            int cartonCount = (i - extraUnit) / requestproduct.getUnitsPerCarton();

            // Calculate 10% Discount for All cartons If customer purchase 3 cartons or more
            if (cartonCount >= 3) {
                discount = (requestproduct.getPrice() * 10 / 100) * cartonCount;
                expectitemresult.put("discountDescription", "10% Discount");
            }

            // Total Price for requested Units and Discount
            double totalCartonPrice = ((requestproduct.getPrice() / requestproduct.getUnitsPerCarton()) * i) - discount;

            price = totalCartonPrice;
        }

        expectitemresult.put("discountAfterprice", new BigDecimal(price).setScale(2, RoundingMode.HALF_UP));
        expectitemresult.put("discount", discount);

        String json = "{\"requestQty\":1,\"discountAfterprice\":0,\"price\":175,\"imgPath\":\"/assets/images/im1.jpg\",\"description\":\"test\",\"id\":1,\"unitsPerCarton\":20,\"productName\":\"Penguin-ears\"}";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(json);

        Mockito.when(webStoreService.getPriceForRequestedItem(jsonNode)).thenReturn(expectitemresult);
        JSONObject actualitemresult = webStoreController.getItemPrice(jsonNode);
        assertEquals(expectitemresult, actualitemresult);


    }
}
