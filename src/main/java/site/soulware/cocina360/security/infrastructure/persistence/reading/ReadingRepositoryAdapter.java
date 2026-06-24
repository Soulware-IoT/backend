package site.soulware.cocina360.security.infrastructure.persistence.reading;

import org.springframework.stereotype.Repository;

import site.soulware.cocina360.security.domain.model.aggregate.Reading;
import site.soulware.cocina360.security.domain.model.valueobject.IoTDeviceId;
import site.soulware.cocina360.security.domain.model.valueobject.ReadingId;
import site.soulware.cocina360.security.domain.repository.ReadingRepository;
import site.soulware.cocina360.security.infrastructure.persistence.reading.mongodb.ReadingDocument;
import site.soulware.cocina360.security.infrastructure.persistence.reading.mongodb.ReadingMongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * MongoDB-backed implementation of the domain {@link ReadingRepository} contract.
 * The engine-agnostic seam: the domain and application layers depend only on
 * {@code ReadingRepository}, unaware that the ledger lives in MongoDB.
 */
@Repository
public class ReadingRepositoryAdapter implements ReadingRepository {

    private final ReadingMongoRepository mongoRepository;

    public ReadingRepositoryAdapter(ReadingMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public Reading save(Reading aggregate) {
        ReadingDocument saved = this.mongoRepository.save(this.toDocument(aggregate));
        return this.toDomain(saved);
    }

    @Override
    public Optional<Reading> findById(ReadingId id) {
        return this.mongoRepository.findById(id.value()).map(this::toDomain);
    }

    @Override
    public List<Reading> findByDeviceId(IoTDeviceId deviceId) {
        return this.mongoRepository.findByDeviceId(deviceId.value()).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void delete(Reading aggregate) {
        this.mongoRepository.deleteById(aggregate.getId().value());
    }

    private ReadingDocument toDocument(Reading reading) {
        return new ReadingDocument(
                reading.getId().value(),
                reading.getDeviceId().value(),
                reading.getTemperatureC(),
                reading.getGasPpm(),
                reading.getSeverity(),
                reading.getOccurredAt(),
                reading.getRecordedAt()
        );
    }

    private Reading toDomain(ReadingDocument document) {
        return Reading.rehydrate(
                ReadingId.of(document.getId()),
                IoTDeviceId.of(document.getDeviceId()),
                document.getTemperatureC(),
                document.getGasPpm(),
                document.getSeverity(),
                document.getOccurredAt(),
                document.getRecordedAt()
        );
    }
}
