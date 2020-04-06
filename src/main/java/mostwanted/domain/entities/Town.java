package mostwanted.domain.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "towns")
public class Town extends BaseEntity{

    @Column(unique = true)
    @NotNull
    private String name;

    @OneToMany(mappedBy = "town")
    private List<District> districts;

    @OneToMany(mappedBy = "homeTown")
    private List<Racer> racers;
}
