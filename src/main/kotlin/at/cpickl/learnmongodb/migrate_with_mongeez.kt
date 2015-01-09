package at.cpickl.learnmongodb

import kotlin.platform.platformStatic
import com.mongodb.MongoClient
import org.mongeez.MongeezRunner
import org.springframework.core.io.ClassPathResource

// http://www.javacodegeeks.com/2014/10/mongodb-incremental-migration-scripts.html
// https://github.com/secondmarket/mongeez
object MigrateMongo {

    private val DB_NAME = "mongeez1"

    platformStatic fun main(args: Array<String>) {
        println("Migrate START")
        val mongo = MongoClient("localhost")

        val manager = MongoManager(mongo)
        manager.dumpMetaData()

        val mongeez = MongeezRunner()
        mongeez.setDbName(DB_NAME)
        mongeez.setExecuteEnabled(true)
        mongeez.setMongo(mongo)
        mongeez.setFile(ClassPathResource("/migrate/mongeez.xml"))
        mongeez.execute()
        manager.dumpMetaData()
        println("Migrate END")
    }

}