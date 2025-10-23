package models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Batch implements Comparable<Batch>{
    private String BID;
    private Product product;
    private long  quantity;
    private LocalDate importDate;
    private final LocalDate expiryDate;
    private boolean active;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Batch() {
        this.BID = "";
        this.product = null;
        this.quantity = 0;
        this.importDate = null;
        this.expiryDate = null;
        this.active = false;
    }

    public Batch(String BID, Product product, long quantity, LocalDate importDate, boolean isactive) {
        this.BID = BID;
        this.product = product;
        this.quantity = quantity;
        this.importDate = importDate;
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

}
