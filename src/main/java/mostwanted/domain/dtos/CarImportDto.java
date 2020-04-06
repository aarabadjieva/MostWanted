package mostwanted.domain.dtos;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
public class CarImportDto {

    @Expose
    @NotNull
    private String brand;

    @Expose
    @NotNull
    private String model;

    @Expose
    private BigDecimal price;

    @Expose
    @NotNull
    private int yearOfProduction;

    @Expose
    private double maxSpeed;

    @Expose
    private double zeroToSixty;

    @Expose
    private String racerName;
}
