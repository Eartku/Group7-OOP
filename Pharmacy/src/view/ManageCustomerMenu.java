package view;

import data.Data;
import interfaces.IManageMenu;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;
import models.Customer;
import service.CustomerManager;
import service.UserManager;

public class ManageCustomerMenu implements IManageMenu {

    private final CustomerManager cm;
    private final UserManager um;
    private final Scanner sc;

    public ManageCustomerMenu(CustomerManager cm, UserManager um, Scanner sc) {
        this.cm = cm;
        this.um = um;
        this.sc = sc;
    }

    @Override
    public void mainMenu() {
        Extension.clearScreen();

        if (cm == null) {
            Log.error("Khong the quan ly khach hang!");
            return;
        }

        while (true) {
            Extension.clearScreen();
            System.out.println("==== CUSTOMER MANAGER ====");
            System.out.println(cm.report());
            cm.showList();
            System.out.println("1. Them khach hang - Add Customer (Danh cho giao dich tai quay)");
            System.out.println("2. Kich hoat/Chan khach hang - Activate/Block Customers");
            System.out.println("3. Chinh sua thong tin khach hang - Edit Customers INFO");
            System.out.println("4. Xem thong tin khach hang - View Customers INFO");
            Log.exit("0. Thoat - Exit");

            int choice = Extension.readIntInRange("Nhap lua chon (0-4):", 0, 4, sc);

            switch (choice) {
                case 1 -> {
                    addMenu();
                    Extension.pause(sc);
                }
                case 2 -> {
                    System.out.println("1. Chan khach hang - Block Customer");
                    System.out.println("2. Kich hoat khach hang - Activate Customer");
                    Log.exit("0. Huy - Cancel");
                    int choice2 = Extension.readIntInRange("Nhap lua chon (0-2):", 0, 2, sc);
                    switch (choice2) {
                        case 1 -> blockMenu();
                        case 2 -> activeMenu();
                        default -> Log.info("Huy thao tac.");
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
                    Log.info("Thoat quan ly khach hang.");
                    return;
                }
                default -> Log.warning("Lua chon khong hop le!");
            }
        }
    }

    @Override
    public void addMenu() {
        Extension.clearScreen();
        System.out.println("==== ADD CUSTOMER ====");
        Log.info("Truoc tien, ban can phai dang ky tai khoan:");

        String username;
        while (true) {
            Log.request("Nhap username (hoac nhap 0 de quay lai): ");
            username = sc.nextLine().trim();
            if (username.equals("0")) {
                Log.info("Huy thao tac.");
                return;
            }
            if (um.exists(username)) {
                Log.warning("Username da ton tai!");
            } else break;
        }

        String password;
        while (true) {
            Log.request("Nhap password: ");
            password = sc.nextLine();
            if (password.length() >= 6) break;
            Log.warning("Password phai dai hon 6 ky tu!");
        }

        String CID = Data.generateNewID(CustomerManager.FILE_PATH, 'C');
        Log.request("Ho va ten: ");
        String fullname = sc.nextLine();

        LocalDate dobDate = null;
        while (true) {
            Log.request("Ngay sinh (dd/MM/yyyy): ");
            String dob = sc.nextLine();
            try {
                dobDate = LocalDate.parse(dob, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                break;
            } catch (Exception e) {
                Log.error("Ngay sinh khong hop le! Nhap lai theo dd/MM/yyyy");
            }
        }

        Log.request("Dia chi: ");
        String address = sc.nextLine();
        Log.request("Email: ");
        String email = sc.nextLine();
        Log.request("So dien thoai: ");
        String phone = sc.nextLine();

        Customer newUser = new Customer(username, password, CID, fullname, dobDate, address, email, phone, true);
        cm.add(newUser);
        um.add(newUser);
        cm.save();
        um.save();
        Log.success("Da them khach hang thanh cong!");
    }

    @Override
    public void blockMenu() {
        Extension.clearScreen();
        while (true) {
            System.out.println("==== BLOCK CUSTOMER ====");
            cm.showList();
            Log.request("Nhap ID khach hang muon chan (hoac nhap 0 de quay lai): ");
            String inputID = sc.nextLine().trim();

            if (inputID.equals("0")) {
                Log.info("Huy thao tac.");
                return;
            }

            if (!cm.exists(inputID)) {
                Log.error("Khong co khach hang nao co ID: " + inputID);
                continue;
            }

            Customer c = cm.get(inputID);
            printCustomer(c);
            if (!c.getStatus()) {
                Log.warning("Khach hang nay da bi chan tu truoc!");
                continue;
            }

            Log.request("Ban co chac muon chan khach hang nay? (y/n): ");
            String confirm = sc.nextLine().trim();
            if (confirm.equalsIgnoreCase("y")) {
                c.setStatus(false);
                Log.success("Da chan thanh cong khach hang " + c.getCID());
                cm.save();
            } else Log.info("Huy thao tac chan.");

            Log.request("Tiep tuc chan khach hang khac? (y/n): ");
            if (!sc.nextLine().trim().equalsIgnoreCase("y")) break;
        }
    }

    @Override
    public void activeMenu() {
        Extension.clearScreen();
        while (true) {
            System.out.println("==== ACTIVATE CUSTOMER ====");
            cm.blackList();
            Log.request("Nhap ID khach hang muon kich hoat (hoac nhap 0 de quay lai): ");
            String inputID = sc.nextLine().trim();

            if (inputID.equals("0")) {
                Log.info("Huy thao tac.");
                return;
            }

            if (!cm.exists(inputID)) {
                Log.error("Khong co khach hang nao co ID: " + inputID);
                continue;
            }

            Customer c = cm.get(inputID);
            printCustomer(c);
            if (c.getStatus()) {
                Log.warning("Khach hang nay da duoc kich hoat tu truoc!");
                continue;
            }

            Log.request("Ban co chac muon kich hoat khach hang nay? (y/n): ");
            String confirm = sc.nextLine().trim();
            if (confirm.equalsIgnoreCase("y")) {
                c.setStatus(true);
                cm.save();
                Log.success("Da kich hoat khach hang: " + c.getCID());
            } else Log.info("Da huy thao tac kich hoat.");

            Log.request("Tiep tuc kich hoat khach hang khac? (y/n): ");
            if (!sc.nextLine().trim().equalsIgnoreCase("y")) break;
        }
    }

    @Override
    public void updateMenu() {
        Extension.clearScreen();
        System.out.println("==== UPDATE CUSTOMER ====");
        cm.showList();
        Log.request("Nhap ID khach hang muon cap nhat (hoac nhap 0 de quay lai): ");
        String ID = sc.nextLine().trim();

        if (ID.equals("0")) {
            Log.info("Huy thao tac.");
            return;
        }

        if (!cm.exists(ID)) {
            Log.error("Khong ton tai khach hang voi ID: " + ID);
            return;
        }

        Customer c = cm.get(ID);
        Log.request("Ho va ten: ");
        c.setFullname(sc.nextLine());

        while (true) {
            Log.request("Ngay sinh (dd/MM/yyyy): ");
            String dob = sc.nextLine();
            try {
                c.setDob(dob);
                break;
            } catch (Exception e) {
                Log.error("Ngay sinh khong hop le!");
            }
        }

        Log.request("Dia chi: ");
        c.setAddress(sc.nextLine());
        Log.request("Email: ");
        c.setEmail(sc.nextLine());
        Log.request("So dien thoai: ");
        c.setPhone(sc.nextLine());

        cm.save();
        Log.success("Cap nhat thong tin thanh cong!");
        Extension.printInBox(() -> printCustomer(c));
    }

    @Override
    public void viewMenu() {
        Extension.clearScreen();
        System.out.println("==== VIEW CUSTOMER ====");
        cm.showList();
        Log.request("Nhap ID hoac Ten khach hang (Nhap 0 de quay lai): ");
        String input = sc.nextLine().trim();

        if (input.equals("0")) {
            Log.info("Huy thao tac.");
            return;
        }

        Customer user = cm.exists(input) ? cm.get(input) : null;
        if (user == null) {
            ArrayList<Customer> list = cm.getCustomerByName(input);
            if (list.isEmpty()) {
                Log.error("Khong tim thay khach hang voi keyword: " + input);
                return;
            }
            for (int i = 0; i < list.size(); i++) {
                System.out.println(i + " | " + list.get(i).getCID() + " | " + list.get(i).getFullname());
            }
            Log.request("Nhap STT muon xem: ");
            int choice = Integer.parseInt(sc.nextLine().trim());
            if (choice >= 0 && choice < list.size()) user = list.get(choice);
        }

        if (user == null) {
            Log.error("Khong tim thay khach hang!");
            return;
        }

        final Customer c = user;
        Extension.printInBox(() -> printCustomer(c));
    }

    public static void printCustomer(Customer c) {
        System.out.println("----- [" + c.getFullname() + "] -----");
        System.out.println("Role: [Customer]");
        System.out.println("CID: " + c.getCID());
        System.out.println("Ho ten: " + c.getFullname());
        System.out.println("Ngay sinh: " + c.getDob());
        System.out.println("Dia chi: " + c.getAddress());
        System.out.println("Email: " + c.getEmail());
        System.out.println("So dien thoai: " + c.getPhone());
        System.out.println("Trang thai: " + (c.getStatus() ? "Hoat dong" : "Bi chan"));
    }
}
