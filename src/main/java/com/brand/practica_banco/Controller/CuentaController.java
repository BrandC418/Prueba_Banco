package com.brand.practica_banco.Controller;

import com.brand.practica_banco.Entity.Cuenta;
import com.brand.practica_banco.Service.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/banco/cuenta")
public class CuentaController {

    @Autowired
    private CuentaService service;

    //url: http://localhost:8080/api/banco/cuenta

    //Listar
    @GetMapping
    public ResponseEntity<?> listar(){
        List<Cuenta> cuentas = service.listar();
        if(cuentas.isEmpty()){
            return ResponseEntity.noContent().build();
        }else {
            return ResponseEntity.ok(cuentas);
        }
    }

    //Guardar
    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody Cuenta cuenta){
        Optional<Cuenta> encontrado = service.buscarPorNombre(cuenta.getNombre());
        if(encontrado.isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"mensaje\":\"La cuenta con nombre: " + cuenta.getNombre()
                    + " ya existe, Prueba guardando otra cuenta con otro nombre diferente.\"}");
        }else{
            Cuenta guardado = service.guardar(cuenta);
            return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
        }
    }

    //Editar
    @PutMapping
    public ResponseEntity<?> editar(@RequestBody Cuenta cuenta){
        Optional<Cuenta> encontrado = service.buscar(cuenta.getId());
        if(encontrado.isPresent()){
            Cuenta actualizado = service.editar(cuenta);
            return ResponseEntity.ok(actualizado);
        }else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"mensaje\":\"La cuenta con id: "
                      + cuenta.getId() + " no ha sido localizada.\"}");
    }

    //Buscar por Id
    @GetMapping("{id}")
    public ResponseEntity<Cuenta> buscarPorId(@PathVariable Long id){
        Optional<Cuenta> encontrado = service.buscar(id);
        if(encontrado.isPresent()) {
            return ResponseEntity.ok(encontrado.get());
        }else
            return ResponseEntity.notFound().build();
    }

    //Eliminar
    @DeleteMapping("{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id){
        Optional<Cuenta> encontrado = service.buscar(id);
        if(encontrado.isPresent()){
            service.eliminar(id);
            return ResponseEntity.ok("{\"mensaje\":\"La cuenta con id: " + id + " ha sido eliminada.\"}");
        }else
            return ResponseEntity.notFound().build();
    }

    //Buscar por nombre
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<?> buscarPorNombre(@PathVariable String nombre){
        Optional<Cuenta> byNombre = service.buscarPorNombre(nombre);
        if(byNombre.isPresent()){
            Cuenta encontrado = byNombre.get();
            return ResponseEntity.ok(encontrado);
        }else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"mensaje\":\"La cuenta con nombre: "
                    + nombre + " no se encuentra.\"}");
    }
}
