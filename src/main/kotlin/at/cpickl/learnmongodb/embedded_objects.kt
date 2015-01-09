package at.cpickl.learnmongodb

import kotlin.platform.platformStatic
import com.mongodb.MongoClient
import com.mongodb.BasicDBObject
import org.bson.types.ObjectId
import com.mongodb.DBObject
import com.mongodb.DBCursor

object EmbeddedDemoApp {

    private val mongo = MongoClient("localhost", 27017)
    private val db = mongo.getDB("embeddedDemoDatabase7")
    private val tableBook = db.getCollection("book")
    private val tablePublisher = db.getCollection("publisher")

    private val bookDboToDomain: (DBObject) -> Book = { (dbo) -> Book(dbo.get("_id") as ObjectId, dbo.get("name") as String, dbo.get("pages") as Int, dbo.get("publisherId") as ObjectId) }

    platformStatic fun main(args: Array<String>) {
        println("Storing publishers.")
        val oreilly = Publisher(ObjectId(), "oreilly")
        val addison = Publisher(ObjectId(), "addison")
        saveOrUpdate(oreilly)
        saveOrUpdate(addison)

        println("Storing books.")
        val hardcoreJava = Book(ObjectId(), "Hardcore Java", 204, oreilly.id)
        val dummy = Book(ObjectId(), "Dummy i am", 2, oreilly.id)
        val javaPuzzlers = Book(ObjectId(), "Java Puzzlers", 72, addison.id)
        saveOrUpdate(hardcoreJava)
        saveOrUpdate(javaPuzzlers)
        saveOrUpdate(dummy)

        println("Printing all entries.")
        listAllPublisher().forEach { publisher ->
            println("\tFound: ${publisher}")
            allBooksForPublisher(publisher).forEach { book ->
                println("\t\t- ${book}")
            }
        }
        println("DONE.")
    }

    fun saveOrUpdate(toSave: Publisher) {
        val result = tablePublisher.save(BasicDBObject()
                .append("_id", toSave.id)
                .append("name", toSave.name))
        println("publisher.id = ${toSave.id}")
        println("result.getUpsertedId() = ${result.getUpsertedId()}")
    }

    fun saveOrUpdate(toSave: Book) {
        tableBook.save(BasicDBObject()
                .append("_id", toSave.id)
                .append("name", toSave.name)
                .append("pages", toSave.pages)
                .append("publisherId", toSave.publisherId))
    }

    fun listAllPublisher(): Collection<Publisher> {
        return tablePublisher.find().toArray().map { dbo -> Publisher(dbo.get("_id") as ObjectId, dbo.get("name") as String) }
    }

    fun allBooksForPublisher(publisher: Publisher): Collection<Book> {
        println("allBooksForPublisher(publisher=${publisher})")
        val query = BasicDBObject().append("publisherId", publisher.id)
        return transformBooks(tableBook.find(query))
    }

    private fun transformBooks(cursor: DBCursor): Collection<Book> {
        return cursor.toArray().map(bookDboToDomain)
    }

}

trait HasMongoId {
    var id: ObjectId?
}

data class Book(override var id: ObjectId? = null, var name: String? = null, var pages: Int? = null, var publisherId: ObjectId? = null) : HasMongoId
data class Publisher(override var id: ObjectId? = null, var name: String? = null) : HasMongoId
