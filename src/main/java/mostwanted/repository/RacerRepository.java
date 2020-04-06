package mostwanted.repository;

import mostwanted.domain.entities.Racer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RacerRepository extends JpaRepository<Racer, Integer> {

    Optional<Racer> findByName(String name);

    @Query("select r from Racer r " +
            "where size(r.cars) >0 " +
            "order by size(r.cars) desc , r.name asc ")
    List<Racer> findAllByCarsCount();
}
