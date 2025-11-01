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
            Log.request("Nhap Username (hoac nhap 0 de quay lai): ");
            username = sc.nextLine().trim();

            if (username.equals("0")) {
                Log.info("Huy thao tac xoa, quay lai menu chinh.");
                // break;
                return null;
            }

            if(um.exists(username)){
                Log.warning("Username da ton tai!");
                System.out.println("Dang nhap? (y/n): ");
                char choice = sc.nextLine().charAt(0);
                if(choice == 'Y' || choice == 'y'){
                    return Login(sc, um, cm);
                }
            }
            else{
                Log.success("Username hop le!");
                break;
            }
        }
        String password;
        while(true){
            Log.request("Nhap Password: ");
            password = sc.nextLine();
            if(password.length() < 6){
                Log.warning("Password it nhat 6 ky tu!");
            }
            else{
                Log.success("Password hop le!");
                break;
            }
        }
        IAuthenticable newUser = new Guest(username, password, true);
        um.add(newUser);
        um.save();
        Log.success("Dang ky thanh cong!");
        return newUser;
    }

    public static IAuthenticable Login(Scanner sc, UserManager um, CustomerManager cm){
        Extension.clearScreen();
        String username, password; 
        try {
            System.out.println("______DANG NHAP_____");
            while(true){
                Log.request("Nhap Username (hoac nhap 0 de quay lai): ");
                username = sc.nextLine().trim();

                if (username.equals("0")) {
                    Log.info("Huy thao tac xoa, quay lai menu chinh.");
                    return null;
                }
                if(um.exists(username)){
                    Log.success("Username hop le!");
                    IAuthenticable user = um.get(username);
                    while (true){
                        Log.request("Nhap mat khau: ");
                        password = sc.nextLine();
                        if(user.checkPassword(password)){
                            Log.success("Password hop le!");
                            if (user instanceof Customer) {
                                Customer c = cm.getByUsername(username);
                                if (c != null) return c; // trả về Customer đầy đủ
                            }
                                return user;
                        }
                        else{
                            Log.error("Sai mat khau!");
                        }
                    }
                }
                else{
                    Log.warning("Khong tim thay username!");
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
            Log.error("Error login: "+ e.getMessage());
            return null;
        }
        
        return null;
    }

    
    public static void getMenu(IAuthenticable user, UserManager um, CustomerManager cm, ProductManager pm, Inventory inv, OrderManager om) {
        if (user instanceof Admin) {
            AdminMenu.showMenu(user,um, cm, pm, inv, om);
        } else if (user instanceof Customer) {
            CustomerMenu.showMenu(user, pm, om, inv,cm, um);
        } else if (user instanceof Guest) {
            GuestMenu.showMenu(user, um, cm, pm, inv, om);
        } else Log.error("Unknown user type!");
    }

    public static Customer updateProfile(Guest g, Scanner sc, UserManager um, CustomerManager cm){
        Extension.clearScreen();
        System.out.println("=== CAP NHAT THONG TIN CA NHAN ===");
        String CID = Data.generateNewID("resources/customers.txt", 'C');

        Log.request("Nhap Ho va ten: ");
        String fullname = sc.nextLine();

        LocalDate dobDate = null;
        while (true) {
            Log.request(" Nhap Ngay sinh (dd/MM/yyyy): ");
            String dob = sc.nextLine();
            try {
                dobDate = LocalDate.parse(dob, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                break; 
            } catch (Exception e) {
                Log.warning("Ngay sinh khong hop le! Nhap lai theo dd/MM/yyyy");
            }
        }

        Log.request("Nhap Dia chi: ");
        String address = sc.nextLine();

        Log.request("Nhap dia chi Email: ");
        String email = sc.nextLine();

        Log.request("Nhap So dien thoai: ");
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

        Log.success("Đã cập nhật hồ sơ thành công! Bạn đã trở thành khách hàng chính thức.");
        return customer;
    }

}
