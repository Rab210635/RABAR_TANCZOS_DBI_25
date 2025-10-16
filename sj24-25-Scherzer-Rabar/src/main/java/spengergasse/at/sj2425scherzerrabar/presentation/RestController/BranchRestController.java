package spengergasse.at.sj2425scherzerrabar.presentation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spengergasse.at.sj2425scherzerrabar.commands.BranchCommand;
import spengergasse.at.sj2425scherzerrabar.domain.Branch;
import spengergasse.at.sj2425scherzerrabar.dtos.BranchDto;
import spengergasse.at.sj2425scherzerrabar.service.BranchService;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/branches")
public class BranchRestController {
    private final BranchService branchService;
    Logger logger = LoggerFactory.getLogger(LoggingController.class);

    public BranchRestController(BranchService branchService) {
        this.branchService = branchService;
    }

    @Operation(summary = "Get all Branches",
            description = "To get every Branch object in the database.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Branch.class))})})
    @GetMapping
    public ResponseEntity<List<BranchDto>> getAllBranches() {
        logger.debug("entered branchrestcontroller getAllBranches");
        List<BranchDto> response = branchService.getAllBranches();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get a Branch",
            description = "To get an specific Branch by their Apikey. The response is the requested Branch object.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Branch.class))}),
            @ApiResponse(responseCode = "404", description = "Branch not found",
                    content = @Content)})
    @GetMapping("/branch")
    public ResponseEntity<BranchDto> getBranch(@RequestParam String apiKey) {
        logger.debug("entered branchrestcontroller getBranch");
        BranchDto response = branchService.getBranchByApiKey(apiKey);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all Branches by a Library",
            description = "To get all Branches by their Library. The response is the requested Branches objects.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Branch.class))}),
            @ApiResponse(responseCode = "404", description = "Library not found",
                    content = @Content)})
    @GetMapping("/library")
    public ResponseEntity<List<BranchDto>> getBranchByLibrary(@RequestParam String library) {
        logger.debug("entered branchrestcontroller getBranchByLibrary");
        List<BranchDto> response = branchService.getBranchesByLibrary(library);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Create a Branch",
            description = "Create a new Branch to be saved in the database. The response is a link to the created Branch object.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Branch.class))})})
    @PostMapping
    public ResponseEntity<BranchDto> createBranch(@RequestBody BranchCommand command) {
        logger.debug("entered branchrestcontroller createBranch");
        BranchDto response = branchService.createBranch(command);

        Link selfLink = linkTo(methodOn(BranchRestController.class).getBranch(response.apiKey())).withSelfRel();
        return ResponseEntity.created(selfLink.toUri()).body(response);
    }

    @Operation(summary = "Update a Branch",
            description = "Update an existing Branch. The response is an updated Branch object.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Branch.class))}),
            @ApiResponse(responseCode = "404", description = "Branch not found",
                    content = @Content)})
    @PutMapping
    public ResponseEntity<BranchDto> updateBranch(@RequestBody BranchCommand command) {
        logger.debug("entered branchrestcontroller updateBranch");
        BranchDto response = branchService.updateBranch(command);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Delete a Branch",
            description = "Delete a existing Branch.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Branch.class))}),
            @ApiResponse(responseCode = "404", description = "Branch not found",
                    content = @Content)})
    @DeleteMapping
    public ResponseEntity<BranchDto> deleteBranch(@RequestParam String apiKey) {
        logger.debug("entered branchrestcontroller deleteBranch");
        branchService.deleteBranch(apiKey);
        return ResponseEntity.noContent().build();
    }
}