package service;
import interfaces.Management;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import models.Batch;
import models.OrderItem;
import models.Product;


// Inventory hay Batch_Management
public class Inventory implements Management<Batch>{
    public static final String FILE_PATH = "resources/inventory.txt";
    private final List<Batch> inv;

    public Inventory(ProductManager pm) {
        inv = loadInventory(pm);
        Collections.sort(inv);
    }

    // lấy lo hang từ file
    private static List<Batch> loadInventory(ProductManager pm){
        List<Batch> list = new ArrayList<>();
        java.io.File file = new File(FILE_PATH);
        if(!file.exists()) return list;
        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String line;
            while ((line = br.readLine())!= null){
                String parts[] = line.split("\\|");
                String BID = parts[0];
                Product p = pm.get(parts[1]);
                if(p==null) continue;
                int quantity = Integer.parseInt(parts[2]);
                LocalDate importDate;
                try {
                    importDate = LocalDate.parse(parts[3], DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                } catch (Exception e) {
                    importDate = null;
                }
                boolean active = Boolean.parseBoolean(parts[4]);
                list.add(new Batch(BID, p, quantity, importDate, active));
            }
        }
        catch(Exception e){
            System.out.println("Error: "+ e.getMessage());
        }
        return list;
    }

    // kiểm tra tồn tại ID lo hang
    @Override
    public boolean exists(String ID) {
        for (Batch u : inv) {
            if (u.getBatchId().equals(ID)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Batch get(String username){
        for (Batch u : inv) {
            if (u.getBatchId().equals(username))
                return u;
        }
        return null;
    }

    public long getStockbyProduct(Product product){
        long total = 0;
        for (Batch b : inv) {
            if(b.getProduct().equals(product) && b.getActive() && !b.isExpired()){
                total += b.getQuantity();
            }
        }
        return total;
    }

    public void deductStock(OrderItem ordered) {
        long needed = ordered.getQuantity(); 
        for (Batch b : inv) {
            if (b.getProduct().equals(ordered.getProduct()) && b.getActive() && !b.isExpired()) {
                long available = b.getQuantity();
                if (needed <= 0) break;

                if (available >= needed) {
                    b.setQuantity(available - needed); 
                    needed = 0; 
                } else {
                    b.setQuantity(0);                  
                    needed -= available;               
                }
            }
        }
        save();
    }

    public double getExportPricebyProduct(Product p) {
        for (Batch b : inv) {
            if (b.getProduct().equals(p) && b.getActive() && !b.isExpired() && b.getQuantity() > 0) {
                return b.getExportPrice()/b.getQuantity();
            }
        }
        return 0; // không có lô hợp lệ
    }


    //lưu file cả list user
    @Override
    public void save() {
        try (FileWriter fw = new FileWriter(FILE_PATH, false)) {
            for (Batch u : inv) {
                fw.append(u.toString()).append("\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving inv: " + e.getMessage());
        }
    }

    @Override
    public void showList(){
        int i = 1;
        for(Batch b : inv){
            System.out.println(i+ ". " + b.getBatchId() + "[" + b.getProduct().getName() + "," + b.getQuantity() + "]" + b.getImportDate());
            i++;
        }
    }

    @Override
    public void add(Batch batch){
        inv.add(batch);
    }    

    @Override
    public void delete(String name){
        inv.removeIf(u -> u.getBatchId().equals(name));
    } 

    public void reportExpiringBatch(ProductManager pm){
        LocalDate today = LocalDate.now();
        final int warningDay = 30;
        boolean found = false;

        System.out.println("DANH SACH LO HANG SAP HET HAN (Duoi 30 ngay)");
        for(Batch b : inv){
            long daysleft = ChronoUnit.DAYS.between(today, b.getExpiryDate());
            if(daysleft <= warningDay){
                Product p = pm.get(b.getProduct().getPID());
                System.out.println(b.getBatchId() + "| SP: "+ p.getName() + "| Nhap ngay: " + b.getImportDate() + "| còn" + daysleft + "ngay | SL: " + b.getQuantity());
                found = true;
            }
        }
        if(!found) System.out.println("Khong co san pham sap het han");

    }


    @Override
    public String report(){
        return "Tong so luong lo hang ton kho trong he thong: " + inv.size() + "\n";
    } 
    public static void main(String[] args) {
        ProductManager pm = new ProductManager();
        Inventory inv = new Inventory(pm);
        inv.showList();
    }
}

