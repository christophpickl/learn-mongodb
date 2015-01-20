package at.cpickl.learnmongodb.morphia

import kotlin.platform.platformStatic
import org.mongodb.morphia.annotations.Entity
import org.mongodb.morphia.annotations.Embedded
import org.mongodb.morphia.annotations.Id
import org.bson.types.ObjectId
import com.mongodb.MongoClient
import org.mongodb.morphia.Morphia
import org.mongodb.morphia.Datastore
import org.mongodb.morphia.dao.BasicDAO

// https://github.com/mongodb/morphia/wiki/QuickStart

object MorphiaApp {

    private val mongo = MongoClient("localhost")
    private val morphia = Morphia()

    platformStatic fun main(args: Array<String>) {
        println("MorphiaApp START")

        val datastore = createDatastore()
        val dao = HotelDao(mongo, morphia)

        //        val hotel1 = Hotel(null, "name1", 42, Address("street1", "city1"))
        //        println("Storing: ${hotel1}")
        //        datastore.save(hotel1)

        // datastore.get(Hotel.class, hotelId);
        //        val storedHotel1 = datastore.find(javaClass<Hotel>()).field("name").equal("name1").first()
        //        println("storedHotel1 = ${storedHotel1}")


        dao.save(Hotel(null, "name2", 21, Address("street2", "city2")))
        dao.find().asList().forEach { println("Found") }

        println("MorphiaApp END")
    }

    private fun createDatastore(): Datastore {
        println("createDatastore()")
        morphia.map(javaClass<Hotel>()).map(javaClass<Address>())
        return morphia.createDatastore(mongo, "myMorphiaDb")
    }
}

class HotelDao(mongo: MongoClient, morphia: Morphia) : BasicDAO<Hotel, String>(mongo, morphia, "myHotelDatabase")

Entity data class Hotel(Id var id: ObjectId? = null, var name: String? = null, var stars: Int? = null, Embedded var address: Address? = null)
// @Transient to exclude a field...
Embedded data class Address(var street: String? = null, var city: String? = null)