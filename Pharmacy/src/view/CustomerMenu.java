package view;

import interfaces.IAuthenticable;
import java.util.Scanner;
import models.Customer;
import service.Inventory;
import service.OrderManager;
import service.ProductManager;

public class CustomerMenu {
    private static final Scanner sc = new Scanner(System.in);

    public static void showMenu(IAuthenticable user, ProductManager pm, OrderManager om, Inventory inv){
        
        ManageProductsMenu mp = new ManageProductsMenu(pm, sc);
        Customer customer = (Customer) user;
        while (true) {
            Extension.clearScreen();
                System.out.println("==== MENU CUSTOMER ====");
                System.out.println("1. Xem va Tim kiem san pham - View & Search products");
                System.out.println("2. Dat mua san pham - Buy products");
                System.out.println("3. Xem thong tin ca nhan - View your profile");
                System.out.println("4. Xem lich su mua hang - Purchase history");
                System.out.println("0. Dang xuat - Logout");
                System.out.print("Nhap lua chon: ");
                int choice = Extension.readIntInRange("Nhap lua chon (0-4):", 0, 4, sc);

                switch (choice) {
                    case 0 -> {
                        System.out.println("Dang xuat thanh cong!\n");
                        return;
                    }
                    case 1 -> {
                        System.out.println("Xem danh sach san pham...");
                        mp.viewMenu();
                    }
                    case 2 -> {
                        System.out.println("Dang ky mua san pham...");
                        //TODO: 
                    }
                    case 3 -> {
                        System.out.println("Xem thong tin ca nhan...");
                        ManageCustomerMenu.printCustomer(customer);
                    }
                    case 4 -> {
                        System.out.println("Xem lich su mua hang...");
                        om.history(customer);
                    }
                    default -> System.out.println("Khong hop le!");
                }
            }
    }
}
