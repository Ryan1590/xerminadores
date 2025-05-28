package equipe.xerminadores.repository;

import equipe.xerminadores.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {
    boolean existsByCrm(String crm);
    Optional<Medico> findByCrm(String crm);
}
