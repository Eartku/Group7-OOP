package models;

public class OrderItem {
    private Product product;
    private long quantity;


    public OrderItem() {
        this.product = null;
        this.quantity = 0;
    }

    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public String getProductsName(){return product.getName();}

    public long getQuantity() { return quantity; }
    public void setQuantity(long quantity) { this.quantity = quantity; }

    public double getTotalPrice(){
        return product.getPrice() * quantity;
    }

    @Override
    public String toString(){
        return product.getName() + "|" + quantity ;
    }
}