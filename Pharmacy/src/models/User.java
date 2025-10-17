package models;

import interfaces.Authenticable;

abstract class User implements Authenticable{
    protected String username;
    protected String password;
    protected int role;
    
    public User(){
        this.username = "";
        this.password = "";
        this.role = -1;
    }

    public User(String username, String password, int role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    @Override
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    @Override
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public int getRole() { return role; }

    @Override
    public void setRole(int role) { this.role = role; }
    
    @Override
    public boolean checkPassword(String inputPassword) {
        return getPassword().equals(inputPassword);
    }

    @Override
    public abstract String toString(); // hàm trừu tượng
    
}
