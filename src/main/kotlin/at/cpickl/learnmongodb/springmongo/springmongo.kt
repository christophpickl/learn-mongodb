package at.cpickl.learnmongodb.springmongo

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.stereotype.Repository
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.core.MongoTemplate
import com.mongodb.WriteConcern
import org.springframework.data.mongodb.core.SimpleMongoDbFactory
import com.mongodb.MongoClient
import org.springframework.data.mongodb.MongoDbFactory
import org.slf4j.LoggerFactory
import org.slf4j.Logger
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import kotlin.platform.platformStatic
import org.springframework.beans.factory.annotation.Autowired


// http://www.mkyong.com/mongodb/spring-data-mongodb-auto-sequence-id-example/

object SpringMongoApp {

    platformStatic fun main(args: Array<String>) {
        val spring = AnnotationConfigApplicationContext(javaClass<AppConfig>())
        val userDao = spring.getBean(javaClass<UserDao>())
        userDao.save(User("christoph", 12))
        userDao.findAll().fold(1, { (i, user) -> println("  - ${i}. ${user}"); i + 1 })
        userDao.save(User("christoph", 42))
        userDao.findAll().fold(1, { (i, user) -> println("  - ${i}. ${user}"); i + 1 })
    }
}

Import(javaClass<MongoAppConfig>())
Configuration open class AppConfig {

    Autowired private var mongoOperations: MongoOperations? = null

    Bean open fun userDao(): UserDao = UserDaoImpl(mongoOperations!!)
}

Configuration open class MongoAppConfig {
    class object {
        val LOG: Logger = LoggerFactory.getLogger(javaClass)
    }

    private val databaseName = "springMongoDatabase"

    Bean open fun mongoDbFactory(): MongoDbFactory {
        LOG.info("mongoDbFactory()")
        val mongo = MongoClient("127.0.0.1")
        return SimpleMongoDbFactory(mongo, databaseName)
    }

    Bean open fun mongoTemplate(): MongoTemplate {
        val template = MongoTemplate(mongoDbFactory())
        // show error, should off on production to speed up performance
        template.setWriteConcern(WriteConcern.SAFE)
        return template
    }

}

trait UserDao {
    fun save(user: User)
    fun findAll(): List<User>
}

Repository class UserDaoImpl(private val mongo: MongoOperations) : UserDao {
    override fun save(user: User) {
        mongo.save(user)
    }
    override fun findAll(): List<User> {
        return mongo.findAll(javaClass<User>())
    }
}

Document(collection="user") data class User(Id var name: String? = null, var age: Int? = null)
