package dev.riddle.ironinvoice.api.features.mappings.api;

import dev.riddle.ironinvoice.api.features.mappings.api.dto.CreateMappingRequest;
import dev.riddle.ironinvoice.api.features.mappings.api.dto.MappingResponse;
import dev.riddle.ironinvoice.api.features.mappings.api.dto.UpdateMappingRequest;
import dev.riddle.ironinvoice.api.features.mappings.api.mapper.MappingMapper;
import dev.riddle.ironinvoice.api.features.mappings.application.MappingService;
import dev.riddle.ironinvoice.api.features.mappings.application.commands.CreateMappingCommand;
import dev.riddle.ironinvoice.api.features.mappings.application.commands.UpdateMappingCommand;
import dev.riddle.ironinvoice.api.security.CurrentUser;
import dev.riddle.ironinvoice.shared.mappings.persistence.MappingEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/mappings")
@RequiredArgsConstructor
public class MappingController {

	private final MappingService mappingService;
	private final MappingMapper mapper;

	@GetMapping
	public ResponseEntity<List<MappingResponse>> getMappings() {
		UUID userId = CurrentUser.requireId();

		List<MappingEntity> mappings = mappingService.getMappingsByUserId(userId);

		return ResponseEntity
			.ok(mappings
				.stream()
				.map(mapper::toResponse)
				.toList());
	}

	@GetMapping("/{id}")
	public ResponseEntity<MappingResponse> getMappingById(
		@PathVariable("id") UUID id
	) {
		UUID userId = CurrentUser.requireId();

		MappingEntity mapping = mappingService.getMappingbyId(id, userId);

		return ResponseEntity.ok(mapper.toResponse(mapping));
	}

	@PostMapping
	public ResponseEntity<MappingResponse> createMapping(@Valid @RequestBody CreateMappingRequest request) {
		UUID userId = CurrentUser.requireId();

		MappingEntity mapping = mappingService.createMapping(new CreateMappingCommand(
			userId,
			request.templateId(),
			request.name(),
			request.config()
		));

		URI location = ServletUriComponentsBuilder
			.fromCurrentRequest()
			.path("/{id}")
			.buildAndExpand(mapping)
			.toUri();

		return ResponseEntity
			.created(location)
			.body(mapper.toResponse(mapping));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<MappingResponse> updateMapping(
		@PathVariable("id") UUID id,
		@Valid @RequestBody UpdateMappingRequest request
	) {
		UUID userId = CurrentUser.requireId();

		MappingEntity updatedMapping = mappingService.updateMapping(new UpdateMappingCommand(
			id,
			userId,
			request.templateId(),
			request.name(),
			request.config()
		));

		return ResponseEntity.ok(mapper.toResponse(updatedMapping));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteMapping(@PathVariable("id") UUID id) {
		UUID userId = CurrentUser.requireId();

		mappingService.deleteMapping(id, userId);

		return ResponseEntity.noContent().build();
	}
}
