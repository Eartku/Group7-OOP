package view;

import data.Data;
import interfaces.Authenticable;
import interfaces.ManageMenu;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import models.Admin;
import models.Customer;
import models.Guest;
import service.CustomerManager;
import service.UserManager;

public class ManageUserMenu implements ManageMenu {
    
    private final CustomerManager cm;
    private final UserManager um;
    private final Scanner sc;

    public ManageUserMenu(CustomerManager cm, UserManager um, Scanner sc) {
        this.cm = cm;
        this.um = um;
        this.sc = sc;
    }


    @Override
    public void mainMenu(){
        MainMenu.clearScreen();

        if (um == null) {
            System.out.println("Khong the quan ly user!");
            return;
        }

        while (true) {
            MainMenu.clearScreen();
            System.out.println("==== USER MANAGER ====");
            System.out.println(um.report());
            um.showList();
            System.out.println("1. Them User");
            System.out.println("2. Xoa User theo username");
            System.out.println("3. Cap nhat User theo username");
            System.out.println("4. Xem thong tin user theo username");
            System.out.println("0. Thoat");
            System.out.print("Chon: ");
            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException _) {
                System.out.println("Nhap so tu 0-4!");
                continue;
            }

            switch (choice) {
                case 1 -> {
                    addMenu();
                }

                case 2 -> {
                    removeMenu();
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
        MainMenu.clearScreen();
        System.out.println("==== ADD USER ====");
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
        String password;
        while (true) {
            System.out.print("Nhap password: ");
            password = sc.nextLine();
            if (password.length() >= 6) {
                break; 
            }
            System.out.println("Password phai dai hon 6 ky tu!");
        }

        int role;
        while (true) {
            System.out.print("Nhap role (Admin: 2 / Customer: 1 / Guest: 0): ");
            try {
                role = Integer.parseInt(sc.nextLine());
                if (role == 0 || role == 1 || role == 2) break;
                System.out.println("Role chi duoc 0, 1, hoac 2!");
            } catch (NumberFormatException e) {
                System.out.println("Nhap so nguyen (0, 1, 2)!");
            }
        }

        Authenticable newUser = switch (role) {
            case 0 -> new Guest(username, password);
            case 2 -> new Admin(username, password);
            case 1 -> {
                String CID = Data.generateNewID(username, 'C');
                System.out.print("Ho va ten: ");
                String fullname = sc.nextLine();

                LocalDate dobDate;
                while (true) {
                    System.out.print("Ngay sinh (dd/MM/yyyy): ");
                    try {
                        dobDate = LocalDate.parse(sc.nextLine(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        break;
                    } catch (Exception e) {
                        System.out.println("Ngay sinh khong hop le! Nhap lai.");
                    }
                }

                System.out.print("Dia chi: ");
                String address = sc.nextLine();
                System.out.print("Email: ");
                String email = sc.nextLine();
                System.out.print("So dien thoai: ");
                String phone = sc.nextLine();

                Customer c = new Customer(username, password, CID, fullname, dobDate, address, email, phone);
                um.appendCustomer("resources/customers.txt", c);
                yield c;
            }
            default -> null;
        };

        if (newUser != null) {
            um.add(newUser);
            System.out.println("Da them user thanh cong!");
            um.save();
        }
    }

    @Override
        public void removeMenu() {
        MainMenu.clearScreen();
        while (true) {
            System.out.println("==== REMOVE USER ====");
            um.showList();
            System.out.print("Nhap Username ban muon xoa (hoac nhap 0 de quay lai): ");
            String username = sc.nextLine().trim();

            if (username.equals("0")) {
                System.out.println("Huy thao tac xoa, quay lai menu chinh.");
                return;
            }

            if (!um.exists(username)) {
                System.out.println("Khong co User nao co username: " + username);
                continue;
            }

            System.out.print("Ban co chac muon xoa user " + username + " ? (y/n): ");
            String confirm = sc.nextLine().trim();

            if (confirm.equalsIgnoreCase("y")) {
                um.delete(username); // hàm void
                if (!um.exists(username)) {
                    System.out.println("Da xoa user: " + username);
                    um.save();
                } else {
                    System.out.println("Xoa khong thanh cong!");
                }
            } else {
                System.out.println("Da huy thao tac xoa.");
            }
            break;
        }
    }

    @Override
    public void updateMenu() {
        MainMenu.clearScreen();
        System.out.println("==== EDIT USER ====");
        um.showList();
        System.out.print("Nhap username muon cap nhat (hoac nhap 0 de quay lai): ");
        String oldUsername = sc.nextLine().trim();
        
        if (oldUsername.equals("0")) {
                System.out.println("Huy thao tac xoa, quay lai menu chinh.");
                return;
            }

        if (!um.exists(oldUsername)) {
            System.out.println("Khong ton tai user voi username: " + oldUsername);
            return;
        }
        Authenticable oldUser = um.get(oldUsername);

        String newUsername;
        while (true) {
            System.out.print("Nhap username moi (bo trong neu giu nguyen): ");
            newUsername = sc.nextLine().trim();
            if (newUsername.isEmpty()) {
                newUsername = oldUser.getUsername();
                break;
            }
            if (newUsername.equals(oldUser.getUsername())) {
                break;
            }

            if (um.exists(newUsername)) {
                System.out.println("Username da ton tai! Vui long nhap ten khac.");
            } else {
                break;
            }
        }
        if (newUsername.isEmpty()) newUsername = oldUser.getUsername();

        System.out.print("Nhap password moi (bo trong neu giu nguyen): ");
        String newPassword = sc.nextLine().trim();
        if (newPassword.isEmpty()) newPassword = oldUser.getPassword();

        int newRole = -1;
        while (true) {
            System.out.print("Nhap role moi (Admin: 2 / Customer: 1 / Guest: 0 / bo trong neu giu nguyen): ");
            String input = sc.nextLine().trim();

            if (input.isEmpty()) {
                // giữ nguyên role cũ
                if (oldUser instanceof Admin) newRole = 2;
                else if (oldUser instanceof Customer) newRole = 1;
                else newRole = 0;
                break;
            }

            try {
                newRole = Integer.parseInt(input);
                if (newRole == 0 || newRole == 1 || newRole == 2) {
                    break;
                } else {
                    System.out.println("Role chi duoc 0, 1, hoac 2!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Nhap so nguyen hop le (0-2) hoac bo trong de giu nguyen!");
            }
        }

        Authenticable newUser = null;
        switch (newRole) {
            case 0 -> newUser = new Guest(newUsername, newPassword);

            case 1 -> {
                if (oldUser instanceof Customer) {
                    Customer fullOld = cm.getbyUsername(oldUsername);
                    if (fullOld != null) {
                        newUser = new Customer(
                            newUsername,
                            newPassword,
                            fullOld.getCID(),
                            fullOld.getFullname(),
                            fullOld.getDob(),
                            fullOld.getAddress(),
                            fullOld.getEmail(),
                            fullOld.getPhone()
                        );
                    } else {
                        newUser = new Customer(newUsername, newPassword);
                    }
                } else {
                    newUser = new Customer(newUsername, newPassword);
                }
            }

            case 2 -> newUser = new Admin(newUsername, newPassword);
        }

        um.updateWithRole(newUser, oldUsername);
        System.out.println("Cap nhat thanh cong user: " + newUsername);
        um.save();
    }

    @Override
    public void viewMenu() {
        while(true){
            MainMenu.clearScreen();
            System.out.println("==== VIEW USER ====");
            um.showList();
            System.out.print("Nhap username ban muon xem (Nhap 0 de quay lai): ");
            String username = sc.nextLine().trim();

            if (username.equals("0")) {
                System.out.println("Huy thao tac xoa, quay lai menu chinh.");
                return;
            }

            Authenticable user = um.get(username);
            if (user == null) {
                System.out.println("Khong tim thay user voi username: " + username);
                return;
            }

            System.out.println("----- THONG TIN USER [" + username +"] -----");
            System.out.println("Username: " + user.getUsername());
            System.out.println("Password: " + user.getPassword());
            if (user instanceof Admin) {
                System.out.println("Role: [Admin]");
            } else if (user instanceof Customer c) {
                System.out.println("Role: [Customer]");
                c = cm.getbyUsername(username);
                if(c != null) ManageCustomerMenu.printCustomer(c);
                else System.out.println("Khach hang khong ton tai trong he thong!");
            } else if (user instanceof Guest) {
                System.out.println("Role: [Guest]");
            }
            System.out.print("Quay lai? Hay nhap 0: ");
            String choice = sc.nextLine().trim();
            if (choice.equals("0")) {
                System.out.println("Da quay lai menu chinh.");
                return;
            }
        }
    }
}
