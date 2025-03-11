package com.example.model;
import java.util.ArrayList;

public class Order {
    
    private int idOrder;
    private String status;
    private User user;
    private ArrayList<Product> productList = new ArrayList<>();

    public void generateOrder(User user, Product product){

    }

    public void updateStatus(){

    }

    private void calculateProximity(City city){

    }

    private void calculateStock(Store store){

    }

    public boolean validatePurchase(){
        return false;
    }



}
