package com.brand.practica_banco.Controller;

import com.brand.practica_banco.Entity.Cuenta;
import com.brand.practica_banco.Entity.CuentaDto;
import com.brand.practica_banco.Exceptions.RecursoNoEncontradoException;
import com.brand.practica_banco.Service.CuentaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/banco/cuenta")
public class CuentaController {

    private final CuentaService service;

    public CuentaController(CuentaService service){
        this.service = service;
    }

    //url: http://localhost:8080/api/banco/cuenta

    //Listar
    @GetMapping
    public ResponseEntity<?> listar(){
        List<Cuenta> cuentas = service.listar();
        return cuentas.isEmpty() ? ResponseEntity.noContent().build():ResponseEntity.ok(cuentas);
    }

    //Guardar
    @PostMapping
    public ResponseEntity<?> guardar(@Valid @RequestBody CuentaDto cuentaDto){
        if(service.buscarPorNombre(cuentaDto.getNombre()).isPresent()){
            throw new IllegalArgumentException("La cuenta con nombre " + cuentaDto.getNombre() + " ya existe.");
        }
        Cuenta cuenta = new Cuenta(null, cuentaDto.getNombre(), cuentaDto.getSaldo());
        Cuenta guardado = service.guardar(cuenta);
        return ResponseEntity.created(URI.create("/api/banco/cuenta/" + guardado.getId())).body(guardado);
    }

    //Editar
    @PutMapping("{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @Valid @RequestBody CuentaDto cuentaDto){
        Cuenta cuenta = service.buscar(id).orElseThrow(
                () -> new RecursoNoEncontradoException("Cuenta no encontrada")
        );
        cuenta.setNombre(cuentaDto.getNombre());
        cuenta.setSaldo(cuentaDto.getSaldo());
        return ResponseEntity.ok(service.editar(cuenta));
    }

    //Buscar por Id
    @GetMapping("{id}")
    public ResponseEntity<Cuenta> buscarPorId(@PathVariable Long id){
        return service.buscar(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta con id " + id + " no encontrado."));
    }

    //Eliminar
    @DeleteMapping("{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id){
        if(service.buscar(id).isEmpty()){
            throw new RecursoNoEncontradoException("Cuenta con id " + id + " no encontrada.");
        }
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    //Buscar por nombre
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<?> buscarPorNombre(@PathVariable String nombre) {
        return service.buscarPorNombre(nombre)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta con nombre " + nombre + " no encontrada."));
    }
}
