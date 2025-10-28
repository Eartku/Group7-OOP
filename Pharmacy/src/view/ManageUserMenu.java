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
    private boolean active = false;

    public ManageUserMenu(CustomerManager cm, UserManager um, Scanner sc) {
        this.cm = cm;
        this.um = um;
        this.sc = sc;
    }


    @Override
    public void mainMenu(){
        Extension.clearScreen();

        if (um == null) {
            System.out.println("Khong the quan ly user!");
            return;
        }

        while (true) {
            Extension.clearScreen();
            System.out.println("==== USER MANAGER ====");
            System.out.println(um.report());
            if(active) um.showList(); else um.hidePassList();
            System.out.println("1. Them User - Add new user");
            System.out.println("2. Kich hoat/Khoa User theo username - Activate/Block user");
            System.out.println("3. Cap nhat User theo username - Edit user");
            System.out.println("4. Xem thong tin user theo username -View user");
            System.out.println("5. Che do AN mat khau - Hide password mode");
            System.out.println("6. che do HIEN mat khau - Show Password mode");
            System.out.println("0. Thoat - Exit");
            System.out.print("Chon: ");
            int choice = Extension.readIntInRange("Nhap lua chon (0-4):", 0, 6, sc);

            switch (choice) {
                case 1 -> {
                    addMenu();
                }
                case 2 -> {
                    System.out.println("1. Chan User - Block User");
                    System.out.println("2. Kich hoat User - Activate User");
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
                case 5 -> {
                    this.active = false;
                }
                case 6 -> {
                    this.active = true;
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
            case 0 -> new Guest(username, password, true);
            case 2 -> new Admin(username, password, true);
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

                Customer c = new Customer(username, password, CID, fullname, dobDate, address, email, phone, true);
                yield c;
            }
            default -> null;
        };

        if (newUser != null) {
            um.add(newUser);
            if (newUser instanceof Customer c) {
                cm.add(c);       // thêm vào danh sách khách hàng trong bộ nhớ
            }
            System.out.println("Da them user thanh cong!");
            UserInfo(newUser);
            um.save();
            cm.save();
            Extension.pause(sc);
        }
    }

    @Override
    public void removeMenu() {
        while(true){
            Extension.clearScreen();
            System.out.println("==== BLOCK USER ====");
            if(active) um.showList(); else um.hidePassList();
            System.out.print("Nhap username (hoac nhap 0 de quay lai): ");
            String username = sc.nextLine().trim();
            if (username.equals("0")) {
                System.out.println("Huy thao tac chan, quay lai menu chinh.");
                return;
            }
            Authenticable user = um.get(username);
            if (user == null || user.getStatus()== false) {
                System.out.println("Khong tim thay user: " + username);
                return;
            }

            if (user instanceof Admin) {
                System.out.println("Khong the block Admin khac HOAC tu block chinh minh!");
                System.out.print("Enter de tiep tuc | 0 đe thoat: ");
                if(sc.nextLine().trim().equals("0")) return;
                continue;
            }

            UserInfo(user);

            System.out.print("Ban co chac muon chan user " + username + "? (y/n): ");
            String confirm0 = sc.nextLine().trim();
            if (!confirm0.equalsIgnoreCase("y")) {
                System.out.println("Da huy thao tac chan.");
                return;
            }

            user.setStatus(false);
            um.save();
            System.out.println("User " + username + " da bi block.");
            System.out.print("Tiep tuc chan? [Nhap 0 de thoat | Enter de tiep tuc]: ");
            String confirm = sc.nextLine().trim();
             if (confirm.equals("0")) {
                System.out.println("Huy thao tac chan, quay lai menu chinh.");
                return;
            }
        }
    }
    
    public void activeMenu() {
        while(true){
            Extension.clearScreen();
            System.out.println("==== ACTIVATE USER ====");
            um.blackList();
            System.out.print("Nhap username (hoac nhap 0 de quay lai): ");
            String username = sc.nextLine().trim();
            if (username.equals("0")) {
                System.out.println("Huy thao tac chan, quay lai menu chinh.");
                return;
            }
            Authenticable user = um.get(username);
            if (user == null || user.getStatus()== true) {
                System.out.println("Khong tim thay user: " + username);
                return;
            }

            UserInfo(user);

            System.out.print("Ban co chac muon kich hoat user " + username + "? (y/n): ");
            String confirm0 = sc.nextLine().trim();
            if (!confirm0.equalsIgnoreCase("y")) {
                System.out.println("Da huy thao tac kich hoat.");
                return;
            }

            user.setStatus(false);
            um.save();
            System.out.println("User " + username + " da duoc kich hoat.");
            System.out.print("Tiep tuc chan? [Nhap 0 de thoat | Enter de tiep tuc]: ");
            String confirm = sc.nextLine().trim();
             if (confirm.equals("0")) {
                System.out.println("Huy thao tac kich hoat, quay lai menu chinh.");
                return;
            }
        }
    }

    
    public void removeMenuPhysical() {
        System.out.print("Nhap username (hoac nhap 0 de quay lai): ");
        String username = sc.nextLine().trim();
        if (username.equals("0")) {
            System.out.println("Huy thao tac xoa, quay lai menu chinh.");
            return;
        }
        Authenticable user = um.get(username);
        if (user == null) {
            System.out.println("Khong tim thay user: " + username);
            return;
        }

        System.out.print("Ban co chac muon xoa vat ly user " + username + "? (y/n): ");
        String confirm = sc.nextLine().trim();
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Da huy thao tac xoa vat ly.");
            return;
        }

        um.delete(username); // xoa khoi UserManager
        if (user instanceof Customer c) {
            cm.delete(c.getCID()); 
            cm.save();
        }
        um.save();
        System.out.println("Da xoa vat ly user: " + username);
    }


    @Override
    public void updateMenu() {
        Extension.clearScreen();
        System.out.println("==== EDIT USER ====");
        
        // Hiển thị danh sách user
        if (active) um.showList(); 
        else um.hidePassList();
        
        // Nhập username cần chỉnh sửa
        System.out.print("Nhap username muon cap nhat (hoac nhap 0 de quay lai): ");
        String oldUsername = sc.nextLine().trim();
        if (oldUsername.equals("0")) {
            System.out.println("Huy thao tac, quay lai menu chinh.");
            return;
        }

        // Lấy user cũ, kiểm tra null
        Authenticable oldUser = um.get(oldUsername);
        if (oldUser == null) {
            System.out.println("Khong ton tai user voi username: " + oldUsername);
            return;
        }

        String role = (oldUser.getRole() == 0)? "Guest": (oldUser.getRole() == 1 ? "Customer" : "Admin");

        System.out.println("[INFO]: Hien tai User["+ oldUsername + "] dang la "+ role + ", trang thai: " + oldUser.getStatusString());
        // Nhập username mới
        String newUsername;
        while (true) {
            System.out.print("Nhap username moi (bo trong neu giu nguyen): ");
            newUsername = sc.nextLine().trim();
            if (newUsername.isEmpty()) {
                newUsername = oldUser.getUsername();
                break;
            }
            if (newUsername.equals(oldUser.getUsername())) break;
            if (um.exists(newUsername)) {
                System.out.println("Username da ton tai! Vui long nhap ten khac.");
            } else {
                break;
            }
        }

        // Nhập password mới
        System.out.print("Nhap password moi (bo trong neu giu nguyen): ");
        String newPassword = sc.nextLine().trim();
        if (newPassword.isEmpty()) newPassword = oldUser.getPassword();

        int oldRole = oldUser.getRole();
        int newRole = -1;
        while (true) {
            System.out.print("Nhap role moi (Admin: 2 / Customer: 1 / Guest: 0 / bo trong neu giu nguyen): ");
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
                System.out.println("Role chi duoc 0, 1, hoac 2!");
            } catch (NumberFormatException e) {
                System.out.println("Nhap so nguyen hop le (0-2) hoac bo trong de giu nguyen!");
            }
        }

        boolean canChange = switch (oldRole) {
            case 0 -> newRole == 1;
            case 1, 2 -> newRole == oldRole;
            default -> false;
        };

        if (!canChange) {
            System.out.println("Khong duoc phep thay doi vai tro nay!");
            return;
        }


        // Lấy trạng thái cũ
        boolean oldStatus = oldUser.getStatus();
        System.out.println("Chon trang thai tai khoan:");
        System.out.println("1. Active");
        System.out.println("2. Blocked");
        System.out.print("Nhap lua chon (1-2, bo trong de giu nguyen): ");
        String statusInput = sc.nextLine().trim();
        boolean newStatus = oldStatus; // mặc định giữ nguyên
        if (!statusInput.isEmpty()) {
            if (statusInput.equals("1")) newStatus = true;
            else if (statusInput.equals("2")) newStatus = false;
        }

        // Tạo user mới theo role
        Authenticable newUser = switch (newRole) {
            case 0 -> new Guest(newUsername, newPassword, oldStatus);
            case 2 -> new Admin(newUsername, newPassword, oldStatus);
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

        System.out.println("Cap nhat thanh cong user: " + newUsername);
        UserInfo(newUser);
        Extension.pause(sc);
        um.save();
    }

    public void UserInfo(Authenticable user){
        Extension.printInBox(() -> {
            System.out.println("----- USER [" + user.getUsername() +"] -----");
            System.out.println("Username: " + user.getUsername());
            System.out.println("Password: " + user.getPassword());
            if (user instanceof Admin) {
                System.out.println("Role: [Admin]");
            } else if (user instanceof Customer c) {
                System.out.println("Role: [Customer]");
                c = cm.getByUsername(user.getUsername());
                if(c != null) ManageCustomerMenu.printCustomer(c);
                else System.out.println("Khach hang khong ton tai trong he thong!");
            } else if (user instanceof Guest) {
                System.out.println("Role: [Guest]");
            }
        });
    }


    @Override
    public void viewMenu() {
        while(true){
            Extension.clearScreen();
            System.out.println("==== VIEW USER ====");
            if(active) um.showList(); else um.hidePassList();
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

            UserInfo(user);
            
            System.out.print("Quay lai? Hay nhap 0, Enter de tiep tuc: ");
            String choice = sc.nextLine().trim();
            if (choice.equals("0")) {
                System.out.println("Da quay lai menu chinh.");
                return;
            }
        }
    }

    public void viewBlackList() {
        while(true){
            Extension.clearScreen();
            System.out.println("==== VIEW USER IN BLACKLIST ====");
            um.blackList();
            System.out.print("Nhap username ban muon xem (Nhap 0 de quay lai): ");
            String username = sc.nextLine().trim();

            if (username.equals("0")) {
                System.out.println("Huy thao tac xoa, quay lai menu chinh.");
                return;
            }

            Authenticable user = um.get(username);
            if (user == null || user.getStatus()== true) {
                System.out.println("Khong tim thay user voi username: " + username);
                return;
            }

            UserInfo(user);

            System.out.print("Ban co muon mo khoa user " + username + "? (y/n): ");
            String confirm = sc.nextLine().trim();
            if (!confirm.equalsIgnoreCase("y")) {
                System.out.println("Huy thao tac mo khoa.");
                return;
            }
            

            user.setStatus(true);
            um.save();
            System.out.println("User " + username + " da duoc mo khoa.");
            System.out.print("Tiep tuc chan? [Nhap 0 de thoat | Enter de tiep tuc]: ");
            String cancel = sc.nextLine().trim();
             if (cancel.equals("0")) {
                System.out.println("Huy thao tac chan, quay lai menu chinh.");
                return;
            }

            System.out.print("Quay lai? Hay nhap 0, Enter de tiep tuc: ");
            String choice = sc.nextLine().trim();
            if (choice.equals("0")) {
                System.out.println("Da quay lai menu chinh.");
                return;
            }
        }
    }
}
