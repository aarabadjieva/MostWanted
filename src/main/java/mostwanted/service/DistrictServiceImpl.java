package mostwanted.service;

import com.google.gson.Gson;
import mostwanted.common.Constants;
import mostwanted.domain.dtos.DistrictImportDto;
import mostwanted.domain.entities.District;
import mostwanted.domain.entities.Town;
import mostwanted.repository.DistrictRepository;
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
public class DistrictServiceImpl implements DistrictService{

    private final static String DISTRICT_JSON_FILE_PATH = System.getProperty("user.dir")+"/src/main/resources/files/districts.json";

    private final DistrictRepository districtRepository;
    private final TownRepository townRepository;
    private final ModelMapper mapper;
    private final Gson gson;
    private final FileUtil fileUtil;
    private final ValidationUtil validator;

    @Autowired
    public DistrictServiceImpl(DistrictRepository districtRepository, TownRepository townRepository, ModelMapper mapper, Gson gson, FileUtil fileUtil, ValidationUtil validator) {
        this.districtRepository = districtRepository;
        this.townRepository = townRepository;
        this.mapper = mapper;
        this.gson = gson;
        this.fileUtil = fileUtil;
        this.validator = validator;
    }

    @Override
    public Boolean districtsAreImported() {
        return this.districtRepository.count()>0;
    }

    @Override
    public String readDistrictsJsonFile() throws IOException {
        return this.fileUtil.readFile(DISTRICT_JSON_FILE_PATH);
    }

    @Override
    public String importDistricts(String districtsFileContent) throws IOException {
        districtsFileContent = readDistrictsJsonFile();
        DistrictImportDto[] dtos = this.gson.fromJson(districtsFileContent, DistrictImportDto[].class);
        List<String> messages = new ArrayList<>();
        for (DistrictImportDto dto : dtos) {
            Town town = this.townRepository.findByName(dto.getTownName()).orElse(null);
            if (!validator.isValid(dto)||town==null){
                messages.add(Constants.INCORRECT_DATA_MESSAGE);
                continue;
            }
            District district = this.districtRepository.findByName(dto.getName()).orElse(null);
            if (district!=null){
                messages.add(Constants.DUPLICATE_DATA_MESSAGE);
                continue;
            }
            district = mapper.map(dto, District.class);
            district.setTown(town);
            this.districtRepository.saveAndFlush(district);
            messages.add(String.format(Constants.SUCCESSFUL_IMPORT_MESSAGE, district.getClass().getSimpleName(), district.getName()));
        }
        return String.join("\n", messages);
    }
}
