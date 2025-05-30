package equipe.xerminadores.controller;

import equipe.xerminadores.model.Agenda;
import equipe.xerminadores.service.AgendaService;
import equipe.xerminadores.service.MedicoService;
import equipe.xerminadores.service.PacienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/agendamentos")
@RequiredArgsConstructor
public class AgendaController {

    private final AgendaService agendaService;
    private final PacienteService pacienteService;
    private final MedicoService medicoService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("agendas", agendaService.listarTodos());
        return "agendamentos/listar";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("agenda", new Agenda());
        model.addAttribute("pacientes", pacienteService.listarTodos());
        model.addAttribute("medicos", medicoService.listarTodos());
        return "agendamentos/cadastro";
    }

    @PostMapping
    public String salvar(@ModelAttribute("agenda") Agenda agenda) {
        agendaService.salvar(agenda);
        return "redirect:/agendamentos";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        var agenda = agendaService.buscarPorId(id).orElseThrow();
        model.addAttribute("agenda", agenda);
        model.addAttribute("pacientes", pacienteService.listarTodos());
        model.addAttribute("medicos", medicoService.listarTodos());
        return "agendamentos/cadastro";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        agendaService.excluir(id);
        return "redirect:/agendamentos";
    }
}
