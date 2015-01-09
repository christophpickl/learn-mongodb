package at.cpickl.learnmongodb

import com.mongodb.MongoClient


class MongoManager(val mongo: MongoClient = MongoClient("localhost")) {

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

}
