package piddubnyi.notes.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import piddubnyi.notes.model.Note;
import piddubnyi.notes.model.Tag;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "notes")
public class NoteEntity {

    @Id
    private String id;
    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank(message = "Text is required")
    private String text;
    private Set<Tag> tags;
    @CreatedDate
    private Instant createdAt;

    public static Note toDomain(NoteEntity noteEntity) {
        return new Note(
                noteEntity.getId(),
                noteEntity.getTitle(),
                noteEntity.getText(),
                noteEntity.getTags(),
                noteEntity.getCreatedAt()
        );
    }

    public static NoteEntity fromDomain(Note note) {
        return new NoteEntity(
                note.id(),
                note.title(),
                note.text(),
                note.tags(),
                note.createdAt()
        );
    }
}
