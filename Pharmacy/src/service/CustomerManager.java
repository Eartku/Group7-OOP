package service;
import interfaces.Management;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import models.Customer;

public final class CustomerManager implements Management<Customer> {

    public static final String FILE_PATH = System.getProperty("user.dir") + "/resources/customers.txt";

    // 2 map để lookup nhanh
    private final Map<String, Customer> byCID = new HashMap<>();
    private final Map<String, Customer> byUsername = new HashMap<>();

    public CustomerManager() {
        loadProfiles();
    }

    // Load profile từ file
    public void loadProfiles() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                String CID = parts[0];
                String fullName = parts[1];
                LocalDate dob = null;
                try { dob = LocalDate.parse(parts[2], DateTimeFormatter.ofPattern("dd/MM/yyyy")); } catch (Exception ignored) {}
                String address = parts[3];
                String email = parts[4];
                String phone = parts[5];
                String username = parts[6];
                boolean status = Boolean.parseBoolean(parts[7]);

                Customer c = new Customer(username, "", CID, fullName, dob, address, email, phone, status);
                byCID.put(CID, c);
                byUsername.put(username, c);
            }
        } catch (Exception e) {
            System.out.println("Error reading customers: " + e.getMessage());
        }
    }

    public Customer getByUsername(String username) {
        return byUsername.get(username);
    }

    @Override
    public boolean exists(String ID) {
        return byCID.containsKey(ID);
    }

    @Override
    public Customer get(String ID) {
        return byCID.get(ID);
    }

    public ArrayList<Customer> getCustomerByName(String keyword) {
        ArrayList<Customer> matched = new ArrayList<>();
        for (Customer c : byCID.values()) {
            if (c.getFullname().toLowerCase().contains(keyword.toLowerCase()))
                matched.add(c);
        }
        return matched;
    }

    public void updateUser(Customer newCustomer, String oldUsername) {
        Customer oldCustomer = byUsername.remove(oldUsername);
        if (oldCustomer != null) {
            byCID.remove(oldCustomer.getCID());
        }
        byUsername.put(newCustomer.getUsername(), newCustomer);
        byCID.put(newCustomer.getCID(), newCustomer);
    }


    @Override
    public void add(Customer c) {
        byCID.put(c.getCID(), c);
        byUsername.put(c.getUsername(), c);
    }

    @Override
    public void delete(String ID) {
        Customer c = byCID.remove(ID);
        if (c != null) byUsername.remove(c.getUsername());
    }

    public void updateCustomer(Customer newCustomer, String oldCID) {
        Customer oldCustomer = byCID.remove(oldCID);
        if (oldCustomer != null) byUsername.remove(oldCustomer.getUsername());
        add(newCustomer);
    }

    @Override
    public void showList() {
        System.out.println("CID | Fullname");
        for (Customer c : byCID.values()) {
            System.out.println(c.getCID() + " | " + c.getFullname());
        }
    }

    @Override
    public String report() {
        return "Tong so luong khach hang: " + byCID.size();
    }

    @Override
    public void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Customer c : byCID.values()) {
                bw.write(c.toString() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving customers: " + e.getMessage());
        }
    }

    // Lấy tất cả customer map để UserManager dùng
    public Map<String, Customer> getCustomerMap() {
        return byUsername;
    }
}
