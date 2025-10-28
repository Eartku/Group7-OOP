package service;

import interfaces.IManagement;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import models.Customer;
import view.Extension;

public final class CustomerManager implements IManagement<Customer> {

    public static final String FILE_PATH = System.getProperty("user.dir") + "/resources/customers.txt";

    // 2 map để lookup nhanh
    private final Map<String, Customer> customerByID = new TreeMap<>();
    private final Map<String, Customer> customerByUsername = new TreeMap<>();

    public CustomerManager() {
        loadProfiles();
    }

    // Load profile từ file
    private void loadProfiles() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return;
        }

        customerByID.clear();
        customerByUsername.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split("\\|");
                if (parts.length < 8) {
                    System.out.println("[WARN] Invalid line in customers.txt: " + line);
                    continue;
                }

                String CID = parts[0].trim();
                String fullName = parts[1].trim();
                LocalDate dob = null;
                try {
                    dob = LocalDate.parse(parts[2].trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                } catch (Exception e) {
                    System.out.println("[WARN] Invalid DOB for CID " + CID + ": " + parts[2]);
                }

                String address = parts[3].trim();
                String email = parts[4].trim();
                String phone = parts[5].trim();
                String username = parts[6].trim();
                boolean status = Boolean.parseBoolean(parts[7].trim());

                Customer c = new Customer(username, "", CID, fullName, dob, address, email, phone, status);
                customerByID.put(CID, c);
                customerByUsername.put(username, c);
            }

        } catch (Exception e) {
            System.out.println("[ERROR] Error reading customers: " + e.getMessage());
        }
    }

    public Customer getByUsername(String username) {
        return customerByUsername.get(username);
    }

    @Override
    public boolean exists(String ID) {
        boolean found = customerByID.containsKey(ID);
        return found;
    }

    @Override
    public Customer get(String ID) {
        return customerByID.get(ID);
    }

    public ArrayList<Customer> getCustomerByName(String keyword) {
        ArrayList<Customer> matched = new ArrayList<>();
        for (Customer c : customerByID.values()) {
            if (c.getFullname().toLowerCase().contains(keyword.toLowerCase()))
                matched.add(c);
        }
        return matched;
    }

    public void updateUser(Customer newCustomer, String oldUsername) {
        Customer oldCustomer = customerByUsername.remove(oldUsername);
        if (oldCustomer != null) {
            customerByID.remove(oldCustomer.getCID());
        }
        customerByUsername.put(newCustomer.getUsername(), newCustomer);
        customerByID.put(newCustomer.getCID(), newCustomer);
    }

    @Override
    public void add(Customer c) {
        customerByID.put(c.getCID(), c);
        customerByUsername.put(c.getUsername(), c);
    }

    @Override
    public void delete(String ID) {
        Customer c = customerByID.remove(ID);
        if (c != null) {
            customerByUsername.remove(c.getUsername());
        }
    }

    public void updateCustomer(Customer newCustomer, String oldCID) {
        Customer oldCustomer = customerByID.get(oldCID);
        if (oldCustomer != null) {
            customerByUsername.remove(oldCustomer.getUsername());
        }
        customerByID.put(newCustomer.getCID(), newCustomer);
        customerByUsername.put(newCustomer.getUsername(), newCustomer);
    }

    @Override
    public void showList() {
        Extension.printTableHeader("Ma khach hang","Ho va ten","Ngay sinh","Dia chi","So dien thoai","Email","Trang thai");
        for (Customer c : customerByID.values()) {
            if(c.getStatus())
                Extension.printTableRow(c.getCID(),c.getFullname(),c.getDobdate(),c.getAddress(),c.getPhone(),c.getEmail(),c.getStatusString());
        }
    }

    @Override
    public void blackList() {
        Extension.printTableHeader("Ma khach hang","Ho va ten","Ngay sinh","Dia chi","So dien thoai","Email","Trang thai");
        for (Customer c : customerByID.values()) {
            if(!c.getStatus())
                Extension.printTableRow(c.getCID(),c.getFullname(),c.getDobdate(),c.getAddress(),c.getPhone(),c.getEmail(),c.getStatusString());
        }
    }

    @Override
    public String report() {
        return "Tong so luong khach hang: " + customerByID.size();
    }

    @Override
    public void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Customer c : customerByID.values()) {
                bw.write(c.toStringProfile() + "\n");
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Error saving customers: " + e.getMessage());
        }
    }

    // Lấy tất cả customer map để UserManager dùng
    public Map<String, Customer> getCustomerMap() {
        return customerByUsername;
    }
}
