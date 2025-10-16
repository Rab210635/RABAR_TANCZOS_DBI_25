package spengergasse.at.sj2425scherzerrabar.presentation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import spengergasse.at.sj2425scherzerrabar.security.TokenService;

@Tag(name = "auth-rest-controller")
@RestController
@RequestMapping("/api/token")
public class AuthRestController {

    Logger logger = LoggerFactory.getLogger(AuthRestController.class);

    private final TokenService tokenService;

    public AuthRestController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Operation(summary = "Generate a JWT token",
            description = "Generates a JWT token for the currently authenticated user.", security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "text/plain",
                    schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<String> generateToken(Authentication authentication) {
        logger.debug("entered AuthRestController generateToken");
        String token = tokenService.generateToken(authentication);
        return ResponseEntity.ok(token);
    }
}