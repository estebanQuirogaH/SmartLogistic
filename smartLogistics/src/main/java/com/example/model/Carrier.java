package com.example.model;

import java.util.HashMap;

public class Carrier {
    
    private int idCarrier;
    private String name;
    private HashMap<Shipment,String> shipmentList = new HashMap<>();

    public boolean deliverShipment(){
        return false;
    }

}
