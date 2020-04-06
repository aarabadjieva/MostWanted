package mostwanted.domain.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "racers")
public class Racer extends BaseEntity{

    @Column(unique = true)
    @NotNull
    private String name;

    @Column
    private int age;

    @Column
    private double bounty;

    @ManyToOne
    @JoinColumn(name = "home_town_id", referencedColumnName = "id")
    private Town homeTown;

    @OneToMany(mappedBy = "racer")
    private List<Car> cars;

    @OneToMany(mappedBy = "racer")
    private List<RaceEntry> entries;

    @Override
    public String toString() {
        if (this.age!=0){
            return String.format("Name: %s\n" +
                    "Age: %d", this.name, this.age);
        }
        return String.format("Name: %s", this.name);
    }
}
