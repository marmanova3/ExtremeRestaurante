package model;

import javax.persistence.*;

@Entity
@Table(name = "cash_register", schema = "public", catalog = "pxisnitl")
public class CashRegisterEntity {
    private int id;
    private Double cashStatus;

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "cash_status", nullable = true, precision = 0)
    public Double getCashStatus() {
        return cashStatus;
    }

    public void setCashStatus(Double cashStatus) {
        this.cashStatus = cashStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CashRegisterEntity that = (CashRegisterEntity) o;

        if (id != that.id) return false;
        if (cashStatus != null ? !cashStatus.equals(that.cashStatus) : that.cashStatus != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (cashStatus != null ? cashStatus.hashCode() : 0);
        return result;
    }
}
