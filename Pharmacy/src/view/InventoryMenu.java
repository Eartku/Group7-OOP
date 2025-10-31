package view;

import data.Data;
import interfaces.IManageMenu;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import models.Batch;
import models.Drug;
import models.NonDrug;
import models.Product;
import service.Inventory;
import service.ProductManager;

public class InventoryMenu implements IManageMenu{
    private final ProductManager pm;
    private final Inventory inv;
    private final Scanner sc;


    public InventoryMenu(ProductManager pm, Inventory inv, Scanner sc) {
        this.pm = pm;
        this.inv = inv;
        this.sc = sc;
    }

    @Override
    public void mainMenu(){
        Extension.clearScreen();

        if (inv == null) {
            System.out.println("Khong the quan ly Kho hang!");
            return;
        }
        while (true) {
            Extension.clearScreen();
            System.out.println("==== INVENTORY MANAGER ====\n");
            System.out.println(inv.report() + "\n");
            inv.showList();
            System.out.println("\n1. Nhap lo hang moi - Import Batch");
            System.out.println("2. Kich hoat/Khoa lo hang trong kho - Activate/Block Batch");
            System.out.println("3. Chinh sua lo hang - Edit Batch");
            System.out.println("4. Tim kiem va Xem - Search & View");
            System.out.println("0. Thoat - Cancel");
            System.out.print("Chon: ");
            int choice = Extension.readIntInRange("Nhap lua chon (0-4):", 0, 4, sc);

            switch (choice) {
                case 1 -> {
                    addMenu();
                }
                case 2 -> {
                    System.out.println("1. Khoa lo hang - Block");
                    System.out.println("2. Kich hoat lo hang - Activate");
                    System.out.println("0. Huy - Cancel");
                    int choice2 = Extension.readIntInRange("Nhap lua chon (0-2):", 0, 2, sc);
                    switch (choice2) {
                        case 0 ->{break;}
                        case 1 ->blockMenu();
                        case 2 ->activeMenu();
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
        Extension.clearScreen();
        System.out.println("==== ADD NEW BATCH ====");
        
        String BID = Data.generateNewID(Inventory.FILE_PATH, 'B');
        Product p = null;
        while(true){
            System.out.println("Chỉ được nhập các sản phẩm có trong danh mục sau:");
            pm.showList();
            System.out.print("Nhập ID sản phẩm (0 để quay lại): ");

            String input = sc.nextLine().trim();

            if (input.equals("0")) {
                System.out.println("Huy thao tac xoa, quay lai menu chinh.");
                return;
            }
            if(!pm.exists(input)) System.out.println("Khong tim thay san pham voi ID: " + input);

            p = pm.get(input);
            if(p!= null){
                System.out.println("Them san pham thanh cong!");
                break;
            }
            else System.out.println("[WARN] San pham null, vui long chon lai");
        }

        long quantity;
        while(true){
            System.out.print("So luong san pham nhap vao: ");
            quantity = Long.parseLong(sc.nextLine().trim());
            if(quantity <=0){
                System.out.println("So luong phai toi thieu la 1");
            } else break;
        }

        LocalDate importDate = null;
        while (true) {
            System.out.print("Ngay nhap hang (dd/MM/yyyy): ");
            String dob = sc.nextLine();
            try {
                importDate = LocalDate.parse(dob, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                break;
            } catch (Exception e) {
                System.out.println("Ngay nhap hang khong hop le! Nhap lai theo dd/MM/yyyy");
            }
        }
        inv.add(new Batch(BID, p, quantity, importDate, true));

        System.out.println("Da them lo hang thanh cong!");
        inv.save();
    }

    @Override
    public void blockMenu() {
        Extension.clearScreen();
        System.out.println("==== BLOCK BATCH ====");

        while (true) {
            inv.showList();
            System.out.print("Nhap ID lo hang muon huy (Cancel) (hoac nhap 0 de quay lai): ");
            String inputID = sc.nextLine().trim();

            // Quay lại menu
            if (inputID.equals("0")) {
                System.out.println("Huy thao tac.");
                return;
            }

            // Kiểm tra tồn tại
            if (!inv.exists(inputID) || !inv.get(inputID).getStatus()) {
                System.out.println("Khong ton tai lo hang: " + inputID);
                continue;
            }

            // Lấy thông tin lô hàng
            Batch b = inv.get(inputID);
            System.out.println("\nThong tin lo hang:");
            System.out.println(" - Ma lo hang: " + b.getBatchId());
            System.out.println(" - San pham: " + b.getProduct().getName());
            System.out.println(" - So luong: " + b.getQuantity());
            System.out.println(" - Ngay nhap: " + b.getImportDate());

            // Xác nhận xóa
            System.out.print("\nBan co chac xoa lo hang nay? (y/n): ");
            String confirm = sc.nextLine().trim();

            if (confirm.equalsIgnoreCase("y")) {
                inv.get(inputID).setStatus(false);
                inv.save();
                System.out.println("Da xoa lo hang: " + b.getBatchId());
            } else {
                System.out.println("Da huy thao tac xoa.");
            }

            // Hỏi có muốn tiếp tục xóa nữa không
            System.out.print("\nXoa them lo hang khac? (y/n): ");
            String again = sc.nextLine().trim();
            if (!again.equalsIgnoreCase("y")) break;
        }
    }

    @Override
    public void activeMenu() {
        Extension.clearScreen();
        System.out.println("==== ACTIVATE BATCH ====");

        while (true) {
            inv.blackList();
            System.out.print("Nhap ID lo hang muon kich hoat (hoac nhap 0 de quay lai): ");
            String inputID = sc.nextLine().trim();

            // Quay lại menu
            if (inputID.equals("0")) {
                System.out.println("Huy thao tac.");
                return;
            }

            // Kiểm tra tồn tại
            if (!inv.exists(inputID) || inv.get(inputID).getStatus()) {
                System.out.println("Khong ton tai lo hang: " + inputID);
                continue;
            }

            // Lấy thông tin lô hàng
            Batch b = inv.get(inputID);
            System.out.println("\nThong tin lo hang:");
            System.out.println(" - Ma lo hang: " + b.getBatchId());
            System.out.println(" - San pham: " + b.getProduct().getName());
            System.out.println(" - So luong: " + b.getQuantity());
            System.out.println(" - Ngay nhap: " + b.getImportDate());

            // Xác nhận xóa
            System.out.print("\nBan co chac xoa lo hang nay? (y/n): ");
            String confirm = sc.nextLine().trim();

            if (confirm.equalsIgnoreCase("y")) {
                inv.get(inputID).setStatus(true);
                inv.save();
                System.out.println("Da xoa lo hang: " + b.getBatchId());
            } else {
                System.out.println("Da huy thao tac kich hoat.");
            }

            // Hỏi có muốn tiếp tục xóa nữa không
            System.out.print("\nXoa them lo hang khac? (y/n): ");
            String again = sc.nextLine().trim();
            if (!again.equalsIgnoreCase("y")) break;
        }
    }

    @Override
    public void updateMenu() {
        Extension.clearScreen();
        System.out.println("==== EDIT BATCH ====");

        while (true) {
            inv.showList();
            System.out.print("Nhap ID lo hang can chinh sua (Nhap 0 de quay lai): ");
            String inputID = sc.nextLine().trim();

            if (inputID.equals("0")) {
                System.out.println("Huy thao tac chinh sua.");
                return;
            }

            if (!inv.exists(inputID)) {
                System.out.println("Khong ton tai lo hang voi ID: " + inputID);
                continue;
            }

            //show thong tin hien tai
            Batch b = inv.get(inputID);
            System.out.println("\nThong tin hien tai:");
            System.out.println(" - ID: " + b.getBatchId());
            System.out.println(" - Ten san pham: " + b.getProduct().getName());
            System.out.println(" - So luong: " + b.getQuantity());
            System.out.println(" - Ngay nhap: " + b.getImportDate());
            if (b.getExpiryDate() != null)
                System.out.println(" - Han dung: " + b.getExpiryDate());
            System.out.println(" - Trang thai lo hang: " + (b.getStatusString()));


            System.out.println("\nDanh muc chinh sua:");
            System.out.println("1. So luong");
            System.out.println("2. Ngay nhap hang");
            System.out.println("3. Dong/Mo trang thai (Active/Inactive)");
            System.out.println("0. Huy");

            System.out.print("Chon: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    System.out.print("Nhap so luong moi: ");
                    try {
                        long qty = Long.parseLong(sc.nextLine().trim());
                        if (qty < 1) {
                            System.out.println("So luong toi thieu la 1!");
                        } else {
                            b.setQuantity(qty);
                            System.out.println("Updated!.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Khong hop le!");
                    }
                    break;
                }
                case "2"-> {
                    System.out.print("Nhap ngay nhap(import) moi (dd/MM/yyyy): ");
                    String dateStr = sc.nextLine().trim();
                    try {
                        b.setImportDate(dateStr);
                        System.out.println("Updated!");
                    } catch (Exception e) {
                        System.out.println("Khong hop le!");
                    }
                    break;
                }
                case "3"-> {
                    System.out.print("Trang thai hien tai" + (b.getStatusString()) + ". Xac nhan doi trang thai (y/n): ");
                    String c = sc.nextLine().trim();
                    if (c.equalsIgnoreCase("y")) {
                        b.setStatus(!b.getStatus());
                        System.out.println("Doi thanh cong: " + (b.getStatusString()));
                    }
                    break;
                }
                case "0"->{
                    System.out.println("Huy thao tac chinh sua.");
                    inv.save();
                    return;
                }
                default ->
                    System.out.println("Lua chon khong phu hop");
            }

            // Lưu lại khi có chỉnh sửa
            inv.save();

            System.out.print("\nChinh sua lo hang khac? (y/n): ");
            String again = sc.nextLine().trim();
            if (!again.equalsIgnoreCase("y")) break;
        }

        inv.save();
        System.out.println("Hoan tat!");
    }


    @Override
    public void viewMenu() {
        Extension.clearScreen();
        System.out.println("==== VIEW BATCH ====");
        inv.showList(); // hiển thị danh sách các lô hiện có

        System.out.print("Nhap ID lo hang muon xem (Nhap 0 de quay lai): ");
        String input = sc.nextLine().trim();
        if (input.equals("0")) {
            System.out.println("Huy thao tac, quay lai menu chinh.");
            return;
        }

        Batch batch = inv.get(input);

        if (batch == null) {
            System.out.println("Khong tim thay lo hang voi ID: " + input);
            return;
        }

        Product p = batch.getProduct();

        System.out.println("----- CHI TIET LO HANG [" + batch.getBatchId() + "] -----");
        System.out.println("Ma lo hang: " + batch.getBatchId());
        System.out.println("San pham: " + p.getName() + " (" + p.getPID() + ")");
        System.out.println("Gia - Nhap vao: " + String.format("%.2f", batch.getProduct().getPrice()) + ".VND | Ban ra: " + String.format("%.2f", batch.getProduct().getPrice()) +".VND");
        System.out.println("Don vi: " + p.getUnit());
        System.out.println("So luong: " + batch.getQuantity());
        System.out.println("Ngay nhap hang: " + batch.getImportDate());
        System.out.println("Trang thai: " + (batch.getStatus() ? "Hoat dong" : "Ngung su dung"));
        
        // Nếu sản phẩm là Drug
        switch (p) {
            case Drug d -> {
                System.out.println("Loai san pham: Thuoc");
                System.out.println("Gia thi truong: " + d.getPrice());
                System.out.println("Han dung: " + d.getShelfLifeInfo());
                System.out.println("Thanh phan chinh: " + d.getIngredient());
                System.out.println("Lieu luong dung: " + d.getDosage());
                System.out.println("Thuoc " + (d.getpR() ? "co" : "khong co") + " ke don cua bac si");
            }
            case NonDrug nd -> {
                System.out.println("Loai san pham: Khong phai thuoc dac tri");
                System.out.println("Gia thi truong: " + nd.getPrice());
                System.out.println("Nha san xuat: " + nd.getManufacturer());
                System.out.println("Loai san pham: " + nd.getType());
                System.out.println("Mo ta cong dung: " + nd.getUsage());
            }
            default -> {
            }
        }

        System.out.print("Nhap 0 de quay lai: ");
        String choice = sc.nextLine().trim();
        if (choice.equals("0")) {
            System.out.println("Quay lai menu chinh...");
        }
    }

}
