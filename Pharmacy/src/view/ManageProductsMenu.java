package view;

import data.Data;
import interfaces.IManageMenu;
import java.util.Scanner;
import models.Drug;
import models.NonDrug;
import models.Product;
import service.Inventory;
import service.ProductManager;

public class ManageProductsMenu implements IManageMenu{
    private final ProductManager pm;
    private final Scanner sc;

    public ManageProductsMenu(ProductManager pm, Scanner sc) {
        this.pm = pm;
        this.sc = sc;
    }

    @Override
    public void mainMenu(){
        Extension.clearScreen();

        if (pm == null) {
            Log.error("Khong the quan ly san pham!");
            return;
        }

        while (true) {
            Extension.clearScreen();
            System.out.println("==== PRODUCT MANAGER ====");
            
            System.out.println(pm.report());
            System.out.println("==== DANH SACH CAC SAN PHAM TRONG DANH MUC CHO PHEP ====");
            pm.showList();
            System.out.println("==== DANH SACH CAC SAN PHAM TRONG DANH MUC KHOA ====");
            pm.blackList();
            System.out.println("1. Them san pham - Add Product");
            System.out.println("2. An/Hien san pham - Hide/Show Product");
            System.out.println("3. Chinh sua thong tin san pham - Edit product INFO");
            System.out.println("4. Tim kiem va Xem - Search & View");
            Log.exit("0. Huy - Cancel");
            int choice = Extension.readIntInRange("Nhap lua chon (0-4):", 0, 4, sc);

            switch (choice) {
                case 1 -> {
                    addMenu();
                    Extension.pause(sc);
                }
                case 2 -> {
                    System.out.println("1. An san pham - Hide Product");
                    System.out.println("2. Hien san pham - Show Product");
                    Log.exit("0. Huy - Cancel");
                    int choice2 = Extension.readIntInRange("Nhap lua chon (0-2):", 0, 2, sc);
                    switch (choice2) {
                        case 0 ->{break;}
                        case 1 ->blockMenu();
                        case 2 ->activeMenu();
                        default -> System.out.println("Khong hop le!");
                    }
                    Extension.pause(sc);
                }
                case 3 -> {
                    updateMenu();
                    Extension.pause(sc);
                }
                case 4 -> {
                    viewMenu();
                    Extension.pause(sc);
                }
                case 0 -> {
                    Log.info("Thoat chuong trinh. Tam biet!");
                    return;
                }
                default -> Log.warning("Lua chon khong hop le!");
            }
        }
    }

