package com.brand.practica_banco.Repository;

import com.brand.practica_banco.Entity.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

    public Optional<Cuenta> findByNombre(String nombre);

}
