package view;

import interfaces.Authenticable;
import java.util.Scanner;
import service.CustomerManager;
import service.Inventory;
import service.OrderManager;
import service.ProductManager;
import service.UserManager;

public class AdminMenu {
    private static final Scanner sc = new Scanner(System.in);

    public static void showMenu(Authenticable user, UserManager um, CustomerManager cm, ProductManager pm, Inventory inv, OrderManager om){
        ManageUserMenu mu = new ManageUserMenu(cm, um, sc);
        ManageCustomerMenu mc = new ManageCustomerMenu(cm, um, sc);
        ManageProductsMenu mp = new ManageProductsMenu(pm, sc);
        ManageOrderMenu mo = new ManageOrderMenu(sc, pm, cm, um, inv, om);
        Extension.clearScreen();
        while (true) {
            System.out.println();
            System.out.println("==== MENU ADMIN ====");
            System.out.println("1. Quan ly nguoi dung");
            System.out.println("2. Quan ly san pham kha dung");
            System.out.println("3. Quan ly khach hang");
            System.out.println("4. Quan ly kho/lo hang");
            System.out.println("5. Quan ly cac don hang");
            System.out.println("0. Dang xuat");
            System.out.print("Nhap lua chon: ");
            int choice = Extension.readIntInRange("Nhap lua chon (0-5):", 0, 5, sc);

            switch (choice) {
                case 0 -> {
                    Extension.dotAnimation("Dang dang xuat", choice, "Dang xuat thanh cong!");
                    return;
                }
                case 1 -> {
                    System.out.println("Quan ly nguoi dung...");
                    mu.mainMenu();
                    break;
                }
                case 2 -> {
                    System.out.println("Quan ly san pham kha dung...");
                    mp.mainMenu();
                }
                case 3 -> {
                    System.out.println("Quan ly khach hang...");
                    mc.mainMenu();
                }
                case 4 -> {
                    System.out.println("Quan ly kho/ lo hang...");
                    InventoryMenu.showMenu(inv, pm);
                }
                case 5 -> {
                    System.out.println("Quan ly cac don hang");
                    mo.mainMenu();
                }
                default -> System.out.println("Khong hop le!");
            }
        }
    }
}
