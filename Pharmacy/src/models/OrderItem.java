package models;

public class OrderItem {
    // khi khách chọn sản phẩm, có số lượng khác nhau
    private Product product;
    private long quantity;


    //Constructor mặc định
    public OrderItem() {
        this.product = null;
        this.quantity = 0;
    }

    //Constructor tham số
    public OrderItem(Product product, long quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    //method getter - setter
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public String getProductsName(){return product.getName();}

    public long getQuantity() { return quantity; }
    public void setQuantity(long quantity) { this.quantity = quantity; }

    public double getTotalPrice(){
        return product.getPrice() * quantity;
    }

    // định dạng trong file
    @Override
    public String toString(){
        return product.getName() + "|" + quantity ;
    }
}