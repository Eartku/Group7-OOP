package view;

import interfaces.Authenticable;
import java.util.Scanner;
import models.Customer;
import models.Guest;
import service.CustomerManager;
import service.Inventory;
import service.OrderManager;
import service.ProductManager;
import service.UserManager;

public class GuestMenu {
    private static final Scanner sc = new Scanner(System.in);

    public static void showMenu(Authenticable user, UserManager um, CustomerManager cm, ProductManager pm,  Inventory inv, OrderManager om){
        MainMenu.clearScreen();
        Guest guest = (Guest) user;
        while (true) {
            System.out.println("==== MENU GUEST ====");
            System.out.println("1. Cap nhat thong tin ca nhan");
            System.out.println("0. Dang xuat");
            System.out.print("Nhap lua chon: ");
            int choice = Integer.parseInt(sc.nextLine().trim());

            switch (choice) {
                case 0 -> {
                    System.out.println("Dang xuat thanh cong!\n");
                    return;
                }
                case 1 -> {
                    Customer newCustomer = guest.toCustomer();
                    if (newCustomer != null) {
                        System.out.println("=== CAP NHAT THONG TIN ===");
                        AuthMenu.updateProfile(guest, sc, um);
                        um.upgradeGuestToCustomer(guest, newCustomer);

                    System.out.println("Ban da duoc nang cap thanh KHACH HANG thanh cong!");
                    System.out.println("Dang chuyen sang menu khach hang...");
                    AuthMenu.getMenu(newCustomer, um, cm, pm, inv, om);
                    return;
                    }
                }
                default -> System.out.println("Khong hop le!");
            }
        }
    }
}
