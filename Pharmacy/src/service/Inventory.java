package service;
import interfaces.IManagement;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import models.Batch;
import models.OrderItem;
import models.Product;
import view.Extension;


// Inventory hay Batch_Management
public class Inventory implements IManagement<Batch>{
    public static final String FILE_PATH = System.getProperty("user.dir") + "/resources/inventory.txt";
    private final ProductManager pm;
    private final Map<String, Batch> inv = new TreeMap<>();

    public Inventory(ProductManager pm) {
        this.pm = pm;
        loadInventory();
    }

    // lấy lo hang từ file
    private void loadInventory(){
        java.io.File file = new File(FILE_PATH);
        if(!file.exists()) return;
        inv.clear();
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
                inv.put(BID,(new Batch(BID, p, quantity, importDate, active)));
            }
        }
        catch(Exception e){
            System.out.println("Error: "+ e.getMessage());
        }
    }

    // kiểm tra tồn tại ID lo hang
    @Override
    public boolean exists(String ID) {
        boolean found = inv.containsKey(ID);
        return found;
    }

    @Override
    public Batch get(String ID){
        return inv.get(ID);
    }

    public long getStockbyProduct(Product product){
        long total = 0;
        for (Batch b : inv.values()) {
            if(b.getProduct().equals(product) && b.getStatus() && !b.isExpired()){
                total += b.getQuantity();
            }
        }
        return total;
    }

    public void deductStock(OrderItem ordered) {
        long needed = ordered.getQuantity(); 
        for (Batch b : inv.values()) {
            if (b.getProduct().equals(ordered.getProduct()) && b.getStatus() && !b.isExpired()) {
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
            for (Batch u : inv.values()) {
                fw.append(u.toString()).append("\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving inv: " + e.getMessage());
        }
    }

    @Override
     public void showList(){
        Extension.printTableHeader("Ma lo hang","Ma san pham","Ten san pham","So luong","Ngay nhap lo hang","Trang thai","Canh bao");
        for (Batch elem : inv.values()) {
            if(elem.getStatus()){
                String status = switch(elem.getExpiryStatus(30)){
                case 1 -> "Sap het han";
                case 0 -> "Con han";
                case -1 ->"Qua han";
                default -> "Nothing";
                };
                Extension.printTableRow(elem.getBatchId(),elem.getProduct().getPID(),elem.getProduct().getName(),elem.getQuantity(),elem.getImportDate(), elem.getStatusString(), status);
            }
        }
    }
    @Override
    public void blackList(){
        Extension.printTableHeader("Ma lo hang","Ma san pham","Ten san pham","So luong","Ngay nhap lo hang","Trang thai","Canh bao");
        for (Batch elem : inv.values()) {
            if(!elem.getStatus()){
                String status = switch(elem.getExpiryStatus(30)){
                case 1 -> "Sap het han";
                case 0 -> "Con han";
                case -1 ->"Qua han";
                default -> "Nothing";
                };
                Extension.printTableRow(elem.getBatchId(),elem.getProduct().getPID(),elem.getProduct().getName(),elem.getQuantity(),elem.getImportDate(), elem.getStatusString(), status);
            }
        }
    }
    

    @Override
    public void add(Batch batch){
        inv.put(batch.getBatchId(), batch);
    }    

    @Override
    public void delete() {
        Iterator<Batch> it = inv.values().iterator();
        while (it.hasNext()) {
            Batch c = it.next();
            if (!c.getStatus()) {
                it.remove();
                inv.remove(c.getBatchId()); 
            }
        }
    } 


    @Override
    public String report(){
        return "Tong so luong lo hang trong he thong: " + inv.size() + "\n";
    }
}

