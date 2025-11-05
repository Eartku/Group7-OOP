package view;

import interfaces.IAuthenticable;
import java.util.Scanner;
import models.Customer;
import models.Guest;
import service.CustomerManager;
import service.Inventory;
import service.OrderManager;
import service.ProductManager;
import service.UserManager;
import ultils.Enhance;
import ultils.Log;

public class GuestMenu {
    private static final Scanner sc = new Scanner(System.in);

    public static void showMenu(IAuthenticable user, UserManager um, CustomerManager cm, ProductManager pm,  Inventory inv, OrderManager om){
        Guest guest = (Guest) user;
        while (true) {
            Enhance.clearScreen();
            System.out.println("==== MENU GUEST ====");
            System.out.println("1. Cap nhat ho so ca nhan - Update Profile");
            System.out.println("2. Xem va Tim kiem san pham - View & Search");
            Log.exit("0. Dang xuat - Logout");
            System.out.print("Nhap lua chon: ");
            int choice = Enhance.readIntInRange("Nhap lua chon (0-2):", 0, 2, sc);

            switch (choice) {
                case 0 -> {
                    System.out.println("Dang xuat thanh cong!\n");
                    return;
                }
                case 1 -> {
                    Customer newCustomer = guest.toCustomer();
                    if (newCustomer != null) {
                        System.out.println("=== CAP NHAT THONG TIN ===");
                        AuthMenu.updateProfile(guest, sc, um, cm);
                        um.upgradeGuestToCustomer(guest, newCustomer);
                    Log.success("Ban da tro thanh KHACH HANG!");
                    Log.info("Chuyen sang menu khach hang...");
                    Enhance.pause(sc);
                    AuthMenu.getMenu(newCustomer, um, cm, pm, inv, om);
                    return;
                    }
                }
                case 2 ->{
                    ManageProductsMenu.viewMenuforCustomer(inv, sc);
                    Enhance.pause(sc);
                }
                default -> Log.warning("Khong hop le!");
            }
        }
    }
}
