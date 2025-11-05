package view;

import interfaces.*;
import java.util.*;
import service.*;
import ultils.*;

public class MainMenu {

    public static void showMenu() {
        Data.initData();
        CustomerManager cm = new CustomerManager();
        UserManager um = new UserManager(cm);
        ProductManager pm = new ProductManager();
        OrderManager om = new OrderManager(cm, pm);
        Inventory inv = new Inventory(pm,om);
        Scanner sc = new Scanner(System.in);
        Enhance.pause(sc);
        IAuthenticable currentUser = null;

        while (true) {
            Enhance.clearScreen();
            System.out.println("\n==== WELCOME ====");
            System.out.println("1. Dang nhap tai khoan - Login");
            System.out.println("2. Dang ky tai khoan - Register");
            Log.exit("0. Thoat - Exit");

            int choice = Enhance.readIntInRange("Nhap lua chon (0-2): ", 0, 2, sc);

            switch (choice) {
                case 1 -> currentUser = AuthMenu.Login(sc, um, cm);
                case 2 -> currentUser = AuthMenu.Register(sc, um, cm);
                case 0 -> {
                    Log.info("Thoat chuong trinh. Tam biet!");
                    sc.close();
                    return;
                }
                default -> Log.warning("Lua chon khong hop le!");
            }

            if (currentUser != null) {
                Log.success("\nDang nhap thanh cong! Welcome " + currentUser.getUsername());
                Enhance.pause(sc);
                AuthMenu.getMenu(currentUser, um, cm, pm, inv, om);
            } else {
                Log.error("Dang nhap/ dang ky that bai.");
            }
        }
    }
}
