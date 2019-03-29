package model;

import javax.persistence.*;

@Entity
@Table(name = "orders", schema = "public", catalog = "pxisnitl")
public class OrdersEntity {
    private int id;
    private Double price;
    private Boolean paid;
    private ItemsEntity item;
    private TablesEntity table;
    private String name;
    private int quantity;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "price", nullable = true, precision = 0)
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Basic
    @Column(name = "paid", nullable = true)
    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    @ManyToOne
    public TablesEntity getTable() {
        return this.table;
    }

    public void setTable(TablesEntity table) {
        this.table = table;
    }

    @ManyToOne(cascade = {CascadeType.ALL})
    public ItemsEntity getItem() {
        return this.item;
    }

    public void setItem(ItemsEntity item) {
        this.item = item;
    }

    @Basic
    @Column(name = "quantity", nullable = true)
    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrdersEntity that = (OrdersEntity) o;

        if (id != that.id) return false;
        if (price != null ? !price.equals(that.price) : that.price != null) return false;
        if (paid != null ? !paid.equals(that.paid) : that.paid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (price != null ? price.hashCode() : 0);
        result = 31 * result + (paid != null ? paid.hashCode() : 0);
        return result;
    }
}
