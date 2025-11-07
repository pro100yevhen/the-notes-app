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

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public Map<String, Integer> getNoteStats(String id) {
        Note note = findById(id);
        String text = note.text();

        if (text == null || text.isBlank()) {
            return Collections.emptyMap();
        }
        String splitter = "\\s+";
        Map<String, Integer> wordFrequencies = Arrays.stream(text.trim().split(splitter))
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.summingInt(word -> 1)
                ));

        return wordFrequencies.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
    }
}