
package piddubnyi.notes.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import piddubnyi.notes.dto.NoteSummaryDto;
import piddubnyi.notes.model.Note;
import piddubnyi.notes.model.Tag;
import piddubnyi.notes.service.NoteService;

import java.util.Set;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("v1/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Note create(@Valid @RequestBody Note note) {
        return noteService.create(note);
    }

    @GetMapping("/{id}")
    public Note findById(@PathVariable String id) {
        return noteService.findById(id);
    }

    @PutMapping("/{id}")
    public Note update(@PathVariable String id, @Valid @RequestBody Note note) {
        return noteService.update(id, note);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        noteService.delete(id);
    }

    @GetMapping("/summaries")
    public Page<NoteSummaryDto> findAllSummaries(
            @RequestParam(required = false) Set<Tag> tags,
            @PageableDefault(size = 20, sort = "createdAt", direction = DESC) Pageable pageable) {
        return noteService.findAllSummaries(tags, pageable);
    }
}

