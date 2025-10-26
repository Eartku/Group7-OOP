package view;

import data.Data;
import interfaces.Authenticable;
import interfaces.ManageMenu;
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

public class ManageOrderMenu implements ManageMenu{
    private final ProductManager pm;
    private final CustomerManager cm;
    private final UserManager um;
    private final Inventory inv;
    private final OrderManager om;
    private final Scanner sc;

    public ManageOrderMenu(Scanner sc, ProductManager pm, CustomerManager cm, UserManager um, Inventory inv, OrderManager om) {
        this.pm = pm;
        this.cm = cm;
        this.um = um;
        this.inv = inv;
        this.om = om;
        this.sc = sc;
    }
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    @Override
    public void mainMenu(){
        Extension.clearScreen();

        if (pm == null) {
            System.out.println("Khong the quan ly cac HOA DON!");
            return;
        }

        while (true) {
            Extension.clearScreen();
            System.out.println("==== ORDER MANAGER ====");
            System.out.println(om.report());
            om.showList();
            System.out.println("1. Them HOA DON moi (neu giao dich tai quay)");
            System.out.println("2. Xoa HOA DON");
            System.out.println("3. Cap nhat thong tin HOA DON (Khong co)");
            System.out.println("4. Truy xuat HOA DON");
            System.out.println("5. Xem cac HOA DON bi huy");
            System.out.println("0. Thoat");
            System.out.print("Chon: ");
            int choice = Extension.readIntInRange("Nhap lua chon (0-4):", 0, 4, sc);

            switch (choice) {
                case 1 -> {
                    addMenu();
                }
                case 2 -> {
                    removeMenu();
                }
                case 3 -> {
                    updateMenu();
                }
                case 4 -> {
                    viewMenu();
                }
                case 5 -> {
                    viewMenu();
                }
                case 0 -> {
                    System.out.println("Thoat chuong trinh. Tam biet!");
                    return;
                }
                default -> System.out.println("Lua chon khong hop le!");
            }
        }
    }

    @Override
    public void addMenu() {
        Extension.clearScreen();
        System.out.println("==== ADD NEW ORDER ====");
        String OID = Data.generateNewID(OrderManager.FILE_PATH, 'O');
        Authenticable c;

        while (true) {
            System.out.print("Khach hang da co tai khoan chua (y/n): ");
            String hasAccount = sc.nextLine().trim();

            // ===== TRƯỜNG HỢP 1: KHÁCH ĐÃ CÓ TÀI KHOẢN =====
            if (hasAccount.equalsIgnoreCase("y")) {
                c = AuthMenu.Login(sc, um, cm);

                if (c == null) {
                    System.out.println("Dang nhap that bai hoac huy thao tac. Thu lai!");
                    System.out.println("Nhan Enter de tiep tuc...");
                    sc.nextLine();
                    continue;
                }

                if (c instanceof Customer customer) {
                    System.out.println("Dang nhap thanh cong, xin chao " + customer.getFullname() + "!");
                    ArrayList<OrderItem> ordered = om.buyProducts(sc, pm);

                    if (!ordered.isEmpty()) {
                        // kiểm tra tồn kho
                        for (OrderItem o : ordered) {
                            long available = inv.getStockbyProduct(o.getProduct());
                            if (available < o.getQuantity()) {
                                System.out.println("Khong du hang cho san pham " + o.getProductsName() +
                                        ". Con: " + available + ", Can ban: " + o.getQuantity());
                                System.out.println("Huy HOA DON do khong du hang!");
                                System.out.println("Nhan Enter de tiep tuc...");
                                sc.nextLine();
                                return;
                            }
                        }

                        for (OrderItem o : ordered) inv.deductStock(o);
                        inv.save();

                        om.add(new Order(OID, ordered, customer, "Confirmed"));
                        om.save();

                        System.out.println("Tao HOA DON thanh cong! Ma don: " + OID);
                    } else {
                        System.out.println("Khong co san pham nao duoc chon. Huy HOA DON!");
                    }
                    System.out.println("Nhan Enter de quay lai menu...");
                    sc.nextLine();
                    break;
                } else {
                    System.out.println("Tai khoan nay khong phai khach hang! Nhan Enter de quay lai.");
                    sc.nextLine();
                }
            }

            // ===== TRƯỜNG HỢP: KHÁCH CHƯA CÓ TÀI KHOẢN =====
            else if (hasAccount.equalsIgnoreCase("n")) {
                System.out.println("=== DANG KY TAI KHOAN MOI ===");

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

                Guest g = new Guest(username, password, true);
                c = AuthMenu.updateProfile(g, sc, um, cm);

                if (c instanceof Customer customer) {
                    um.add(customer);
                    um.save();
                    cm.add(customer);
                    cm.save();

                    System.out.println("Dang ky thanh cong, chao mung " + customer.getFullname() + "!");
                    ArrayList<OrderItem> ordered = om.buyProducts(sc, pm);

                    if (!ordered.isEmpty()) {
                        for (OrderItem o : ordered) {
                            long available = inv.getStockbyProduct(o.getProduct());
                            if (available < o.getQuantity()) {
                                System.out.println("Khong du hang cho san pham " + o.getProductsName() +
                                        ". Con: " + available + ", Can ban: " + o.getQuantity());
                                System.out.println("Huy HOA DON do khong du hang!");
                                System.out.println("Nhan Enter de tiep tuc...");
                                sc.nextLine();
                                return;
                            }
                        }

                        for (OrderItem o : ordered) inv.deductStock(o);
                        inv.save();

                        om.add(new Order(OID, ordered, customer, "Confirmed"));
                        om.save();

                        System.out.println("Tao HOA DON thanh cong! Ma don: " + OID);
                    } else {
                        System.out.println("Khong co san pham nao duoc chon. Huy HOA DON!");
                    }
                    System.out.println("Nhan Enter de quay lai menu...");
                    sc.nextLine();
                    break;
                } else {
                    System.out.println("Dang ky that bai! Nhan Enter de quay lai...");
                    sc.nextLine();
                    return;
                }
            }

            // ===== TRƯỜNG HỢP: NHẬP LINH TINH =====
            else {
                System.out.println("Lua chon khong hop le! Vui long nhap lai (y/n).");
            }
        }
    }


