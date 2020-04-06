package mostwanted.service;

import com.google.gson.Gson;
import mostwanted.common.Constants;
import mostwanted.domain.dtos.CarImportDto;
import mostwanted.domain.entities.Car;
import mostwanted.domain.entities.Racer;
import mostwanted.repository.CarRepository;
import mostwanted.repository.RacerRepository;
import mostwanted.util.FileUtil;
import mostwanted.util.ValidationUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CarServiceImpl implements CarService{

    private final static String CARS_JSON_FILE_PATH = System.getProperty("user.dir")+"/src/main/resources/files/cars.json";

    private final CarRepository carRepository;
    private final RacerRepository racerRepository;
    private final ModelMapper mapper;
    private final Gson gson;
    private final FileUtil fileUtil;
    private final ValidationUtil validator;

    @Autowired
    public CarServiceImpl(CarRepository carRepository, RacerRepository racerRepository, ModelMapper mapper, Gson gson, FileUtil fileUtil, ValidationUtil validator) {
        this.carRepository = carRepository;
        this.racerRepository = racerRepository;
        this.mapper = mapper;
        this.gson = gson;
        this.fileUtil = fileUtil;
        this.validator = validator;
    }

    @Override
    public Boolean carsAreImported() {
        return this.carRepository.count()>0;
    }

    @Override
    public String readCarsJsonFile() throws IOException {
        return this.fileUtil.readFile(CARS_JSON_FILE_PATH);
    }

    @Override
    public String importCars(String carsFileContent) throws IOException {
        carsFileContent = readCarsJsonFile();
        CarImportDto[] dtos = this.gson.fromJson(carsFileContent, CarImportDto[].class);
        List<String> messages = new ArrayList<>();
        for (CarImportDto dto : dtos) {
            Racer racer = this.racerRepository.findByName(dto.getRacerName()).orElse(null);
            if (!validator.isValid(dto)||racer==null){
                messages.add(Constants.INCORRECT_DATA_MESSAGE);
                continue;
            }
            Car car = mapper.map(dto, Car.class);
            car.setRacer(racer);
            this.carRepository.saveAndFlush(car);
            messages.add(String.format(Constants.SUCCESSFUL_IMPORT_MESSAGE,
                    car.getClass().getSimpleName(), car.getBrand() + " " +
                    car.getModel() + " @ " + car.getYearOfProduction()));
        }
        return String.join("\n", messages);
    }
}
