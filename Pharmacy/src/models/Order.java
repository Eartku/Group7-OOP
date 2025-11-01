package models;

import interfaces.IStatus;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class Order implements  Comparable<Order>,IStatus{
    private String OID;
    private final List<OrderItem> items;
    private Customer customer;
    private LocalDate purchaseDate;
    private boolean status;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Order() {
        this.OID = "";
        this.items = new ArrayList<>();
        this.customer = null;
        this.purchaseDate = null;
    }

    public Order(String OID, List<OrderItem> items, Customer customer, boolean status) {
        this.OID = OID;
        this.items = new ArrayList<>(items);
        this.customer = customer;
        this.purchaseDate = LocalDate.now();
        this.status = status;
    }

    public Order(String OID, List<OrderItem> items, Customer customer, LocalDate purchaseDate, boolean status) {
        this.OID = OID;
        this.items = new ArrayList<>(items);
        this.customer = customer;
        this.purchaseDate = purchaseDate;
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

    @Override
    public boolean getStatus() { return status; }
    @Override
    public void setStatus(boolean status) { this.status = status; }
    @Override
    public String getStatusString() {
        if (customer == null) return "Unknown"; 
        if (!customer.getStatus()) return "Customer blocked";
        return status ? "Order active" : "Order inactive";
    }


    @Override
    public String toString() {
        return OID + "|" +  customer.getCID() + "|" + purchaseDate.format(FORMATTER) + "|" + status;
    }

    @Override
    public int compareTo(Order o){
        return this.getOID().compareTo(o.getOID());    }
}
