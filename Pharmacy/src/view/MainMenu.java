package view;

import data.Data;
import interfaces.IAuthenticable;
import java.util.Scanner;
import service.CustomerManager;
import service.Inventory;
import service.OrderManager;
import service.ProductManager;
import service.UserManager;

public class MainMenu{

    public static void showMenu(){
        Data.initData();
        CustomerManager cm = new CustomerManager();
        UserManager um = new UserManager(cm);
        ProductManager pm = new ProductManager();
        Inventory inv = new Inventory(pm);
        OrderManager om = new OrderManager(cm, pm);

        Scanner sc = new Scanner(System.in);
        IAuthenticable currentUser = null;

        while (true) {
            System.out.println("\n==== WELCOME ====");
            System.out.println("1. Dang nhap tai khoan - Login");
            System.out.println("2. Dang ky tai khoan - Register");
            System.out.println("0. Thoat - Exit");
            System.out.print("Chon: ");
            int choice =Extension.readIntInRange("Nhap lua chon (0-2):", 0, 2, sc);

            switch (choice) {
                case 1 -> {
                    currentUser = AuthMenu.Login(sc, um, cm);
                }
                case 2 -> {
                    currentUser = AuthMenu.Register(sc, um, cm);
                }
                case 0 -> {
                    System.out.println("Thoat chuong trinh. Tam biet!");
                    sc.close();
                    return;
                }
                default -> System.out.println("Lua chon khong hop le!");
            }

            if (currentUser != null) {
                System.out.println("\nDang nhap thanh cong! Welcome " + currentUser.getUsername());
                AuthMenu.getMenu(currentUser, um, cm, pm, inv, om);
            } else {
                System.out.println("Dang nhap/ dang ky that bai.");
            }
        }
    }
}
