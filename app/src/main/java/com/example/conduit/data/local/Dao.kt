package com.example.conduit.data.local
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.conduit.data.local.offlineModel.OfflineArticle
import com.example.conduit.data.local.offlineModel.OfflineArticleResponse
import com.example.conduit.model.Author
import com.example.conduit.model.UserX

@Dao
interface AuthDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(userX: UserX)

    @Query("SELECT * FROM userx")
    suspend fun getUser():UserX


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(offlineArticle: OfflineArticle)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuthor(author: Author)

    @Query("SELECT * FROM offlinearticle INNER JOIN author ON offlinearticle.username = author.username WHERE offlinearticle.type = :type ORDER BY offlinearticle.createdAt ")
    suspend fun getInsertedArticles(type:String) : List<OfflineArticleResponse>


}