    public void OrderforCustomer(Customer customer) {
        Extension.clearScreen();
        System.out.println("==== MUA HANG ====");
        System.out.println(" Hello, " + customer.getFullname() + "!");
        System.out.println("Vui long chon san pham trong danh sach:");

        String OID = Data.generateNewID(OrderManager.FILE_PATH, 'O');

        // Chọn sản phẩm
        List<OrderItem> ordered = om.buyProducts(sc, pm);

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
                System.out.println("Huy HOA DON do khong du hang");
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
            total += item.getProduct().getPrice()* item.getQuantity(); // thực tế là giá bán ra của inventory với sản phẩm đó
        }

        System.out.printf("Tong so tien can thanh toan: %.2f VND%n", total);
        System.out.print("Xac nhan thanh toan (y/n): ");
        String confirm = sc.nextLine().trim();

        if (confirm.equalsIgnoreCase("y")) {
            Order order = new Order(OID, ordered, customer, "Pending");
            om.add(order);
            om.save();
            inv.save();
            System.out.println("Thanh toán thành công!");
            System.out.println("Ma HOA DON: " + OID);
        } else {
            System.out.println("Huy HOA DON.");
        }

        System.out.println("Enter --> back ...");
        sc.nextLine();
    }


    //Menu 
    @Override
    public void removeMenu() { // hàm xóa này chỉ đơn giản là đặt trang thái đon hàng là "Canceled"
        Extension.clearScreen();
        while (true) {
            System.out.println("==== REMOVE ORDER ====");
            om.showList();
            System.out.print("Nhap ID HOA DON muon xoa (hoac nhap 0 de quay lai): ");
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
                om.get(inputID).setStatus("Canceled");
                System.out.println("Da xoa san pham: " + inputID);
                om.save();
            } else {
                System.out.println("Da huy thao tac xoa.");
            }
            break; // ra khỏi vòng while sau khi thao tác xong
        }
    }

    @Override
    public void viewMenu() {
        Extension.clearScreen();
        while (true) {
            System.out.println("==== DANH SACH HOA DON ====");
            om.showList();
            System.out.print("Nhap ID HOA DON muon xem (Nhap 0 de quay lai): ");
            String input = sc.nextLine().trim();

            if (input.equals("0")) {
                System.out.println("Quay lai menu chinh...");
                return;
            }

            Order order = om.get(input);
            if (order == null) {
                System.out.println("Khong tim thay HOA DON co ma " + input);
                continue;
            }

            //chi tiet HOA DON
            Extension.printInBox(() -> printOrderDetails(order));
            
            System.out.println("\nNhan Enter de xem don khac, hoac nhap 0 de quay lai.");
            String choice = sc.nextLine().trim();
            if (choice.equals("0")) break;
            Extension.clearScreen();
        }
    }

    public void ViewOrderforCustomer() {
        Extension.clearScreen();
        while (true) {
            System.out.println("==== DANH SACH HOA DON ====");
            om.showList();
            System.out.print("Nhap ID HOA DON muon xem (Nhap 0 de quay lai): ");
            String input = sc.nextLine().trim();

            if (input.equals("0")) {
                System.out.println("Quay lai menu chinh...");
                return;
            }

            Order order = om.get(input);
            if (order == null) {
                System.out.println("Khong tim thay HOA DON co ID " + input);
                continue;
            }

            // chi tiet HOA DON
            Extension.printInBox(() -> printOrderDetails(order));
            
            System.out.println("\nNhan Enter de xem don khac, hoac nhap 0 de quay lai.");
            String choice = sc.nextLine().trim();
            if (choice.equals("0")) break;
            Extension.clearScreen();
        }
    }

    public void printOrderDetails(Order order) {
        System.out.println("\n===== CHI TIET HOA DON =====");
        System.out.println("Ma HOA DON: " + order.getOID());
        System.out.println("Ngay dat: " + order.getpurchaseDate().format(FORMATTER));
        System.out.println("Khach hang: " + order.getCustomer().getFullname());
        System.out.println("Trang thai: " + order.getStatus());
        double total = 0;
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            long quantity = item.getQuantity();
            double subtotal = product.getPrice() * quantity;
            total += subtotal;

            switch (product) {
                case Drug d -> {
                    System.out.println("[THUOC] " + d.getName() + " (" + quantity + " " + d.getUnit() + ")");
                    System.out.println(" - Gia: " + d.getPrice() + " VND");
                }
                case NonDrug nd -> {
                    System.out.println("[PHI THUOC] " + nd.getName() + " (" + quantity + " " + nd.getUnit() + ")");
                    System.out.println(" - Gia: " + nd.getPrice() + " VND");
                }
                default -> System.out.println("[?] San pham khong xac dinh");
            }
            System.out.println(" → Thanh tien: " + subtotal + " VND");
            System.out.println("------------------------------------");
        }
        System.out.printf("Tong thanh toan: %.2f VND%n", total);
    }


    @Override
    public void updateMenu() {
        Extension.clearScreen();
        System.out.println("==== CAP NHAT TRANG THAI HOA DON ====");
        om.showList();

        System.out.print("Nhap ID HOA DON muon cap nhat (hoac 0 de quay lai): ");
        String id = sc.nextLine().trim();

        if (id.equals("0")) return;

        Order order = om.get(id);
        if (order == null) {
            System.out.println("Khong tim thay HOA DON!");
            System.out.println("Nhan Enter de quay lai...");
            sc.nextLine();
            return;
        }
        Extension.printInBox(() -> printOrderDetails(order));

        System.out.println("Trang thai hien tai: " + order.getStatus());
        System.out.println("Chon trang thai moi:");
        System.out.println("1. Pending");
        System.out.println("2. Confirmed");
        System.out.println("3. Canceled");
        System.out.print("Nhap lua chon: ");

        int choice = Extension.readIntInRange("Nhap lua chon (1-3):", 1, 3, sc);
        String newStatus;

        switch (choice) {
            case 1 -> newStatus = OrderStatus.PENDING;
            case 2 -> newStatus = OrderStatus.CONFIRMED;
            case 3 -> newStatus = OrderStatus.CANCELED;
            default -> {
                System.out.println("Lua chon khong hop le!");
                return;
            }
        }

        if (order.getStatus().equalsIgnoreCase(newStatus)) {
            System.out.println("Trang thai khong thay doi.");
        } else {
            order.setStatus(newStatus);
            om.save();
            System.out.println("Cap nhat thanh cong trang thai hoa don!");
        }

        System.out.println("Nhan Enter de quay lai...");
        sc.nextLine();
    }


    public class OrderStatus {
        public static final String PENDING = "Pending";
        public static final String CONFIRMED = "Confirmed";
        public static final String CANCELED = "Canceled";
    }

}
