package es.wokis.projectfinance.data.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import es.wokis.projectfinance.data.dao.category.CategoryDAO
import es.wokis.projectfinance.data.dao.invoice.InvoiceDAO
import es.wokis.projectfinance.data.dao.reaction.ReactionDAO
import es.wokis.projectfinance.data.database.AppDatabase.Companion.DATABASE_VERSION
import es.wokis.projectfinance.data.local.category.dbo.CategoryDBO
import es.wokis.projectfinance.data.local.invoice.dbo.InvoiceDBO
import es.wokis.projectfinance.data.local.reaction.dbo.ReactionDBO

@Database(
    version = DATABASE_VERSION,
    entities = [InvoiceDBO::class, CategoryDBO::class, ReactionDBO::class],
    exportSchema = true,
    autoMigrations = [AutoMigration(from = 1000003, to = 1000004)]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getInvoiceDAO(): InvoiceDAO
    abstract fun getCategoryDAO(): CategoryDAO
    abstract fun getReactionDAO(): ReactionDAO

    companion object {
        const val DATABASE_VERSION = 1000004

        val MIGRATION_1000002_1000003 = object : Migration(1000002, 1000003) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS category(`categoryId` INTEGER NOT NULL, `color` TEXT NOT NULL, `categoryName` TEXT NOT NULL, PRIMARY KEY(`categoryId`))")
                database.execSQL("INSERT INTO category(categoryId, color, categoryName) VALUES (1, '#FFAABB', 'NONE')")
                database.execSQL("ALTER TABLE invoice ADD foreignCategoryId INTEGER DEFAULT 1 NOT NULL")
            }
        }

        val MIGRATION_1000003_1000004 = object : Migration(1000003, 1000004) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS reaction(`id` INTEGER NOT NULL, `unicode` TEXT NOT NULL, `invoiceId` INTEGER NOT NULL, PRIMARY KEY(`id`))")

                database.execSQL("ALTER TABLE invoice ADD userId TEXT")
                database.execSQL("ALTER TABLE invoice ADD serverId TEXT")
                database.execSQL("ALTER TABLE invoice ADD synchronize INTEGER DEFAULT 1 NOT NULL")
                database.execSQL("ALTER TABLE invoice ADD updated INTEGER DEFAULT 0 NOT NULL")
            }
        }
    }
}