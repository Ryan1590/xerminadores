package equipe.xerminadores.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "medicos")
@Data
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    @Column(nullable = false, unique = true)
    private String crm;
    private String especialidade;
}
