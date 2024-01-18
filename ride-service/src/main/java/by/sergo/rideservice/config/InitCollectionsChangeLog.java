package by.sergo.rideservice.config;

import by.sergo.rideservice.domain.Ride;
import by.sergo.rideservice.repository.RideRepository;
import io.mongock.api.annotations.*;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.schema.JsonSchemaProperty;
import org.springframework.data.mongodb.core.schema.MongoJsonSchema;
import org.springframework.data.mongodb.core.validation.Validator;

@ChangeUnit(id = "init-ride", order = "001", author = "Sergei-Karachun")
public class InitCollectionsChangeLog {

    private final MongoTemplate mongoTemplate;
    private final RideRepository rideService;

    public InitCollectionsChangeLog(MongoTemplate mongoTemplate,
                                    RideRepository rideService) {
        this.mongoTemplate = mongoTemplate;
        this.rideService = rideService;
    }
    @BeforeExecution
    public void beforeExecution(MongoTemplate mongoTemplate) {
        mongoTemplate.createCollection("ride", CollectionOptions.empty()
                        .validator(Validator.schema(MongoJsonSchema.builder()
                                .required("pickUpAddress", "destinationAddress", "creatingTime", "status")
                                .properties(
                                        JsonSchemaProperty.objectId("id"),
                                        JsonSchemaProperty.string("pickUpAddress"),
                                        JsonSchemaProperty.string("destinationAddress"),
                                        JsonSchemaProperty.float64("price"),
                                        JsonSchemaProperty.int64("driverId"),
                                        JsonSchemaProperty.int64("passengerId"),
                                        JsonSchemaProperty.date("creatingTime"),
                                        JsonSchemaProperty.date("startTime"),
                                        JsonSchemaProperty.date("endTime"),
                                        JsonSchemaProperty.string("status"))
                                .build())));
    }

    @RollbackBeforeExecution
    public void rollbackBefore() {
        mongoTemplate.dropCollection("ride");
    }
    @Execution
    public void changeSet() {
        rideService.findAll()
                .stream()
                .forEach(client -> mongoTemplate.save(client, "ride"));
    }

    @RollbackExecution
    public void rollback() {
        mongoTemplate.dropCollection(Ride.class);
    }
}
