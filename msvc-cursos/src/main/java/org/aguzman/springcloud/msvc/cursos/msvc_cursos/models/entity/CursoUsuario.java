package org.aguzman.springcloud.msvc.cursos.msvc_cursos.models.entity;

import javax.persistence.*;

@Entity
@Table(name = "cursos_usuarios")
public class CursoUsuario {

    @Id
    @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", unique = true)
    private Long usuarioId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(!(o instanceof CursoUsuario)) return false;
        CursoUsuario that = (CursoUsuario) o;
        return this.usuarioId != null && getId().equals(that.getId());
    }

}
