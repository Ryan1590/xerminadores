package equipe.xerminadores.service;

import equipe.xerminadores.exception.CpfJaCadastradoException;
import equipe.xerminadores.model.Paciente;
import equipe.xerminadores.repository.PacienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    public List<Paciente> listarTodos() {
        return pacienteRepository.findAll();
    }

    public Optional<Paciente> buscarPorId(Long id) {
        return pacienteRepository.findById(id);
    }

    public Paciente salvar(Paciente paciente) {
        boolean cpfJaExiste = pacienteRepository.findByCpf(paciente.getCpf())
                .filter(p -> !p.getId().equals(paciente.getId()))
                .isPresent();

        if (cpfJaExiste) {
            throw new CpfJaCadastradoException("CPF já cadastrado para outro paciente.");
        }

        return pacienteRepository.save(paciente);
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
        pacienteRepository.deleteById(id);
    }


}
