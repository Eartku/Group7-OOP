package models;

public class Guest extends User {
    public Guest(String username, String password, boolean status) {
        super(username, password, 0, status); 
    }

    @Override
    public String toString() {
        return username + "|" + password + "|" + role + "|" + status;
    }

    public Customer toCustomer() {
        Customer c = new Customer(this.username, this.password, this.status);
        c.setCID("");
        c.setFullname("");
        c.setAddress("");
        c.setEmail("");
        c.setPhone("");
        c.setRole(1);  
        return c;
    }
}
