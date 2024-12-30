package br.com.nazasoftapinfe.entitiy;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

import javax.persistence.*;

@Data
@Table(name = "logs")
@Entity
public class LogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String level; // INFO, WARN, ERROR
    private String message;
    private LocalDateTime timestamp;

    @Column(columnDefinition = "TEXT")
    private String exception; // Detalhes da exceç

    private String ip;

    private String clientIp;

}
