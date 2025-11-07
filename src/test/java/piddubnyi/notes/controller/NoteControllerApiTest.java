package piddubnyi.notes.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import piddubnyi.notes.entity.NoteEntity;
import piddubnyi.notes.model.Note;
import piddubnyi.notes.repository.NoteRepository;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NoteControllerApiTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private NoteRepository noteRepository;

    @BeforeEach
    void setUp() {
        noteRepository.deleteAll();
    }

    @Test
    void getNoteStats_shouldReturnCorrectStats_whenNoteExists() {
        NoteEntity note = new NoteEntity(null, "Title", "test note is a test note", emptySet(), null);
        note = noteRepository.save(note);
        String noteId = note.getId();

        ResponseEntity<Map> response = restTemplate.getForEntity("/v1/notes/{id}/stats", Map.class, noteId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Map<String, Integer> stats = response.getBody();
        assertThat(stats).isInstanceOf(LinkedHashMap.class);

        Map<String, Integer> expectedStats = new LinkedHashMap<>();
        expectedStats.put("note", 2);
        expectedStats.put("test", 2);
        expectedStats.put("a", 1);
        expectedStats.put("is", 1);

        assertThat(stats).isEqualTo(expectedStats);
    }

    @Test
    void getNoteStats_shouldReturnNotFound_whenNoteDoesNotExist() {
        String noteId = "non-existent-id";

        ResponseEntity<Map> response = restTemplate.getForEntity("/v1/notes/{id}/stats", Map.class, noteId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void createNote_shouldReturnCreated_whenNoteIsValid() {
        Note noteToCreate = new Note(null, "New Note", "Some text", emptySet(), null);

        ResponseEntity<Note> response = restTemplate.postForEntity("/v1/notes", noteToCreate, Note.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Note createdNote = response.getBody();
        assertThat(createdNote).isNotNull();
        assertThat(createdNote.id()).isNotNull();
        assertThat(createdNote.title()).isEqualTo("New Note");

        assertThat(noteRepository.findById(createdNote.id())).isPresent();
    }

    @Test
    void createNote_shouldReturnBadRequest_whenTitleIsBlank() {
        Note noteToCreate = new Note(null, "", "Some text", emptySet(), null);

        ResponseEntity<Object> response = restTemplate.postForEntity("/v1/notes", noteToCreate, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateNote_shouldReturnOk_whenNoteIsValid() {
        NoteEntity existingNote = noteRepository.save(new NoteEntity(null, "Old Title", "Old text", emptySet(), null));
        Note noteToUpdate = new Note(null, "Updated Title", "Updated text", emptySet(), null);

        restTemplate.put("/v1/notes/{id}", noteToUpdate, existingNote.getId());

        ResponseEntity<Note> response = restTemplate.getForEntity("/v1/notes/{id}", Note.class, existingNote.getId());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().title()).isEqualTo("Updated Title");
        assertThat(response.getBody().text()).isEqualTo("Updated text");
    }

    @Test
    void updateNote_shouldReturnBadRequest_whenTitleIsBlank() {
        NoteEntity existingNote = noteRepository.save(new NoteEntity(null, "Old Title", "Old text", emptySet(), null));
        Note noteToUpdate = new Note(null, "", "Updated text", emptySet(), null);

        ResponseEntity<Object> response = restTemplate.exchange("/v1/notes/{id}", org.springframework.http.HttpMethod.PUT, new HttpEntity<>(noteToUpdate), Object.class, existingNote.getId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void deleteNote_shouldReturnNoContent_whenNoteExists() {
        NoteEntity existingNote = noteRepository.save(new NoteEntity(null, "To Be Deleted", "Some text", emptySet(), null));

        restTemplate.delete("/v1/notes/{id}", existingNote.getId());

        assertThat(noteRepository.findById(existingNote.getId())).isNotPresent();
    }
}
