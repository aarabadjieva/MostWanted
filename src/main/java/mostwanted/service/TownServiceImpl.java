package mostwanted.service;

import com.google.gson.Gson;
import mostwanted.common.Constants;
import mostwanted.domain.dtos.TownImportDto;
import mostwanted.domain.entities.Town;
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
public class TownServiceImpl implements TownService{

    private final static String TOWNS_JSON_FILE_PATH = System.getProperty("user.dir") + "/src/main/resources/files/towns.json";

    private final TownRepository townRepository;
    private final ModelMapper mapper;
    private final Gson gson;
    private final FileUtil fileUtil;
    private final ValidationUtil validator;

    @Autowired
    public TownServiceImpl(TownRepository townRepository, ModelMapper mapper, Gson gson, FileUtil fileUtil, ValidationUtil validationUtil) {
        this.townRepository = townRepository;
        this.mapper = mapper;
        this.gson = gson;
        this.fileUtil = fileUtil;
        this.validator = validationUtil;
    }

    @Override
    public Boolean townsAreImported() {
        return this.townRepository.count()>0;
    }

    @Override
    public String readTownsJsonFile() throws IOException {
        return this.fileUtil.readFile(TOWNS_JSON_FILE_PATH);
    }

    @Override
    public String importTowns(String townsFileContent) throws IOException {
        townsFileContent = readTownsJsonFile();
        TownImportDto[] dtos = this.gson.fromJson(townsFileContent, TownImportDto[].class);
        List<String> messages = new ArrayList<>();
        for (TownImportDto dto : dtos) {
            if (!validator.isValid(dto)){
                messages.add(Constants.INCORRECT_DATA_MESSAGE);
                continue;
            }
            Town town = this.townRepository.findByName(dto.getName()).orElse(null);
            if (town!=null){
                messages.add(Constants.DUPLICATE_DATA_MESSAGE);
                continue;
            }
            town = mapper.map(dto, Town.class);
            this.townRepository.saveAndFlush(town);
            messages.add(String.format(Constants.SUCCESSFUL_IMPORT_MESSAGE, town.getClass().getSimpleName(), town.getName()));
        }
        return String.join("\n", messages);
    }

    @Override
    public String exportRacingTowns() {
        List<Town> townsByCountOfRacers = this.townRepository.findAllByRacersCount();
        List<String> result = new ArrayList<>();
        for (Town town : townsByCountOfRacers) {
            result.add(String.format("Name: %s\n" +
                    "Racers: %d\n", town.getName(), town.getRacers().size()));
        }
        return String.join("\n", result);
    }
}
