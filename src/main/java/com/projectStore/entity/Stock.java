package com.projectStore.entity;

import java.util.HashMap;

public class Stock {
    private HashMap<Product, Integer> productList = new HashMap<>();

    public void addAmount(Product product, int amount) {
        productList.put(product, productList.getOrDefault(product, 0) + amount);
    }

    public void deleteAmount(Product product, int amount) {
        productList.put(product, Math.max(0, productList.getOrDefault(product, 0) - amount));
    }

    public void updateProductPrice(Product product, double price) {
        // product.setPrice(price);
    }

    public HashMap<Product, Integer> getProductList() {
        return productList;
    }
}
