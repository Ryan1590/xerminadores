package equipe.xerminadores.controller;

import equipe.xerminadores.model.Agenda;
import equipe.xerminadores.repository.AgendaRepository;
import equipe.xerminadores.service.AgendaService;
import equipe.xerminadores.service.MedicoService;
import equipe.xerminadores.service.PacienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/agendamentos")
@RequiredArgsConstructor
public class AgendaController {

    private final AgendaService agendaService;
    private final PacienteService pacienteService;
    private final MedicoService medicoService;
    private AgendaRepository agendaRepository;

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
    public String salvar(@ModelAttribute("agenda") Agenda agenda, RedirectAttributes redirectAttributes) {
        try {
            agendaService.salvar(agenda);
            redirectAttributes.addFlashAttribute("mensagem", "Agendamento salvo com sucesso!");
            redirectAttributes.addFlashAttribute("tipoMensagem", "sucesso");
            return "redirect:/agendamentos";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagem", "Erro ao salvar agendamento: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensagem", "erro");
            // Redireciona conforme for novo ou edição
            if (agenda.getId() == null) {
                return "redirect:/agendamentos/novo";
            } else {
                return "redirect:/agendamentos/editar/" + agenda.getId();
            }
        }
    }

    @GetMapping("/horarios-disponiveis") // <- apenas isso
    @ResponseBody
    public List<String> buscarHorariosDisponiveis(
            @RequestParam Long medicoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return agendaService.gerarHorariosDisponiveis(data, medicoId);
    }


    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        var agenda = agendaService.buscarPorId(id).orElseThrow();

        // Formata o horario para string e passa como atributo separado
        String horarioFormatado = agenda.getHorario() != null
                ? agenda.getHorario().format(DateTimeFormatter.ofPattern("HH:mm"))
                : "";

        model.addAttribute("agenda", agenda);
        model.addAttribute("pacientes", pacienteService.listarTodos());
        model.addAttribute("medicos", medicoService.listarTodos());
        model.addAttribute("horarioSelecionado", horarioFormatado);

        List<String> horariosDisponiveis = agendaService.gerarHorariosDisponiveis(agenda.getData(), agenda.getMedico().getId());
        model.addAttribute("horariosDisponiveis", horariosDisponiveis);

        return "agendamentos/cadastro";
    }


    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            agendaService.excluir(id);
            redirectAttributes.addFlashAttribute("mensagem", "Agendamento excluído com sucesso!");
            redirectAttributes.addFlashAttribute("tipoMensagem", "sucesso");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagem", "Erro ao excluir agendamento: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensagem", "erro");
        }
        return "redirect:/agendamentos";
    }
}
