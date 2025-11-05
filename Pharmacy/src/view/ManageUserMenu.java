package view;

import interfaces.IAuthenticable;
import interfaces.IManageMenu;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import models.Admin;
import models.Customer;
import models.Guest;
import service.CustomerManager;
import service.UserManager;
import ultils.Data;
import ultils.Enhance;
import ultils.Log;

public class ManageUserMenu implements IManageMenu {
    
    private final CustomerManager cm;
    private final UserManager um;
    private final Scanner sc;
    private boolean active = false;

    public ManageUserMenu(CustomerManager cm, UserManager um, Scanner sc) {
        this.cm = cm;
        this.um = um;
        this.sc = sc;
    }

    @Override
    public void mainMenu(){
        Enhance.clearScreen();

        if (um == null) {
            Log.error("Khong the quan ly user!");
            return;
        }

        while (true) {
            Enhance.clearScreen();
            System.out.println("==== USER MANAGER ====");
            System.out.println(um.report());
            System.out.println("==== DANH SACH USER HOAT DONG ====");
            if(active) um.showList(); else um.hidePassList();
            System.out.println("==== DANH SACH USER BI BLOCK ====");
            if(active) um.blackList(); else um.hideblackList();
            System.out.println("1. Them User - Add new user");
            System.out.println("2. Kich hoat/Khoa User theo username - Activate/Block user");
            System.out.println("3. Cap nhat User theo username - Edit user");
            System.out.println("4. Xem thong tin user theo username -View user");
            System.out.println("5. Che do AN mat khau - Hide password mode");
            System.out.println("6. Che do HIEN mat khau - Show Password mode");
            Log.exit("0. Thoat - Exit");
            int choice = Enhance.readIntInRange("Nhap lua chon (0-6):", 0, 6, sc);

            switch (choice) {
                case 1 -> {
                    addMenu();
                    Enhance.pause(sc);
                }
                case 2 -> {
                    System.out.println("1. Chan User - Block User");
                    System.out.println("2. Kich hoat User - Activate User");
                    Log.exit("0. Huy - Cancel");
                    int choice2 = Enhance.readIntInRange("Nhap lua chon (0-2):", 0, 2, sc);
                    switch (choice2) {
                        case 0 -> { break; }
                        case 1 -> blockMenu();
                        case 2 -> activeMenu();
                        default -> Log.warning("Lua chon khong hop le!");
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
                case 5 -> this.active = false;
                case 6 -> this.active = true;
                case 0 -> {
                    Log.exit("Thoat chuong trinh. Tam biet!");
                    return;
                }
                default -> Log.warning("Lua chon khong hop le!");
            }
        }
    }

    @Override
    public void addMenu(){
        Enhance.clearScreen();
        System.out.println("==== ADD USER ====");
        String username;
        while(true){
            Log.request("Nhap username (hoac nhap 0 de quay lai): ");
            username = sc.nextLine().trim();
            if (username.equals("0")) {
                Log.info("Huy thao tac.");
                return;
            }
            if(um.exists(username)){
                Log.warning("Username da ton tai!");
            }
            else break;
        }

        String password;
        while (true) {
            Log.request("Nhap password: ");
            password = sc.nextLine().trim();
            if (password.length() >= 6) break;
            Log.error("Password phai dai hon 6 ky tu!");
        }

        int role;
        while (true) {
            Log.request("Nhap role (Admin: 2 / Customer: 1 / Guest: 0): ");
            try {
                role = Integer.parseInt(sc.nextLine());
                if (role == 0 || role == 1 || role == 2) break;
                Log.warning("Role chi duoc 0, 1, hoac 2!");
            } catch (NumberFormatException e) {
                Log.error("Nhap so nguyen hop le (0, 1, 2)!");
            }
        }

        IAuthenticable newUser = switch (role) {
            case 0 -> new Guest(username, password, true);
            case 2 -> new Admin(username, password, true);
            case 1 -> {
                String CID = Data.generateNewID(username, 'C');
                Log.request("Ho va ten: ");
                String fullname = sc.nextLine();

                LocalDate dobDate;
                while (true) {
                    Log.request("Ngay sinh (dd/MM/yyyy): ");
                    try {
                        dobDate = LocalDate.parse(sc.nextLine(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        break;
                    } catch (Exception e) {
                        Log.error("Ngay sinh khong hop le! Nhap lai.");
                    }
                }

                Log.request("Dia chi: ");
                String address = sc.nextLine();
                Log.request("Email: ");
                String email = sc.nextLine();
                Log.request("So dien thoai: ");
                String phone = sc.nextLine();

                Customer c = new Customer(username, password, CID, fullname, dobDate, address, email, phone, true);
                yield c;
            }
            default -> null;
        };

        if (newUser != null) {
            um.add(newUser);
            if (newUser instanceof Customer c) cm.add(c);
            Log.success("Da them user thanh cong!");
            UserInfo(newUser);
            um.save();
            cm.save();
        }
    }

    @Override
    public void blockMenu() {
        while(true){
            Enhance.clearScreen();
            System.out.println("==== BLOCK USER ====");
            if(active) um.showList(); else um.hidePassList();
            Log.request("Nhap username (hoac nhap 0 de quay lai): ");
            String username = sc.nextLine().trim();
            if (username.equals("0")) {
                Log.info("Huy thao tac.");
                return;
            }
            IAuthenticable user = um.get(username);
            if (user == null || !user.getStatus()) {
                Log.warning("Khong tim thay user: " + username);
                return;
            }

            if (user instanceof Admin) {
                Log.error("Khong the block Admin khac hoac tu block chinh minh!");
                Log.info("Enter de tiep tuc | 0 de thoat.");
                if(sc.nextLine().trim().equals("0")) return;
                continue;
            }

            UserInfo(user);

            Log.request("Ban co chac muon chan user " + username + "? (y/n): ");
            String confirm0 = sc.nextLine().trim();
            if (!confirm0.equalsIgnoreCase("y")) {
                Log.info("Da huy thao tac chan.");
                return;
            }

            user.setStatus(false);
            um.save();
            Log.success("User " + username + " da bi block.");
            Log.request("Tiep tuc chan? [Nhap 0 de thoat | Enter de tiep tuc]: ");
            String confirm = sc.nextLine().trim();
            if (confirm.equals("0")) {
                Log.info("Huy thao tac chan..");
                return;
            }
        }
    }
    
    @Override
    public void activeMenu() {
        while(true){
            Enhance.clearScreen();
            System.out.println("==== ACTIVATE USER ====");
            um.blackList();
            Log.request("Nhap username (hoac nhap 0 de quay lai): ");
            String username = sc.nextLine().trim();
            if (username.equals("0")) {
                Log.info("Huy thao tac.");
                return;
            }
            IAuthenticable user = um.get(username);
            if (user == null || user.getStatus()) {
                Log.warning("Khong tim thay user: " + username);
                return;
            }

            UserInfo(user);

            Log.request("Ban co chac muon kich hoat user " + username + "? (y/n): ");
            String confirm0 = sc.nextLine().trim();
            if (!confirm0.equalsIgnoreCase("y")) {
                Log.info("Da huy thao tac kich hoat.");
                return;
            }

            user.setStatus(true);
            um.save();
            Log.success("User " + username + " da duoc kich hoat.");
            Log.request("Tiep tuc kich hoat? [Nhap 0 de thoat | Enter de tiep tuc]: ");
            String confirm = sc.nextLine().trim();
             if (confirm.equals("0")) {
                Log.info("Huy thao tac kich hoat, quay lai menu chinh.");
                return;
            }
        }
    }

    @Override
    public void updateMenu() {
        Enhance.clearScreen();
        System.out.println("==== EDIT USER ====");
        
        if (active) um.showList(); 
        else um.hidePassList();
        
        Log.request("Nhap username muon cap nhat (hoac nhap 0 de quay lai): ");
        String oldUsername = sc.nextLine().trim();
        if (oldUsername.equals("0")) {
            Log.info("Huy thao tac.");
            return;
        }

        IAuthenticable oldUser = um.get(oldUsername);
        if (oldUser == null) {
            Log.warning("Khong ton tai user voi username: " + oldUsername);
            return;
        }

        String role = (oldUser.getRole() == 0)? "Guest": (oldUser.getRole() == 1 ? "Customer" : "Admin");
        Log.info("[INFO] Hien tai User["+ oldUsername + "] dang la "+ role + ", trang thai: " + oldUser.getStatusString());

        String newUsername;
        while (true) {
            Log.request("Nhap username moi (bo trong neu giu nguyen): ");
            newUsername = sc.nextLine().trim();
            if (newUsername.isEmpty()) {
                newUsername = oldUser.getUsername();
                break;
            }
            if (newUsername.equals(oldUser.getUsername())) break;
            if (um.exists(newUsername)) Log.warning("Username da ton tai! Vui long nhap ten khac.");
            else break;
        }

        Log.request("Nhap password moi (bo trong neu giu nguyen): ");
        String newPassword = sc.nextLine().trim();
        if (newPassword.isEmpty()) newPassword = oldUser.getPassword();

        int oldRole = oldUser.getRole();
        int newRole = -1;
        while (true) {
            Log.request("Nhap role moi (Admin: 2 / Customer: 1 / Guest: 0 / bo trong neu giu nguyen): ");
            String input = sc.nextLine().trim();
            if (input.isEmpty()) {
                if (oldUser instanceof Admin) newRole = 2;
                else if (oldUser instanceof Customer) newRole = 1;
                else newRole = 0;
                break;
            }
            try {
                newRole = Integer.parseInt(input);
                if (newRole >= 0 && newRole <= 2) break;
                Log.warning("Role chi duoc 0, 1, hoac 2!");
            } catch (NumberFormatException e) {
                Log.error("Nhap so nguyen hop le (0-2) hoac bo trong de giu nguyen!");
            }
        }

        boolean canChange = switch (oldRole) {
            case 0 -> newRole == 1;
            case 1, 2 -> newRole == oldRole;
            default -> false;
        };

        if (!canChange) {
            Log.error("Khong duoc phep thay doi vai tro nay!");
            return;
        }

        boolean oldStatus = oldUser.getStatus();
        System.out.println("Chon trang thai tai khoan:");
        System.out.println("1. Active");
        System.out.println("2. Blocked");
        Log.request("Nhap lua chon (1-2, bo trong de giu nguyen): ");
        String statusInput = sc.nextLine().trim();
        boolean newStatus = oldStatus;
        if (!statusInput.isEmpty()) {
            if (statusInput.equals("1")) newStatus = true;
            else if (statusInput.equals("2")) newStatus = false;
        }

        IAuthenticable newUser = switch (newRole) {
            case 0 -> new Guest(newUsername, newPassword, newStatus);
            case 2 -> new Admin(newUsername, newPassword, newStatus);
            case 1 -> {
                Customer fullOld = (oldUser instanceof Customer) ? cm.getByUsername(oldUsername) : null;
                if (fullOld != null) {
                    yield new Customer(
                        newUsername,
                        newPassword,
                        fullOld.getCID(),
                        fullOld.getFullname(),
                        fullOld.getDob(),
                        fullOld.getAddress(),
                        fullOld.getEmail(),
                        fullOld.getPhone(),
                        newStatus
                    );
                } else {
                    yield new Customer(newUsername, newPassword, newStatus);
                }
            }
            default -> null;
        };

        um.replaceUser(oldUser, newUser);
        if (newUser instanceof Customer newCustomer) {
            cm.updateCustomer(newCustomer, oldUsername);
            cm.save();
        }

        Log.success("Cap nhat thanh cong user: " + newUsername);
        UserInfo(newUser);

        um.save();
    }

    public void UserInfo(IAuthenticable user){
        Enhance.printInBox(() -> {
            System.out.println("----- USER [" + user.getUsername() +"] -----");
            System.out.println("Username: " + user.getUsername());
            System.out.println("Password: " + user.getPassword());
            if (user instanceof Admin) Log.info("Role: [Admin]");
            else if (user instanceof Customer c) {
                Log.info("Role: [Customer]");
                c = cm.getByUsername(user.getUsername());
                if(c != null) ManageCustomerMenu.printCustomer(c);
                else Log.warning("Khach hang khong ton tai trong he thong!");
            }
            else if (user instanceof Guest) Log.info("Role: [Guest]");
        });
    }

    @Override
    public void viewMenu() {
        while(true){
            Enhance.clearScreen();
            System.out.println("==== VIEW USER ====");
            if(active) um.showList(); else um.hidePassList();
            Log.request("Nhap username ban muon xem (Nhap 0 de quay lai): ");
            String username = sc.nextLine().trim();

            if (username.equals("0")) {
                Log.info("Da quay lai menu chinh.");
                return;
            }

            IAuthenticable user = um.get(username);
            if (user == null) {
                Log.warning("Khong tim thay user voi username: " + username);
                return;
            }

            UserInfo(user);
            Log.request("Quay lai? Hay nhap 0, Enter de tiep tuc: ");
            String choice = sc.nextLine().trim();
            if (choice.equals("0")) {
                Log.info("Da quay lai menu chinh.");
                return;
            }
        }
    }
}
