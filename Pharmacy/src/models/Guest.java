package models;

public class Guest extends User { // class con kế thừa User

//Mục đích tạo ra: Khi đăng ký người dùng chỉ có nhập username, password nhưng chưa cập nhật hồ sơ
//----> tạo ra Guest để đưa vào Menu cập nhật hồ sơ từ đó chuyển sang Customer

    //constructor tham số
    public Guest(String username, String password, boolean status) {
        super(username, password, 0, status); 
    }

    //định dạng chính user
    @Override
    public String toString() {
        return username + "|" + password + "|" + role + "|" + status;
    }

    // hàm cho phép thay thế từ khách vãng lai (Guest) sang khách thật sự (Customer) khi đã có hồ sơ
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
