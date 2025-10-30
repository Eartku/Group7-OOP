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
import view.Extension;


public class OrderManager implements IManagement<Order> {
    //các đơn hàng sẽ được lưu trong file "orders.txt"
    public static final String FILE_PATH = System.getProperty("user.dir") + "/resources/orders.txt";
    private static final String FILE_PATH_2 = System.getProperty("user.dir") +"/resources/orderitems.txt";
    private final CustomerManager cm;
    private final ProductManager pm;

    //quản lý bằng ArrayList
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
                        items.add(new OrderItem(pm.get(pid), qty));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
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
                try {
                    purchaseDate = LocalDate.parse(parts[2], DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                } catch (Exception e) {
                    purchaseDate = null;
                }
                orders.put(oid, new Order(oid, loadItems(oid), cm.get(cid), purchaseDate, status));

            }
        } catch(Exception e){
            System.out.println("Error: "+ e.getMessage());
        }
    }

    // kiểm tra tồn tại theo OID
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
            System.out.println("Error saving orders: " + e.getMessage());
        }

        try (FileWriter fl = new FileWriter(FILE_PATH_2, false)) {
            for (Order u : orders.values()) {
                fl.append(u.getOID()); 
                for (OrderItem item : u.getItems()) {
                    if (item.getProduct() == null) {
                        System.err.println("Bỏ qua item NULL trong hóa đơn " + u.getOID());
                        continue; // bỏ qua sản phẩm lỗi
                    }
                    fl.append("|").append(item.getProduct().getPID());
                    fl.append("|").append(String.valueOf(item.getQuantity()));
                }
                fl.append("\n"); 
            }
        } catch (IOException e) {
            System.out.println("Error saving order items: " + e.getMessage());
        }
    }


    @Override
    public void showList(){
        Extension.printTableHeader("Ma don hang","Ma khach hang","So san pham","Trang thai","Ngay dat mua");
        for (Order elem : orders.values()) {
            if(elem.getStatus())
                Extension.printTableRow(elem.getOID(),elem.getCustomer().getCID(),elem.getItems().size(),elem.getStatusString(),elem.getpurchaseDate());
        }
    }

    @Override
    public void blackList(){
        Extension.printTableHeader("Ma don hang","Ma khach hang","So san pham","Trang thai","Ngay dat mua");
        for (Order elem : orders.values()) {
            if(!elem.getStatus())
                Extension.printTableRow(elem.getOID(),elem.getCustomer().getCID(),elem.getItems().size(),elem.getStatusString(),elem.getpurchaseDate());
        }
    }

    public static ArrayList<OrderItem> buyProducts(Scanner sc, ProductManager pm) {
        ArrayList<OrderItem> list = new ArrayList<>();
        while(true){
            pm.showList();
            System.out.println("Nhap ten hay ID pham muon mua (Nhap 0 de thoat): ");
            String choice = sc.nextLine().trim();
            if(choice.equals("0")) break;

            Product selected = pm.selectProduct(choice, sc);

            if(selected == null){
                System.out.println("San pham khong ton tai!");
                continue;
            }

            System.out.println("Nhap so luong san pham: ");
            int quantity = Integer.parseInt(sc.nextLine().trim());
            if(quantity <= 0){
                System.out.println("So luong toi thieu phai la 1!");
                continue;
            }
             // Kiểm tra trùng sản phẩm thì chỉ cần cộng dồn số lượng
            boolean found = false;
            for (OrderItem oi : list) {
                if (oi.getProduct().equals(selected)) {
                    oi.setQuantity(oi.getQuantity() + quantity);
                    found = true;
                    break;
                }
            }

            System.out.println("Da chon " + quantity + " x " + selected.getName());
            if(!found) list.add(new OrderItem(selected, quantity));

            System.out.println("Chon them san pham khac? (0 to Stop)");
            String more = sc.nextLine().trim();
            if(more.equalsIgnoreCase("0")) break;
        }
        return list;
    }


    @Override
    public void add(Order order){
        orders.put(order.getOID(), order);
    }  

    @Override
    public void delete() {
        Iterator<Order> it = orders.values().iterator();
        while (it.hasNext()) {
            Order c = it.next();
            if (!c.getStatus()) {
                it.remove();
                orders.remove(c.getOID()); 
            }
        }
    } 

    @Override
    public String report(){
        return "Tong so don hang trong he thong: " + orders.size() + "\n";
    } 

    public void history(Customer cs) {
        Extension.printTableHeader("Ma hoa don", "Ten cac san pham", "So luong", "Trang thai");
        for (Order o : orders.values()) {
            if (o.getCustomer().equals(cs)) {
                StringBuilder item = new StringBuilder();
                for (OrderItem it : o.getItems()) {
                    if (item.length() > 0) item.append(", ");
                    item.append(it.getProductsName());
                }
                Extension.printTableRow(
                    o.getOID(),
                    item.toString(),
                    String.valueOf(o.getItems().size()),
                    o.getStatusString()
                );
            }
        }
    }    
}