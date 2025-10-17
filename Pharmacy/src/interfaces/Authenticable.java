package interfaces;

public interface Authenticable {
    // một user cần phải được kiểm tra mật khẩu, cần username và role
    
    // Kiểm tra mật khẩu 
    boolean checkPassword(String inputPassword);

    // Lấy tên người dùng
    String getUsername();

    String getPassword();

    // Lấy role 
    int getRole();

    // Đặt role 
    void setRole(int role);
}
