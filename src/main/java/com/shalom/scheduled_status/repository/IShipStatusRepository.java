package com.shalom.scheduled_status.repository;

import com.shalom.scheduled_status.document.ShipStatusDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IShipStatusRepository extends MongoRepository<ShipStatusDocument, String> {
    List<ShipStatusDocument> findByComplete(boolean value);
}
