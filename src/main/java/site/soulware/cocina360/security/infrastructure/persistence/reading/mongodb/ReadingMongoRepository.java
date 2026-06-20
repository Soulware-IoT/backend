package site.soulware.cocina360.security.infrastructure.persistence.reading.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface ReadingMongoRepository extends MongoRepository<ReadingDocument, UUID> {

    List<ReadingDocument> findByDeviceId(UUID deviceId);
}