    @Override
    public void addMenu(){
        while(true){
            Extension.clearScreen();
            System.out.println("==== ADD NEW PRODUCT ====");
            String PID = Data.generateNewID(ProductManager.FILE_PATH, 'P');
            Log.request("Ten san pham (Nhap 0 de quay lai): ");
            String name;
            while(true){
                Log.request("Ten san pham (Nhap 0 de quay lai): ");
                name = sc.nextLine().trim();
                if (name.equals("0")) {
                    Log.info("Huy thao tac.");
                    return;
                }
                if(!name.isEmpty()) break;
                else Log.warning("Khong duoc bo trong mien nay!");
            }

            
            String unit;
            while(true){
                Log.request("Don vi san pham (Vien/Vi/Hop/Cai/vv): ");
                unit = sc.nextLine().trim();
                if(!unit.isEmpty()) break;
                else Log.warning("Khong duoc bo trong mien nay!");
            }

            double price;
            while (true) {
                Log.request("Gia thi truong: ");
                String inputPrice = sc.nextLine().trim();
                try {
                    price = Double.parseDouble(inputPrice);
                    if (price <= 0) {
                        Log.warning("Gia phai lon hon 0!");
                        continue;
                    }
                    break;
                } catch (NumberFormatException e) {
                    Log.warning("Gia tien khong hop le! Vui long nhap so.");
                }
            }

            Log.request("Thoi han su dung (theo thang), bo trong neu khong co: ");
            String input = sc.nextLine().trim();
            Integer SLM;

            if (!input.isEmpty()) {
                try {
                    SLM = Integer.valueOf(input);
                    if (SLM < 0) {
                        Log.warning("Thoi han khong the am! Mac dinh la 0 neu khong co.");
                        SLM = 0;
                    }
                } catch (NumberFormatException e) {
                    Log.error("Gia tri khong hop le! Mac dinh la 0.");
                    SLM = 0;
                }
            } else {
                SLM = 0;
            }

            String confirm;
            while (true) {
                Log.request("San pham co phai la thuoc? (y/n): ");
                confirm = sc.nextLine().trim().toLowerCase();
                if (confirm.equals("y") || confirm.equals("n")) break;
                Log.warning("Chi nhap y hoac n!");
            }

            Product product;

            if (confirm.equalsIgnoreCase("y")) {
                String ingredient;
                while(true){
                    Log.request("Nhap thanh phan chinh cua thuoc: ");
                    ingredient = sc.nextLine().trim();
                    if(!ingredient.isEmpty()) break;
                    else Log.warning("Khong duoc bo trong mien nay!");
                }
                Log.success("Successful!");
                String dosage;
                while(true){
                    dosage = sc.nextLine().trim();
                    if(!dosage.isEmpty()) break;
                    else Log.warning("Khong duoc bo trong mien nay!");
                }           
                Log.success("Successful!");
                
                boolean pr = false;
                while (true) {
                    Log.request("Thuoc co ke don cua bac si khong? (y/n):");
                    String pR = sc.nextLine().trim().toLowerCase();
                    if (pR.equals("y") || pR.equals("n")) break;
                    Log.warning("Chi nhap y hoac n!");
                    pr = pR.equalsIgnoreCase("y");
                }
                Log.success("Successful!");

                product = new Drug(PID, name, unit, price, SLM, dosage, ingredient, pr, true);
            } else {
                
                String manufacturer;
                while(true){
                    Log.request("Don vi/Cong ty san xuat: ");
                    manufacturer= sc.nextLine().trim();
                    if(!manufacturer.isEmpty()) break;
                    else Log.warning("Khong duoc bo trong mien nay!");
                }
                Log.success("Successful!");
                String usage;
                while(true){
                    Log.request("Nhap cong dung san pham");
                    usage = sc.nextLine().trim();
                    if(!usage.isEmpty()) break;
                    else Log.warning("Khong duoc bo trong mien nay!");
                }           
                Log.success("Successful!");
                
                String type;
                while(true){
                    Log.request("Nhap loai san pham");
                    type = sc.nextLine().trim();
                    if(!type.isEmpty()) break;
                    else Log.warning("Khong duoc bo trong mien nay!");
                } 
                Log.success("Successful!");

                product = new NonDrug(PID, name, unit, price, SLM, manufacturer, type, usage, true);
            }

            pm.add(product);
            Log.success("Da them san pham thanh cong!");
            pm.save();
            Log.request("Tiep tuc them? [0 de thoat]: ");
            Extension.pause(sc);
            if(sc.nextLine().trim().equals("0")) return;
        }
    }

    @Override
    public void blockMenu() {
        while (true) {
            Extension.clearScreen();
            System.out.println("==== HIDE-INACTIVE PRODUCT ====");
            System.out.println("==== DANH SACH CAC SAN PHAM CON HOAT DONG ====");
            pm.showList();
            Log.request("Nhap ID hoac ten san pham muon AN (HIDE) (hoac nhap 0 de quay lai): ");
            String inputID = sc.nextLine().trim();

            if (inputID.equals("0")) {
                Log.info("Huy thao tac.");
                return;
            }

            Product target = pm.selectProduct(inputID, sc);

            if (target == null || target.getStatus() == false) {
                Log.error("Khong tim thay san pham voi tu khoa: " + inputID);
                Extension.pause(sc);
                continue;
            }

            // Xác nhận xóa
            Log.request("Ban co chac muon AN san pham " + target.getName() + "? (y/n): ");
            String confirm = sc.nextLine().trim();

            if (confirm.equalsIgnoreCase("y")) {
                target.setStatus(false);
                Log.success("Da an san pham: " + target.getName());
                pm.save();
            } else {
                Log.info("Da huy thao tac AN. Qual lai menu chinh");
            }
            Log.request("Tiep tuc? [0 de thoat]: ");
            if(sc.nextLine().trim().equals("0")) return;
            Extension.pause(sc);
        }
    }

    @Override
    public void activeMenu() {
        while (true) {
            Extension.clearScreen();
            System.out.println("==== SHOW-ACTIVATE PRODUCT ====");
            System.out.println("==== DANH SACH CAC SAN PHAM DANG BI KHOA ====");
            pm.blackList();
            Log.request("Nhap ID hoac ten san pham muon HIEN (hoac nhap 0 de quay lai): ");
            String inputID = sc.nextLine().trim();

            if (inputID.equals("0")) {
                Log.info("Huy thao tac.");
                return;
            }

            Product target = pm.selectProduct(inputID, sc);

            if (target == null || target.getStatus() == true) {
                Log.error("Khong tim thay san pham voi tu khoa: " + inputID);
                Extension.pause(sc);
                continue;
            }

            Log.request("Ban co chac muon HIEN THI san pham " + target.getName() + "? (y/n): ");
            String confirm = sc.nextLine().trim();

            if (confirm.equalsIgnoreCase("y")) {
                target.setStatus(true);
                Log.success("Da HIEN san pham: " + target.getName());
                pm.save();
            } else {
                Log.info("Da huy thao tac HIEN. Quay lai menu chinh");
            }
            Log.request("Tiep tuc? [0 de thoat]: ");
            if(sc.nextLine().trim().equals("0")) return;
            Extension.pause(sc);
        }
    }

