package mostwanted.service;

import com.google.gson.Gson;
import mostwanted.common.Constants;
import mostwanted.domain.dtos.RacerImportDto;
import mostwanted.domain.entities.Car;
import mostwanted.domain.entities.Racer;
import mostwanted.domain.entities.Town;
import mostwanted.repository.RaceRepository;
import mostwanted.repository.RacerRepository;
import mostwanted.repository.TownRepository;
import mostwanted.util.FileUtil;
import mostwanted.util.ValidationUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class RacerServiceImpl implements RacerService {

    private final static String RACERS_JSON_FILE_PATH = System.getProperty("user.dir") + "/src/main/resources/files/racers.json";

    private final RacerRepository racerRepository;
    private final TownRepository townRepository;
    private final ModelMapper mapper;
    private final Gson gson;
    private final FileUtil fileUtil;
    private final ValidationUtil validator;

    @Autowired
    public RacerServiceImpl(RacerRepository racerRepository, TownRepository townRepository, ModelMapper mapper, Gson gson, FileUtil fileUtil, ValidationUtil validator) {
        this.racerRepository = racerRepository;
        this.townRepository = townRepository;
        this.mapper = mapper;
        this.gson = gson;
        this.fileUtil = fileUtil;
        this.validator = validator;
    }

    @Override
    public Boolean racersAreImported() {
        return this.racerRepository.count()>0;
    }

    @Override
    public String readRacersJsonFile() throws IOException {
        return this.fileUtil.readFile(RACERS_JSON_FILE_PATH);
    }

    @Override
    public String importRacers(String racersFileContent) throws IOException {
        racersFileContent = readRacersJsonFile();
        RacerImportDto[] dtos = this.gson.fromJson(racersFileContent, RacerImportDto[].class);
        List<String> messages = new ArrayList<>();
        for (RacerImportDto dto : dtos) {
            Town town = this.townRepository.findByName(dto.getHomeTown()).orElse(null);
            if (!validator.isValid(dto)||town==null){
                messages.add(Constants.INCORRECT_DATA_MESSAGE);
                continue;
            }
            Racer racer = this.racerRepository.findByName(dto.getName()).orElse(null);
            if (racer!=null){
                messages.add(Constants.DUPLICATE_DATA_MESSAGE);
                continue;
            }
            racer = mapper.map(dto, Racer.class);
            racer.setHomeTown(town);
            this.racerRepository.saveAndFlush(racer);
            messages.add(String.format(Constants.SUCCESSFUL_IMPORT_MESSAGE,
                    racer.getClass().getSimpleName(), racer.getName()));
        }
        return String.join("\n", messages);
    }

    @Override
    public String exportRacingCars() {
        List<Racer> racersByCarsCount = this.racerRepository.findAllByCarsCount();
        List<String> result = new ArrayList<>();
        for (Racer racer : racersByCarsCount) {
            result.add(String.format("%s\n" +
                    "Cars:", racer.toString()));
            for (Car car : racer.getCars()) {
                result.add(car.toString());
            }
            result.add(System.lineSeparator());
        }
        return String.join("\n", result);
    }
}
