package piddubnyi.notes.dto;

import java.time.Instant;

public record NoteSummaryDto(
        String id,
        String title,
        Instant createdAt
) {}
