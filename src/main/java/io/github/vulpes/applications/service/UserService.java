package io.github.vulpes.applications.service;

import io.github.vulpes.applications.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    UserDTO createUser(UserDTO dto);
    UserDTO getUserById(Long id);
    UserDTO getUserByEmail(String email);
    UserDTO updateUser(Long id, UserDTO dto);

    void updatePassword(Long id, String password);
    void updateProfiles(Long id, List<Long> profiles);
    void deleteUser(Long id);

}
