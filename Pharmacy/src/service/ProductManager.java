package service;
import interfaces.Management;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import models.Drug;
import models.NonDrug;
import models.Product;


public class ProductManager implements Management<Product>{
    public static final String FILE_PATH = "resources/products.txt";
    private final List<Product> products;

    public ProductManager() {
        products = loadProducts();
        Collections.sort(products);
    }

    // lấy p từ file
        private static List<Product> loadProducts() {
        List<Product> list = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length < 9) continue; // bỏ qua dòng lỗi

                String type = parts[8].trim(); // lấy ký tự D hoặc N để phân biệt trong file txt

                switch (type) {
                    case "D" -> list.add(new Drug(
                            parts[0], // PID
                            parts[1], // name
                            parts[2], // unit
                            Double.parseDouble(parts[3]), //price
                            Integer.parseInt(parts[4]),//SLM
                            parts[5], // dosage
                            parts[6], // ingredient
                            Boolean.parseBoolean(parts[7])
                    ));

                    case "N" -> list.add(new NonDrug(
                            parts[0],
                            parts[1],
                            parts[2],
                            Double.parseDouble(parts[3]),
                            Integer.parseInt(parts[4]),
                            parts[5], //type
                            parts[6], // 
                            parts[7]  // usage
                    ));

                    default -> System.out.println("San phan khong xac dinh: " + type);
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading products: " + e.getMessage());
        }
        return list;
    }

    // kiểm tra tồn tại pname
    @Override
    public boolean exists(String ID) {
        for (Product u : products) {
            if (u.getPID().equals(ID)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Product get(String ID){
        for (Product u : products) {
            if (u.getPID().equals(ID))
                return u;
        }
        return null;
    }

    public Product getbyIndex(int index){
        return products.get(index-1);
    }

    public ArrayList<Product> getProductbyName(String keyword){
        ArrayList<Product> matched = new ArrayList<>();
        for (Product u : products) {
            if (u.getName().toLowerCase().contains(keyword.toLowerCase().trim())){
                matched.add(u);
            }
        }
        return matched;
    }

    // thay vì làm dài code phần menu, tái sử dụng trong ProductManger
    public Product selectProduct(String keyword, Scanner sc) {
        for (Product p : products) {
            if (p.getPID().equalsIgnoreCase(keyword)) {
                return p;
            }
        }

        ArrayList<Product> matched = getProductbyName(keyword);
        if (matched.isEmpty()) return null;

        if (matched.size() == 1) return matched.get(0);

        System.out.println("Co nhieu san pham trung ten, hay chon STT:");
        for (int i = 0; i < matched.size(); i++) {
            Product p = matched.get(i);
            System.out.println((i + 1) + ". " + p.getPID() + " | " + p.getName() + " | " + p.getPrice() + " VND");
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
        int i = 1;
        for (Product p : products) {
            System.out.println(i + ". " + p.getPID() + "-" + p.getName() + ": " + p.getPrice() +" VND");
            i++;
        }
    }

    @Override
    public void add(Product p){
        products.add(p);
    }  

    @Override
    public void delete(String ID){
        products.removeIf(u -> u.getPID().equals(ID));
    }

    @Override
    public String report(){
        int count = 0;
        for (Product p : products) {
            if (p instanceof Drug) {
                count++;
            }
        }
        int count2 = 0;
        for (Product p : products) {
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
            for (Product p : products) {
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
                            "D"
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
                            "N"
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
}

