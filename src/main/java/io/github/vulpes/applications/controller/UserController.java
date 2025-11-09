package io.github.vulpes.applications.controller;

import io.github.vulpes.applications.dto.UserDTO;
import io.github.vulpes.applications.service.UserService;
import io.github.vulpes.infrastructure.http.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "APIs for user management")
@SecurityRequirement(name = "bearer-key")
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Transactional
    @Operation(summary = "Create user", description = "Creates a new user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO dto){
        UserDTO userDTO = userService.createUser(dto);
        return ResponseEntity.created(URI.create("/users/" + userDTO.getId())).body(userDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves a user by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> getUserById(@Parameter(description = "User ID", example = "1") @PathVariable Long id){
        UserDTO userDTO = userService.getUserById(id);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping
    @Operation(summary = "Get user by email", description = "Retrieves a user by email address")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> getUserByEmail(@Parameter(description = "User email", example = "user@example.com") @RequestParam String email){
        UserDTO userDTO = userService.getUserByEmail(email);
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("/{id}/password")
    @Transactional
    @Operation(summary = "Update user password", description = "Updates the password for a user")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Password updated", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> updatePassword(@Parameter(description = "User ID", example = "1") @PathVariable Long id,
                                            @Valid @RequestBody UserDTO dto){
        userService.updatePassword(id, dto.getPassword());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/profiles")
    @Transactional
    @Operation(summary = "Update user profiles", description = "Updates the profiles for a user")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Profiles updated", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> updateProfiles(@Parameter(description = "User ID", example = "1") @PathVariable Long id,
                                             @Valid @RequestBody UserDTO dto){
        userService.updateProfiles(id, dto.getProfilesId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "Update user", description = "Updates data of an existing user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User updated",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> updateUser(@Parameter(description = "User ID", example = "1") @PathVariable Long id,
                                              @Valid @RequestBody UserDTO dto){
        UserDTO userDTO = userService.updateUser(id, dto);
        return ResponseEntity.created(URI.create("/users/" + userDTO.getId())).body(userDTO);
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "Delete user", description = "Removes a user by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> deleteUser(@Parameter(description = "User ID", example = "1") @PathVariable Long id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
