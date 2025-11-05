package models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Customer extends User implements Comparable<Customer>{

// quan hệ Kế thừa Customer -> User

// thuộc tính (attributes)
    private String CID;
    private String fullname;
    private LocalDate dob;       
    private String address;
    private String email;
    private String phone;

    private static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");// định dạng ngày

    //không tạo constructor mặc định để tránh tình trạng tạo ra đối tượng không có gì cả

    //Constructor khi chưa có Profile
    public Customer(String username, String password, boolean  status) {
        super(username, password, 1, status);
        this.CID = "";
        this.fullname = "";
        this.dob = null;
        this.address = "";
        this.email = "";
        this.phone = "";
    }
    // Constructor có tham số
    public Customer(String username, String password, String CID, String fullname, LocalDate dob, String address, String email, String phone, boolean status) {
    super(username, password, 1, status); 
    this.CID = CID;
    this.fullname = fullname;
    this.dob = dob;
    this.address = address;
    this.email = email;
    this.phone = phone; 
    }

    // Getter và Setter (Methods)
    public String getCID() { return CID; }
    public void setCID(String CID) { this.CID = CID; }

    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }

    public void setDob(String input){
        try {
            this.dob = LocalDate.parse(input,FORMATTER);
        } catch (DateTimeParseException e){throw new IllegalArgumentException("Ngay sinh khong hop le! DD/MM/YYYY");}
    }
    public void setDate(LocalDate date){
        this.dob = date;
    }
    public LocalDate getDob(){return dob;}
    public String getDobdate(){
        return (dob != null) ? dob.format(FORMATTER) : "Chua co!";
    }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    //định dạng theo user
    @Override
    public String toString(){
        return username + "|" + password + "|" + role;
    }

    // định dạng chính trong file hồ sơ
    public String toStringProfile(){
        return  CID + "|" + fullname + "|" + getDobdate() + "|" + address + "|" + email + "|" + phone + "|" + username + "|" + status;
    }

    //implement Comparable
    @Override
    public int compareTo(Customer c){
        return Integer.compare(Integer.parseInt(this.getCID().substring(1)),Integer.parseInt(c.getCID().substring(1)));

    }
}

