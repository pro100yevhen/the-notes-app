package piddubnyi.notes.exception;

public class NoteNotFoundException extends RuntimeException {

    public NoteNotFoundException(String id) {
        super("Note with id " + id + " not found");
    }
}
