package com.brand.practica_banco.Controller;

import com.brand.practica_banco.Entity.Cuenta;
import com.brand.practica_banco.Entity.CuentaDto;
import com.brand.practica_banco.Exceptions.RecursoNoEncontradoException;
import com.brand.practica_banco.Service.CuentaService;
import jakarta.validation.Valid;
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
        Cuenta cuenta = new Cuenta(null, cuentaDto.getNombre(), cuentaDto.getSaldo());
        Cuenta guardado = service.guardar(cuenta);
        return ResponseEntity.created(URI.create("/api/banco/cuenta/" + guardado.getId())).body(guardado);
    }

    //Editar
    @PutMapping("{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @Valid @RequestBody CuentaDto cuentaDto){
        Cuenta cuenta = service.buscar(id);
        cuenta.setNombre(cuentaDto.getNombre());
        cuenta.setSaldo(cuentaDto.getSaldo());
        return ResponseEntity.ok(service.editar(cuenta));
    }

    //Buscar por Id
    @GetMapping("{id}")
    public ResponseEntity<Cuenta> buscarPorId(@PathVariable Long id){
        Cuenta cuenta = service.buscar(id);
        return ResponseEntity.ok(cuenta);
    }

    //Eliminar
    @DeleteMapping("{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id){
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    //Buscar por nombre
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<?> buscarPorNombre(@PathVariable String nombre) {
        Cuenta cuenta = service.buscarPorNombre(nombre);
        return ResponseEntity.ok(cuenta);
    }
}
