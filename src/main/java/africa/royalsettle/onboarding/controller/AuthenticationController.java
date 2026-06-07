package africa.royalsettle.onboarding.controller;

import africa.royalsettle.common.anotations.WrapResponse;
import africa.royalsettle.onboarding.dto.*;
import africa.royalsettle.onboarding.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@WrapResponse
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Login, refresh-token, and logout operations")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    @Operation(summary = "Log in", description = "Authenticates a customer and creates a token session.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Authentication successful",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Missing credentials"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public LoginResponse login(@RequestBody @Valid LoginRequest request) {
        return authenticationService.login(request);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh token pair", description = "Rotates a valid refresh token and returns a new token pair.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Token pair refreshed",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid, expired, or replayed refresh token")
    })
    public LoginResponse refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return authenticationService.refreshToken(request);
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Log out",
            description = "Revokes the active token session.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Logout successful",
                    content = @Content(schema = @Schema(implementation = LogoutResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid refresh token"),
            @ApiResponse(responseCode = "401", description = "Access token is missing or invalid")
    })
    public LogoutResponse logout(
            @Parameter(hidden = true) HttpServletRequest request,
            @Valid @RequestBody LogoutRequest logoutRequest
    ) {
        return authenticationService.logout(request, logoutRequest);
    }
}
