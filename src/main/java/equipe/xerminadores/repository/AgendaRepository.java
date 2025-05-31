package equipe.xerminadores.repository;

import equipe.xerminadores.model.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AgendaRepository extends JpaRepository<Agenda, Long> {
    List<Agenda> findByMedicoIdAndData(Long medicoId, LocalDate data);

    List<Agenda> findByPacienteId(Long pacienteId);

    List<Agenda> findByMedicoId(Long medicoId);
}
