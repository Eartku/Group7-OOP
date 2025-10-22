package models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Batch implements Comparable<Batch>{
    private String BID;
    private Product product;
    private long  quantity;
    private double importPrice;
    private double exportPrice;
    private LocalDate importDate;
    private LocalDate expiryDate;
    private boolean active;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Batch() {
        this.BID = "";
        this.product = null;
        this.quantity = 0;
        this.importPrice = 0.0;
        this.exportPrice = 0.0;
        this.importDate = null;
        this.expiryDate = null;
        this.active = false;
    }

    public Batch(String BID, Product product, long quantity, LocalDate importDate, boolean isactive) {
        this.BID = BID;
        this.product = product;
        this.quantity = quantity;
        this.importDate = importDate;
        this.importPrice = quantity * product.getPrice(); // giá nhập tăng 10% so với giá thị trường
        this.exportPrice = importPrice;
        if(product.getSLM() != null){
            this.expiryDate = importDate.plusMonths(product.getSLM());
        } else {
            this.expiryDate = null;
        }
        this.active = isactive;
    }

    public String getBatchId() { return BID; }
    public void setBatchId(String batchId) { this.BID = batchId; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public long getQuantity() { return quantity; }
    public void setQuantity(long quantity) { this.quantity = quantity; }

    public boolean getActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public double getImportPrice(){return this.importPrice;}
    public void setImportPrice(double price){this.importPrice = price;}
    public double getExportPrice(){return this.exportPrice;}

    public String getImportDate() { return importDate.format(FORMATTER); }
    public void setImportDate(String input) {
        try {
            this.importDate = LocalDate.parse(input, FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Ngay nhap khong hop le! (dd/MM/yyyy)");
        }
    }

    public String getExpiryDateString() { return expiryDate.format(FORMATTER); }
    public LocalDate getExpiryDate() { return this.expiryDate; }
    public void setExpiryDate(String input) {
        try {
            this.expiryDate = LocalDate.parse(input, FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Ngay khong hop le (dd/MM/yyyy)!");
        }
    }

    public boolean isExpired() {
        return expiryDate != null && LocalDate.now().isAfter(expiryDate);
    }

    @Override
    public String toString() {
        return  BID + "|" + product.getPID() + 
               "|" + quantity + 
               "|" + importDate.format(FORMATTER) + 
               "|" + active;
    }

    @Override
    public int compareTo(Batch bch){
        return this.getBatchId().compareTo(bch.getBatchId());
    }

    public void setExportPrice(double exportPrice) {
        this.exportPrice = exportPrice;
    }
}
