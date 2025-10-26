package service;

import interfaces.Authenticable;
import interfaces.Management;
import java.io.*;
import java.util.*;
import models.Admin;
import models.Customer;
import models.Guest;
import view.Extension;

public class UserManager implements Management<Authenticable> {

    private static final String FILE_PATH = System.getProperty("user.dir") + "/resources/users.txt";

    private final Map<String, Authenticable> users = new HashMap<>(); // key = username
    private final CustomerManager cm;

    public UserManager(CustomerManager cm) {
        this.cm = cm;
        loadUsers();
    }

    private void loadUsers() {
        Map<String, Customer> customerMap = cm.getCustomerMap(); // lấy tất cả customer từ CustomerManager

        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                String username = parts[0];
                String password = parts[1];
                int role = Integer.parseInt(parts[2]);
                boolean status = Boolean.parseBoolean(parts[3]);

                switch (role) {
                    case 0 -> users.put(username, new Guest(username, password, status));
                    case 1 -> {
                        Customer profile = customerMap.get(username);
                        if (profile != null) {
                            profile.setPassword(password);
                            profile.setStatus(status);
                            users.put(username, profile);
                        } else {
                            users.put(username, new Customer(username, password, status));
                        }
                    }
                    case 2 -> users.put(username, new Admin(username, password, status));
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading users: " + e.getMessage());
        }
    }

    @Override
    public boolean exists(String username) {
        return users.containsKey(username);
    }

    @Override
    public Authenticable get(String username) {
        return users.get(username);
    }

    @Override
    public void add(Authenticable user) {
        users.put(user.getUsername(), user);
    }

    @Override
    public void delete(String username) {
        Authenticable u = users.remove(username);
        if (u instanceof Customer customer) {
            cm.delete(customer.getCID()); // xóa đồng bộ profile
        }
    }

    public void upgradeGuestToCustomer(Guest g, Customer c) {
        users.remove(g.getUsername());
        users.put(c.getUsername(), c);
    }

    public void replaceUser(Authenticable oldUser, Authenticable newUser) {
        users.put(oldUser.getUsername(), newUser);
    }

    public void updateUser(Authenticable newUser) {
        users.put(newUser.getUsername(), newUser);
    }

    @Override
    public void showList() {
        Extension.printTableHeader("Username", "Password(Show)", "Role", "Status");
        for (Authenticable u : users.values()) {
            if (u.getStatus()) {
                String role = switch (u.getRole()) {
                    case 1 -> "Customer";
                    case 2 -> "Admin";
                    case 0 -> "Guest";
                    default -> "Nothing";
                };
                Extension.printTableRow(u.getUsername(), u.getPassword(), role, u.getStatus());
            }
        }
    }

    public void hidePassList() {
        Extension.printTableHeader("Username", "Password(Hide)", "Role", "Status");
        for (Authenticable u : users.values()) {
            if (u.getStatus()) {
                String role = switch (u.getRole()) {
                    case 1 -> "Customer";
                    case 2 -> "Admin";
                    case 0 -> "Guest";
                    default -> "Nothing";
                };
                Extension.printTableRow(u.getUsername(), Extension.maskPassword(u.getPassword(), "-"), role, u.getStatus());
            }
        }
    }

    public void blackList() {
        Extension.printTableHeader("Username", "Password", "Role", "Status");
        for (Authenticable u : users.values()) {
            if (!u.getStatus()) {
                String role = switch (u.getRole()) {
                    case 1 -> "Customer";
                    case 2 -> "Admin";
                    case 0 -> "Guest";
                    default -> "Nothing";
                };
                Extension.printTableRow(u.getUsername(), u.getPassword(), role, u.getStatus());
            }
        }
    }


    @Override
    public void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Authenticable u : users.values()) {
                String line = String.join("|",
                u.getUsername(),
                u.getPassword(),
                String.valueOf(u.getRole()),
                String.valueOf(u.getStatus())
            );
            bw.write(line + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
        cm.save(); // đồng bộ profile Customer
    }

    @Override
    public String report() {
        int guest = 0, customer = 0, admin = 0, blocked = 0;
        for (Authenticable u : users.values()) {
            if (!u.getStatus()) blocked++;
            if (u instanceof Guest) guest++;
            else if (u instanceof Customer) customer++;
            else if (u instanceof Admin) admin++;
        }
        return "Tong so luong tai khoan: " + users.size() +
                "\nGuest: " + guest +
                "\nCustomer: " + customer +
                "\nAdmin: " + admin +
                "\nBlocked: " + blocked;
    }
}
