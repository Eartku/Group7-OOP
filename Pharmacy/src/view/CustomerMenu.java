package view;

import interfaces.IAuthenticable;
import java.util.Scanner;
import models.Customer;
import service.CustomerManager;
import service.Inventory;
import service.OrderManager;
import service.ProductManager;
import service.UserManager;
import ultils.Enhance;
import ultils.Log;

public class CustomerMenu {
    private static final Scanner sc = new Scanner(System.in);

    public static void showMenu(IAuthenticable user,  ProductManager pm, OrderManager om, Inventory inv, CustomerManager cm, UserManager um){
        ManageOrderMenu mo = new ManageOrderMenu(sc, pm, cm, um, inv, om);
        Customer customer = (Customer) user;
        while (true) {
            Enhance.clearScreen();
                System.out.println("==== MENU CUSTOMER ====");
                System.out.println("1. Xem va Tim kiem san pham - View & Search products");
                System.out.println("2. Dat mua san pham - Buy products");
                System.out.println("3. Xem thong tin ca nhan - View your profile");
                System.out.println("4. Xem lich su mua hang - Purchase history");
                Log.exit("0. Dang xuat - Logout");
                System.out.print("Nhap lua chon: ");
                int choice = Enhance.readIntInRange("Nhap lua chon (0-4):", 0, 4, sc);

                switch (choice) {
                    case 0 -> {
                        Log.info("Dang xuat thanh cong!\n");
                        return;
                    }
                    case 1 -> {
                        System.out.println("Xem danh sach san pham...");
                        ManageProductsMenu.viewMenuforCustomer(inv, sc);
                    }
                    case 2 -> {
                        if(!customer.getStatus()) {
                            System.out.println("Tai khoan cua ban da bi khoa. Nen khong the thuc hien chuc nang nay.");
                            Enhance.pause(sc);
                            break;
                        }
                        System.out.println("Dang ky mua san pham...");
                        mo.OrderforCustomer(customer);
                    }
                    case 3 -> {
                        System.out.println("Xem thong tin ca nhan...");
                        Enhance.printInBox(() -> {ManageCustomerMenu.printCustomer(customer);});
                        Enhance.pause(sc);
                    }
                    case 4 -> {
                        System.out.println("Xem lich su mua hang...");
                        mo.history(customer, sc);
                        Enhance.pause(sc);
                    }
                    default -> Log.warning("Khong hop le!");
                }
            }
    }
}
