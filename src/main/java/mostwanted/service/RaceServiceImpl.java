package mostwanted.service;

import mostwanted.common.Constants;
import mostwanted.domain.dtos.races.EntryImportDto;
import mostwanted.domain.dtos.races.EntryImportRootDto;
import mostwanted.domain.dtos.races.RaceImportDto;
import mostwanted.domain.dtos.races.RaceImportRootDto;
import mostwanted.domain.entities.District;
import mostwanted.domain.entities.Race;
import mostwanted.domain.entities.RaceEntry;
import mostwanted.repository.DistrictRepository;
import mostwanted.repository.RaceEntryRepository;
import mostwanted.repository.RaceRepository;
import mostwanted.util.FileUtil;
import mostwanted.util.ValidationUtil;
import mostwanted.util.XmlParser;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class RaceServiceImpl implements RaceService {

    private final static String RACES_XML_FILE_PATH = System.getProperty("user.dir") + "/src/main/resources/files/races.xml";

    private final RaceRepository raceRepository;
    private final DistrictRepository districtRepository;
    private final RaceEntryRepository raceEntryRepository;
    private final ModelMapper mapper;
    private final FileUtil fileUtil;
    private final XmlParser xmlParser;
    private final ValidationUtil validator;

    @Autowired
    public RaceServiceImpl(RaceRepository raceRepository, DistrictRepository districtRepository, RaceEntryRepository raceEntryRepository, ModelMapper mapper, FileUtil fileUtil, XmlParser xmlParser, ValidationUtil validator) {
        this.raceRepository = raceRepository;
        this.districtRepository = districtRepository;
        this.raceEntryRepository = raceEntryRepository;
        this.mapper = mapper;
        this.fileUtil = fileUtil;
        this.xmlParser = xmlParser;
        this.validator = validator;
    }

    @Override
    public Boolean racesAreImported() {
        return this.raceRepository.count()>0;
    }

    @Override
    public String readRacesXmlFile() throws IOException {
        return this.fileUtil.readFile(RACES_XML_FILE_PATH);
    }

    @Override
    public String importRaces() throws JAXBException {
        RaceImportRootDto raceImportRootDto = this.xmlParser.parseXml(RaceImportRootDto.class, RACES_XML_FILE_PATH);
        List<String> messages = new ArrayList<>();
        for (RaceImportDto raceImportDto : raceImportRootDto.getRaceImportDtos()) {
            District district = this.districtRepository.findByName(raceImportDto.getDistrictName()).orElse(null);
            if (district==null||!validator.isValid(raceImportDto)){
                messages.add(Constants.INCORRECT_DATA_MESSAGE);
                continue;
            }
            EntryImportRootDto entryImportRootDto = raceImportDto.getEntries();
            List<RaceEntry> entries = new ArrayList<>();
            for (EntryImportDto entryDto : entryImportRootDto.getEntries()) {
                RaceEntry raceEntry = this.raceEntryRepository.findById(entryDto.getId()).orElse(null);
                if (raceEntry==null){
                    messages.add(Constants.INCORRECT_DATA_MESSAGE);
                    continue;
                }
                entries.add(raceEntry);
            }
            Race race = mapper.map(raceImportDto, Race.class);
            race.setDistrict(district);
            race.setEntries(entries);
            this.raceRepository.saveAndFlush(race);
            messages.add(String.format(Constants.SUCCESSFUL_IMPORT_MESSAGE,
                    race.getClass().getSimpleName(), race.getId()));
        }
        return String.join("\n", messages);
    }
}