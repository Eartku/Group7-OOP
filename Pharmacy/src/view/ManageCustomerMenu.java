package view;

import data.Data;
import interfaces.ManageMenu;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;
import models.Customer;
import service.CustomerManager;
import service.UserManager;

public class ManageCustomerMenu implements ManageMenu{

    private final CustomerManager cm;
    private final UserManager um;
    private final Scanner sc;

    public ManageCustomerMenu(CustomerManager cm, UserManager um, Scanner sc) {
        this.cm = cm;
        this.um = um;
        this.sc = sc;
    }

    @Override
    public void mainMenu(){
        Extension.clearScreen();

        if (cm == null) {
            System.out.println("Khong the quan ly khach hang!");
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
            System.out.println("0. Thoat");
            System.out.print("Chon: ");
            int choice = Extension.readIntInRange("Nhap lua chon (0-4):", 0, 4, sc);

            switch (choice) {
                case 1 -> {
                    addMenu();
                }
                case 2 -> {
                    System.out.println("1. An khach hang - Block Customer");
                    System.out.println("2. Kich hoat khach hang - Activate Customer");
                    System.out.println("0. Huy - Cancel");
                    int choice2 = Extension.readIntInRange("Nhap lua chon (0-2):", 0, 2, sc);
                    switch (choice2) {
                        case 0 ->{break;}
                        case 1 ->removeMenu();
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
        System.out.println("==== ADD CUSTOMER ====");
        System.out.println("Truoc tien, ban can phai dang ky tai khoan: ");
        String username;
        while(true){
            System.out.print("Nhap username (hoac nhap 0 de quay lai): ");
            username = sc.nextLine().trim();
            if (username.equals("0")) {
                System.out.println("Huy thao tac xoa, quay lai menu chinh.");
                return;
            }
            if(um.exists(username)){
                System.out.println("Username da ton tai!");
            }
            else break;
            
        }
        System.out.print("Nhap password: ");
        String password;
        while (true) {
            System.out.print("Nhap password: ");
            password = sc.nextLine();
            if (password.length() >= 6) {
                break; 
            }
            System.out.println("Password phai dai hon 6 ky tu!");
        }
        
        String CID = Data.generateNewID(CustomerManager.FILE_PATH, 'C');
        System.out.print("Ho va ten: ");
        String fullname = sc.nextLine();

        LocalDate dobDate = null;
        while (true) {
            System.out.print("Ngay sinh (dd/MM/yyyy): ");
            String dob = sc.nextLine();
            try {
                dobDate = LocalDate.parse(dob, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                break;
            } catch (Exception e) {
                System.out.println("Ngay sinh khong hop le! Nhap lai theo dd/MM/yyyy");
            }
        }

        System.out.print("Dia chi: ");
        String address = sc.nextLine();
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("So dien thoai: ");
        String phone = sc.nextLine();

        Customer newUser = new Customer(username, password, CID, fullname, dobDate, address, email, phone, true);
        cm.add(newUser);
        um.add(newUser);
        System.out.println("Da them khach hang thanh cong!");
        um.save();
        cm.save();
    }

    @Override
    public void removeMenu() {
        Extension.clearScreen();
        while (true) {
            System.out.println("==== BLOCK CUSTOMER ====");
            cm.showList();
            System.out.print("Nhap ID khach hang muon chan (hoac nhap 0 de quay lai): ");
            String inputID = sc.nextLine().trim();

            if (inputID.equals("0")) {
                System.out.println("Huy thao tac xoa, quay lai menu chinh.");
                return;
            }

            if (!cm.exists(inputID)) {
                System.out.println("Khong co khach hang nao co ID: " + inputID);
                continue;
            }

            Customer c = cm.get(inputID);
            printCustomer(c);
            if(c.getStatus() == false) {
                System.out.println("Khach hang nay da bi chan tu truoc!");
                System.out.print("Nhap 0 de thoat | Enter de tiep tuc: ");
                if(sc.nextLine().trim().equals("0")) return;
                continue;
            }
            System.out.print("Ban co chac muon xoa khach hang nay khoi danh sach:  " + inputID + " ? (y/n): ");
            String confirm = sc.nextLine().trim();
            if (confirm.equalsIgnoreCase("y")) {
                c.setStatus(false);
                System.out.println("Da chan thanh cong khach hang "+ c.getCID());
            } else {
                System.out.println("Da huy thao tac CHAN.");
            }
            System.out.println("Ban co muon tiep tuc Chan? [Nhap 0 de thoat | Enter de tiep tuc]");
            String confirm0 = sc.nextLine().trim();
            if (confirm0.equals("0")) {
                System.out.println("Quay lai menu chinh.");
                return;
            }
        }
    }


    public void activeMenu() {
        Extension.clearScreen();
        while (true) {
            System.out.println("==== ACTIVATE CUSTOMER IN BLACKLIST====");
            cm.showBlackList();
            System.out.print("Nhap ID khach hang muon bo chan (hoac nhap 0 de quay lai): ");
            String inputID = sc.nextLine().trim();

            if (inputID.equals("0")) {
                System.out.println("Huy thao tac xoa, quay lai menu chinh.");
                return;
            }

            if (!cm.exists(inputID)) {
                System.out.println("Khong co khach hang nao co ID: " + inputID);
                continue;
            }

            Customer c = cm.get(inputID);
            printCustomer(c);
            if(c.getStatus() == true) {
                System.out.println("Khach hang nay da duoc kich hoat tu truoc!");
                System.out.print("Nhap 0 de thoat | Enter de tiep tuc: ");
                if(sc.nextLine().trim().equals("0")) return;
                continue;
            }
            System.out.print("Ban co chac muon kich hoat lai khach hang nay?:  " + inputID + " ? (y/n): ");
            String confirm = sc.nextLine().trim();

            if (confirm.equalsIgnoreCase("y")) {
                cm.get(inputID).setStatus(true);
                System.out.println("Da kich hoat khach hang: " + inputID );
            } else {
                System.out.println("Da huy thao tac KICH HOAT.");
            }
            System.out.println("Ban co muon tiep tuc kich hoạt? [Nhap 0 de thoat | Enter de tiep tuc]");
            String confirm0 = sc.nextLine().trim();
            if (confirm0.equals("0")) {
                System.out.println("Quay lai menu chinh.");
                return;
            }
        }
    }

    @Override
    public void updateMenu() {
        Extension.clearScreen();
        System.out.println("==== UPDATE CUSTOMER ====");
        cm.showList();
        System.out.print("Nhap ID khach hang muon cap nhat (hoac nhap 0 de quay lai): ");
        String ID = sc.nextLine().trim();
        
        if (ID.equals("0")) {
                System.out.println("Huy thao tac xoa, quay lai menu chinh.");
                return;
            }

        if (!cm.exists(ID)) {
            System.out.println("Khong ton tai khach hang voi ID: " + ID);
            return;
        }
        Customer oldCus = cm.get(ID);
        System.out.print("Ho va ten: ");
        String fullname = sc.nextLine();
        oldCus.setFullname(fullname);

        while (true) {
            System.out.print("Ngay sinh (dd/MM/yyyy): ");
            String dob = sc.nextLine();
            try {
                oldCus.setDob(dob);
                break;
            } catch(Exception e) { System.out.println(e.getMessage()); }
        }

        System.out.print("Dia chi: ");
        String address = sc.nextLine();
        oldCus.setAddress(address);

        System.out.print("Email: ");
        String email = sc.nextLine();
        oldCus.setEmail(email);
        
        System.out.print("So dien thoai: ");
        String phone = sc.nextLine();
        oldCus.setPhone(phone);

        cm.save();
        System.out.println("Cap nhat thong tin khach hang thanh cong!");
        Extension.printInBox(() -> {printCustomer(oldCus);});
    }

    @Override
    public void viewMenu() {
        Extension.clearScreen();
        System.out.println("==== VIEW CUSTOMER ====");
        cm.showList();
        System.out.print("Nhap ID (hoac Ho va Ten) khach hang muon xem (Nhap 0 de quay lai): ");
        String input = sc.nextLine().trim();

        if (input.equals("0")) {
            System.out.println("Huy thao tac xoa, quay lai menu chinh.");
            return;
        }
        Customer user = null;
        if(cm.exists(input)) user = cm.get(input);
        else{
            ArrayList<Customer> list = cm.getCustomerByName(input);
            int i = 0;
            for(Customer u : list){
                System.out.println(i+ "|| " + u.getCID() + "|" + u.getFullname() + "|"+ u.getUsername());
                i++;
            }
            System.out.print("\nNhap STT cua khach hang muon xem: ");
            int choice = Integer.parseInt(sc.nextLine().trim()); 
            if (choice >= 0 && choice < list.size()) {
                user = list.get(choice);
            }
        }

        if (user == null) {
            System.out.println("Khong tim thay user voi keyword: " + input);
            return;
        }

        final Customer c = user;
        Extension.printInBox(() -> {printCustomer(c);});

        System.out.print("Quay lai? Hay nhap 0: ");
        String choice = sc.nextLine().trim();
        if (choice.equals("0")) {
            System.out.println("Huy thao tac xoa, quay lai menu chinh.");
        }
    }

    public static void printCustomer(Customer user) {
            System.out.println("-----  [" + user.getFullname() + "] -----");
            System.out.println("Role: [Customer]");
            System.out.println("CID: " + user.getCID());
            System.out.println("Ho ten: " + user.getFullname());
            System.out.println("Ngay sinh: " + user.getDob());
            System.out.println("Dia chi: " + user.getAddress());
            System.out.println("Email: " + user.getEmail());
            System.out.println("So dien thoai: " + user.getPhone());
    }

}
