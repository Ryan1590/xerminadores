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

    @GetMapping
    public ResponseEntity<List<Agenda>> listarTodos() {
        List<Agenda> agendas = agendaService.listarTodos();
        return ResponseEntity.ok(agendas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Agenda> buscarPorId(@PathVariable Long id) {
        Optional<Agenda> agenda = agendaService.buscarPorId(id);
        return agenda.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Agenda agenda) {
        if (agendaService.horarioNoPassadoOuEmAndamento(agenda.getData(), agenda.getHorario())) {
            return ResponseEntity.badRequest().body("Horário inválido: não pode agendar no passado ou horário já iniciado.");
        }

        if (!agendaService.horarioValido(agenda.getHorario())) {
            return ResponseEntity.badRequest().body("Horário inválido. Use horários entre 08:00 e 18:00 de meia em meia hora.");
        }

        if (agendaService.existeConflito(agenda)) {
            return ResponseEntity.badRequest().body("Conflito: já existe um agendamento com o mesmo médico, data e horário.");
        }

        agendaService.salvar(agenda);
        return ResponseEntity.ok(agenda);
    }



    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Agenda novaAgenda) {
        Optional<Agenda> agendaExistente = agendaService.buscarPorId(id);

        if (agendaExistente.isPresent()) {
            if (agendaService.horarioNoPassadoOuEmAndamento(novaAgenda.getData(), novaAgenda.getHorario())) {
                return ResponseEntity.badRequest().body("Horário inválido: não pode agendar no passado ou horário já iniciado.");
            }

            if (!agendaService.horarioValido(novaAgenda.getHorario())) {
                return ResponseEntity.badRequest().body("Horário inválido. Use horários entre 08:00 e 18:00 de meia em meia hora.");
            }

            novaAgenda.setId(id); // necessário para validação de conflito ignorar a si mesma
            if (agendaService.existeConflito(novaAgenda)) {
                return ResponseEntity.badRequest().body("Conflito: já existe um agendamento com o mesmo médico, data e horário.");
            }

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
