package com.webstore.demo.services;

import com.fasterxml.jackson.databind.JsonNode;
import net.minidev.json.JSONObject;

import java.util.List;

public interface WebStoreService {

    /**
     * Get All Product
     *
     * @return
     * @throws Exception
     */
    public List<JSONObject> getAllProduct() throws Exception;

    /**
     * Get Price List Of Requested Product
     *
     * @param product
     * @return
     * @throws Exception
     */
    public List<JSONObject> getPriceList(String product) throws Exception;

    /**
     * Get Product Selling Price to Customer
     *
     * @param cartItem
     * @return
     * @throws Exception
     */
    public JSONObject getPriceForRequestedItem(JsonNode cartItem) throws Exception;
}
