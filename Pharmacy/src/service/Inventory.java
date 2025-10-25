package service;
import interfaces.Management;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import models.Batch;
import models.OrderItem;
import models.Product;
import view.Extension;


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
    public Batch get(String ID){
        for (Batch u : inv) {
            if (u.getBatchId().equals(ID))
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
        Extension.printTableHeader("Ma lo hang","Ma san pham","Ten san pham","So luong","Ngay nhap lo hang","Trang thai","Canh bao");
        for (Batch elem : inv) {
            String status = switch(elem.getExpiryStatus(30)){
                case 1 -> "Sap het han";
                case 0 -> "Con han";
                case -1 ->"Qua han";
                default -> "Nothing";
            };
            Extension.printTableRow(elem.getBatchId(),elem.getProduct().getPID(),elem.getProduct().getName(),elem.getQuantity(),elem.getImportDate(), elem.getActive()?"Hoat dong":"Da khoa", status);
        }
    }

    @Override
    public void add(Batch batch){
        inv.add(batch);
    }    

    @Override
    public void delete(String ID){
        inv.removeIf(u -> u.getBatchId().equals(ID));
    } 


    @Override
    public String report(){
        return "Tong so luong lo hang ton kho trong he thong: " + inv.size() + "\n";
    }
}

