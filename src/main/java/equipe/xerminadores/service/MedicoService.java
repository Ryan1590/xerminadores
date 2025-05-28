package equipe.xerminadores.service;

import equipe.xerminadores.exception.DuplicateCrmException;
import equipe.xerminadores.model.Medico;
import equipe.xerminadores.repository.MedicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicoService {

    private final MedicoRepository medicoRepository;

    public MedicoService(MedicoRepository medicoRepository) {
        this.medicoRepository = medicoRepository;
    }

    public List<Medico> listarTodos() {
        return medicoRepository.findAll();
    }

    public Optional<Medico> buscarPorId(Long id) {
        return medicoRepository.findById(id);
    }

    public Medico salvar(Medico medico) {
        // Se for novo registro (id == null)
        if (medico.getId() == null) {
            if (medicoRepository.existsByCrm(medico.getCrm())) {
                throw new DuplicateCrmException("CRM '" + medico.getCrm() + "' já está cadastrado.");
            }
            return medicoRepository.save(medico);
        }
        // Se for atualização: permitir mesmo CRM se for o mesmo registro,
        // mas bloquear se outro registro já usar aquele CRM
        Optional<Medico> existente = medicoRepository.findByCrm(medico.getCrm());
        if (existente.isPresent() && !existente.get().getId().equals(medico.getId())) {
            throw new DuplicateCrmException("CRM '" + medico.getCrm() + "' já está cadastrado.");
        }
        return medicoRepository.save(medico);
    }


    public Medico atualizar(Long id, Medico medicoAtualizado) {
        return medicoRepository.findById(id).map(medico -> {
            medico.setNome(medicoAtualizado.getNome());
            medico.setCrm(medicoAtualizado.getCrm());
            medico.setEspecialidade(medicoAtualizado.getEspecialidade());
            return medicoRepository.save(medico);
        }).orElseThrow(() -> new RuntimeException("Médico não encontrado com id " + id));
    }

    public void deletar(Long id) {
        medicoRepository.deleteById(id);
    }
}
