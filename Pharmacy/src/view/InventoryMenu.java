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

public class InventoryMenu implements IManageMenu {
    private final ProductManager pm;
    private final Inventory inv;
    private final Scanner sc;

    public InventoryMenu(ProductManager pm, Inventory inv, Scanner sc) {
        this.pm = pm;
        this.inv = inv;
        this.sc = sc;
    }

    @Override
    public void mainMenu() {
        Extension.clearScreen();

        if (inv == null) {
            Log.error("Khong the quan ly Kho hang!");
            return;
        }
        while (true) {
            Extension.clearScreen();
            System.out.println("==== INVENTORY MANAGER ====\n");
            System.out.println(inv.report() + "\n");
            System.out.println("==== DANH SACH CAC LO HANG TRONG KHO ====");
            inv.showList();
            inv.blackList();
            System.out.println("\n1. Nhap lo hang moi - Import Batch");
            System.out.println("2. Kich hoat/Khoa lo hang trong kho - Activate/Block Batch");
            System.out.println("3. Chinh sua lo hang - Edit Batch");
            System.out.println("4. Tim kiem va Xem - Search & View");
            Log.exit("0. Thoat - Cancel");
            int choice = Extension.readIntInRange("Nhap lua chon (0-4):", 0, 4, sc);

            switch (choice) {
                case 1 -> {
                    addMenu();
                    Extension.pause(sc);
                }
                case 2 -> {
                    System.out.println("1. Khoa lo hang - Block");
                    System.out.println("2. Kich hoat lo hang - Activate");
                    Log.exit("0. Huy - Cancel");
                    int choice2 = Extension.readIntInRange("Nhap lua chon (0-2):", 0, 2, sc);
                    switch (choice2) {
                        case 0 -> {
                            Log.info("Huy thao tac.");
                            break;
                        }
                        case 1 -> blockMenu();
                        case 2 -> activeMenu();
                        default -> Log.error("Khong hop le!");
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
                    Log.info("Thoat kho hang.");
                    return;
                }
                default -> Log.warning("Lua chon khong hop le!");
            }
        }
    }

    @Override
    public void addMenu() {
        Extension.clearScreen();
        System.out.println("==== ADD NEW BATCH ====");

        String BID = Data.generateNewID(Inventory.FILE_PATH, 'B');
        Product p = null;
        while (true) {
            Log.exit("Chi duoc nhap cac san pham co trong danh muc sau:");
            pm.showList();
            Log.request("Nhap ID san pham (0 de quay lai): ");
            String input = sc.nextLine().trim();

            if (input.equals("0")) {
                Log.info("Huy thao tac.");
                return;
            }
            if (!pm.exists(input)) {
                Log.warning("Khong tim thay san pham voi ID: " + input);
                continue;
            }

            p = pm.get(input);
            if (p != null) {
                Log.success("Them san pham thanh cong!");
                break;
            } else Log.warning("San pham null, vui long chon lai");
        }

        long quantity;
        while (true) {
            Log.request("So luong san pham nhap vao: ");
            try {
                quantity = Long.parseLong(sc.nextLine().trim());
                if (quantity <= 0) {
                    Log.warning("So luong phai toi thieu la 1!");
                } else break;
            } catch (NumberFormatException e) {
                Log.error("Gia tri nhap khong hop le!");
            }
        }

        LocalDate importDate = null;
        while (true) {
            Log.request("Ngay nhap hang (dd/MM/yyyy): ");
            String dob = sc.nextLine();
            try {
                importDate = LocalDate.parse(dob, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                break;
            } catch (Exception e) {
                Log.error("Ngay nhap hang khong hop le! Nhap lai theo dd/MM/yyyy");
            }
        }

        inv.add(new Batch(BID, p, quantity, importDate, true));
        Log.success("Da them lo hang thanh cong!");
        inv.save();
    }

    @Override
    public void blockMenu() {
        Extension.clearScreen();
        System.out.println("==== BLOCK BATCH ====");

        while (true) {
            inv.showList();
            Log.request("Nhap ID lo hang muon huy (hoac 0 de quay lai): ");
            String inputID = sc.nextLine().trim();

            if (inputID.equals("0")) {
                Log.info("Huy thao tac.");
                return;
            }

            if (!inv.exists(inputID) || !inv.get(inputID).getStatus()) {
                Log.warning("Khong ton tai lo hang: " + inputID);
                continue;
            }

            Batch b = inv.get(inputID);
            System.out.println("\nThong tin lo hang:");
            System.out.println(" - Ma lo hang: " + b.getBatchId());
            System.out.println(" - San pham: " + b.getProduct().getName());
            System.out.println(" - So luong: " + b.getQuantity());
            System.out.println(" - Ngay nhap: " + b.getImportDate());

            Log.request("\nBan co chac khoa lo hang nay? (y/n): ");
            String confirm = sc.nextLine().trim();

            if (confirm.equalsIgnoreCase("y")) {
                b.setStatus(false);
                inv.save();
                Log.success("Da khoa lo hang: " + b.getBatchId());
            } else Log.info("Huy thao tac.");

            Log.request("\nKhoa them lo hang khac? (y/n): ");
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
            Log.request("Nhap ID lo hang muon kich hoat (0 de quay lai): ");
            String inputID = sc.nextLine().trim();

            if (inputID.equals("0")) {
                Log.info("Huy thao tac.");
                return;
            }

            if (!inv.exists(inputID) || inv.get(inputID).getStatus()) {
                Log.warning("Khong ton tai lo hang: " + inputID);
                continue;
            }

            Batch b = inv.get(inputID);
            System.out.println("\nThong tin lo hang:");
            System.out.println(" - Ma lo hang: " + b.getBatchId());
            System.out.println(" - San pham: " + b.getProduct().getName());
            System.out.println(" - So luong: " + b.getQuantity());
            System.out.println(" - Ngay nhap: " + b.getImportDate());

            Log.request("\nBan co chac kich hoat lai lo hang nay? (y/n): ");
            String confirm = sc.nextLine().trim();

            if (confirm.equalsIgnoreCase("y")) {
                b.setStatus(true);
                inv.save();
                Log.success("Da kich hoat lo hang: " + b.getBatchId());
            } else Log.info("Huy thao tac kich hoat.");

            Log.request("\nKich hoat them lo hang khac? (y/n): ");
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
            Log.request("Nhap ID lo hang can chinh sua (0 de quay lai): ");
            String inputID = sc.nextLine().trim();

            if (inputID.equals("0")) {
                Log.info("Huy thao tac chinh sua.");
                return;
            }

            if (!inv.exists(inputID)) {
                Log.warning("Khong ton tai lo hang voi ID: " + inputID);
                continue;
            }

            Batch b = inv.get(inputID);
            System.out.println("\nThong tin hien tai:");
            System.out.println(" - ID: " + b.getBatchId());
            System.out.println(" - Ten san pham: " + b.getProduct().getName());
            System.out.println(" - So luong: " + b.getQuantity());
            System.out.println(" - Ngay nhap: " + b.getImportDate());
            if (b.getExpiryDate() != null)
                System.out.println(" - Han dung: " + b.getExpiryDate());
            System.out.println(" - Trang thai lo hang: " + b.getStatusString());

            System.out.println("\nDanh muc chinh sua:");
            System.out.println("1. So luong");
            System.out.println("2. Ngay nhap hang");
            System.out.println("3. Doi trang thai (Active/Inactive)");
            Log.exit("0. Huy");

            Log.request("Chon: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    Log.request("Nhap so luong moi: ");
                    try {
                        long qty = Long.parseLong(sc.nextLine().trim());
                        if (qty < 1) Log.warning("So luong toi thieu la 1!");
                        else {
                            b.setQuantity(qty);
                            Log.success("Cap nhat thanh cong!");
                        }
                    } catch (NumberFormatException e) {
                        Log.error("Khong hop le!");
                    }
                }
                case "2" -> {
                    Log.request("Nhap ngay nhap moi (dd/MM/yyyy): ");
                    String dateStr = sc.nextLine().trim();
                    try {
                        b.setImportDate(dateStr);
                        Log.success("Cap nhat ngay nhap hang thanh cong!");
                    } catch (Exception e) {
                        Log.error("Ngay nhap khong hop le!");
                    }
                }
                case "3" -> {
                    Log.request("Trang thai hien tai: " + b.getStatusString() + ". Doi trang thai? (y/n): ");
                    String c = sc.nextLine().trim();
                    if (c.equalsIgnoreCase("y")) {
                        b.setStatus(!b.getStatus());
                        Log.success("Doi thanh cong sang: " + b.getStatusString());
                    }
                }
                case "0" -> {
                    Log.info("Huy thao tac chinh sua.");
                    inv.save();
                    return;
                }
                default -> Log.warning("Lua chon khong phu hop!");
            }

            inv.save();

            System.out.print("\nChinh sua lo hang khac? (y/n): ");
            String again = sc.nextLine().trim();
            if (!again.equalsIgnoreCase("y")) break;
        }

        inv.save();
        Log.success("Hoan tat!");
    }

    @Override
    public void viewMenu() {
        Extension.clearScreen();
        System.out.println("==== VIEW BATCH ====");
        inv.showList();

        Log.request("Nhap ID lo hang muon xem (0 de quay lai): ");
        String input = sc.nextLine().trim();
        if (input.equals("0")) {
            Log.info("Quay lai menu chinh.");
            return;
        }

        Batch batch = inv.get(input);
        if (batch == null) {
            Log.warning("Khong tim thay lo hang voi ID: " + input);
            return;
        }

        Product p = batch.getProduct();

        System.out.println("----- CHI TIET LO HANG [" + batch.getBatchId() + "] -----");
        System.out.println("Ma lo hang: " + batch.getBatchId());
        System.out.println("San pham: " + p.getName() + " (" + p.getPID() + ")");
        System.out.println("Gia nhap: " + batch.getProduct().getPrice() + " VND");
        System.out.println("Don vi: " + p.getUnit());
        System.out.println("So luong: " + batch.getQuantity());
        System.out.println("Ngay nhap: " + batch.getImportDate());
        System.out.println("Trang thai: " + (batch.getStatus() ? "Hoat dong" : "Ngung su dung"));

        switch (p) {
            case Drug d -> {
                System.out.println("Loai san pham: Thuoc");
                System.out.println("Han dung: " + d.getShelfLifeInfo());
                System.out.println("Thanh phan: " + d.getIngredient());
                System.out.println("Lieu dung: " + d.getDosage());
                System.out.println("Thuoc " + (d.getpR() ? "co" : "khong co") + " ke don bac si");
            }
            case NonDrug nd -> {
                System.out.println("Loai san pham: Khong phai thuoc dac tri");
                System.out.println("Nha san xuat: " + nd.getManufacturer());
                System.out.println("Loai san pham: " + nd.getType());
                System.out.println("Mo ta: " + nd.getUsage());
            }
            default -> {
            }
        }
    }
}
