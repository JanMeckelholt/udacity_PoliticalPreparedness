package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.politicalpreparedness.elections.model.Election

@Dao
interface ElectionDao {

    @Query("select * from election_table")
    fun getElectionsLiveData(): LiveData<List<Election>?>

    @Query("select * from election_table")
    fun getElections(): List<Election>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg elections: Election)
    @Delete
    fun delete(election: Election)

    @Query("select * from election_table where id =:id")
    fun getElection(id: Int): Election?

    @Query("delete from election_table")
    fun clearTable()
}