package com.brand.practica_banco.Service;

import com.brand.practica_banco.Entity.Cuenta;
import com.brand.practica_banco.Exceptions.RecursoNoEncontradoException;
import com.brand.practica_banco.Repository.CuentaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CuentaService implements ICuentaService{

    private final CuentaRepository repository;

    public CuentaService(CuentaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Cuenta guardar(Cuenta cuenta) {
        if(repository.findByNombre(cuenta.getNombre()).isPresent()){
            throw new IllegalArgumentException("El nombre de la cuenta ya existe. Prueba con otro.");
        }
        return repository.save(cuenta);
    }

    @Override
    @Transactional
    public Cuenta editar(Cuenta cuenta) {
        if(!repository.existsById(cuenta.getId())){
            throw new RecursoNoEncontradoException("Cuenta con ID " + cuenta.getId() + " no encontrada.");
        }
        return repository.save(cuenta);
    }

    @Override
    public List<Cuenta> listar() {
        return repository.findAll(Sort.by(Sort.Direction.ASC,"id"));
    }

    @Override
    public Cuenta buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta con ID " + id + " no encontrada."));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if(!repository.existsById(id)){
            throw new RecursoNoEncontradoException("Cuenta con ID " + id + " no encontrada.");
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Cuenta buscarPorNombre(String nombre) {
        return repository.findByNombre(nombre)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta con nombre " + nombre + " no encontrada."));
    }
}
