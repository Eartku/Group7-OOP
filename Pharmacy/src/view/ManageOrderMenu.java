package view;

import data.Data;
import interfaces.Authenticable;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import models.Customer;
import models.Drug;
import models.Guest;
import models.NonDrug;
import models.Order;
import models.OrderItem;
import models.Product;
import service.CustomerManager;
import service.Inventory;
import service.OrderManager;
import service.ProductManager;
import service.UserManager;

public class ManageOrderMenu {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static void showMenu(ProductManager pm, CustomerManager cm, UserManager um, Inventory inv, OrderManager om){
        Scanner sc = new Scanner(System.in);
        MainMenu.clearScreen();

        if (pm == null) {
            System.out.println("Khong the quan ly cac don hang!");
            return;
        }

        while (true) {
            MainMenu.clearScreen();
            System.out.println("==== ORDER MANAGER ====");
            System.out.println(om.report());
            om.showList();
            System.out.println("1. Them don hang moi (neu giao dich tai quay)");
            System.out.println("2. Xoa don hang");
            System.out.println("3. Cap nhat thong tin don hang (Khong co)");
            System.out.println("4. Truy xuat don hang");
            System.out.println("0. Thoat");
            System.out.print("Chon: ");
            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException _) {
                System.out.println("Nhap so tu 0-4!");
                continue;
            }

