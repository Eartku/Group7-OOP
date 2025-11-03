package view;

import data.Data;
import interfaces.IAuthenticable;
import interfaces.IManageMenu;
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

public class ManageOrderMenu implements IManageMenu {
    private final ProductManager pm;
    private final CustomerManager cm;
    private final UserManager um;
    private final Inventory inv;
    private final OrderManager om;
    private final Scanner sc;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ManageOrderMenu(Scanner sc, ProductManager pm, CustomerManager cm, UserManager um, Inventory inv, OrderManager om) {
        this.pm = pm;
        this.cm = cm;
        this.um = um;
        this.inv = inv;
        this.om = om;
        this.sc = sc;
    }

    @Override
    public void mainMenu() {
        Extension.clearScreen();

        if (pm == null) {
            Log.error("Khong the quan ly cac HOA DON!");
            return;
        }

        while (true) {
            Extension.clearScreen();
            System.out.println("==== ORDER MANAGER ====");
            
            System.out.println(om.report());
            System.out.println("==== DANH SACH TAT CA DON HANG ====");
            om.showList();
            om.blackList();
            System.out.println("1. Them HOA DON moi (neu giao dich tai quay)");
            System.out.println("2. Kich hoat/Khoa HOA DON");
            System.out.println("3. Cap nhat thong tin HOA DON (Khong co)");
            System.out.println("4. Truy xuat HOA DON");
            Log.exit("0. Thoat");
            int choice = Extension.readIntInRange("Nhap lua chon (0-5):", 0, 5, sc);

            switch (choice) {
                case 1 -> {
                    addMenu();
                    Extension.pause(sc);
                }
                case 2 -> {
                    System.out.println("1. Khoa HOA DON - Block Order");
                    System.out.println("2. Kich hoat HOA DON - Activate Order");
                    Log.exit("0. Huy - Cancel");
                    int choice2 = Extension.readIntInRange("Nhap lua chon (0-2):", 0, 2, sc);
                    switch (choice2) {
                        case 1 -> blockMenu();
                        case 2 -> activeMenu();
                        default -> Log.info("Huy thao tac.");
                    }
                    Extension.pause(sc);
                }
                case 3 -> {
                    updateMenu();
                    Extension.pause(sc);
                }
                case 4 ->{
                    viewMenu();
                    Extension.pause(sc);
                }
                case 0 -> {
                    Log.exit("Thoat chuong trinh. Tam biet!");
                    return;
                }
                default -> Log.warning("Lua chon khong hop le!");
            }
        }
    }

