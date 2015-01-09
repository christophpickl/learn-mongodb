package at.cpickl.learnmongodb.springmongo

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.MongoOperations
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
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.WriteResultChecking
import java.io.File

Configuration open class MongoAppConfig {
    class object {
        val LOG: Logger = LoggerFactory.getLogger(javaClass)
    }

    private val databaseName = "springMongoDatabase"

    Bean open fun mongoDbFactory(): MongoDbFactory = SimpleMongoDbFactory(MongoClient("127.0.0.1"), databaseName)

    Bean open fun mongoTemplate(): MongoTemplate {
        val template = MongoTemplate(mongoDbFactory())
        // show error, should off on production to speed up performance
        template.setWriteConcern(WriteConcern.SAFE)
        template.setWriteResultChecking(WriteResultChecking.EXCEPTION) // is by default set to NONE
        return template
    }

}

class MongodbUserDao(private val mongo: MongoOperations) : UserDao {
    class object {
        val LOG: Logger = LoggerFactory.getLogger(javaClass)
    }
    override fun createOrUpdate(user: User) {
        LOG.debug("createOrUpdate(user='{}')", user)
        mongo.save(user.toDbo())
    }
    override fun findAll(): List<User> {
        return mongo.findAll(javaClass<UserDbo>()).map(UserDbo.transformUserDboToUser)
    }
    override fun findByFirstCharInName(search: Char): List<User> {
        LOG.debug("findByFirstCharInName(search='{}')", search)
        return mongo.find(Query(Criteria.where("name").regex("${search}.*")), javaClass<UserDbo>()).map(UserDbo.transformUserDboToUser)
    }

    private fun User.toDbo(): UserDbo = UserDbo.transformUserToUserDbo(this)
}

// should not be used outside of here
Document(collection="user") data class UserDbo(Id var name: String? = null, var age: Int? = null) {
    class object {
        val transformUserToUserDbo: (User) -> UserDbo = { (user) -> UserDbo(user.name, user.age) }
        val transformUserDboToUser: (UserDbo) -> User = { (dbo) -> User(dbo.name!!, dbo.age!!) }
    }
}
