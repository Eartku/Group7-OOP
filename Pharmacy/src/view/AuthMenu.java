package view;

import data.Data;
import interfaces.IAuthenticable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import models.Admin;
import models.Customer;
import models.Guest;
import service.CustomerManager;
import service.Inventory;
import service.OrderManager;
import service.ProductManager;
import service.UserManager;

public class AuthMenu {

    public static IAuthenticable Register(Scanner sc, UserManager um, CustomerManager cm){
        Extension.clearScreen();
        System.out.println("______DANG KY_____");
        String username;
        while(true){
            System.out.println("Nhap Username (hoac nhap 0 de quay lai): ");
            username = sc.nextLine().trim();

            if (username.equals("0")) {
                System.out.println("Huy thao tac xoa, quay lai menu chinh.");
                // break;
                return null;
            }

            if(um.exists(username)){
                System.out.println("Username da ton tai!");
                System.out.println("Dang nhap? (y/n): ");
                char choice = sc.nextLine().charAt(0);
                if(choice == 'Y' || choice == 'y'){
                    return Login(sc, um, cm);
                }
            }
            else{
                System.out.println("Username hop le!");
                break;
            }
        }
        String password;
        while(true){
            System.out.println("Nhap Password: ");
            password = sc.nextLine();
            if(password.length() < 6){
                System.out.println("Password it nhat 6 ky tu!");
            }
            else{
                System.out.println("Password hop le!");
                break;
            }
        }
        IAuthenticable newUser = new Guest(username, password, true);
        um.add(newUser);
        um.save();
        System.out.println("Dang ky thanh cong!");
        return newUser;
    }

    public static IAuthenticable Login(Scanner sc, UserManager um, CustomerManager cm){
        Extension.clearScreen();
        String username, password; 
        try {
            System.out.println("______DANG NHAP_____");
            while(true){
                System.out.println("Nhap Username (hoac nhap 0 de quay lai): ");
                username = sc.nextLine().trim();

                if (username.equals("0")) {
                    System.out.println("Huy thao tac xoa, quay lai menu chinh.");
                    break;
                }
                if(um.exists(username)){
                    IAuthenticable user = um.get(username);
                    while (true){
                        System.out.println("Nhap mat khau: ");
                        password = sc.nextLine();
                        if(user.checkPassword(password)){
                            System.out.println("Password hop le!");
                            if (user instanceof Customer) {
                                Customer c = cm.getByUsername(username);
                                if (c != null) return c; // trả về Customer đầy đủ
                            }
                                return user;
                        }
                        else{
                            System.out.println("Sai mat khau!");
                        }
                    }
                }
                else{
                    System.out.println("Khong tim thay username!");
                    System.out.println("Dang ky tai khoan? (y/n): ");
                    char choice = sc.nextLine().charAt(0);
                    if(choice == 'Y' || choice == 'y'){
                        Register(sc, um, cm);
                        break;
                    }
                    return null;
                }
            }
        } catch (Exception e) {
            System.out.println("Error login: "+ e.getMessage());
            return null;
        }
        
        return null;
    }

    
    public static void getMenu(IAuthenticable user, UserManager um, CustomerManager cm, ProductManager pm, Inventory inv, OrderManager om) {
        if (user instanceof Admin) {
            AdminMenu.showMenu(user,um, cm, pm, inv, om);
        } else if (user instanceof Customer) {
            CustomerMenu.showMenu(user, pm, om, inv);
        } else if (user instanceof Guest) {
            GuestMenu.showMenu(user, um, cm, pm, inv, om);
        } else throw new IllegalArgumentException("Unknown user type!");
    }

    public static Customer updateProfile(Guest g, Scanner sc, UserManager um, CustomerManager cm){
        Extension.clearScreen();
        System.out.println("=== CAP NHAT THONG TIN CA NHAN ===");
        String CID = Data.generateNewID("resources/customers.txt", 'C');

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

        Customer customer = new Customer(
            g.getUsername(),
            g.getPassword(),
            CID,
            fullname,
            dobDate,
            address, 
            email, 
            phone,
            true
        );

        cm.add(customer);
        cm.save();

        um.replaceUser(g, customer);
        um.save();

        System.out.println("Đã cập nhật hồ sơ thành công! Bạn đã trở thành khách hàng chính thức.");
        return customer;
    }

}
