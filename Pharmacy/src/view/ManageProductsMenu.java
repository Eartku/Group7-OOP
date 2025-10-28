package view;

import data.Data;
import interfaces.ManageMenu;
import java.util.Scanner;
import models.Drug;
import models.NonDrug;
import models.Product;
import service.ProductManager;

public class ManageProductsMenu implements ManageMenu{
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
            System.out.println("Khong the quan ly san pham!");
            return;
        }

        while (true) {
            Extension.clearScreen();
            System.out.println("==== PRODUCT MANAGER ====");
            System.out.println(pm.report());
            pm.showList();
            System.out.println("1. Them san pham - Add Product");
            System.out.println("2. An/Hien san pham - Hide/Show Product");
            System.out.println("3. Chinh sua thong tin san pham - Edit product INFO");
            System.out.println("4. Tim kiem va Xem - Search & View");
            System.out.println("0. Huy - Cancel");
            System.out.print("Chon: ");
            int choice = Extension.readIntInRange("Nhap lua chon (0-4):", 0, 4, sc);

            switch (choice) {
                case 1 -> {
                    addMenu();
                }
                case 2 -> {
                    System.out.println("1. An san pham - Hide Product");
                    System.out.println("2. Hien san pham - Show Product");
                    System.out.println("0. Huy - Cancel");
                    int choice2 = Extension.readIntInRange("Nhap lua chon (0-2):", 0, 2, sc);
                    switch (choice2) {
                        case 0 ->{break;}
                        case 1 ->removeMenu();
                        case 2 ->showMenu();
                        default -> System.out.println("Khong hop le!");
                    }
                }
                case 3 -> {
                    updateMenu();
                }
                case 4 -> {
                    viewMenu();
                }
                case 0 -> {
                    System.out.println("Thoat chuong trinh. Tam biet!");
                    return;
                }
                default -> System.out.println("Lua chon khong hop le!");
            }
        }
    }

    @Override
    public void addMenu(){
        while(true){
            Extension.clearScreen();
            System.out.println("==== ADD NEW PRODUCT ====");
            String PID = Data.generateNewID(ProductManager.FILE_PATH, 'P');
            System.out.print("Ten san pham (Nhap 0 de quay lai): ");
            String name = sc.nextLine().trim();

            if (name.equals("0")) {
                    System.out.println("Huy thao tac xoa, quay lai menu chinh.");
                    return;
                }

            System.out.print("Don vi san pham (Vien/Vi/Hop/Cai/vv): ");
            String unit = sc.nextLine().trim();

            double price;
            while (true) {
                System.out.print("Gia thi truong: ");
                String inputPrice = sc.nextLine().trim();
                try {
                    price = Double.parseDouble(inputPrice);
                    if (price <= 0) {
                        System.out.println("Gia phai lon hon 0!");
                        continue;
                    }
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Gia khong hop le! Vui long nhap so.");
                }
            }

            System.out.print("Thoi han su dung (thang), bo trong neu khong co: ");
            String input = sc.nextLine().trim();
            Integer SLM = null;

            if (!input.isEmpty()) {
                try {
                    SLM = Integer.valueOf(input);
                    if (SLM < 0) {
                        System.out.println("Thoi han khong the am! Mac dinh de trong neu khong co.");
                        SLM = null;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Gia tri khong hop le! Bo qua truong nay.");
                    SLM = null;
                }
            }


            String confirm;
            while (true) {
                System.out.print("San pham co phai la thuoc? (y/n): ");
                confirm = sc.nextLine().trim().toLowerCase();
                if (confirm.equals("y") || confirm.equals("n")) break;
                System.out.println("Chi nhap y hoac n!");
            }

            Product product;

            if (confirm.equalsIgnoreCase("y")) {
                System.out.println("Nhap thanh phan chinh cua thuoc: ");
                String ingredient = sc.nextLine().trim();
                System.out.println("Nhap lieu luong dung:");
                String dosage = sc.nextLine().trim();
                System.out.println("Thuoc co ke don cua bac si khong? (y/n):");
                String pr = sc.nextLine().trim(); boolean pR = false;
                if (pr.equalsIgnoreCase("y")) pR = true;

                product = new Drug(PID, name, unit, price, SLM, dosage, ingredient, pR, true);
            } else {
                System.out.println("Don vi/Cong ty san xuat: ");
                String manufacturer= sc.nextLine().trim();
                System.out.println("Nhap cong dung:");
                String usage = sc.nextLine().trim();
                System.out.println("Loai san pham (VD: Ho tro suc khoe, Lam dep, Y te,...)");
                String type = sc.nextLine().trim();

                product = new NonDrug(PID, name, unit, price, SLM, manufacturer, type, usage, true);
            }

            pm.add(product);
            System.out.println("Da them san pham thanh cong!");
            pm.save();
            System.out.print("Tiep tuc them? [0 de thoat | Enter de tiep tuc]: ");
            if(sc.nextLine().trim().equals("0")) return;
        }
    }

    @Override
    public void removeMenu() {
        while (true) {
            Extension.clearScreen();
            System.out.println("==== HIDE PRODUCT ====");
            pm.showList();
            System.out.print("Nhap ID hoac ten san pham muon AN (hoac nhap 0 de quay lai): ");
            String inputID = sc.nextLine().trim();

            if (inputID.equals("0")) {
                System.out.println("Huy thao tac xoa, quay lai menu chinh.");
                return;
            }

            Product target = pm.selectProduct(inputID, sc);

            if (target == null || target.getStatus() == false) {
                System.out.println("Khong tim thay san pham voi tu khoa: " + inputID);
                continue;
            }

            // Xác nhận xóa
            System.out.print("Ban co chac muon AN san pham " + target.getName() + "? (y/n): ");
            String confirm = sc.nextLine().trim();

            if (confirm.equalsIgnoreCase("y")) {
                pm.get(inputID).setStatus(false);
                System.out.println("Da an san pham: " + target.getName());
                pm.save();
            } else {
                System.out.println("Da huy thao tac AN.");
            }
            System.out.print("Tiep tuc? [0 de thoat | Enter de tiep tuc]: ");
            if(sc.nextLine().trim().equals("0")) return;
        }
    }

    public void showMenu() {
        while (true) {
            Extension.clearScreen();
            System.out.println("==== SHOW PRODUCT ====");
            pm.showUnvaiableList();
            System.out.print("Nhap ID hoac ten san pham muon HIEN (hoac nhap 0 de quay lai): ");
            String inputID = sc.nextLine().trim();

            if (inputID.equals("0")) {
                System.out.println("Huy thao tac HIEN, quay lai menu chinh.");
                return;
            }

            Product target = pm.selectProduct(inputID, sc);

            if (target == null || target.getStatus() == true) {
                System.out.println("Khong tim thay san pham voi tu khoa: " + inputID);
                continue;
            }

            System.out.print("Ban co chac muon HIEN THI san pham " + target.getName() + "? (y/n): ");
            String confirm = sc.nextLine().trim();

            if (confirm.equalsIgnoreCase("y")) {
                pm.get(inputID).setStatus(true);
                System.out.println("Da HIEN san pham: " + target.getName());
                pm.save();
            } else {
                System.out.println("Da huy thao tac HIEN.");
            }
            System.out.print("Tiep tuc? [0 de thoat | Enter de tiep tuc]: ");
            if(sc.nextLine().trim().equals("0")) return;
        }
    }

    @Override
    public void updateMenu() {
        while(true){
            Extension.clearScreen();
            System.out.println("==== EDIT PRODUCT ====");
            pm.showList();
            System.out.print("Nhap ID hoac ten san pham muon chinh sua (hoac nhap 0 de quay lai): ");
            String ID = sc.nextLine().trim();
            
            if (ID.equals("0")) {
                    System.out.println("Huy thao tac xoa, quay lai menu chinh.");
                    return;
                }

            if (!pm.exists(ID)) {
                System.out.println("Khong ton san pham voi ID: " + ID);
                continue;
            }
            
            Product oldProduct = pm.selectProduct(ID, sc);

            System.out.print("Ten san pham moi (bo trong neu giu nguyen): ");
            String name = sc.nextLine();

            System.out.print("Gia moi (bo trong neu giu nguyen): ");
            String p = sc.nextLine().trim();
            Double price = (p.isEmpty()? oldProduct.getPrice() : Double.valueOf(p));

            System.out.print("Thoi han su dung moi (bo trong neu giu nguyen): ");
            String SLM = sc.nextLine().trim();
            Integer slm = SLM.isEmpty() ? oldProduct.getSLM() : Integer.valueOf(SLM);

            switch (oldProduct) {
                case Drug d -> {
                    d.setName(name.isEmpty() ? d.getName() : name);
                    d.setPrice(p.isEmpty() ? d.getPrice() : price);
                    d.setShelfLifeMonths(SLM.isEmpty() ? d.getSLM() : slm);
                    
                    System.out.print("Nhap thanh phan chinh cua thuoc (bo trong neu giu nguyen): ");
                    String ingredient = sc.nextLine();
                    d.setIngredient(ingredient.isEmpty() ? d.getIngredient() : ingredient);
                    
                    System.out.print("Nhap lieu luong dung (bo trong neu giu nguyen): ");
                    String dosage = sc.nextLine();
                    d.setDosage(dosage.isEmpty() ? d.getDosage() : dosage);
                    
                    System.out.print("Thuoc co ke don cua bac si khong (y/n, bo trong neu giu nguyen): ");
                    String pr = sc.nextLine().trim();
                    if(!pr.isEmpty()) d.setpR(pr.equalsIgnoreCase("y"));
                }
                case NonDrug d -> {
                    d.setName(name.isEmpty() ? d.getName() : name);
                    d.setPrice(p.isEmpty() ? d.getPrice() : price);
                    d.setShelfLifeMonths(SLM.isEmpty() ? d.getSLM() : slm);
                    
                    System.out.print("Nhap nha san xuat moi (bo trong neu giu nguyen): ");
                    String manufacturer = sc.nextLine();
                    d.setManufacturer(manufacturer.isEmpty() ? d.getManufacturer() : manufacturer);
                    
                    System.out.print("Nhap cong dung san pham (bo trong neu giu nguyen): ");
                    String usage = sc.nextLine();
                    d.setUsage(usage.isEmpty() ? d.getUsage() : usage);
                    
                    System.out.print("Loai san pham (bo trong neu giu nguyen): ");
                    String type = sc.nextLine();
                    d.setType(type.isEmpty() ? d.getType() : type);
                }
                default -> {System.out.println("Khong phai san pham cua nha thuoc");}
            }
            pm.save();
            System.out.println("Cap nhat thong tin san pham thanh cong!");
            printProduct(oldProduct);
            System.out.print("Tiep tuc chinh sua? [0 de thoat | Enter de tiep tuc]: ");
            if(sc.nextLine().trim().equals("0")) return;
        }  
    }

    @Override
    public void viewMenu() {
            while(true){
            Extension.clearScreen();
            System.out.println("==== VIEW PRODUCTS ====");
            pm.showList();
            System.out.print("Nhap ID hoac ten san pham muon xem (Nhap 0 de quay lai): ");
            String input = sc.nextLine().trim();

            if (input.equals("0")) {
                System.out.println("Huy thao tac, quay lai menu chinh.");
                return;
            }
            Product product = pm.selectProduct(input, sc);

            printProduct(product);
            
            System.out.print("Quay lai? Hay nhap 0: ");
            String choice = sc.nextLine().trim();
            if (choice.equals("0")) {
                System.out.println("Huy thao tac xoa, quay lai menu chinh.");
                return;
            }
        }
    }
    public void printProduct(Product product){
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
                }
                case null ->{System.out.println("Khong tim thay san pham nao!");}
                default -> throw new AssertionError();
            }
        });
    }
}
