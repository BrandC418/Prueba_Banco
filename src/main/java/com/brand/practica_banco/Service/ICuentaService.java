package com.brand.practica_banco.Service;

import com.brand.practica_banco.Entity.Cuenta;

import java.util.List;
import java.util.Optional;

public interface ICuentaService {

    public Cuenta guardar(Cuenta cuenta);

    public Cuenta editar(Cuenta cuenta);

    public List<Cuenta> listar();

    public Optional<Cuenta> buscar(Long id);

    public void eliminar(Long id);

    public Optional<Cuenta> buscarPorNombre(String nombre);
}
