package interfaces;

public interface IManagement<T> { // quản lý đối tượng T
    //CRUD - Create - Read - Update - Delete

    boolean exists(String key);

    T get(String key);

    void add(T obj);   // them doi tương vào danh sách

    void delete();  // xoa doi tuong trong danh sach

    void showList(); // in ra các  obj hoạt động trong list

    void blackList(); // in ra các  obj bi khóa  trong list

    String report(); // báo cáo quản lý

    void save(); //lưu
}
