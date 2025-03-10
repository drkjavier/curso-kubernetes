package org.aguzman.springcloud.msvc.cursos.msvc_cursos.repository;

import org.aguzman.springcloud.msvc.cursos.msvc_cursos.models.entity.Curso;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CursoRepository extends CrudRepository<Curso, Long> {


    @Query("delete from CursoUsuario c where c.id = ?1")
    @Modifying
    void eliminarCursoUsuarioPorId(Long id);


}
