package at.cpickl.learnmongodb.springmongo

import kotlin.platform.platformStatic
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Configuration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.context.annotation.Bean


// http://docs.spring.io/spring-data/data-mongo/docs/1.6.1.RELEASE/reference/html/#repositories.query-methods
// http://www.mkyong.com/mongodb/spring-data-mongodb-auto-sequence-id-example/

// @param name ... unique primary key/ID
data class User(val name: String, val age: Int)

trait UserDao {
    fun createOrUpdate(user: User)
    fun findAll(): List<User>
    fun findByFirstCharInName(search: Char): List<User>
}


class SpringMongoApp(private val userDao: UserDao) {

    class object {
        platformStatic fun main(args: Array<String>) {
            println("SpringMongoApp START")
            val spring = AnnotationConfigApplicationContext(javaClass<AppConfig>())
            try {
                spring.getBean(javaClass<SpringMongoApp>()).exec()
            } finally {
                spring.close()
            }
            println("SpringMongoApp END")
        }
    }

    fun exec() {
        listAllUsers()
        userDao.createOrUpdate(User("costi", 3))
        userDao.createOrUpdate(User("xavier", 4))
        listAllUsers()
        printUsers(userDao.findByFirstCharInName('c'))
    }

    private fun listAllUsers() {
        println("List of all users:")
        printUsers(userDao.findAll())
    }
    private fun printUsers(users: Collection<User>) {
        users.fold(1, { (i, user) -> println("  - ${i}. ${user}"); i + 1 })
    }
}

Import(javaClass<MongoAppConfig>())
Configuration open class AppConfig {
    Autowired private var mongoOperations: MongoOperations? = null
    Bean open fun userDao(): UserDao = MongodbUserDao(mongoOperations!!)
    Bean open fun springMongoApp() = SpringMongoApp(userDao())
}
