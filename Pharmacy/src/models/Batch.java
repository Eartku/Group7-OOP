package models;

import interfaces.IStatus;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class Batch implements Comparable<Batch>, IStatus{ 
    private String BID;
    private Product product;
    private long  quantity;
    private LocalDate importDate;
    private final LocalDate expiryDate;
    private boolean status;

// Một Sản phẩm(Product) có thể được tham chiếu bởi nhiều Lô(Batch)
// Một Batch chỉ có thể tham chiếu đúng 1 Product
// Quantity là số lượng sản phẩm trong lô đó
// Quan hệ UML giữa Product - Batch là Association (1 chiều)
// (vì Batch biết đến Product nhưng Product không biết đến Batch)


    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy"); //chỉ là định dạng ngày tháng

    //constructor mặc định
    public Batch() {
        this.BID = "";
        this.product = null;
        this.quantity = 0;
        this.importDate = null;
        this.expiryDate = null;
        this.status = false;
    }

    //constructor có tham số
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
        this.status = isactive;
    }

    //getter - setter các thông số
    public String getBatchId() { return BID; }          //Mã
    public void setBatchId(String batchId) { this.BID = batchId; }

    public Product getProduct() { return product; }     //Lấy sản phẩm
    public void setProduct(Product product) { this.product = product; }

    public long getQuantity() { return quantity; }      //Lấy số lượng lô hàng
    public void setQuantity(long quantity) { this.quantity = quantity; }

    // các hàm về trạng thái khi nó implements interface IStatus
    @Override
    public boolean getStatus() { return status; }
    @Override
    public void setStatus(boolean active) { this.status = active; }
    @Override
    public String getStatusString() {
        String statusBatch;
        if (isExpired()) {
            statusBatch = "Qua han";
        } 
        else if (quantity == 0) {
            statusBatch = "Het hang";
        } 
        else if (quantity > 0 && quantity <= 20) {
            statusBatch = "Sap het hang";
        }
        
        else {
            statusBatch = status ? "Active - Con hang" : "Inactive - Con hang";
        }
        return statusBatch;
    }



    public LocalDate getImportDate() { return importDate; }
    public String getImportDateS() { return importDate.format(FORMATTER); }
    public void setImportDate(String input) {
        try {
            this.importDate = LocalDate.parse(input, FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Ngay nhap khong hop le! (dd/MM/yyyy)");
        }
    }

    public String getExpiryDateString() { return expiryDate.format(FORMATTER); }
    public LocalDate getExpiryDate() { return this.expiryDate; }

    // kiểm tra lô hàng hết hạn = cách xét xem ngày
    public boolean isExpired() {
        return expiryDate != null && LocalDate.now().isAfter(expiryDate);
    }

    public int getExpiryStatus(int warningDays) {
        if (expiryDate == null) return 0;
        LocalDate today = LocalDate.now();
        long daysLeft = ChronoUnit.DAYS.between(today, expiryDate);

        if (daysLeft < 0) return -1;            // quá hạn
        if (daysLeft <= warningDays) return 1;  // sắp hết hạn
        return 0;                               // còn hạn
    }


    @Override
    public String toString() {
        return  BID + "|" + product.getPID() + 
               "|" + quantity + 
               "|" + importDate.format(FORMATTER) + 
               "|" + status;
    }

    //Hàm so sánh giữa các Batch với nhau thông qua BatchID implement interface Comparable
    @Override
    public int compareTo(Batch bch){
        return this.getBatchId().compareTo(bch.getBatchId());
    }

}
