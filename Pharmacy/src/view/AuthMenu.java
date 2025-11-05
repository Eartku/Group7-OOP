package view;

import interfaces.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import models.*;
import service.*;
import ultils.*;

public class AuthMenu {

    private static boolean hasLetterAndDigit(String input) {
        if (input == null || input.isEmpty()) return false;
        return input.matches("^(?=.*[A-Za-z])(?=.*\\d).+$");
    }

    public static IAuthenticable Register(Scanner sc, UserManager um, CustomerManager cm){
        Enhance.clearScreen();
        System.out.println("______DANG KY_____");
        String username;
        while(true){
            Log.request("Nhap Username (hoac nhap 0 de quay lai): ");
            username = sc.nextLine().trim();


            if(username.isEmpty()) {
                Log.warning("Khong duoc bo trong mien nay");
                continue;
            }

            if (username.equals("0")) {
                Log.info("Huy thao tac xoa, quay lai menu chinh.");
                // break;
                return null;
            }

            if(!hasLetterAndDigit(username)){
                Log.warning("Username phai co it nhat 1 chu cai va so");
                continue;
            }

            if(um.exists(username)){
                Log.error("Username da ton tai!");
                Log.request("Dang nhap? (y/n): ");
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
                if(!hasLetterAndDigit(password)){
                    Log.warning("Password phai co chu cai va so");
                    continue;
                }
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
        Enhance.clearScreen();
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

    public static Customer updateProfile(Scanner sc){
        Enhance.clearScreen();
        System.out.println("=== CAP NHAT THONG TIN CA NHAN ===");
        String CID = Data.generateNewID("resources/customers.txt", 'C');

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

        Customer c = new Customer("", "", false);
        c.setFullname(fullname);
        c.setCID(CID);
        c.setDate(dobDate);
        c.setAddress(address);
        c.setEmail(email);
        c.setPhone(phone);
        Log.success("Da cap nhat ho so thanh cong!.");
        return c;
    }

}
