package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "hits")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EndpointHit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "app")
    private String app;

    @NotBlank
    @Column(name = "uri")
    private String uri;

    @NotBlank
    @Column(name = "ip")
    private String ip;

    @NotNull
    @Column(name = "timestamp")
    private LocalDateTime timestamp;
}
