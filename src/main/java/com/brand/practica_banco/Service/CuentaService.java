package com.brand.practica_banco.Service;

import com.brand.practica_banco.Entity.Cuenta;
import com.brand.practica_banco.Repository.CuentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CuentaService implements ICuentaService{

    @Autowired
    private CuentaRepository repository;

    @Override
    public Cuenta guardar(Cuenta cuenta) {
        return repository.save(cuenta);
    }

    @Override
    public Cuenta editar(Cuenta cuenta) {
        return repository.save(cuenta);
    }

    @Override
    public List<Cuenta> listar() {
        return repository.findAll(Sort.by(Sort.Direction.ASC,"id"));
    }

    @Override
    public Optional<Cuenta> buscar(Long id) {
        return repository.findById(id);
    }

    @Override
    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Optional<Cuenta> buscarPorNombre(String nombre) {
        return repository.findByNombre(nombre);
    }
}
