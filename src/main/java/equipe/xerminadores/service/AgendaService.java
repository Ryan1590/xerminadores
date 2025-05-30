package equipe.xerminadores.service;

import equipe.xerminadores.model.Agenda;
import equipe.xerminadores.repository.AgendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AgendaService {

    private final AgendaRepository repository;

    public List<Agenda> listarTodos() {
        return repository.findAll();
    }

    public void salvar(Agenda agenda) {
        repository.save(agenda);
    }

    public Optional<Agenda> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }
}
