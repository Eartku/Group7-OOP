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

//Order: đơn hàng - hóa đơn
//Có OrderItem (quan hệ Composition) chưa danh sách sản phẩm đã mua
// 1 Order chỉ có 1 Customer (quan hệ Association 1 chiều Order -> Customer)

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");// định dạng ngày

    // Constructor mặc định
    public Order() {
        this.OID = "";
        this.items = new ArrayList<>();
        this.customer = null;
        this.purchaseDate = null;
    }

    // Constructor có tham số nhưng tạo đơn hàng ngay thời điểm hiện tại (Ví dụ như mới đặt hàng xong)
    public Order(String OID, List<OrderItem> items, Customer customer, boolean status) {
        this.OID = OID;
        this.items = new ArrayList<>(items);
        this.customer = customer;
        this.purchaseDate = LocalDate.now();
        this.status = status;
    }

    // Constructor có tham số nhưng tạo đơn hàng ngay thời điểm biết trước (cần dùng trong đọc file)
    public Order(String OID, List<OrderItem> items, Customer customer, LocalDate purchaseDate, boolean status) {
        this.OID = OID;
        this.items = new ArrayList<>(items);
        this.customer = customer;
        this.purchaseDate = purchaseDate;
        this.status = status;
    }

    //Methods getter - setter
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

    // implement IStatus
    @Override
    public boolean getStatus() { return status; }
    @Override
    public void setStatus(boolean status) { this.status = status; }
    @Override
    public String getStatusString() {
        if (customer == null) return "Unknown"; 
        if (!customer.getStatus()) return "Customer blocked";
        return status ? "Active" : "Inactive";
    }


    // định dạng chính trong file
    @Override
    public String toString() {
        return OID + "|" +  customer.getCID() + "|" + purchaseDate.format(FORMATTER) + "|" + status;
    }

    // So sánh - implement Comparable
    @Override
    public int compareTo(Order o){
        return this.getOID().compareTo(o.getOID());    }
}
