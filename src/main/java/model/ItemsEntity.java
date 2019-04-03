package model;

import javax.persistence.*;

@Entity
@Table(name = "items", schema = "public", catalog = "pxisnitl")
public class ItemsEntity {
    private int id;
    private String name;
    private Double price;
    private String image;
    private Boolean softDelete;
    private CategoriesEntity category;

    public ItemsEntity() {
    }

    public ItemsEntity(String name, Double price, CategoriesEntity category) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.image = "";
        this.softDelete = false;
    }

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
    @Column(name = "name", nullable = false, length = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "price", nullable = false, precision = 0)
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Basic
    @Column(name = "image", nullable = true, length = 100)
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Basic
    @Column(name = "soft_delete", nullable = true)
    public Boolean getSoftDelete() {
        return softDelete;
    }

    public void setSoftDelete(Boolean softDelete) {
        this.softDelete = softDelete;
    }

    @ManyToOne
    public CategoriesEntity getCategory() {
        return this.category;
    }

    public void setCategory(CategoriesEntity category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemsEntity that = (ItemsEntity) o;

        if (id != that.id) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (price != null ? !price.equals(that.price) : that.price != null) return false;
        if (image != null ? !image.equals(that.image) : that.image != null) return false;
        if (softDelete != null ? !softDelete.equals(that.softDelete) : that.softDelete != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (price != null ? price.hashCode() : 0);
        result = 31 * result + (image != null ? image.hashCode() : 0);
        result = 31 * result + (softDelete != null ? softDelete.hashCode() : 0);
        return result;
    }
}
