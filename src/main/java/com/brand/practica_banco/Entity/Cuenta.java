package com.brand.practica_banco.Entity;
import jakarta.validation.constraints.*;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cuentas")
@EntityListeners(AuditingEntityListener.class)  //Para auditorias
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100, unique = true)
    @NotBlank(message = "El nombre no puede estar vacío.")
    @Size(max = 100, message = "El nombre no puede tener mas de 100 caracteres.")
    private String nombre;

    @Column(nullable = false, precision = 15, scale = 2)
    @NotNull(message = "El saldo no puede ser nulo.")
    @DecimalMin(value = "0.0", inclusive = true, message = "El saldo no puede ser negativo.")
    @DecimalMax(value = "10000000.00", message = "El saldo no puede exceder $10,000,000.00")
    private BigDecimal saldo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    @CreatedDate
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Builder
    public Cuenta(Long id, String nombre, BigDecimal saldo){
        this.id = id;
        this.nombre = nombre;
        this.saldo = saldo;
        //Los campos de auditoria (fechaCreacion, fechaActualizacion) NO se incluye aquí.
    }
}
