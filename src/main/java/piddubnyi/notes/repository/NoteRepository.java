package piddubnyi.notes.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import piddubnyi.notes.dto.NoteSummaryDto;
import piddubnyi.notes.entity.NoteEntity;
import piddubnyi.notes.model.Tag;

import java.util.Set;

public interface NoteRepository extends MongoRepository<NoteEntity, String> {

    Page<NoteSummaryDto> findByTagsIn(Set<Tag> tags, Pageable pageable);

    Page<NoteSummaryDto> findAllProjectedBy(Pageable pageable);
}