    @Override
    public void addMenu() {
        Extension.clearScreen();
        System.out.println("==== ADD NEW ORDER ====");
        String OID = Data.generateNewID(OrderManager.FILE_PATH, 'H');
        IAuthenticable c;

        while (true) {
            Log.request("Khach hang da co tai khoan chua (y/n) - (Nhap 0 de quay lai): ");
            String hasAccount = sc.nextLine().trim();

            if (hasAccount.equals("0")) {
                Log.info("Huy thao tac.");
                return;
            }

            if (hasAccount.equalsIgnoreCase("y")) {
                c = AuthMenu.Login(sc, um, cm);
                if (c == null) {
                    Log.warning("Dang nhap that bai hoac huy thao tac. Thu lai!");
                    continue;
                }

                if (c instanceof Customer customer) {
                    Log.success("Dang nhap thanh cong, xin chao " + customer.getFullname() + "!");
                    ArrayList<OrderItem> ordered = OrderManager.buyProducts(sc, pm, inv);
                    if (!ordered.isEmpty()) {
                        for (OrderItem o : ordered) {
                            long available = inv.getStockbyProduct(o.getProduct());
                            if (available < o.getQuantity()) {
                                Log.error("Khong du hang cho san pham " + o.getProductsName() +
                                        ". Con: " + available + ", Can ban: " + o.getQuantity());
                                Log.warning("Huy HOA DON do khong du hang!");
                                return;
                            } else {
                                inv.deductStock(o);
                                inv.save();
                            }
                        }
                        om.add(new Order(OID, ordered, customer, true));
                        om.save();
                        Log.success("Tao HOA DON thanh cong! Ma don: " + OID);
                    } else {
                        Log.warning("Khong co san pham nao duoc chon. Huy HOA DON!");
                    }
                    break;
                } else {
                    Log.warning("Tai khoan nay khong phai khach hang! Nhan Enter de quay lai.");
                }
            } else if (hasAccount.equalsIgnoreCase("n")) {
                System.out.println("=== DANG KY TAI KHOAN MOI ===");
                String username;
                while (true) {
                    Log.request("Nhap username: ");
                    username = sc.nextLine().trim();
                    if (um.exists(username)) {
                        Log.warning("Username da ton tai, vui long nhap lai!");
                    } else break;
                }
                String password;
                while (true) {
                    Log.request("Nhap password: ");
                    password = sc.nextLine().trim();
                    if (password.length() < 6) {
                        Log.error("Mat khau phai co it nhat 6 ky tu!");
                    } else break;
                }

                Guest g = new Guest(username, password, true);
                c = AuthMenu.updateProfile(g, sc, um, cm);

                if (c instanceof Customer customer) {
                    um.add(customer);
                    um.save();
                    cm.add(customer);
                    cm.save();

                    Log.success("Dang ky thanh cong, chao mung " + customer.getFullname() + "!");
                    ArrayList<OrderItem> ordered = OrderManager.buyProducts(sc, pm, inv);

                    if (!ordered.isEmpty()) {
                        for (OrderItem o : ordered) {
                            long available = inv.getStockbyProduct(o.getProduct());
                            if (available < o.getQuantity()) {
                                Log.error("Khong du hang cho san pham " + o.getProductsName() +
                                        ". Con: " + available + ", Can ban: " + o.getQuantity());
                                Log.warning("Huy HOA DON do khong du hang!");
                                sc.nextLine();
                                return;
                            } else {
                                long bef = inv.getStockbyProduct(o.getProduct());
                                inv.deductStock(o);
                                long aft = inv.getStockbyProduct(o.getProduct());
                                Log.warning("[DEBUG] So luong trong kho cua"+ o.getProduct().getName() + " tai thoi diem truoc ["+ bef + "] - Hien tai ["+ aft+"] ");
                                inv.save();
                            }
                        }
                        om.add(new Order(OID, ordered, customer, true));
                        om.save();
                        Log.success("Tao HOA DON thanh cong! Ma don: " + OID);
                    } else {
                        Log.warning("Khong co san pham nao duoc chon. Huy HOA DON!");
                    }
                    Log.info("Nhan Enter de quay lai menu...");
                    sc.nextLine();
                    break;
                } else {
                    Log.error("Dang ky that bai! Nhan Enter de quay lai...");
                    sc.nextLine();
                    return;
                }
            } else {
                Log.warning("Lua chon khong hop le! Vui long nhap lai (y/n).");
            }
        }
    }

    public void OrderforCustomer(Customer customer) {
        Extension.clearScreen();
        if (!customer.getStatus()) {
            Log.error("Ban da bi khoa! Khong the mua hang!");
            return;
        }
        System.out.println("==== MUA HANG ====");
        Log.info(" Hello, " + customer.getFullname() + "!");
        System.out.println("Vui long chon san pham trong danh sach:");

        String OID = Data.generateNewID(OrderManager.FILE_PATH, 'H');
        List<OrderItem> ordered = OrderManager.buyProducts(sc, pm, inv);

        if (ordered.isEmpty()) {
            Log.warning("Chua chon san pham nao. Quay lai menu!");
            return;
        }

        for (OrderItem item : ordered) {
            long available = inv.getStockbyProduct(item.getProduct());
            if (available < item.getQuantity()) {
                Log.error("Khong du hang cho san pham " + item.getProductsName() +
                        ". Con: " + available + ", Can ban: " + item.getQuantity());
                Log.warning("Huy HOA DON do khong du hang");
                Extension.pause(sc);
                return;
            }
        }
        for (OrderItem o : ordered) {
            long bef = inv.getStockbyProduct(o.getProduct());
            inv.deductStock(o);
            long aft = inv.getStockbyProduct(o.getProduct());
            Log.warning("[DEBUG] So luong trong kho cua"+ o.getProduct().getName() + " tai thoi diem truoc ["+ bef + "] - Hien tai ["+ aft+"] ");
        }

        double total = 0;
        for (OrderItem item : ordered) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }

        Log.info(String.format("Tong so tien can thanh toan: %.2f VND", total));
        Log.request("Xac nhan thanh toan (y/n): ");
        String confirm = sc.nextLine().trim();

