package mostwanted.domain.dtos.raceentries;

import lombok.Getter;
import lombok.Setter;
import mostwanted.domain.dtos.races.RaceImportDto;
import mostwanted.domain.entities.RaceEntry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Getter
@Setter
@XmlRootElement(name = "race-entries")
@XmlAccessorType(XmlAccessType.FIELD)
public class RaceEntryImportRootDto {

    @XmlElement(name = "race-entry")
    List<RaceEntryImportDto> raceImportDtos;
}