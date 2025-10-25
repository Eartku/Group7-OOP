package models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class Order implements  Comparable<Order>{
    private String OID;
    private final List<OrderItem> items;
    private Customer customer;
    private LocalDate purchaseDate;
    private String status;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Order() {
        this.OID = "";
        this.items = new ArrayList<>();
        this.customer = null;
        this.purchaseDate = null;
    }

    public Order(String OID, List<OrderItem> items, Customer customer, String status) {
        this.OID = OID;
        this.items = new ArrayList<>(items);
        this.customer = customer;
        this.purchaseDate = LocalDate.now();
        this.status = status;
    }

    public Order(String OID, List<OrderItem> items, Customer customer, LocalDate purchasDate, String status) {
        this.OID = OID;
        this.items = new ArrayList<>(items);
        this.customer = customer;
        this.purchaseDate = purchasDate;
        this.status = status;
    }

    public String getOID() { return OID; }
    public void setOID(String OID) { this.OID = OID; }

    public List<OrderItem> getItems() { return items; }
    public void showItems(){
        for(OrderItem o : items){
            System.out.print(o.toString());
        }
    }


    public Customer getCustomer() { 
        return this.customer; 
    }
    public void setCustomer(Customer customer) { 
        this.customer = customer; 
    }


    public LocalDate getpurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(String input) {
        try {
            this.purchaseDate = LocalDate.parse(input, FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Ngay thanh toan khong hop le! (dd/MM/yyyy)");
        }
    }

    public double getTotal(){
        double total = 0.0;
        for(OrderItem o : items){
            total += o.getTotalPrice();
        }
        return total;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return OID + "|" +  customer.getCID() + "|" + purchaseDate.format(FORMATTER);
    }

    @Override
    public int compareTo(Order o){
        return this.getOID().compareTo(o.getOID());    }
}
