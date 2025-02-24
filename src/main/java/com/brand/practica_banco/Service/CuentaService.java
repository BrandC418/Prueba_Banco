package com.brand.practica_banco.Service;

import com.brand.practica_banco.Entity.Cuenta;
import com.brand.practica_banco.Repository.CuentaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CuentaService implements ICuentaService{

    private final CuentaRepository repository;

    public CuentaService(CuentaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Cuenta guardar(Cuenta cuenta) {
        return repository.save(cuenta);
    }

    @Override
    @Transactional
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
    @Transactional
    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public Optional<Cuenta> buscarPorNombre(String nombre) {
        return repository.findByNombre(nombre);
    }
}
