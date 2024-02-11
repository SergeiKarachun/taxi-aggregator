db = db.getSiblingDB('ridedb');

db.createUser({
    user: 'root',
    pwd: 'example',
    roles: [
        { role: 'readWrite', db: 'ridedb' }
    ]
});

db.createCollection('ride');

db.ride.insertOne({
    _id: ObjectId('65afd8b6759a765221df8051'),
    pickUpAddress: 'home',
    destinationAddress: 'work',
    price: 5.50,
    passengerId: 1,
    creatingTime: ISODate('2024-02-05T16:44:52.453Z'),
    status: 'CREATED',
    paymentMethod: 'CASH',
    _class: 'by.sergo.rideservice.domain.Ride'
});

db.ride.insertOne({
    _id: ObjectId('65c1112fa3c5564557a6e769'),
    pickUpAddress: 'home',
    destinationAddress: 'work',
    price: 4.24,
    driverId: 1,
    passengerId: 1,
    creatingTime: ISODate('2024-02-05T16:47:43.641Z'),
    startTime: ISODate('2024-02-05T16:46:52.473Z'),
    endTime: ISODate('2024-02-05T16:46:58.590Z'),
    status: 'FINISHED',
    paymentMethod: 'CARD',
    _class: 'by.sergo.rideservice.domain.Ride'
});

db.ride.insertOne({
    _id: ObjectId('65c1112fa3c5564557a6e111'),
    pickUpAddress: 'home',
    destinationAddress: 'work',
    price: 4.24,
    driverId: 1,
    passengerId: 1,
    creatingTime: ISODate('2024-02-05T16:47:43.641Z'),
    startTime: ISODate('2024-02-05T16:46:52.473Z'),
    status: 'TRANSPORT',
    paymentMethod: 'CARD',
    _class: 'by.sergo.rideservice.domain.Ride'
});

