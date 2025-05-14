package org.aguzman.springcloud.msvc.auth.services;

import org.aguzman.springcloud.msvc.auth.models.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.text.MessageFormat;
import java.util.Collections;

@Service
public class UsuarioService implements UserDetailsService {

    private static final String USERNAME_NOT_FOUND_MSG = "Error en el login, no existe el usuario {0} en el sistema";

    @Autowired
    private WebClient.Builder webClient;

    private Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("load user by username {}", email);
        try {
            final Usuario usuario = webClient.build().get().uri("http://msvc-usuarios/login", uriBuilder -> uriBuilder.queryParam("email", email).build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(Usuario.class).block();
            logger.info("usuario login: {}", usuario.getEmail());
            return new User(email, usuario.getPassword(), true, true, true,
                    true, Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));

        } catch (Exception e) {
            logger.error(MessageFormat.format("Se presento un error al hacer login, mensaje: {0}", e.getMessage()), e);
            throw new UsernameNotFoundException(MessageFormat.format(USERNAME_NOT_FOUND_MSG, email));
        }
    }
}
