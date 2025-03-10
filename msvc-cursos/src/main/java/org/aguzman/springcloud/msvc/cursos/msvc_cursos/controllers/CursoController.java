package org.aguzman.springcloud.msvc.cursos.msvc_cursos.controllers;

import org.aguzman.springcloud.msvc.cursos.msvc_cursos.models.Usuario;
import org.aguzman.springcloud.msvc.cursos.msvc_cursos.models.entity.Curso;
import org.aguzman.springcloud.msvc.cursos.msvc_cursos.services.CursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class CursoController {

    @Autowired
    private CursoService service;

    @GetMapping
    public ResponseEntity<List<Curso>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Curso> porId(@PathVariable Long id) {
        final Optional<Curso> cursoOp = service.porIdConUsuarios(id);
        if (cursoOp.isPresent()) {
            return ResponseEntity.ok(cursoOp.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Curso curso, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validar(bindingResult);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(curso));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @Valid @RequestBody Curso curso, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validar(bindingResult);
        }
        final Optional<Curso> cursoOp = service.porId(id);
        if (cursoOp.isPresent()) {
            Curso entity = cursoOp.get();
            entity.setNombre(curso.getNombre());
            return ResponseEntity.ok(service.guardar(entity));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Curso> eliminar(@PathVariable Long id) {
        final Optional<Curso> cursoOp = service.porId(id);
        if (cursoOp.isPresent()) {
            service.eliminar(cursoOp.get().getId());
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/asignar-usuario/{cursoId}")
    public ResponseEntity<?> asignarUsuario(@PathVariable Long cursoId, @Valid @RequestBody Usuario usuario) {
        Optional<Usuario> usuarioOp;
        try {
           usuarioOp = service.asignarUsuario(usuario, cursoId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", MessageFormat
                    .format("No existe el usuario por id o error en la comunicacion: {0}", e.getMessage())));
        }
        if (usuarioOp.isPresent()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioOp.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/crear-usuario/{cursoId}")
    public ResponseEntity<?> crearUsuario(@PathVariable Long cursoId, @Valid @RequestBody Usuario usuario) {
        Optional<Usuario> usuarioOp;
        try {
           usuarioOp = service.crearUsuario(usuario, cursoId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", MessageFormat
                    .format("No se pudo crear el usuario o error en la comunicacion: {0}", e.getMessage())));
        }
        if (usuarioOp.isPresent()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioOp.get());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/eliminar-usuario/{cursoId}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long cursoId, @Valid @RequestBody Usuario usuario) {
        Optional<Usuario> usuarioOp;
        try {
            usuarioOp = service.eliminarUsuario(usuario.getId(), cursoId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", MessageFormat
                    .format("No existe el usuario por id o error en la comunicacion: {0}", e.getMessage())));
        }
        if (usuarioOp.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(usuarioOp.get());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/eliminar-curso-usuario/{id}")
    public ResponseEntity<?> eliminarCursoUsuarioPorId(@PathVariable Long id) {
        service.eliminarCursoUsuarioPorId(id);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<Map<String, String>> validar(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), MessageFormat
                .format("El campo {0} {1}", error.getField(), error.getDefaultMessage())));
        return ResponseEntity.badRequest().body(errors);
    }

}
