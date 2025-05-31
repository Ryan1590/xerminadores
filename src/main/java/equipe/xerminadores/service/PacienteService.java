package equipe.xerminadores.service;

import equipe.xerminadores.exception.DataNascimentoInvalidaException;
import equipe.xerminadores.model.Paciente;
import equipe.xerminadores.repository.PacienteRepository;
import equipe.xerminadores.repository.AgendaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final AgendaRepository agendaRepository;  // injetar o repository de agendamentos

    public PacienteService(PacienteRepository pacienteRepository, AgendaRepository agendaRepository) {
        this.pacienteRepository = pacienteRepository;
        this.agendaRepository = agendaRepository;
    }

    public List<Paciente> listarTodos() {
        return pacienteRepository.findAll();
    }

    public Optional<Paciente> buscarPorId(Long id) {
        return pacienteRepository.findById(id);
    }

    public Paciente salvar(Paciente paciente) {
        if (paciente.getDataNascimento() != null && paciente.getDataNascimento().isAfter(LocalDate.now())) {
            throw new DataNascimentoInvalidaException("Data de nascimento não pode ser no futuro.");
        }

        if (paciente.getId() != null) {
            return atualizar(paciente.getId(), paciente);
        } else {
            return pacienteRepository.save(paciente);
        }
    }

    public Paciente atualizar(Long id, Paciente atualizado) {
        return pacienteRepository.findById(id).map(p -> {
            p.setNome(atualizado.getNome());
            p.setCpf(atualizado.getCpf());
            p.setDataNascimento(atualizado.getDataNascimento());
            p.setTelefone(atualizado.getTelefone());
            return pacienteRepository.save(p);
        }).orElseThrow(() -> new RuntimeException("Paciente não encontrado com id " + id));
    }

    public void deletar(Long id) {
        // Verifica se paciente possui agendamentos vinculados
        boolean existeAgendamento = !agendaRepository.findByPacienteId(id).isEmpty();
        if (existeAgendamento) {
            throw new IllegalStateException("Paciente possui agendamentos vinculados e não pode ser deletado.");
        }
        pacienteRepository.deleteById(id);
    }
}
