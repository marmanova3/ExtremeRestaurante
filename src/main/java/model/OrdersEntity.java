package model;

import javax.persistence.*;

@Entity
@Table(name = "orders", schema = "public", catalog = "pxisnitl")
public class OrdersEntity {
    private int id;
    private Double price;
    private Boolean paid;

    @Id
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
