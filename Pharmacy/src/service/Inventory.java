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
import models.Order;
import models.OrderItem;
import models.Product;
import view.Extension;
import view.Log;


// Inventory hay Batch_Management
public final class Inventory implements IManagement<Batch>{
    public static final String FILE_PATH = System.getProperty("user.dir") + "/resources/inventory.txt";

    private final ProductManager pm;// cần quản lý sản phẩm
    private OrderManager om = null; // cần xem lại đơn hàng để lấy dữ liệu xuất hàng
    
    private final Map<String, Batch> inv = new TreeMap<>();             // Map tìm lô theo mã
    private final Map<String, List<Batch>> inv_byPID = new TreeMap<>(); // Map tìm lô theo sản phẩm (sp tồn kho)

    private final Map<LocalDate, List<Order>> exports = new TreeMap<>(); // danh sách truy vấn xuất hàng theo ngày
    private final Map<LocalDate, List<Batch>> imports = new TreeMap<>(); // danh sách truy vấn nhập hàng theo ngày

    

    public Inventory(ProductManager pm, OrderManager om) {   //Constructor
        this.pm = pm;
        this.om = om;
        loadInventory();
        cancelBatch();
    }

    // lấy lo hang từ file
    private void loadInventory(){ 
        exports.clear();
        Map<String, Order> orderList = om.getMap();
        for (Order order : orderList.values()) { 
            LocalDate date = order.getpurchaseDate();
            List<Order> list = exports.getOrDefault(date, new ArrayList<>());
            list.add(order);
            exports.put(date, list);
        }
        
        java.io.File file = new File(FILE_PATH);    // load dữ liệu từ file inventory.txt vào
        if(!file.exists()) return;
        inv.clear();                                // clear Map, tránh lỗi
        inv_byPID.clear();
        imports.clear();
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

                if (importDate != null) {
                    List<Batch> list1 = imports.getOrDefault(importDate, new ArrayList<>()); // nhập lô ---> thêm nhập
                    list1.add(b);
                    imports.put(importDate, list1);
                }


                Log.exit("[Debug] Load Batch [" + BID + ";" + quantity + "] successfully.");
            }
            Log.exit("[Debug] Inventory data has been loaded successfully!\n");
        }
        catch(Exception e){
            Log.error("Error in inventory: "+ e.getMessage());
        }
    }

    // Tổng số lượng tồn kho
    public long getStockbyProduct(Product product) {
        long total = 0;
        List<Batch> batches = inv_byPID.get(product.getPID());
        if (batches == null) return 0; // tránh null pointer

        for (Batch b : batches) {
            if (b.getStatus() && !b.isExpired()) {
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
    
    // Hàm hủy lô hàng nếu đã hết hàng
    public void cancelBatch(){
        for (Batch elem : inv.values()) {
            if(elem.getQuantity() == 0 || elem.isExpired()){
                elem.setStatus(false);
            }
        }
    }
    
    
    // Hiển thị danh sách sản phẩm tồn kho
    public void showStockList() {
        int printed = 0;
        Extension.printTableHeader("Ma san pham", "Ten san pham", "Don vi", "Gia ca", "So luong ton");// tiêu đề bảng

        for (List<Batch> batches : inv_byPID.values()) { // nếu sản phẩm đó ko có danh sách lô thì bỏ qua bước sau
            if (batches == null || batches.isEmpty()) continue;

            Batch firstBatch = batches.get(0); // Lấy Product từ 1 batch bất kỳ (assume tất cả batch trong list cùng product)
            if (firstBatch == null) continue;
            Product p = firstBatch.getProduct();
            if (p == null) continue;

            long available = getStockbyProduct(p); // Tính tổng tồn khả dụng (bỏ qua batch expired hoặc inactive)

            // Chỉ hiển thị khi product active và còn hàng khả dụng
            if (p.getStatus() && available > 0) {
                String priceStr = String.format("%.2f VND", p.getPrice());
                Extension.printTableRow(
                    p.getPID(),
                    p.getName(),
                    p.getUnit(),
                    priceStr,
                    String.valueOf(available)
                );
                printed++;
            }
        }
        if (printed == 0) {
            Extension.printTableRow("Danh sach rong"); // danh sachs rỗng
        }
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

    // danh sách nhập hàng
    public void showImportHistory() {
        int count = 0;
        Extension.printTableHeader("Ngay nhap", "Ma lo hang", "Ma san pham", "Ten san pham", "So luong");

        for (Map.Entry<LocalDate, List<Batch>> entry : imports.entrySet()) {
            LocalDate date = entry.getKey();
            List<Batch> list = entry.getValue();

            for (Batch b : list) {
                String productId = b.getProduct() == null ? "Unknown" : b.getProduct().getPID();
                String productName = b.getProduct() == null ? "Unknown" : b.getProduct().getName();

                Extension.printTableRow(
                    date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    b.getBatchId(),
                    productId,
                    productName,
                    "+" +String.valueOf(b.getQuantity())
                );
                count++;
            }
        }

        if (count == 0) {
            Extension.printTableRow("Danh sach nhap hang rong");
        }
    } 

    // danh sách xuất hàng
    public void showExportHistory() {
        int count = 0;
        Extension.printTableHeader("Ngay xuat", "Ma don hang", "Ten khach hang", "So san pham" ,"So luong");

        for (Map.Entry<LocalDate, List<Order>> entry : exports.entrySet()) {
            LocalDate date = entry.getKey();
            List<Order> list = entry.getValue();

            for (Order o : list) {
                Extension.printTableRow(
                    date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    o.getOID(),
                    o.getCustomer() == null ? "Khach le" : o.getCustomer().getFullname(),
                    String.valueOf(o.getItems().size()),
                    "-"+String.valueOf(o.getTotalItems())
                );
                count++;
            }
        }

        if (count == 0) {
            Extension.printTableRow("Danh sach xuat hang rong");
        }
    }




    //IMPLEMENT MANAGEMENT

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

    @Override
    public void add(Batch batch) {
        // Thêm vào danh sách chính
        inv.put(batch.getBatchId(), batch);

        // Gộp lô vào danh sách theo sản phẩm
        List<Batch> productList = inv_byPID.getOrDefault(batch.getProduct().getPID(), new ArrayList<>());
        productList.add(batch);
        inv_byPID.put(batch.getProduct().getPID(), productList);

        //Ghi nhận lịch sử nhập hàng (imports)
        LocalDate date = batch.getImportDate();
        if (date != null) {
            List<Batch> importList = imports.getOrDefault(date, new ArrayList<>());
            importList.add(batch);
            imports.put(date, importList);
        }

        Log.success("Đã thêm lô hàng " + Log.toInfo(batch.getBatchId()) + " vào kho.");
    }

        

    @Override
    public void delete() {
        Iterator<Map.Entry<String, Batch>> it = inv.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Batch> entry = it.next();
            if (!entry.getValue().getStatus()) {
                it.remove(); 
            }
        }
    }

    @Override
    public void save() {
        cancelBatch();
        try (FileWriter fw = new FileWriter(FILE_PATH, false)) {
            for (Batch u : inv.values()) {
                fw.append(u.toString()).append("\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving inv: " + e.getMessage());
        }
    }


  @Override
    public void showList() {
        int k = 0;
        Extension.printTableHeader("Ma lo hang", "Ma san pham", "Ten san pham", "So luong", "Ngay nhap lo hang", "Trang thai");

        for (Batch elem : inv.values()) {
            if (elem == null) continue;          
            if (elem.getStatus()) {
                String importDate = elem.getImportDateS() == null ? "Chua co" : elem.getImportDateS();
                String productId = elem.getProduct() == null ? "Unknown" : elem.getProduct().getPID();
                String productName = elem.getProduct() == null ? "Unknown" : elem.getProduct().getName();

                Extension.printTableRow(
                    elem.getBatchId(),
                    productId,
                    productName,
                    String.valueOf(elem.getQuantity()),
                    importDate,
                    elem.getStatusString()
                );
                k++;
            }
        }

        if (k == 0) {
            Extension.printTableRow("Danh sach rong");
        }
    }

    @Override
    public void blackList() {
        int k = 0;
        Extension.printTableHeader("Ma lo hang", "Ma san pham", "Ten san pham", "So luong", "Ngay nhap lo hang", "Trang thai");

        for (Batch elem : inv.values()) {
            if (elem == null) continue;
            if (!elem.getStatus()) {
                String importDate = elem.getImportDateS() == null ? "Chua co" : elem.getImportDateS();
                String productId = elem.getProduct() == null ? "Unknown" : elem.getProduct().getPID();
                String productName = elem.getProduct() == null ? "Unknown" : elem.getProduct().getName();

                Extension.printTableRow(
                    elem.getBatchId(),
                    productId,
                    productName,
                    String.valueOf(elem.getQuantity()),
                    importDate,
                    elem.getStatusString()
                );
                k++;
            }
        }

        if (k == 0) {
            Extension.printTableRow("Danh sach rong");
        }
    }

 

    @Override
    public String report(){
        return "Tong so luong lo hang trong he thong: " + inv.size() + "\n";
    }
}

