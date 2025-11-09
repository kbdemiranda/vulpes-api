package io.github.vulpes.applications.service.impl;

import io.github.vulpes.applications.dto.UserDTO;
import io.github.vulpes.applications.service.UserService;
import io.github.vulpes.domain.models.Profile;
import io.github.vulpes.domain.models.Usuario;
import io.github.vulpes.infrastructure.exceptions.VulpesException;
import io.github.vulpes.infrastructure.jpa.ProfileRepository;
import io.github.vulpes.infrastructure.jpa.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, ProfileRepository profileRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UserDTO createUser(UserDTO dto) {
        List<Profile> profiles = getProfiles(dto.getProfilesId());

        Usuario user = Usuario.builder()
                .nome(dto.getNome())
                .sobrenome(dto.getSobrenome())
                .email(dto.getEmail())
                .senha(passwordEncoder.encode(dto.getPassword()))
                .perfis(profiles)
                .cadastradoEm(LocalDateTime.now())
                .build();

        Usuario savedUser = userRepository.save(user);

        return new UserDTO(savedUser);
    }

    @Override
    public UserDTO getUserById(Long id) {
        Usuario user = getUser(id);
        return new UserDTO(user);
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        Usuario user = getUser(email);
        return new UserDTO(user);
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO dto) {
        Usuario user = getUser(id);
        user.setNome(dto.getNome());
        user.setSobrenome(dto.getSobrenome());
        user.setEmail(dto.getEmail());
        user.setAtualizadoEm(LocalDateTime.now());

        Usuario updatedUser = userRepository.save(user);
        return new UserDTO(updatedUser);
    }

    @Override
    public void updatePassword(Long id, String password) {
        Usuario user = getUser(id);
        user.setSenha(passwordEncoder.encode(password));
        user.setAtualizadoEm(LocalDateTime.now());

        userRepository.save(user);
    }

    @Override
    public void updateProfiles(Long id, List<Long> profiles) {
        Usuario user = getUser(id);
        user.setPerfis(getProfiles(profiles));

        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        Usuario user = getUser(id);
        userRepository.deleteUsuario(user.getId());
    }

    private Usuario getUser(String email){
        return userRepository.
                findByEmail(email)
                .orElseThrow(() -> new VulpesException(404, "User not found"));
    }

    private Usuario getUser(Long id){
        return userRepository
                .findById(id)
                .orElseThrow(() -> new VulpesException(404, "User not found"));
    }

    private List<Profile> getProfiles(List<Long> profileIds){
        return profileRepository.findAllById(profileIds);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getUser(username);
    }
}
