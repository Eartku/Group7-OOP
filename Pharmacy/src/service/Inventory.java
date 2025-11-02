package service;
import interfaces.IManagement;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import models.Batch;
import models.OrderItem;
import models.Product;
import view.Extension;
import view.Log;


// Inventory hay Batch_Management
public class Inventory implements IManagement<Batch>{
    public static final String FILE_PATH = System.getProperty("user.dir") + "/resources/inventory.txt";

    private final ProductManager pm;                                    // Quản lý sản phẩm
    private final Map<String, Batch> inv = new TreeMap<>();             // Map tìm lô theo mã
    private final Map<String, List<Batch>> inv_byPID = new TreeMap<>(); // Map tìm lô theo sản phẩm (sp tồn kho)

    public Inventory(ProductManager pm) {   //Constructor
        this.pm = pm;
        loadInventory();
    }

    // lấy lo hang từ file
    private void loadInventory(){ 
        java.io.File file = new File(FILE_PATH);    // load dữ liệu từ file inventory.txt vào
        if(!file.exists()) return;
        inv.clear();                                // clear Map, tránh lỗi
        inv_byPID.clear();
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
                Batch b = (new Batch(BID, p, quantity, importDate, active));
                inv.put(BID, b);
                List<Batch> list = inv_byPID.getOrDefault(p.getPID(), new ArrayList<>());   // nếu PID mới --> tạo ngay mảng mới lưu trữ lô hàng cùng sản phẩm
                list.add(b);                                                                // nếu PID trùng thì thêm Batch (lô) vào mảng
                inv_byPID.put(b.getProduct().getPID(), list);                               // đưa (key, value) vào TreeMap
                Log.exit("[Debug] Load Batch [" + BID + ";" + quantity + "] successfully.");
            }
            Log.exit("[Debug] Inventory data has been loaded successfully!\n");
        }
        catch(Exception e){
            Log.error("Error in inventory: "+ e.getMessage());
        }
    }

    // kiểm tra tồn tại ID lo hang
    @Override
    public boolean exists(String ID) {
        boolean found = inv.containsKey(ID);
        return found;
    }

    // lấy lô hàng từ Batch ID
    @Override
    public Batch get(String ID){
        return inv.get(ID);
    }

    // Tổng số lượng tồn kho
    public long getStockbyProduct(Product product){
        long total = 0;
        for (Batch b : inv.values()) {
            if(b.getProduct().equals(product) && b.getStatus() && !b.isExpired()){
                total += b.getQuantity();
            }
        }
        return total;
    }

    // Hàm trừ số lượng trong kho khi có đơn đặt hàng
    public void deductStock(OrderItem ordered) {
        Product p = ordered.getProduct();

        if(!p.getStatus()){ // neu sản phẩm bị khóa hay xóa
            Log.error("San pham nay khong con kha dung!");
            return;
        }

        long needed = ordered.getQuantity(); 

        List<Batch> batches = inv_byPID.get(p.getPID());
        if (batches == null || batches.isEmpty()) {
            Log.error("San pham nay khong co lo hang nao!");
            return;
        }

        for (Batch b : batches) {
            if (!b.getStatus() || b.isExpired()) continue;
            long available = b.getQuantity();
            if (available >= needed) {
                b.setQuantity(available - needed);
                needed = 0;
            } else {
                b.setQuantity(0);
                needed -= available;
            }
        }

        if (needed > 0) {
        Log.warning("⚠ Không đủ hàng! Thiếu " + Log.toError(String.valueOf(needed)) + " đơn vị.");
    }
        save();
    }

    //lưu file 
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

    // Hiển thị dạng bảng các lô hàng vẫn còn hoạt động
    @Override
     public void showList(){
        int k =0;
        Extension.printTableHeader("Ma lo hang","Ma san pham","Ten san pham","So luong","Ngay nhap lo hang","Trang thai","Canh bao");
        for (Batch elem : inv.values()) {
            if(elem.getStatus()){
                Extension.printTableRow(elem.getBatchId(),elem.getProduct().getPID(),elem.getProduct().getName(),elem.getQuantity(),elem.getImportDate(), elem.getStatusString(), elem.isExString());
            }
            k++;
        }
        if(k == 0){Extension.printTableRow("Danh sach rong");}
    }

    // Hiển thị dạng bảng các lô hàng không còn hoạt động hay bị hủy
    @Override
    public void blackList(){
        int k = 0;
        Extension.printTableHeader("Ma lo hang","Ma san pham","Ten san pham","So luong","Ngay nhap lo hang","Trang thai","Canh bao");
        for (Batch elem : inv.values()) {
            if(!elem.getStatus()){
                Extension.printTableRow(elem.getBatchId(),elem.getProduct().getPID(),elem.getProduct().getName(),elem.getQuantity(),elem.getImportDate(), elem.getStatusString(), elem.isExString());
            }
            k++;
        }
        if(k == 0){Extension.printTableRow("Danh sach rong");}
    }
    
    // Hiển thị danh sách sản phẩm tồn kho
    public void showStockList() { 
        int k = 0;
        Extension.printTableHeader("Ma san pham", "Ten san pham", "Don vi", "Gia ca");

        for (List<Batch> batches : inv_byPID.values()) {
            if (batches.isEmpty()) continue;

            Batch firstBatch = batches.get(0);
            Product p = firstBatch.getProduct();

            // Kiểm tra trạng thái sản phẩm
            if (p.getStatus()) {
                Extension.printTableRow(p.getPID(), p.getName(), p.getUnit(), p.getPrice() + " VND");
            }
            k++;
        }
        if(k == 0){Extension.printTableRow("Danh sach rong");}
    }


    // Chọn sản phẩm chỉ khi tồn tại trong kho
    public Product selectProduct(String keyword, Scanner sc) {
        //Tìm theo mã sản phẩm (PID)
        List<Batch> list = inv_byPID.get(keyword);
        if (list != null && !list.isEmpty()) {
            return list.get(0).getProduct(); // cùng 1 Product cho mọi batch nên chỉ lấy Batch đầu là đủ
        }

        //Tìm theo tên sản phẩm (search by keyword)
        ArrayList<Product> matched = new ArrayList<>();
        for (List<Batch> batches : inv_byPID.values()) {
            if (batches.isEmpty()) continue;
            Product p = batches.get(0).getProduct();
            if (p.getName().toLowerCase().contains(keyword.toLowerCase())) {
                matched.add(p);
            }
        }

        if (matched.isEmpty()) return null;

        Log.success("Da tim thay: " + matched.size() + " san pham.");

        //Nếu chỉ có 1 sản phẩm khớp → trả về luôn
        if (matched.size() == 1) return matched.get(0);

        //Nếu có nhiều sản phẩm cùng tên → để user chọn
        Log.request("Co nhieu san pham trung ky tu, chon theo STT cua danh sach sau:");
        for (int i = 0; i < matched.size(); i++) {
            Product p = matched.get(i);
            System.out.println(Log.toInfo(String.valueOf((i + 1))) + "\t|\t" + p.getPID() + "\t|\t" + p.getName() + "\t|\t" + p.getPrice() + " VND");
        }

        Log.request("Nhap STT: ");
        try {
            int choice = Integer.parseInt(sc.nextLine().trim());
            if (choice >= 1 && choice <= matched.size()) {
                return matched.get(choice - 1);
            }
        } catch (NumberFormatException e) {
            Log.warning("Lua chon khong hop le!");
        }
        return null; // không có trả về null
    }


    @Override
    public void add(Batch batch) {
        inv.put(batch.getBatchId(), batch);
        List<Batch> list = inv_byPID.getOrDefault(batch.getProduct().getPID(), new ArrayList<>());
        list.add(batch);
        inv_byPID.put(batch.getProduct().getPID(), list);
    }
        

    @Override
    public void delete() {
        Iterator<Map.Entry<String, Batch>> it = inv.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Batch> entry = it.next();
            if (!entry.getValue().getStatus()) {
                it.remove(); // xóa an toàn
            }
        }
    }
 


    @Override
    public String report(){
        return "Tong so luong lo hang trong he thong: " + inv.size() + "\n";
    }
}

