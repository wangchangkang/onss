package work.onss.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PictureRepository extends MongoRepository<Picture, String> {

    Optional<Picture> findBySidAndFilePath(String sid, String filePath);
}
