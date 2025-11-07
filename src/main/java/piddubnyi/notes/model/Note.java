package piddubnyi.notes.model;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.Set;

public record Note(
        String id,
        @NotBlank(message = "Title is required")
        String title,
        @NotBlank(message = "Text is required")
        String text,
        Set<Tag> tags,
        Instant createdAt
) {}