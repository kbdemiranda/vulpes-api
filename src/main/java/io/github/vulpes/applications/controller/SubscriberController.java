package io.github.vulpes.applications.controller;

import io.github.vulpes.applications.dto.SubscriberDTO;
import io.github.vulpes.applications.dto.AssociatePlatformsDTO;
import io.github.vulpes.applications.service.SubscriberService;
import io.github.vulpes.infrastructure.http.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import java.net.URI;

@RestController
@RequestMapping("/subscribers")
@Tag(name = "Subscribers", description = "APIs for subscriber management")
@SecurityRequirement(name = "bearer-key")
@Validated
public class SubscriberController {

    private final SubscriberService subscriberService;

    @Autowired
    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @GetMapping
    @Operation(summary = "List subscribers", description = "Returns paginated list of subscribers, with optional name filter")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List returned successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> listSubscribers(
            @Parameter(description = "Filter by name", example = "John") @RequestParam(required = false) String name,
            @Parameter(description = "Page number (0..N)", example = "0")
            @PositiveOrZero @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Items per page", example = "10")
            @Min(1) @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(subscriberService.listSubscribers(name, PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get subscriber", description = "Returns a subscriber by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Subscriber found",
                    content = @Content(schema = @Schema(implementation = SubscriberDTO.class))),
            @ApiResponse(responseCode = "404", description = "Subscriber not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> getSubscriber(@Parameter(description = "Subscriber ID", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(subscriberService.getSubscriber(id));
    }

    @PostMapping
    @Transactional
    @Operation(summary = "Create subscriber", description = "Creates a new subscriber")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Subscriber created",
                    content = @Content(schema = @Schema(implementation = SubscriberDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> createSubscriber(@Valid @RequestBody SubscriberDTO dto) {
        SubscriberDTO subscriberDTO = subscriberService.createSubscriber(dto);
        return ResponseEntity.created(URI.create("/subscribers/" + subscriberDTO.getId())).body(subscriberDTO);
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "Update subscriber", description = "Updates data of an existing subscriber")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Subscriber updated",
                    content = @Content(schema = @Schema(implementation = SubscriberDTO.class))),
            @ApiResponse(responseCode = "404", description = "Subscriber not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> updateSubscriber(
            @Parameter(description = "Subscriber ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody SubscriberDTO dto) {
        SubscriberDTO subscriberDTO = subscriberService.updateSubscriber(id, dto);
        return ResponseEntity.created(URI.create("/subscribers/" + subscriberDTO.getId())).body(subscriberDTO);
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "Delete subscriber", description = "Removes a subscriber by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Subscriber deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Subscriber not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> deleteSubscriber(@Parameter(description = "Subscriber ID", example = "1") @PathVariable Long id) {
        subscriberService.deleteSubscriber(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/associate-platforms")
    @Transactional
    @Operation(summary = "Associate platforms", description = "Associates multiple platforms to a subscriber")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Association completed"),
            @ApiResponse(responseCode = "400", description = "Business validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Subscriber/Platform not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> associatePlatforms(
            @Parameter(description = "Subscriber ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody AssociatePlatformsDTO dto) {
        try {
            subscriberService.associatePlatforms(id, dto.getPlatformIds());
            return ResponseEntity.ok("Platforms associated successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{subscriberId}/disassociate-platform/{platformId}")
    @Transactional
    @Operation(summary = "Disassociate platform", description = "Removes the association of a platform from a subscriber")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Disassociation completed"),
            @ApiResponse(responseCode = "400", description = "Business validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Subscriber/Platform not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> disassociatePlatform(
            @Parameter(description = "Subscriber ID", example = "1") @PathVariable Long subscriberId,
            @Parameter(description = "Platform ID", example = "2") @PathVariable Long platformId) {
        try {
            subscriberService.disassociatePlatform(subscriberId, platformId);
            return ResponseEntity.ok("Platform disassociated successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
