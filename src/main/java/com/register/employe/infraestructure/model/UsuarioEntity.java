package com.register.employe.infraestructure.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "USUARIO", schema = "DB_REGISTER_EMPLOYE")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String idUsuario;
    private String nombres;
    private String apellidos;
    private String usuario;
    private String clave;
    private String correoElectronico;
    private String imagenPerfil;
    private String imagenPerfilUrl;
    private LocalDateTime ultimaFechaIngreso;
    private LocalDateTime ultimaFechaIngresoVisualizacion;

    @Column(name = "FECHA_REGISTRO", columnDefinition = "TIMESTAMP")
    private LocalDateTime fechaRegistro;
    private String role; // ROLE_USER{ update, create (READ, EDIT) }, ROLE_ADMIN{ DELETE }
    private String[] autorizacion; // ROLE_USER{ delete, update, create }, ROLE_ADMIN
    private Boolean snActivo;
    private Boolean snBloqueado;

}
