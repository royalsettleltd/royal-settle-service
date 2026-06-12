package africa.royalsettle.ajo.controller;

import africa.royalsettle.ajo.dto.AjoResponse;
import africa.royalsettle.ajo.dto.CreateAjoRequest;
import africa.royalsettle.ajo.service.AjoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ajo")
@Tag(name = "Ajo", description = "Ajo creation, invitation, membership, and listing operations")
@SecurityRequirement(name = "bearerAuth")
public class AjoController {

    private final AjoService ajoService;

    @PostMapping("/create")
    @Operation(
            summary = "Create an Ajo",
            description = "Creates an Ajo and adds the authenticated KYC-verified customer as its first member."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Ajo created",
                    content = @Content(schema = @Schema(implementation = AjoResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Validation failed or KYC is incomplete"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public ResponseEntity<AjoResponse> createAjo(@Valid @RequestBody CreateAjoRequest request) {
        return ResponseEntity.ok(ajoService.createAjoWithInitiator(request));
    }

    @GetMapping("/{ajoId}")
    @Operation(summary = "Get an Ajo invitation link")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Invitation link returned"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Ajo not found")
    })
    public ResponseEntity<String> getAjoInvitationLink(
            @Parameter(description = "Database identifier of the Ajo", example = "42")
            @PathVariable Long ajoId
    ) {
        return ResponseEntity.ok(ajoService.getAjoInvitationLink(ajoId));
    }

    @PostMapping("/join")
    @Operation(
            summary = "Join an Ajo",
            description = "Adds the authenticated KYC-verified customer to an Ajo using its invitation code."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer joined the Ajo"),
            @ApiResponse(responseCode = "400", description = "Invalid code, duplicate membership, full Ajo, or incomplete KYC"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public ResponseEntity<String> joinAjo(
            @Parameter(description = "Ajo invitation code", example = "RSAJ-A1B2C3")
            @RequestParam String code
    ) {
        return ResponseEntity.ok(ajoService.joinAjo(code));
    }

    @GetMapping("/getAllMyAjos")
    @Operation(summary = "List the authenticated customer's Ajos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ajos returned"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public ResponseEntity<Page<AjoResponse>> getAllMyAjos(
            @Parameter(
                    description = "One-based page number",
                    example = "1",
                    schema = @Schema(minimum = "1")
            )
            @RequestParam(defaultValue = "1")
            int pageNumber,
            @Parameter(
                    description = "Number of records per page",
                    example = "10",
                    schema = @Schema(minimum = "1", maximum = "100")
            )
            @RequestParam(defaultValue = "10")
            int pageSize
    ) {
        return ResponseEntity.ok(ajoService.getAllMyAjos(pageNumber, pageSize));
    }
}
