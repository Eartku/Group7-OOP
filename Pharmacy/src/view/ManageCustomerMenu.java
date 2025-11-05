package view;

import interfaces.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import models.*;
import service.*;
import ultils.*;

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
        Enhance.clearScreen();

        if (cm == null) {
            Log.error("Khong the quan ly khach hang!");
            return;
        }

        while (true) {
            Enhance.clearScreen();
            System.out.println("==== CUSTOMER MANAGER ====");
            System.out.println(cm.report());
            System.out.println("==== DANH SACH TAT CA KHACH HANG ====");
            cm.showList();
            cm.blackList();
            System.out.println("1. Them khach hang - Add Customer (Danh cho giao dich tai quay)");
            System.out.println("2. Kich hoat/Chan khach hang - Activate/Block Customers");
            System.out.println("3. Chinh sua thong tin khach hang - Edit Customers INFO");
            System.out.println("4. Xem thong tin khach hang - View Customers INFO");
            Log.exit("0. Thoat - Exit");

            int choice = Enhance.readIntInRange("Nhap lua chon (0-4):", 0, 4, sc);

            switch (choice) {
                case 1 -> {
                    addMenu();
                    Enhance.pause(sc);
                }
                case 2 -> {
                    System.out.println("1. Chan khach hang - Block Customer");
                    System.out.println("2. Kich hoat khach hang - Activate Customer");
                    Log.exit("0. Huy - Cancel");
                    int choice2 = Enhance.readIntInRange("Nhap lua chon (0-2):", 0, 2, sc);
                    switch (choice2) {
                        case 1 -> blockMenu();
                        case 2 -> activeMenu();
                        default -> Log.info("Huy thao tac.");
                    }
                    Enhance.pause(sc);
                }
                case 3 -> {
                    updateMenu();
                    Enhance.pause(sc);
                }
                case 4 -> {
                    viewMenu();
                    Enhance.pause(sc);
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
        Enhance.clearScreen();
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
            if (username.isEmpty()) {
                Log.warning("Username khong duoc de trong!");
                continue;
            }
            if (um.exists(username)) {
                Log.warning("Username da ton tai!");
            } else break;
        }

        String password;
        while (true) {
            Log.request("Nhap password: ");
            password = sc.nextLine().trim();
            if (password.isEmpty()) {
                Log.warning("Password khong duoc de trong!");
                continue;
            }
            if (password.length() < 6) {
                Log.warning("Password phai dai hon 6 ky tu!");
            } else break;
        }

        String CID = Data.generateNewID(CustomerManager.FILE_PATH, 'C');

        String fullname;
        while (true) {
            Log.request("Ho va ten: ");
            fullname = sc.nextLine().trim();
            if (fullname.isEmpty()) {
                 Log.warning("Khong duoc de trong mien nay!");
            } else break;
        }

        LocalDate dobDate = null;
        while (true) {
            Log.request("Ngay sinh (dd/MM/yyyy): ");
            String dob = sc.nextLine().trim();
            if (dob.isEmpty()) {
                 Log.warning("Khong duoc de trong mien nay!");
                continue;
            }
            try {
                dobDate = LocalDate.parse(dob, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                break;
            } catch (Exception e) {
                Log.error("Ngay sinh khong hop le! Nhap lai theo dd/MM/yyyy");
            }
        }

        String address;
        while (true) {
            Log.request("Dia chi: ");
            address = sc.nextLine().trim();
            if (address.isEmpty()) {
                 Log.warning("Khong duoc de trong mien nay!");
            } else break;
        }

        String email;
        while (true) {
            Log.request("Email: ");
            email = sc.nextLine().trim();
            if (email.isEmpty()) {
                 Log.warning("Khong duoc de trong mien nay!");
            } else break;
        }

        String phone;
        while (true) {
            Log.request("So dien thoai: ");
            phone = sc.nextLine().trim();
            if (phone.isEmpty()) {
                Log.warning("Khong duoc de trong mien nay!");
            } else break;
        }

        Customer newUser = new Customer(username, password, CID, fullname, dobDate, address, email, phone, true);
        cm.add(newUser);
        um.add(newUser);
        cm.save();
        um.save();
        Log.success("Da them khach hang thanh cong!");
    }

    @Override
    public void blockMenu() {
        Enhance.clearScreen();
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
        Enhance.clearScreen();
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
        Enhance.clearScreen();
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
        Log.request("Ho va ten (bo trong neu giu nguyen): ");
        String name = sc.nextLine();
        c.setFullname(name.isEmpty() ? c.getFullname() : name);
        Log.success("Successfull!");

        while (true) {
            Log.request("Ngay sinh (dd/MM/yyyy) (bo trong neu giu nguyen): ");
            String dobS = sc.nextLine();
            c.setDob(dobS.isEmpty() ? c.getDobdate() : dobS);
            String dob = sc.nextLine();
            try {
                c.setDob(dob);
                break;
            } catch (Exception e) {
                Log.error("Ngay sinh khong hop le!");
            }
            Log.success("Successfull!");

        }

        Log.request("Dia chi (bo trong neu giu nguyen): ");
        String adress = sc.nextLine();
        c.setAddress(adress.isEmpty() ? c.getAddress() : adress);
        Log.success("Successfull!");
        Log.request("Email (bo trong neu giu nguyen):");
        String email = sc.nextLine();
        c.setFullname(email.isEmpty() ? c.getEmail() : email);
        Log.success("Successfull!");
        Log.request("So dien thoai (bo trong neu giu nguyen): ");
        String phone = sc.nextLine();
        c.setFullname(phone.isEmpty() ? c.getPhone() : phone);
        Log.success("Successfull!");
        

        cm.save();
        Log.success("Cap nhat thong tin thanh cong!");
        Enhance.printInBox(() -> printCustomer(c));
    }

    @Override
    public void viewMenu() {
        Enhance.clearScreen();
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
        Enhance.printInBox(() -> printCustomer(c));
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
