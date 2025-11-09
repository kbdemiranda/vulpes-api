package io.github.vulpes.applications.controller;

import io.github.vulpes.applications.dto.PaymentDTO;
import io.github.vulpes.applications.service.PaymentService;
import io.github.vulpes.infrastructure.http.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
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
@RequestMapping("/payments")
@Tag(name = "Payments", description = "APIs for payment management")
@SecurityRequirement(name = "bearer-key")
@Validated
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @Transactional
    @Operation(summary = "Register payment", description = "Creates a new payment")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Payment created",
                    content = @Content(schema = @Schema(implementation = PaymentDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> registerPayment(@Valid @RequestBody PaymentDTO dto) {
        PaymentDTO paymentDTO = paymentService.registerPayment(dto);
        return ResponseEntity.created(URI.create("/payments/" + paymentDTO.getId())).body(paymentDTO);
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "Update payment", description = "Updates an existing payment")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Payment updated",
                    content = @Content(schema = @Schema(implementation = PaymentDTO.class))),
            @ApiResponse(responseCode = "404", description = "Payment not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> updatePayment(
            @Parameter(description = "Payment ID", example = "10") @PathVariable Long id,
            @Valid @RequestBody PaymentDTO dto) {
        PaymentDTO paymentDTO = paymentService.updatePayment(id, dto);
        return ResponseEntity.created(URI.create("/payments/" + paymentDTO.getId())).body(paymentDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment", description = "Retrieves a payment by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment found",
                    content = @Content(schema = @Schema(implementation = PaymentDTO.class))),
            @ApiResponse(responseCode = "404", description = "Payment not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> getPayment(@Parameter(description = "Payment ID", example = "10") @PathVariable Long id) {
        PaymentDTO paymentDTO = paymentService.getPayment(id);
        return ResponseEntity.ok(paymentDTO);
    }

    @GetMapping
    @Operation(summary = "List payments", description = "Returns paginated list of payments, with optional filter by subscriber name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List returned successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> listPayments(
            @Parameter(description = "Filter by subscriber name", example = "John") @RequestParam(required = false) String subscriberName,
            @Parameter(description = "Page number (0..N)", example = "0")
            @PositiveOrZero @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Items per page", example = "10")
            @Min(1) @RequestParam(defaultValue = "10") int size
    ) {
        Page<PaymentDTO> payments = paymentService.listPayments(subscriberName, PageRequest.of(page, size));
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/subscriber/{id}")
    @Operation(summary = "List payments by subscriber", description = "Returns paginated list of payments for a subscriber")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List returned successfully"),
            @ApiResponse(responseCode = "404", description = "Subscriber not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> listPaymentsBySubscriber(
            @Parameter(description = "Subscriber ID", example = "1") @PathVariable Long id,
            @Parameter(description = "Page number (0..N)", example = "0")
            @PositiveOrZero @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Items per page", example = "10")
            @Min(1) @RequestParam(defaultValue = "10") int size
    ) {
        Page<PaymentDTO> payments = paymentService.listPaymentsBySubscriber(id, PageRequest.of(page, size));
        return ResponseEntity.ok(payments);
    }

}
