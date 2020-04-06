package mostwanted.domain.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "cars")
public class Car extends BaseEntity{

    @Column
    @NotNull
    private String brand;

    @Column
    @NotNull
    private String model;

    @Column
    private BigDecimal price;

    @Column
    @NotNull
    private int yearOfProduction;

    @Column
    private double maxSpeed;

    @Column
    private double zeroToSitxy;

    @ManyToOne
    @JoinColumn(name = "racer_id", referencedColumnName = "id")
    private Racer racer;

    @OneToMany(mappedBy = "car")
    private List<RaceEntry> entries;

    @Override
    public String toString() {
        return String.format("%s %s %d",
                this.brand, this.model, this.yearOfProduction);
    }
}