    @Override
    public void updateMenu() {
        while(true){
            Extension.clearScreen();
            System.out.println("==== EDIT-UPDATE PRODUCT ====");
            System.out.println("==== DANH SACH CAC SAN PHAM TRONG DANH MUC CHO PHEP ====");
            pm.showList();
            System.out.println("==== DANH SACH CAC SAN PHAM TRONG DANH MUC KHOA ====");
            pm.blackList();
            Log.request("Nhap ID hoac ten san pham muon chinh sua (hoac nhap 0 de quay lai): ");
            String ID = sc.nextLine().trim();
            
            if (ID.equals("0")) {
                    Log.info("Huy thao tac xoa.");
                    return;
                }

            if (!pm.exists(ID)) {
                Log.error("Khong tim thay san pham voi tu khoa: " + ID);
                Extension.pause(sc);
                continue;
            }
            
            Product oldProduct = pm.selectProduct(ID, sc);

            Log.request("Ten san pham moi (bo trong neu giu nguyen): ");
            String name = sc.nextLine();
            Log.success("Successful!");

            Log.request("Gia moi (bo trong neu giu nguyen): ");
            String p = sc.nextLine().trim();
            Double price = (p.isEmpty()? oldProduct.getPrice() : Double.valueOf(p));
            Log.success("Successful!");

            Log.request("Thoi han su dung moi (bo trong neu giu nguyen): ");
            String SLM = sc.nextLine().trim();
            Integer slm = SLM.isEmpty() ? oldProduct.getSLM() : Integer.valueOf(SLM);
            Log.success("Successful!");

            switch (oldProduct) {
                case Drug d -> {
                    d.setName(name.isEmpty() ? d.getName() : name);
                    d.setPrice(p.isEmpty() ? d.getPrice() : price);
                    d.setShelfLifeMonths(SLM.isEmpty() ? d.getSLM() : slm);
                    
                    Log.request("Nhap thanh phan chinh cua thuoc (bo trong neu giu nguyen): ");
                    String ingredient = sc.nextLine();
                    d.setIngredient(ingredient.isEmpty() ? d.getIngredient() : ingredient);
                    Log.success("Successful!");
                    
                    Log.request("Nhap lieu luong dung (bo trong neu giu nguyen): ");
                    String dosage = sc.nextLine();
                    d.setDosage(dosage.isEmpty() ? d.getDosage() : dosage);
                    Log.success("Successful!");
                    
                    Log.request("Thuoc co ke don cua bac si khong (y/n, bo trong neu giu nguyen): ");
                    String pr = sc.nextLine().trim();
                    if(!pr.isEmpty()) d.setpR(pr.equalsIgnoreCase("y"));
                    Log.success("Successful!");
                }
                case NonDrug d -> {
                    d.setName(name.isEmpty() ? d.getName() : name);
                    d.setPrice(p.isEmpty() ? d.getPrice() : price);
                    d.setShelfLifeMonths(SLM.isEmpty() ? d.getSLM() : slm);
                    
                    Log.request("Nhap nha san xuat moi (bo trong neu giu nguyen): ");
                    String manufacturer = sc.nextLine();
                    d.setManufacturer(manufacturer.isEmpty() ? d.getManufacturer() : manufacturer);
                    Log.success("Successful!");
                    
                    Log.request("Nhap cong dung san pham (bo trong neu giu nguyen): ");
                    String usage = sc.nextLine();
                    d.setUsage(usage.isEmpty() ? d.getUsage() : usage);
                    Log.success("Successful!");
                    
                    Log.request("Loai san pham (bo trong neu giu nguyen): ");
                    String type = sc.nextLine();
                    d.setType(type.isEmpty() ? d.getType() : type);
                    Log.success("Successful!");
                }
                default -> {Log.error("Khong phai san pham cua nha thuoc");}
            }
            pm.save();
            Log.success("Cap nhat thong tin san pham thanh cong!");
            printProduct(oldProduct);
            Log.request("Tiep tuc chinh sua? [0 de thoat]: ");
            if(sc.nextLine().trim().equals("0")) return;
            Extension.pause(sc);
        }  
    }

