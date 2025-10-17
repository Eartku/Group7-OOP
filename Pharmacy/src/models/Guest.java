package models;

public class Guest extends User{

    public Guest(String username, String password) {
        super(username, password, 0); 
    }

    @Override
    public boolean checkPassword(String inputPassword) {
        return getPassword().equals(inputPassword);
    }

    @Override
    public String getUsername() {
        return super.getUsername();
    }

    @Override
    public int getRole() {
        return super.getRole();
    }

    @Override
    public void setRole(int role) {
        super.setRole(role);
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
