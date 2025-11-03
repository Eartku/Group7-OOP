package interfaces;

public interface IStatus { // trạng thái thực thể trong quản lý - vì khi xóa vật lý thì mất luôn dữ liệu, không thể dùng lại, 
    //không cần thì có thể khóa lại, mở lại cũng dễ hơn
    void setStatus(boolean status);
    boolean getStatus();
    String getStatusString();
}
