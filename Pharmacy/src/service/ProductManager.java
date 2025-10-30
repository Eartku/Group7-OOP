package service;
import interfaces.IManagement;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import models.Drug;
import models.NonDrug;
import models.Product;
import view.Extension;


public class ProductManager implements IManagement<Product>{
    public static final String FILE_PATH = System.getProperty("user.dir") + "/resources/products.txt";
 
    private final Map<String, Product> products = new TreeMap<>();

    public ProductManager() {
        loadProducts();
    }

    // lấy p từ file
    private void loadProducts() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return ;

        products.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length < 9){
                    System.out.println("[WARN] Invalid line in products.txt: " + line);
                    continue; // bỏ qua dòng lỗi
                }

                String key = parts[8].trim(); // lấy ký tự D hoặc N để phân biệt trong file txt
                boolean status = Boolean.parseBoolean(parts[9]);
                String PID = parts[0].trim(); // PID
                String name = parts[1].trim();// name
                String unit = parts[2].trim();
                double price = Double.parseDouble(parts[3].trim());
                Integer SLM = Integer.valueOf(parts[4]);
                Product p = switch (key) {
                    case "D" -> new Drug(
                            PID, name, unit, price, SLM,
                            parts[5], // dosage
                            parts[6], // ingredient
                            Boolean.parseBoolean(parts[7]),//pR
                            status
                            
                    );

                    case "N" -> new NonDrug(
                            PID, name, unit, price, SLM,
                            parts[6], //man
                            parts[7], // type
                            parts[5],  // usage
                            status
                    );

                    default -> null;
                };
                products.put(PID, p);
            }
        } catch (Exception e) {
            System.out.println("[WARN] Error loading products: " + e.getMessage());
        }
    }

    // kiểm tra tồn tại pname
    @Override
    public boolean exists(String ID) {
        boolean found = products.containsKey(ID);
        return found;
    }

    @Override
    public Product get(String ID){
        return products.get(ID);
    }

    public ArrayList<Product> getProductByName(String keyword) {
        ArrayList<Product> matched = new ArrayList<>();
        for (Product p : products.values()) {
            if (p.getName().toLowerCase().contains(keyword.toLowerCase().trim())) {
                matched.add(p);
            }
        }
        return matched;
    }

    // thay vì làm dài code phần menu, tái sử dụng trong ProductManger
    public Product selectProduct(String keyword, Scanner sc) {
        Product direct = products.get(keyword);
        if(direct != null) return direct;

        ArrayList<Product> matched = getProductByName(keyword);
        if (matched.isEmpty()) return null;
        System.out.println("Da tim thay: " + matched.size());

        if (matched.size() == 1) return matched.get(0);

        System.out.println("Co nhieu san pham trung ten, hay chon STT:");
        for (int i = 0; i < matched.size(); i++) {
            Product p = matched.get(i);
            System.out.println((i + 1) + "\t|\t" + p.getPID() + "\t|\t " + p.getName() + "\t|\t " + p.getPrice() + " VND");
        }

        System.out.print("Nhap STT: ");
        try {
            int choice = Integer.parseInt(sc.nextLine().trim());
            if (choice >= 1 && choice <= matched.size()) {
                return matched.get(choice - 1);
            }
        } catch (NumberFormatException e) {
            System.out.println("Lua chon khong hop le!");
        }

        return null;
    }

    @Override
    public void showList(){
        Extension.printTableHeader("Ma san pham","Ten san pham","Don vi","Gia ca","Han dung","Trang thai");
        for (Product elem : products.values()) {
            if(elem.getStatus())
                Extension.printTableRow(elem.getPID(),elem.getName(),elem.getUnit(),elem.getPrice()+"VND",elem.getShelfLifeInfo(),elem.getStatusString());
        }
    }

    @Override
    public void blackList(){
        Extension.printTableHeader("Ma san pham","Ten san pham","Don vi","Gia ca","Han dung","Trang thai");
        for (Product elem : products.values()) {
            if(!elem.getStatus())
            Extension.printTableRow(elem.getPID(),elem.getName(),elem.getUnit(),elem.getPrice()+"VND",elem.getShelfLifeInfo(),elem.getStatusString());
        }
    }

    @Override
    public void add(Product p){
        products.put(p.getPID(), p);
    }  

    @Override
    public void delete() {
        Iterator<Product> it = products.values().iterator();
        while (it.hasNext()) {
            Product c = it.next();
            if (!c.getStatus()) {
                it.remove();
                products.remove(c.getPID()); 
            }
        }
    }

    @Override
    public String report(){
        int count = 0;
        for (Product p : products.values()) {
            if (p instanceof Drug) {
                count++;
            }
        }
        int count2 = 0;
        for (Product p : products.values()) {
            if (p instanceof NonDrug) {
                count2++;
            }
        }
        return "Tong so luong san pham trong he thong: " + products.size() + "\n"
        +  "So luong thuoc: " +  count + "\n"
        +  "So luong phi thuoc " +  count2 + "\n";
    } 

    @Override
    public void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Product p : products.values()) {
                String line = "";
                switch (p) {
                    case Drug d -> line = String.join("|",  
                            d.getPID(),
                            d.getName(),
                            d.getUnit(),
                            String.valueOf(d.getPrice()),
                            String.valueOf(d.getSLM()),
                            d.getDosage(),
                            d.getIngredient(),
                            String.valueOf(d.getpR()),
                            "D",
                            String.valueOf(d.getStatus())
                    );
                    case NonDrug n -> line = String.join("|",  
                            n.getPID(),
                            n.getName(),
                            n.getUnit(),
                            String.valueOf(n.getPrice()),
                            String.valueOf(n.getSLM()),
                            n.getUsage(),
                            n.getManufacturer(),
                            n.getType(),
                            "N",
                            String.valueOf(n.getStatus())
                    );
                    default -> {
                    }
                }
                    bw.write(line);
                    bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Loi khi luu san pham: " + e.getMessage());
        }
    }

    public int length(){return products.size();}
}

