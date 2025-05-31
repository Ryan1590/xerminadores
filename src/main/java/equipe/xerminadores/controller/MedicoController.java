package equipe.xerminadores.controller;

import equipe.xerminadores.exception.DuplicateCrmException;
import equipe.xerminadores.model.Medico;
import equipe.xerminadores.service.MedicoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/medicos")
public class MedicoController {

    private final MedicoService medicoService;

    public MedicoController(MedicoService medicoService) {
        this.medicoService = medicoService;
    }

    // Listar médicos para HTML
    @GetMapping
    public String listarMedicos(Model model) {
        List<Medico> medicos = medicoService.listarTodos();
        model.addAttribute("medicos", medicos);
        return "medicos/listar";
    }

    // Mostrar formulário para cadastrar novo médico
    @GetMapping("/novo")
    public String mostrarFormularioCadastro(Model model) {
        model.addAttribute("medico", new Medico());
        return "medicos/cadastro";  // mesma view para cadastro
    }

    // Mostrar formulário para editar médico
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicao(@PathVariable Long id, Model model) {
        Medico medico = medicoService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));
        model.addAttribute("medico", medico);
        return "medicos/cadastro";  // mesma view para edição
    }

    @PostMapping
    public String salvarMedico(@ModelAttribute Medico medico, RedirectAttributes redirectAttributes) {
        try {
            medicoService.salvar(medico);
            redirectAttributes.addFlashAttribute("mensagem", "Médico salvo com sucesso!");
            redirectAttributes.addFlashAttribute("tipoMensagem", "sucesso");
            return "redirect:/medicos";
        } catch (DuplicateCrmException e) {
            // CRM já existe: volta para o formulário e exibe o erro
            redirectAttributes.addFlashAttribute("mensagem", e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensagem", "erro");
            // Redireciona para novo ou edição, conforme venha o id
            if (medico.getId() == null) {
                return "redirect:/medicos/novo";
            } else {
                return "redirect:/medicos/editar/" + medico.getId();
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagem", "Erro ao salvar médico: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensagem", "erro");
            return "redirect:/medicos";
        }
    }

    @GetMapping("/deletar/{id}")
    public String deletarMedico(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            medicoService.deletar(id);
            redirectAttributes.addFlashAttribute("mensagem", "Médico deletado com sucesso!");
            redirectAttributes.addFlashAttribute("tipoMensagem", "sucesso");
        } catch (IllegalStateException e) {
            // Exceção lançada caso médico esteja vinculado a algum agendamento
            redirectAttributes.addFlashAttribute("mensagem", "Não é possível deletar o médico pois ele possui agendamentos vinculados.");
            redirectAttributes.addFlashAttribute("tipoMensagem", "erro");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagem", "Erro ao deletar médico: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensagem", "erro");
        }
        return "redirect:/medicos";
    }
}