        if (confirm.equalsIgnoreCase("y")) {
            Order order = new Order(OID, ordered, customer, true);
            om.add(order);
            om.save();
            inv.save();
            Log.success("Thanh toan thanh cong!");
            Log.success("Ma HOA DON: " + OID);
        } else {
            Order order = new Order(OID, ordered, customer, false);
            om.add(order);
            om.save();
            inv.save();
            Log.info("Da huy HOA DON.");
        }
    }

    @Override
    public void blockMenu() {
        Extension.clearScreen();
        while (true) {
            System.out.println("==== INACTIVE ORDER ====");
            System.out.println("DANH SACH DON HANG ACTIVE ");
            om.showList();
            Log.request("Nhap ID HOA DON muon xoa (hoac nhap 0 de quay lai): ");
            String inputID = sc.nextLine().trim();

            if (inputID.equals("0")) {
                Log.info("Huy thao tac.");
                return;
            }

            Log.request("Ban co chac muon xoa san pham " + inputID + "? (y/n): ");
            String confirm = sc.nextLine().trim();

            if (confirm.equalsIgnoreCase("y")) {
                om.get(inputID).setStatus(false);
                Log.success("Da xoa san pham: " + inputID);
                om.save();
            } else {
                Log.info("Da huy thao tac khoa.");
            }
            break;
        }
    }

    @Override
    public void activeMenu() {
        Extension.clearScreen();
        while (true) {
            System.out.println("==== ACTIVE ORDER ====");
            System.out.println("DANH SACH DON HANG INACTIVE ");
            om.blackList();
            Log.request("Nhap ID HOA DON muon kich hoat (hoac nhap 0 de quay lai): ");
            String inputID = sc.nextLine().trim();

            if (inputID.equals("0")) {
                Log.info("Huy thao tac.");
                return;
            }

            Log.request("Ban co chac muon kich hoat san pham " + inputID + "? (y/n): ");
            String confirm = sc.nextLine().trim();

            if (confirm.equalsIgnoreCase("y")) {
                om.get(inputID).setStatus(true);
                Log.success("Da kich hoat don hang: " + inputID);
                om.save();
            } else {
                Log.info("Da huy thao tac kich hoat.");
            }
            break;
        }
    }

    @Override
    public void viewMenu() {
        while (true) {
            Extension.clearScreen();
            System.out.println("==== DANH SACH HOA DON ====");
            om.showList();
            Log.request("Nhap ID HOA DON muon xem (Nhap 0 de quay lai): ");
            String input = sc.nextLine().trim();

            if (input.equals("0")) {
                Log.info("Quay lai menu chinh...");
                return;
            }

            Order order = om.get(input);
            if (order == null) {
                Log.warning("Khong tim thay HOA DON co ma " + input);
                continue;
            }

            Extension.printInBox(() -> printOrderDetails(order));

            Log.info("\nNhan Enter de xem don khac, hoac nhap 0 de quay lai.");
            String choice = sc.nextLine().trim();
            if (choice.equals("0")) break;
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
                default -> Log.error("[?] San pham khong xac dinh");
            }
            System.out.println(" → Thanh tien: " + subtotal + " VND");
            System.out.println("------------------------------------");
        }
        System.out.println(String.format("Tong thanh toan: %.2f VND", total));
    }

    public void history(Customer cs, Scanner sc) {
        if (cs == null) {
            Log.error("Khach hang khong hop le!");
            return;
        }

        while(true){
            Extension.clearScreen();
            System.out.println("===== LICH SU MUA HANG CUA KHACH HANG =====" );
            om.historyList(cs);
            Log.request("\nNhap ma hoa don de xem chi tiet (Nhap 0 de thoat): ");
            String oid = sc.nextLine().trim();


            if (oid.equals("0")) {
                Log.info("Huy thao tac.");
                return; // bỏ qua nếu không nhập
            }

            Order selected = om.get(oid.trim());
            if (selected == null || !selected.getCustomer().equals(cs)) {
                Log.error("Khong tim thay hoa don nao!");
                return;
            }

            Extension.printInBox(() -> printOrderDetails(selected));

            Log.info("\nNhan Enter de xem don khac, hoac nhap 0 de quay lai.");
            String choice = sc.nextLine().trim();
            if (choice.equals("0")) break;
        }
    }   

    @Override
    public void updateMenu() {
        Extension.clearScreen();
        System.out.println("==== CAP NHAT TRANG THAI HOA DON ====");
        om.showList();

        Log.request("Nhap ID HOA DON muon cap nhat (hoac 0 de quay lai): ");
        String id = sc.nextLine().trim();
        if (id.equals("0")) return;

        Order order = om.get(id);
        if (order == null) {
            Log.warning("Khong tim thay HOA DON!");
            return;
        }
        Extension.printInBox(() -> printOrderDetails(order));

        Log.info("Trang thai hien tai: " + order.getStatus());
        System.out.println("Chon trang thai moi (khong duoc bo trong):");
        System.out.println("1. Active");
        System.out.println("2. Inactive");
        Log.request("Nhap lua chon: ");
        int choice = Extension.readIntInRange("Nhap lua chon (1-2):", 1, 2, sc);
        boolean newStatus = choice == 1;

        if (order.getStatus() == newStatus) {
            Log.info("Trang thai khong thay doi.");
        } else {
            order.setStatus(newStatus);
            om.save();
            Log.success("Cap nhat thanh cong trang thai hoa don!");
        }
    }
}
