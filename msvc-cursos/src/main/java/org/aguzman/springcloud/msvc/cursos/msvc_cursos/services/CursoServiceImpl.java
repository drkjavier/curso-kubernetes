package org.aguzman.springcloud.msvc.cursos.msvc_cursos.services;

import org.aguzman.springcloud.msvc.cursos.msvc_cursos.clients.UsuarioClientRest;
import org.aguzman.springcloud.msvc.cursos.msvc_cursos.models.Usuario;
import org.aguzman.springcloud.msvc.cursos.msvc_cursos.models.entity.Curso;
import org.aguzman.springcloud.msvc.cursos.msvc_cursos.models.entity.CursoUsuario;
import org.aguzman.springcloud.msvc.cursos.msvc_cursos.repository.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class CursoServiceImpl implements CursoService {

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private UsuarioClientRest usuarioClientRest;

    @Override
    @Transactional(readOnly = true)
    public List<Curso> listar() {
        return StreamSupport.stream(cursoRepository.findAll().spliterator(), false).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Curso> porId(Long id) {
        return cursoRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Curso> porIdConUsuarios(Long id) {
        Optional<Curso> optCurso = cursoRepository.findById(id);
        if (optCurso.isPresent()) {
            Curso curso = optCurso.get();
            if (!curso.getCursoUsuarios().isEmpty()) {
                final List<Long> ids = curso.getCursoUsuarios().stream().map(CursoUsuario::getUsuarioId).toList();
                curso.setUsuarios(usuarioClientRest.obtenerAlumnosPorCurso(ids));
            }
            return Optional.of(curso);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public Curso guardar(Curso curso) {
        return cursoRepository.save(curso);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        cursoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void eliminarCursoUsuarioPorId(Long id) {
        cursoRepository.eliminarCursoUsuarioPorId(id);
    }

    @Override
    @Transactional
    public Optional<Usuario> asignarUsuario(Usuario usuario, Long cursoId) {
        Optional<Curso> optCurso = cursoRepository.findById(cursoId);
        if (optCurso.isPresent()) {
            Usuario usuarioMsvc = usuarioClientRest.detalle(usuario.getId());
            Curso curso = optCurso.get();
            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usuarioMsvc.getId());
            curso.addCursoUsuario(cursoUsuario);
            cursoRepository.save(curso);
            return Optional.of(usuarioMsvc);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<Usuario> crearUsuario(Usuario usuario, Long cursoId) {
        Optional<Curso> optCurso = cursoRepository.findById(cursoId);
        if (optCurso.isPresent()) {
            final Usuario usuarioMsvc = usuarioClientRest.crear(usuario);
            Curso curso = optCurso.get();
            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usuarioMsvc.getId());
            curso.addCursoUsuario(cursoUsuario);
            cursoRepository.save(curso);
            return Optional.of(usuarioMsvc);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<Usuario> eliminarUsuario(Long usuarioId, Long cursoId) {
        Optional<Curso> optCurso = cursoRepository.findById(cursoId);
        if (optCurso.isPresent()) {
            final Usuario usuarioMsvc = usuarioClientRest.detalle(usuarioId);
            Curso curso = optCurso.get();
            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usuarioMsvc.getId());
            curso.removeCursoUsuario(cursoUsuario);
            cursoRepository.save(curso);
            return Optional.of(usuarioMsvc);
        }
        return Optional.empty();
    }
}
