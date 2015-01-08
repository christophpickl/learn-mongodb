# Learn MongoDB

Just a project to learn and provide showcases for kotlin (JVM language) and mongodb.

## Links

* [http://www.mongodb.org](http://www.mongodb.org)
* [http://projects.spring.io/spring-data-mongodb](http://projects.spring.io/spring-data-mongodb)

# Mongo Daemon Usage


    C:\> cd C:\Program Files\MongoDB 2.6 Standard\bin
    C:\Program Files\MongoDB 2.6 Standard\bin> mongod.exe --dbpath C:\Data\tmp\mongoDB

# MongoDB Shell


    C:\Program Files\MongoDB 2.6 Standard\bin>mongo

    [...]

    > show dbs

    admin       (empty)
    local       0.078GB
    myDatabase  0.078GB

    > use myDatabase

    switched to db myDatabase

    > show collections

    system.indexes
    user

    > db.user.find()

    { "_id" : ObjectId("54ae5d8445e3e2f83a5f2de2"), "name" : "xmas2", "age" : 112 }

    >
