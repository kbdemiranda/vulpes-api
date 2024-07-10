package io.github.vulpes.applications.controller;

import io.github.vulpes.applications.dto.LoginDTO;
import io.github.vulpes.domain.models.Usuario;
import io.github.vulpes.infrastructure.security.AuthResponse;
import io.github.vulpes.infrastructure.security.TokenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "APIs para autenticação de usuários")
public class AuthController {

    private final AuthenticationManager authManager;
    private final TokenService tokenService;

    public AuthController(AuthenticationManager authManager, TokenService tokenService) {
        this.authManager = authManager;
        this.tokenService = tokenService;
    }

//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody @Valid LoginDTO dto){
//        var token = new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha());
//        var authentication = authManager.authenticate(token);
//
//        var tokenJWT = tokenService.generateToken((Usuario) authentication.getPrincipal());
//
//        return ResponseEntity.ok(tokenJWT);
//    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dto) {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = tokenService.generateToken((Usuario) authentication.getPrincipal());
            long expiresIn = tokenService.getExpirationTime().getEpochSecond();
            return ResponseEntity.ok(new AuthResponse(token, expiresIn));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


}
