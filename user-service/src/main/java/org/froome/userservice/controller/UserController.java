package org.froome.userservice.controller;

import org.froome.userservice.exception.ForbiddenException;
import org.froome.userservice.model.dto.ExceptionDto;
import org.froome.userservice.model.dto.UserDto;
import org.froome.userservice.service.AuthService;
import org.froome.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "User", description = "Specific to users, manages the registration, login, update and deletion of users.")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "There is no need to be authenticated to register a new user.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User created"),
                    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "409", description = "Conflict", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            }
    )
    public ResponseEntity<UserDto> register(@Valid @RequestBody UserDto userDto, Authentication authentication) {
        String token = authentication != null ? authentication.getPrincipal().toString() : null;
        if (userService.countUsers() > 0 && userDto.isAdmin() && (token == null || authService.isNotAdmin(token))) {
            throw new ForbiddenException("Only an admin can create another admin user.");
        }

        UserDto createdUser = userService.register(userDto);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login a user",
            description = "There is no need to be authenticated to login a user. The token returned is used to authenticate the user in the future.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User logged in"),
                    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            }
    )
    public ResponseEntity<String> login(
            @Parameter(description = "Email of the user", required = true)
            @Email @RequestParam String email,
            @Parameter(description = "Password of the user", required = true)
            @RequestParam String password) {
        String token = userService.login(email, password);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @GetMapping
    @Operation(
            summary = "Get all users",
            description = "Only an admin can get all users.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<List<UserDto>> getUsers(Authentication authentication) {
        String token = authentication.getPrincipal().toString();
        if (authService.isNotAdmin(token)) {
            throw new ForbiddenException("You are not allowed to access this resource.");
        } else {
            List<UserDto> users = userService.getUsers();
            return new ResponseEntity<>(users, HttpStatus.OK);
        }
    }

    @GetMapping("/me")
    @Operation(
            summary = "Get the authenticated user",
            description = "The user must be authenticated to get the authenticated user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<UserDto> getMe(Authentication authentication) {
        String token = authentication.getPrincipal().toString();
        UserDto user = userService.getUser(authService.getUserIdFromToken(token));
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get a user by id",
            description = "The user must be authenticated to get a user by id.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found"),
                    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<UserDto> getUser(
            @Parameter(description = "ID of the user to be retrieved", required = true)
            @PathVariable long id,
            Authentication authentication) {
        String token = authentication.getPrincipal().toString();
        if (authService.isNotAdminOrSameUser(token, id)) {
            throw new ForbiddenException("You are not allowed to access this resource.");
        } else {
            UserDto user = userService.getUser(id);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a user",
            description = "The user must be authenticated to update a user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User updated"),
                    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<UserDto> updateUser(
            @Parameter(description = "ID of the user to be updated", required = true)
            @PathVariable long id,
            @Valid @RequestBody UserDto userDto,
            Authentication authentication) {
        String token = authentication.getPrincipal().toString();
        if (authService.isNotAdminOrSameUser(token, id)) {
            throw new ForbiddenException("You are not allowed to access this resource.");
        } else {
            UserDto updatedUser = userService.updateUser(id, userDto);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a user",
            description = "The user must be authenticated to delete a user.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "User deleted"),
                    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID of the user to be deleted", required = true)
            @PathVariable long id,
            Authentication authentication) {
        String token = authentication.getPrincipal().toString();
        if (authService.isNotAdminOrSameUser(token, id)) {
            throw new ForbiddenException("You are not allowed to access this resource.");
        } else {
            userService.deleteUser(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}

