package interfaces;

public interface IAuthenticable extends  IStatus{
    // một user cần phải được kiểm tra mật khẩu, cần username và role và cả trạng thái --> extends cho nhanh
    
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
