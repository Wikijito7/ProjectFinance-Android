package es.wokis.projectfinance.data.dao.reaction

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import es.wokis.projectfinance.data.local.reaction.dbo.ReactionDBO

@Dao
interface ReactionDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addReaction(reaction: ReactionDBO): Long

    @Query("DELETE FROM reaction WHERE id = :id")
    fun removeReaction(id: Long): Int

    @Query("SELECT unicode FROM reaction GROUP BY unicode ORDER BY count(unicode) DESC LIMIT 5")
    fun getMostUsedEmojies(): List<String>

    @Query("DELETE FROM reaction WHERE NOT EXISTS(SELECT 1 FROM invoice WHERE id = invoiceId)")
    fun removeReactionsWithoutInvoice(): Int
}