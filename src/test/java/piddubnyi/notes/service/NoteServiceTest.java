package piddubnyi.notes.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import piddubnyi.notes.entity.NoteEntity;
import piddubnyi.notes.exception.NoteNotFoundException;
import piddubnyi.notes.model.Note;
import piddubnyi.notes.repository.NoteRepository;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {
    private final String noteId = "testNoteId";

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteService noteService;

    @ParameterizedTest
    @MethodSource("provideTextForStats")
    void getNoteStats_shouldReturnSortedWordCount_whenNoteExistsWithText(String text, Map<String, Integer> expectedStats) {
        Note note = new Note(noteId, "Title", text, emptySet(), null);
        NoteEntity noteEntity = NoteEntity.fromDomain(note);
        when(noteRepository.findById(noteId)).thenReturn(Optional.of(noteEntity));

        Map<String, Integer> stats = noteService.getNoteStats(noteId);

        assertThat(stats).isEqualTo(expectedStats);
    }

    @Test
    void getNoteStats_shouldThrowNoteNotFoundException_whenNoteDoesNotExist() {
        when(noteRepository.findById(noteId)).thenReturn(Optional.empty());

        assertThrows(NoteNotFoundException.class, () -> noteService.getNoteStats(noteId));
    }

    private static Stream<Arguments> provideTextForStats() {
        return Stream.of(
                Arguments.of("note is just a note", new LinkedHashMap<>() {{
                    put("note", 2);
                    put("a", 1);
                    put("is", 1);
                    put("just", 1);
                }}),
                Arguments.of("Test test TeSt", new LinkedHashMap<>() {{
                    put("Test", 1);
                    put("test", 1);
                    put("TeSt", 1);
                }}),
                Arguments.of("", Collections.emptyMap()),
                Arguments.of(null, Collections.emptyMap())
        );
    }
}
