package mostwanted.repository;

import mostwanted.domain.entities.Town;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TownRepository extends JpaRepository<Town, Integer> {
    Optional<Town> findByName(String name);

    @Query("select t from Town t " +
            "where size(t.racers) >0 " +
            "order by size(t.racers) desc , t.name asc ")
    List<Town> findAllByRacersCount();
}
