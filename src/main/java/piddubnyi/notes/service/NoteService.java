package piddubnyi.notes.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import piddubnyi.notes.dto.NoteSummaryDto;
import piddubnyi.notes.entity.NoteEntity;
import piddubnyi.notes.exception.NoteNotFoundException;
import piddubnyi.notes.model.Note;
import piddubnyi.notes.model.Tag;
import piddubnyi.notes.repository.NoteRepository;

import java.util.Set;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public Note findById(String id) {
        return noteRepository
                .findById(id).map(NoteEntity::toDomain)
                .orElseThrow(() -> new NoteNotFoundException(id));
    }

    public Note create(Note note) {
        return NoteEntity.toDomain(noteRepository.save(NoteEntity.fromDomain(note)));
    }

    public Note update(String id, Note note) {
        noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException(id));

        Note noteToUpdate = new Note(id, note.title(), note.text(), note.tags(), note.createdAt());
        return NoteEntity.toDomain(noteRepository.save(NoteEntity.fromDomain(noteToUpdate)));
    }

    public void delete(String id) {
        noteRepository
                .findById(id)
                .orElseThrow(() -> new NoteNotFoundException(id));
        noteRepository.deleteById(id);
    }

    public Page<NoteSummaryDto> findAllSummaries(Set<Tag> tags, Pageable pageable) {
        if (tags == null || tags.isEmpty()) {
            return noteRepository.findAllProjectedBy(pageable);
        } else {
            return noteRepository.findByTagsIn(tags, pageable);
        }
    }
}
