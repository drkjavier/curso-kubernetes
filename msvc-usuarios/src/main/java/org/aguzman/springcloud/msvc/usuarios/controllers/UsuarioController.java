package org.aguzman.springcloud.msvc.usuarios.controllers;

import org.aguzman.springcloud.msvc.usuarios.models.entity.Usuario;
import org.aguzman.springcloud.msvc.usuarios.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.MessageFormat;
import java.util.*;

@RestController
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ApplicationContext context;

    @GetMapping("/crash")
    public void crash() {
        ((ConfigurableApplicationContext) context).close();
    }

    @GetMapping
    public Map<String, List<Usuario>> listar() {
        return Collections.singletonMap("users", usuarioService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> porId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.porId(id).orElse(null));
    }

    @PostMapping
    public ResponseEntity guardar(@Valid @RequestBody Usuario usuario, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validar(bindingResult);
        }
        if (!usuario.getEmail().isEmpty() && usuarioService.existeEmail(usuario.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("error", "El email ya esta en uso"));

        }
        return ResponseEntity.ok(usuarioService.guardar(usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @Valid @RequestBody final Usuario usuario, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validar(bindingResult);
        }
        final Optional<Usuario> usuarioOp = usuarioService.porId(id);
        if (usuarioOp.isPresent()) {
            Usuario entity = usuarioOp.get();
            if (!usuario.getEmail().isEmpty() && !usuario.getEmail().equalsIgnoreCase(entity.getEmail()) &&
                    usuarioService.porEmail(usuario.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El email ya esta en uso"));
            }
            entity.setNombre(usuario.getNombre());
            entity.setEmail(usuario.getEmail());
            entity.setPassword(usuario.getPassword());
            return ResponseEntity.ok(usuarioService.guardar(entity));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        Optional<Usuario> usuarioOp = usuarioService.porId(id);
        if (usuarioOp.isPresent()) {
            Usuario usuario = usuarioOp.get();
            usuarioService.eliminar(usuario.getId());
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("usuarios-por-curso")
    public ResponseEntity<?> obtenerAlumnosPorCurso(@RequestParam List<Long> ids) {
        return ResponseEntity.ok(usuarioService.listarPorIds(ids));
    }

    private ResponseEntity<Map<String, String>> validar(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), MessageFormat
                .format("El campo {0} {1}", error.getField(), error.getDefaultMessage())));
        return ResponseEntity.badRequest().body(errors);
    }

}
