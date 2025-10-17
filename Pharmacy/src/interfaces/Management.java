package interfaces;

public interface Management<T> { // quản lý đối tượng T
    //CRUD - Create - Read - Update - Delete

    boolean exists(String key);

    T get(String key);

    void add(T obj);   // them doi tương vào danh sách

    void delete(String key);  // xoa doi tuong trong danh sach

    void showList(); // in ra các  obj trong list

    // tính năng update thông quan setter của đối tượng

    String report(); // báo cáo quản lý

    void save();
}
