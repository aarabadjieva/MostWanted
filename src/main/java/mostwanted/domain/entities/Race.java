package mostwanted.domain.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "races")
public class Race extends BaseEntity{

    @Column
    @NotNull
    @Min(0)
    private int laps;

    @ManyToOne
    @JoinColumn(name = "district_id", referencedColumnName = "id")
    @NotNull
    private District district;

    @OneToMany(mappedBy = "race")
    private List<RaceEntry> entries;
}
