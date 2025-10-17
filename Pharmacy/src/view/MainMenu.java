package view;

import interfaces.Authenticable;
import java.io.IOException;
import java.util.Scanner;
import service.CustomerManager;
import service.Inventory;
import service.OrderManager;
import service.ProductManager;
import service.UserManager;

public class MainMenu{
    public static void clearScreen() {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                // Windows
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
       
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Không thể xóa màn hình: " + e.getMessage());
        }
    }
    public static void showMenu(){
        UserManager um = new UserManager();
        CustomerManager cm = new CustomerManager();
        ProductManager pm = new ProductManager();
        Inventory inv = new Inventory(pm);
        OrderManager om = new OrderManager(pm, cm);

        Scanner sc = new Scanner(System.in);
        Authenticable currentUser = null;
        clearScreen();

        while (true) {
            System.out.println("\n==== WELCOME ====");
            System.out.println("1. Dang nhap tai khoan");
            System.out.println("2. Dang ky tai khoan");
            System.out.println("0. Thoat");
            System.out.print("Chon: ");
            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException _) {
                System.out.println("Nhap so tu 0-2!");
                continue;
            }

            switch (choice) {
                case 1 -> {currentUser = AuthMenu.Login(sc, um, cm);}
                case 2 -> {currentUser = AuthMenu.Register(sc, um, cm);}
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
