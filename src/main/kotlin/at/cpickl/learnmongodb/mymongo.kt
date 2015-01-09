package at.cpickl.learnmongodb

import kotlin.platform.platformStatic
import com.mongodb.MongoClient
import com.mongodb.BasicDBObject
import com.mongodb.DBObject
import com.mongodb.DB
import java.io.File
import com.mongodb.gridfs.GridFS
import org.slf4j.Logger
import org.slf4j.LoggerFactory

// http://docs.mongodb.org/ecosystem/tutorial/getting-started-with-java-driver/

// tasks:
// - write result, throw exception on error
// - nested objects
// - lists
// - complex queries
//   * and/or, gt/lower
//   * in (list)
//   * reusing query parts

object MongoDemoApp {

    private val mongo = MongoManager()
    private val userRepo = UserRepository(mongo.db())
    private val files = FileStore(mongo.db())

    platformStatic fun main(args: Array<String>) {
        exec()
    }

    fun exec() {
        println("mongo ... START")
        //        val db = mongo.getDB("myDatabaseName") // gets created, if isnt yet created
        //        val wasAuthenticated = db.authenticate("username", "password".toCharArray())

//        mongo.dumpMetaData()
//
//        listUsers()
//        userRepo.save(springmongo.User("xmas", 42))
//        listUsers()
//        userRepo.update("xmas", springmongo.User("xmas3", 112))
//        userRepo.update("not existing!", springmongo.User("foo", 1))
//        listUsers()

//        files.save(File("README.md"))
        files.listAll()

        println("mongo ... END")
    }

    private fun listUsers() {
        println("List all users:")
        userRepo.listAll().forEach { u -> println("- ${u}") }
    }

}

data class User(val name: String, val age: Int)

// http://docs.mongodb.org/manual/core/gridfs/
// http://www.mkyong.com/mongodb/java-mongodb-save-image-example/
class FileStore(private val db: DB) {
    class object {
        private val LOG: Logger = LoggerFactory.getLogger(javaClass)
        private val BUCKET_NAME = "myFileBucketName"
    }
    private val fileSystem = GridFS(db, BUCKET_NAME)
    fun save(fileToStore: File) {
        LOG.info("save(fileToStore.absolutePath={})", fileToStore.getAbsolutePath())
        val gfsFile = fileSystem.createFile(fileToStore)
        gfsFile.save()
    }
    fun listAll() {
        val cursor = fileSystem.getFileList()
        println("listAll files ...")
        while (cursor.hasNext()) {
            println("\tFile: ${cursor.next()}")
        }
//        GridFSDBFile imageForOutput = gfsPhoto.findOne(newFileName);
//        imageForOutput.writeTo("c:\\JavaWebHostingNew.png"); //output to new file
    }

}

class UserRepository(private val db: DB) {
    class object {
        private val TABLE_NAME = "user"
    }

    private val transformDbo2Domain: (DBObject) -> springmongo.UserDbo = { (dbo) -> springmongo.UserDbo(dbo.get("name") as String, dbo.get("age") as Int) }
    private val transformDomain2Dbo: (springmongo.UserDbo) -> DBObject = { (domain) -> BasicDBObject().append("name", domain.name).append("age", domain.age) }

    fun listAll(): Collection<springmongo.UserDbo> {
        val table = table()
        val cursor = table.find()
        try {
            return cursor.toArray().map(transformDbo2Domain)
        } finally {
            cursor.close()
        }
    }

    fun save(user: springmongo.UserDbo) {
        println("save(user=${user})")
        /*val result = */table().insert(transformDomain2Dbo(user))
    }

    fun update(queryUserName: String, user: springmongo.UserDbo) {
        println("update(queryUserName=${queryUserName}, user=${user})")
        val query = BasicDBObject().append("name", queryUserName)
        val result = table().update(query, BasicDBObject().append("\$set", transformDomain2Dbo(user)))
        println("Update affected results: ${result.getN()}")
    }

    fun delete(queryUserName: String) {
        println("delete=${queryUserName}")
        val query = BasicDBObject().append("name", queryUserName)
        val result = table().remove(query)
        println("Delete affected results: ${result.getN()}")
    }

    private fun table() = db.getCollection(TABLE_NAME)
}

class MongoManager {
    class object {
        private val DB_NAME = "myDatabase"
    }
    private val mongo = MongoClient("localhost", 27017)


    fun dumpMetaData() {
        val dbNames = mongo.getDatabaseNames()
        println("Dumping meta data of ${dbNames.size()} database(s).")
        for (dbName in dbNames) {
            val db = mongo.getDB(dbName)
            val collectionNames = db.getCollectionNames()
            println("\tDatabase '${dbName}' contains ${collectionNames.size()} collection(s).")
            for (collectionName in collectionNames) { // collection == table
                val collection = db.getCollection(collectionName)
                println("\t\tCollection: ${collectionName} (${collection.count()} entries)")
                val cursor = collection.find()
                cursor.toArray().fold(1, { (i, dbo) -> println("\t\t- ${i}. ${dbo}"); i + 1 })
                cursor.close()
            }
        }
    }

    fun db() = mongo.getDB(DB_NAME)

}