            switch (choice) {
                case 1 -> {
                    AddOrder(sc, pm, um, cm, om, inv);
                }
                case 2 -> {
                    RemoveOrder(sc, om);
                }
                case 3 -> {
                    System.out.println("Gia dinh don hang da dc thanh toan sau khi tao don hang, nen ko co phan chinh sua");
                }
                case 4 -> {
                    ViewOrder(sc, pm, inv, om, cm);
                }
                case 0 -> {
                    System.out.println("Thoat chuong trinh. Tam biet!");
                    return;
                }
                default -> System.out.println("Lua chon khong hop le!");
            }
        }
    }

    public static void AddOrder(Scanner sc, ProductManager pm, UserManager um, CustomerManager cm, OrderManager om, Inventory inv){
        MainMenu.clearScreen();
        System.out.println("==== ADD NEW ORDER ====");
        String OID = Data.generateNewID(OrderManager.FILE_PATH, 'O');
        Authenticable c;
        

        while (true) {
            System.out.print("Khach hang da co tai khoan chua (y/n): ");
            String hasAccount = sc.nextLine().trim();

            // Nếu có tài khoản 
            if (hasAccount.equalsIgnoreCase("y")) {
                c = AuthMenu.Login(sc, um, cm);

                if (c instanceof Customer customer) {
                    System.out.println("Dang nhap thanh cong, xin chao " + customer.getFullname() + "!");
                    ArrayList<OrderItem> ordered = om.selectProducts(sc, pm);
                    if(!ordered.isEmpty()){
                        om.add(new Order(OID,ordered,customer));
                        om.save();
                    }
                    break;
                } else {
                    System.out.println("Tai khoan nay khong phai khach hang! Nhan Enter de quay lai.");
                    sc.nextLine();
                }
            }

            //  Nếu chưa có tài khoản thì phải đăng ký
            else if (hasAccount.equalsIgnoreCase("n")) {
                System.out.println("=== Dang ky tai khoan moi ===");

                String username;
                while (true) {
                    System.out.print("Nhap username: ");
                    username = sc.nextLine().trim();
                    if (um.exists(username)) {
                        System.out.println("Username da ton tai, vui long nhap lai!");
                    } else break;
                }

                String password;
                while (true) {
                    System.out.print("Nhap password: ");
                    password = sc.nextLine().trim();
                    if (password.length() < 6) {
                        System.err.println("Mat khau phai co it nhat 6 ky tu!");
                    } else break;
                }

                Guest g = new Guest(username, password);
                c = AuthMenu.updateProfile(g, sc, um);

                if (c instanceof Customer customer) {
                    um.add(customer); um.save();
                    cm.add(customer); cm.save();
                    System.out.println("Dang ky thanh cong, chao mung " + customer.getFullname() + "!");
                    ArrayList<OrderItem> ordered = om.selectProducts(sc, pm);
                    for (OrderItem o: ordered) {
                        inv.deductStock(o);
                    }
                    if(!ordered.isEmpty()){
                        om.add(new Order(OID,ordered,customer));
                        om.save();
                        inv.save();
                    }
                    break;
                } else {
                    System.out.println("Dang ky that bai!");
                    return;
                }
            }
            else {
                System.out.println("Lua chon khong hop le! Vui long nhap lai (y/n).");
            }
            
        }
    }
    public static void OrderforCustomer(Scanner sc, Customer customer, ProductManager pm, OrderManager om, Inventory inv) {
        MainMenu.clearScreen();
        System.out.println("==== MUA HANG ====");
        System.out.println(" Hello, " + customer.getFullname() + "!");
        System.out.println("Vui long chon san pham trong danh sach:");

        String OID = Data.generateNewID(OrderManager.FILE_PATH, 'O');

        // Chọn sản phẩm
        List<OrderItem> ordered = om.selectProducts(sc, pm);

        // Kiểm tra nếu danh sách trống
        if (ordered.isEmpty()) {
            System.out.println("Chua chon san pham nao. Quay lai menu!");
            System.out.println("Enter de tiep tuc...");
            sc.nextLine();
            return;
        }

        //kiểm tra tồn kho
        for(OrderItem item : ordered){
            long avaiable = inv.getStockbyProduct(item.getProduct());
            if(avaiable < item.getQuantity()){
                System.out.println("Khong du hang cho san pham " + item.getProductsName() + "Con: " + avaiable + ", Can ban: "+ item.getQuantity());
                System.out.println("Huy don hang do khong du hang");
                sc.nextLine();
                return;
            }
        }
        for (OrderItem c: ordered) {
            inv.deductStock(c);
        }

        // Tính tổng tiền
        double total = 0;
        for (OrderItem item : ordered) {
            total += inv.getExportPricebyProduct(item.getProduct()) * item.getQuantity(); // thực tế là giá bán ra của inventory với sản phẩm đó
        }

        System.out.printf("Tong so tien can thanh toan: %.2f VND%n", total);
        System.out.print("Xac nhan thanh toan (y/n): ");
        String confirm = sc.nextLine().trim();

        if (confirm.equalsIgnoreCase("y")) {
            Order order = new Order(OID, ordered, customer);
            om.add(order);
            om.save();
            inv.save();
            System.out.println("Thanh toán thành công!");
            System.out.println("Ma don hang: " + OID);
        } else {
            System.out.println("Huy don hang.");
        }

        System.out.println("Enter --> back ...");
        sc.nextLine();
    }


    //Menu 
    public static void RemoveOrder(Scanner sc, OrderManager om) {
        MainMenu.clearScreen();
        while (true) {
            System.out.println("==== REMOVE ORDER ====");
            om.showList();
            System.out.print("Nhap ID don hang muon xoa (hoac nhap 0 de quay lai): ");
            String inputID = sc.nextLine().trim();

            // 0 → quay lại menu chính
            if (inputID.equals("0")) {
                System.out.println("Huy thao tac xoa, quay lai menu chinh.");
                return;
            }

            // Xác nhận xóa
            System.out.print("Ban co chac muon xoa san pham " + inputID + "? (y/n): ");
            String confirm = sc.nextLine().trim();

            if (confirm.equalsIgnoreCase("y")) {
                om.delete(inputID); // giả sử là hàm void
                System.out.println("Da xoa san pham: " + inputID);
                om.save();
            } else {
                System.out.println("Da huy thao tac xoa.");
            }
            break; // ra khỏi vòng while sau khi thao tác xong
        }
    }

    public static void ViewOrder(Scanner sc, ProductManager pm, Inventory inv, OrderManager om, CustomerManager cm) {
        MainMenu.clearScreen();
        while (true) {
            System.out.println("==== DANH SACH DON HANG ====");
            om.showList();
            System.out.print("Nhap ID don hang muon xem (Nhap 0 de quay lai): ");
            String input = sc.nextLine().trim();

            if (input.equals("0")) {
                System.out.println("Quay lai menu chinh...");
                return;
            }

            Order order = om.get(input);
            if (order == null) {
                System.out.println("Khong tim thay don hang co ma " + input);
                continue;
            }

            // --- Thông tin đơn hàng ---
            System.out.println("\n===== CHI TIET DON HANG =====");
            System.out.println("Ma don hang: " + order.getOID());
            System.out.println("Ngay dat: " + order.getpurchaseDate().format(FORMATTER));
            System.out.println("Khach hang: " + order.getCustomer().getFullname());
            System.out.println("------------------------------------");

            double total = 0;
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                long quantity = item.getQuantity();
                double price = product.getPrice();
                double subtotal = price * quantity;
                total += subtotal;

                // Hiển thị chi tiết theo loại sản phẩm
                switch (product) {
                    case Drug d -> {
                        System.out.println("[THUOC] " + d.getName() + " (" + quantity + " " + d.getUnit() + ")");
                        System.out.println(" - Gia: " + price + " VND");
                        System.out.println(" - Han dung: " + d.getShelfLifeInfo());
                        System.out.println(" - Thanh phan: " + d.getIngredient());
                        System.out.println(" - Lieu luong: " + d.getDosage());
                        System.out.println(" - Ke don: " + (d.getpR() ? "Co" : "Khong"));
                    }
                    case NonDrug nd -> {
                        System.out.println("[SAN PHAM KHAC] " + nd.getName() + " (" + quantity + " " + nd.getUnit() + ")");
                        System.out.println(" - Gia: " + price + " VND");
                        System.out.println(" - Nha san xuat: " + nd.getManufacturer());
                        System.out.println(" - Loai: " + nd.getType());
                        System.out.println(" - Cong dung: " + nd.getUsage());
                    }
                    default -> System.out.println("[?] San pham khong xac dinh");
                }

                System.out.println(" → Thanh tien: " + subtotal + " VND");
                System.out.println("------------------------------------");
            }

            System.out.printf("Tong thanh toan: %.2f VND%n", total);

            System.out.println("\nNhan Enter de xem don khac, hoac nhap 0 de quay lai.");
            String choice = sc.nextLine().trim();
            if (choice.equals("0")) break;
            MainMenu.clearScreen();
        }
    }

    public static void ViewOrderforCustomer(Scanner sc, ProductManager pm, Inventory inv, OrderManager om, CustomerManager cm) {
        MainMenu.clearScreen();
        while (true) {
            System.out.println("==== DANH SACH DON HANG ====");
            om.showList();
            System.out.print("Nhap ID don hang muon xem (Nhap 0 de quay lai): ");
            String input = sc.nextLine().trim();

            if (input.equals("0")) {
                System.out.println("Quay lai menu chinh...");
                return;
            }

            Order order = om.get(input);
            if (order == null) {
                System.out.println("Khong tim thay don hang co ma " + input);
                continue;
            }

            // --- Thông tin đơn hàng ---
            System.out.println("\n===== CHI TIET DON HANG =====");
            System.out.println("Ma don hang: " + order.getOID());
            System.out.println("Ngay dat: " + order.getpurchaseDate().format(FORMATTER));
            System.out.println("Khach hang: " + order.getCustomer().getFullname());

            double total = 0;
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                long quantity = item.getQuantity();
                double price = product.getPrice();
                double subtotal = price * quantity;
                total += subtotal;

                // Hiển thị chi tiết theo loại sản phẩm
                switch (product) {
                    case Drug d -> {
                        System.out.println("[THUOC] " + d.getName() + " (" + quantity + " " + d.getUnit() + ")");
                        System.out.println(" - Gia: " + d.getPrice() + " VND");
                    }
                    case NonDrug nd -> {
                        System.out.println("[SAN PHAM KHAC] " + nd.getName() + " (" + quantity + " " + nd.getUnit() + ")");
                        System.out.println(" - Gia: " + nd.getPrice() + " VND");
                    }
                    default -> System.out.println("[?] San pham khong xac dinh");
                }

                System.out.println(" → Thanh tien: " + subtotal + " VND");
                System.out.println("------------------------------------");
            }

            System.out.printf("Tong thanh toan: %.2f VND%n", total);

            System.out.println("\nNhan Enter de xem don khac, hoac nhap 0 de quay lai.");
            String choice = sc.nextLine().trim();
            if (choice.equals("0")) break;
            MainMenu.clearScreen();
        }
    }


}
