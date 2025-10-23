package models;

public class Guest extends User{

    public Guest(String username, String password) {
        super(username, password, 0); 
    }

    @Override
    public String toString(){
        return username + "|" + password + "|" + role;
    }

    public Customer toCustomer() {
        Customer c = new Customer(this.username, this.password);

        c.setCID("");         
        c.setFullname("");
        c.setAddress("");
        c.setEmail("");
        c.setPhone("");
        c.setRole(1);  
        return c;
    }

}
