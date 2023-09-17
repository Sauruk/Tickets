package com.example.tickets.controller;

import com.example.tickets.dto.BuyTicketDto;
import com.example.tickets.dto.Ticket;
import com.example.tickets.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "ticket", description = "Ticket API")
@RestController
@Validated
@RequestMapping("/ticket")
public class TicketController {

    private TicketService ticketService;

    @Operation(summary = "Login", tags = "login",
            parameters = @Parameter(name = "date", description = "date of ride"))
                         @Parameter(name = "depart", description = "Department point name")
                         @Parameter(name = "destination", description = "Destination point name")
                         @Parameter(name = "company", description = "Transport company name")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of tickets ready to buy",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = Ticket.class)))
                    })
    })
    @GetMapping("/list")
    public Mono<List<Ticket>> getAvailableTickets(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date, String depart, String destination, String company) {
        return ticketService.getAvailableTickets(date, depart, destination, company);
    }

    @Operation(summary = "User's tickets", tags = "my")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tickets of the current user",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = Ticket.class)))
                    })
    })
    @GetMapping("/my")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public Mono<List<Ticket>> getAvailableTickets(Mono<Principal> principal) {
        return principal.map(Principal::getName).flatMap(ticketService::getClientTickets);
    }

    @Operation(summary = "Buy ticket", tags = "buy",
            parameters = @Parameter(name = "dto",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BuyTicketDto.class))
                    }))
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Buy ticket",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Ticket.class))
                    })
    })
    @PostMapping("/buy")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public Mono<Ticket> getAvailableTickets(@Valid BuyTicketDto dto, Mono<Principal> principal) {
        return principal.map(Principal::getName).flatMap(login -> ticketService.buyTicket(login, dto));
    }

    @Autowired
    public void setTicketService(TicketService ticketService) {
        this.ticketService = ticketService;
    }
}
