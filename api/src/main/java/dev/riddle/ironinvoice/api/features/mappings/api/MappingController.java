package dev.riddle.ironinvoice.api.features.mappings.api;

import dev.riddle.ironinvoice.api.features.mappings.api.dto.MappingResponse;
import dev.riddle.ironinvoice.api.features.mappings.api.mapper.MappingMapper;
import dev.riddle.ironinvoice.api.features.mappings.application.MappingService;
import dev.riddle.ironinvoice.api.security.CurrentUser;
import dev.riddle.ironinvoice.shared.mappings.persistence.MappingEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
