package mostwanted.domain.dtos;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class RacerImportDto {

    @Expose
    @NotNull
    private String name;

    @Expose
    private int age;

    @Expose
    private double bounty;

    @Expose
    private String homeTown;

}
