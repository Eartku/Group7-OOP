package view;

import interfaces.Authenticable;
import java.util.Scanner;
import models.Customer;
import service.Inventory;
import service.OrderManager;
import service.ProductManager;

public class CustomerMenu {
    private static final Scanner sc = new Scanner(System.in);

    public static void showMenu(Authenticable user, ProductManager pm, OrderManager om, Inventory inv){
        Extension.clearScreen();
        ManageProductsMenu mp = new ManageProductsMenu(pm, sc);
        Customer customer = (Customer) user;
        while (true) {
                System.out.println("==== MENU CUSTOMER ====");
                System.out.println("1. Xem san pham");
                System.out.println("2. Tim kiem san pham");
                System.out.println("3. Xem thong tin ca nhan");
                System.out.println("4. Xem lich su mua hang");
                System.out.println("0. Dang xuat");
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
