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
import models.Customer;
import models.Order;
import models.OrderItem;
import models.Product;
import ultils.Enhance;
import ultils.Log;
import view.ManageProductsMenu;


public class OrderManager implements IManagement<Order> {
    //các đơn hàng sẽ được lưu trong file "orders.txt"
    public static final String FILE_PATH = System.getProperty("user.dir") + "/resources/orders.txt";
    private static final String FILE_PATH_2 = System.getProperty("user.dir") +"/resources/orderitems.txt";
    private final CustomerManager cm; // cần quản lý khách hàng
    private final ProductManager pm;  // cần quản lý sản phẩm

    //quản lý bằng Map theo OID
    private final Map<String, Order> orders = new TreeMap<>();

    //constructor
    public OrderManager(CustomerManager cm, ProductManager pm) {
        this.cm = cm;
        this.pm = pm;
        loadOrders();
    }

    // load các sản phẩm của lô hàng đó thông qua OID
    private List<OrderItem> loadItems(String OID){
        List<OrderItem> items = new ArrayList<>();
        java.io.File file = new File(FILE_PATH_2); // định dạng OID;Item1;q1;item2;q2;item3;q3;... với item là obj product

        if(!file.exists()) return items;
        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String line;
            while((line = br.readLine())!=null){
                String[] parts = line.split("\\|");
                if(parts[0].equals(OID)){
                    for (int i = 1; i < parts.length; i+= 2){
                        String pid = parts[i];
                        int qty = Integer.parseInt(parts[i+1]);
                        OrderItem it = new OrderItem(pm.get(pid), qty);
                        items.add(it);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("[WARNING] Error in order items loader: " + e.getMessage());
        }
        return items;
    }
    // load các đơn hàng từ file dựa vào OID
    private void loadOrders(){ // định dạng file: OID|CID|puschasedate|status
        java.io.File file = new File(FILE_PATH);
        if(!file.exists()) return;
        orders.clear();
        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String line;
            while ((line = br.readLine())!= null){
                String parts[] = line.split("\\|");
                String oid = parts[0];
                String cid = parts[1];
                boolean status = Boolean.parseBoolean(parts[3]);
                LocalDate purchaseDate;
                List<OrderItem> items = loadItems(oid);
                try {
                    purchaseDate = LocalDate.parse(parts[2], DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                } catch (Exception e) {
                    purchaseDate = null;
                }
                Order o = new Order(oid, items , cm.get(cid), purchaseDate, status);
                orders.put(oid, o);
                Log.info("[Debug] Load Order [" + o.getOID()+";"+ o.getCustomer().getCID()+"] successfully.");
            }
            Log.info("[Debug] Orders data has been loaded successfully!\n");
        } catch(Exception e){
            System.out.println(" [WARNING] Error in order manager: "+ e.getMessage());
        }
    }

// hàm trả về lại danh sách đơn hàng để quản lý nhập xuất
    public Map<String, Order> getMap(){
        return orders;
    }
    

    public static ArrayList<OrderItem> buyProducts(Scanner sc, ProductManager pm, Inventory inv) {
        ArrayList<OrderItem> list = new ArrayList<>();
        while(true){
            
            inv.showStockList();
            Log.request("Nhap ten hay ID pham muon mua (Nhap 0 de thoat): ");
            String choice = sc.nextLine().trim();
            if(choice.equals("0")) break;

            if(choice.isEmpty()){
                Log.warning("Vui long nhap lai!");
                sc.nextLine();
                continue;
            } 

            Product selected = pm.selectProduct(choice, sc);


            if(selected == null){
                Log.error("San pham khong ton tai!");
                continue;
            }

            ManageProductsMenu.printProduct(selected);

            String quantity = "";
            long quantityValue = 0;
            while (true) {
                Log.request("Nhap so luong san pham: ");
                quantity = sc.nextLine().trim();
                // Kểm tra rỗng
                if (quantity.isEmpty()) {
                    Log.warning("Vui long nhap lai! Khong duoc de trong.");
                    continue;
                }
                // Kiểm tra phải là số nguyên dương
                if (!quantity.matches("\\d+")) {
                    Log.warning("So luong chi duoc phep la so nguyen duong!");
                    continue;
                }
                // Chuển sang số
                quantityValue = Long.parseLong(quantity);
                // Kiểm tra giá trị
                if (quantityValue <= 0) {
                    Log.warning("So luong toi thieu phai la 1!");
                    continue;
                }
                // Nếu hợp lệ thì thoát vòng lặp
                break;
            }

            // Khi thoát ra thì quantityValue là số lượng hợp lệ
             // Kiểm tra trùng sản phẩm thì chỉ cần cộng dồn số lượng
            boolean found = false;
            for (OrderItem oi : list) {
                if (oi.getProduct().equals(selected)) {
                    oi.setQuantity(oi.getQuantity() + quantityValue);
                    found = true;
                    break;
                }
            }

            Log.success("Da chon " + quantity + " x " + selected.getName());
            if(!found) list.add(new OrderItem(selected, quantityValue));

            Log.request("Chon them san pham khac? (0 to Stop)");
            String more = sc.nextLine().trim();
            if(more.equalsIgnoreCase("0")) break;
        }
        return list;
    }

    public long quantity(Order ordered){
        long q = 0;
        for (OrderItem elem : ordered.getItems()) {
            if(elem.getProduct().getStatus()){
                q += elem.getQuantity();
            }
        }
        return q;
    }

    //IMPLEMENT MANAGEMENT

    @Override
    public void add(Order order){
        orders.put(order.getOID(), order);
    }  


    @Override
    public void delete() {
        Iterator<Order> it = orders.values().iterator();
        while (it.hasNext()) {
            Order o = it.next();
            if (!o.getStatus()) {
                it.remove(); 
            }
        }
}


    @Override
    public boolean exists(String ID) {
        boolean found = orders.containsKey(ID);
        return found;
    }

    // lấy đơn hàng theo ID
    @Override
    public Order get(String ID){
        return orders.get(ID);
    }

    //lưu file cả list user
    @Override
    public void save() {
        // Ghi order
        try (FileWriter fw = new FileWriter(FILE_PATH, false)) {
            for (Order u : orders.values()) {
                fw.append(u.toString()).append("\n");
            }
        } catch (IOException e) {
            Log.error("[WARNING] Error saving orders: " + e.getMessage());
        }

        try (FileWriter fl = new FileWriter(FILE_PATH_2, false)) {
            for (Order u : orders.values()) {
                fl.append(u.getOID()); 
                for (OrderItem item : u.getItems()) {
                    if (item.getProduct() == null) {
                        Log.warning("[WARNING] Bo qua item NULL " + u.getOID());
                        continue; // bỏ qua sản phẩm lỗi
                    }
                    fl.append("|").append(item.getProduct().getPID());
                    fl.append("|").append(String.valueOf(item.getQuantity()));
                }
                fl.append("\n"); 
            }
        } catch (IOException e) {
            Log.error("[WARNING] Error in order manager: " + e.getMessage());
        }
    }


    @Override
    public void showList(){
        int k =0;
        Enhance.printTableHeader("Ma don hang","Ma khach hang","So luong san pham","Trang thai","Ngay dat");
        for (Order elem : orders.values()) {
            if(elem.getStatus())
                Enhance.printTableRow(elem.getOID(),elem.getCustomer().getCID(),quantity(elem),elem.getStatusString(),elem.getpurchaseDate());
            k++;
        }
        if(k == 0){Enhance.printTableRow("Danh sach rong");}
    }

    @Override
    public void blackList(){
        int k=0;
        Enhance.printTableHeader("Ma don hang","Ma khach hang","So luong san pham","Trang thai","Ngay dat mua");
        for (Order elem : orders.values()) {
            if(!elem.getStatus())
                Enhance.printTableRow(elem.getOID(),elem.getCustomer().getCID(),quantity(elem),elem.getStatusString(),elem.getpurchaseDate());
            k++;
        }
        if(k == 0){Enhance.printTableRow("Danh sach rong");}
    }

    @Override
    public String report(){
        return "Tong so don hang trong he thong: " + orders.size() + "\n";
    } 

    public void historyList(Customer cs){
        Enhance.printTableHeader("Ma hoa don", "San pham", "Tong SL", "Trang thai");
        for (Order order : orders.values()) {
            if (order.getCustomer() == null) continue;
            if (!order.getCustomer().equals(cs)) continue;

            // Ghép tên sản phẩm từ các OrderItem
            String productNames = order.getItems().stream()
                    .map(OrderItem::getProductsName)
                    .reduce((a, b) -> a + "_ " + b)
                    .orElse("(0)");

            long totalQty = quantity(order);

            Enhance.printTableRow(
                    order.getOID(),
                    productNames,
                    String.valueOf(totalQty),
                    order.getStatusString()
            );
        }
    }
}