package equipe.xerminadores.api;

import equipe.xerminadores.model.Agenda;
import equipe.xerminadores.service.AgendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/agendas")
@RequiredArgsConstructor
public class AgendasRestController {

    private final AgendaService agendaService;

    // GET /api/agendas
    @GetMapping
    public ResponseEntity<List<Agenda>> listarTodos() {
        List<Agenda> agendas = agendaService.listarTodos();
        return ResponseEntity.ok(agendas);
    }

    // GET /api/agendas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Agenda> buscarPorId(@PathVariable Long id) {
        Optional<Agenda> agenda = agendaService.buscarPorId(id);
        return agenda.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/agendas
    @PostMapping
    public ResponseEntity<Agenda> criar(@RequestBody Agenda agenda) {
        agendaService.salvar(agenda);
        return ResponseEntity.ok(agenda);
    }

    // PUT /api/agendas/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Agenda> atualizar(@PathVariable Long id, @RequestBody Agenda novaAgenda) {
        Optional<Agenda> agendaExistente = agendaService.buscarPorId(id);

        if (agendaExistente.isPresent()) {
            Agenda agenda = agendaExistente.get();
            agenda.setData(novaAgenda.getData());
            agenda.setHorario(novaAgenda.getHorario());
            agenda.setMedico(novaAgenda.getMedico());
            agenda.setPaciente(novaAgenda.getPaciente());
            agendaService.salvar(agenda);
            return ResponseEntity.ok(agenda);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/agendas/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        Optional<Agenda> agenda = agendaService.buscarPorId(id);
        if (agenda.isPresent()) {
            agendaService.excluir(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
