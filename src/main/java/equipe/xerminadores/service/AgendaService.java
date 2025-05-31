package equipe.xerminadores.service;

import equipe.xerminadores.model.Agenda;
import equipe.xerminadores.repository.AgendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    public List<String> gerarHorariosDisponiveis(LocalDate data, Long medicoId) {
        List<String> todosHorarios = new ArrayList<>();
        LocalTime inicio = LocalTime.of(8, 0);
        LocalTime fim = LocalTime.of(18, 0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        while (!inicio.isAfter(fim.minusMinutes(30))) {
            todosHorarios.add(inicio.format(formatter));
            inicio = inicio.plusMinutes(30);
        }

        // Consulta corretamente os agendamentos existentes
        List<Agenda> agendamentos = repository.findByMedicoIdAndData(medicoId, data);

        // Formata corretamente os horários agendados para comparação
        Set<String> ocupados = agendamentos.stream()
                .map(a -> a.getHorario().format(formatter)) // Aqui está o ponto certo!
                .collect(Collectors.toSet());

        // Remove os horários ocupados
        return todosHorarios.stream()
                .filter(h -> !ocupados.contains(h))
                .collect(Collectors.toList());
    }

    public boolean existeConflito(Agenda agenda) {
        List<Agenda> agendasDoMesmoHorario = listarTodos().stream()
                .filter(a ->
                        a.getMedico().getId().equals(agenda.getMedico().getId()) &&
                                a.getData().equals(agenda.getData()) &&
                                a.getHorario().equals(agenda.getHorario()) &&
                                !a.getId().equals(agenda.getId()) // evita falso positivo na edição
                )
                .toList();

        return !agendasDoMesmoHorario.isEmpty();
    }

    public boolean horarioValido(LocalTime horario) {
        LocalTime inicio = LocalTime.of(8, 0);
        LocalTime fim = LocalTime.of(18, 0);

        if (horario.isBefore(inicio) || horario.isAfter(fim)) {
            return false;
        }

        int minuto = horario.getMinute();
        // valida se é múltiplo de 30 minutos (0 ou 30)
        return minuto == 0 || minuto == 30;
    }

    public boolean horarioNoPassadoOuEmAndamento(LocalDate data, LocalTime horario) {
        LocalDateTime dataHoraAgendada = LocalDateTime.of(data, horario);
        LocalDateTime agora = LocalDateTime.now();

        // Considera que um agendamento começa exatamente no horário marcado,
        // então se já passou desse horário, não pode agendar
        return !dataHoraAgendada.isAfter(agora);
    }
}
