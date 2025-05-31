package equipe.xerminadores.controller;

import equipe.xerminadores.exception.CpfJaCadastradoException;
import equipe.xerminadores.exception.DataNascimentoInvalidaException;
import equipe.xerminadores.model.Paciente;
import equipe.xerminadores.service.PacienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @GetMapping
    public String listarPacientes(Model model) {
        model.addAttribute("pacientes", pacienteService.listarTodos());
        return "pacientes/listar";
    }

    @GetMapping("/novo")
    public String novoPaciente(Model model) {
        model.addAttribute("paciente", new Paciente());
        return "pacientes/cadastro";
    }

    @GetMapping("/editar/{id}")
    public String editarPaciente(@PathVariable Long id, Model model) {
        Paciente paciente = pacienteService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
        model.addAttribute("paciente", paciente);
        return "pacientes/cadastro";
    }

    @PostMapping
    public String salvarPaciente(@ModelAttribute Paciente paciente, RedirectAttributes redirect) {
        try {
            pacienteService.salvar(paciente);
            redirect.addFlashAttribute("mensagem", "Paciente salvo com sucesso!");
            redirect.addFlashAttribute("tipoMensagem", "sucesso");
        } catch (DataNascimentoInvalidaException e) {
            redirect.addFlashAttribute("mensagem", e.getMessage());
            redirect.addFlashAttribute("tipoMensagem", "erro");
            redirect.addFlashAttribute("paciente", paciente);  // manter os dados do formulário
            return "redirect:/pacientes/novo";
        } catch (CpfJaCadastradoException e) {
            redirect.addFlashAttribute("mensagem", e.getMessage());
            redirect.addFlashAttribute("tipoMensagem", "erro");
            redirect.addFlashAttribute("paciente", paciente);
            return "redirect:/pacientes/novo";
        } catch (Exception e) {
            redirect.addFlashAttribute("mensagem", "Erro ao salvar: " + e.getMessage());
            redirect.addFlashAttribute("tipoMensagem", "erro");
        }
        return "redirect:/pacientes";
    }


    @GetMapping("/deletar/{id}")
    public String deletarPaciente(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            pacienteService.deletar(id);
            redirect.addFlashAttribute("mensagem", "Paciente deletado com sucesso!");
            redirect.addFlashAttribute("tipoMensagem", "sucesso");
        } catch (IllegalStateException e) {
            // Exceção lançada caso paciente esteja vinculado a algum agendamento
            redirect.addFlashAttribute("mensagem", "Não é possível deletar o paciente pois ele possui agendamentos vinculados.");
            redirect.addFlashAttribute("tipoMensagem", "erro");
        } catch (Exception e) {
            redirect.addFlashAttribute("mensagem", "Erro ao deletar: " + e.getMessage());
            redirect.addFlashAttribute("tipoMensagem", "erro");
        }
        return "redirect:/pacientes";
    }

}