    @Override
    public void viewMenu() {
            while(true){
            Extension.clearScreen();
            System.out.println("==== VIEW PRODUCTS ====");
            System.out.println("==== DANH SACH CAC SAN PHAM TRONG DANH MUC CHO PHEP ====");
            pm.showList();
            System.out.println("==== DANH SACH CAC SAN PHAM TRONG DANH MUC KHOA ====");
            pm.blackList();
            Log.request("Nhap ID hoac ten san pham muon xem (Nhap 0 de quay lai): ");
            String input = sc.nextLine().trim();

            if (input.equals("0")) {
                Log.info("Huy thao tac.");
                return;
            }

            if (!pm.exists(input)) {
                Log.error("Khong tim thay san pham voi tu khoa: " + input);
                Extension.pause(sc);
                continue;
            }

            Product product = pm.selectProduct(input, sc);

            printProduct(product);
            
            Log.request("Quay lai? Hay nhap 0: ");
            String choice = sc.nextLine().trim();
            if (choice.equals("0")) {
                Log.info("Huy thao tac, quay lai menu chinh.");
                return;
            }
        }
    }

    public static void viewMenuforCustomer(Inventory inv, Scanner sc) {
        while(true){
            Extension.clearScreen();
            System.out.println("==== VIEW PRODUCTS ====");
            inv.showStockList();
            Log.request("Nhap ID hoac ten san pham muon xem (Nhap 0 de quay lai): ");
            String input = sc.nextLine().trim();

            if (input.equals("0")) {
                Log.info("Huy thao tac, quay lai menu chinh.");
                return;
            }
            
            Product p = inv.selectProduct(input, sc);
            Extension.printInBox(() ->{
                switch (p) {
                    case Drug d ->{
                        System.out.println("----- THONG TIN SAN PHAM - THUOC [" + d.getPID() +"] -----");
                        System.out.println("Ma san pham: " + d.getPID());
                        System.out.println("Ten san pham: " + d.getName());
                        System.out.println("Don vi san pham: " + d.getUnit());
                        System.out.println("Gia thi truong: " + d.getPrice());
                        System.out.println("Han dung: " + d.getShelfLifeInfo());
                        System.out.println("Thanh phan chinh: " + d.getIngredient());
                        System.out.println("Lieu luong dung: " + d.getDosage());
                        System.out.println("Thuoc " + (d.getpR() ? "co" : "khong co") + " ke don cua bac si");
                        System.out.println("Tinh trang: " + (d.getStatusString()));
                    }
                    case NonDrug d ->{
                        System.out.println("----- THONG TIN SAN PHAM - + [" + d.getPID() +"] -----");
                        System.out.println("Ma san pham: " + d.getPID());
                        System.out.println("Ten san pham: " + d.getName());
                        System.out.println("Don vi san pham: " + d.getUnit());
                        System.out.println("Gia thi truong: " + d.getPrice());
                        System.out.println("Nha san xuat: " + d.getManufacturer());
                        System.out.println("Loai san pham: " + d.getType());
                        System.out.println("Mo ta cong dung: " + d.getUsage());
                        System.out.println("Tinh trang: " + (d.getStatusString()));
                    }
                    case null ->{Log.error("Khong tim thay san pham nao!");}
                    default -> throw new AssertionError();
                }
            }); 
            Log.request("Quay lai? Hay nhap 0: ");
            String choice = sc.nextLine().trim();
            if (choice.equals("0")) {
                Log.info("Huy thao tac, quay lai menu chinh.");
                return;
            }
            Extension.pause(sc);
        }
    }

    public static void printProduct(Product product){
        Extension.printInBox(() ->{
            switch (product) {
                case Drug d ->{
                    System.out.println("----- THONG TIN SAN PHAM - THUOC [" + d.getPID() +"] -----");
                    System.out.println("Ma san pham: " + d.getPID());
                    System.out.println("Ten san pham: " + d.getName());
                    System.out.println("Don vi san pham: " + d.getUnit());
                    System.out.println("Gia thi truong: " + d.getPrice());
                    System.out.println("Han dung: " + d.getShelfLifeInfo());
                    System.out.println("Thanh phan chinh: " + d.getIngredient());
                    System.out.println("Lieu luong dung: " + d.getDosage());
                    System.out.println("Thuoc " + (d.getpR() ? "co" : "khong co") + " ke don cua bac si");
                    System.out.println("Tinh trang: " + (d.getStatusString()));
                }
                case NonDrug d ->{
                    System.out.println("----- THONG TIN SAN PHAM - + [" + d.getPID() +"] -----");
                    System.out.println("Ma san pham: " + d.getPID());
                    System.out.println("Ten san pham: " + d.getName());
                    System.out.println("Don vi san pham: " + d.getUnit());
                    System.out.println("Gia thi truong: " + d.getPrice());
                    System.out.println("Nha san xuat: " + d.getManufacturer());
                    System.out.println("Loai san pham: " + d.getType());
                    System.out.println("Mo ta cong dung: " + d.getUsage());
                    System.out.println("Tinh trang: " + (d.getStatusString()));
                }
                case null ->{Log.error("Khong tim thay san pham nao!");}
                default -> throw new AssertionError();
            }
        });
    }
}
