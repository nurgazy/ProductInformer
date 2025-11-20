package com.nurgazy_bolushbekov.product_informer.barcode_collection.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.nurgazy_bolushbekov.product_informer.barcode_collection.entity.BarcodeDoc
import com.nurgazy_bolushbekov.product_informer.barcode_collection.entity.BarcodeDocDetail
import com.nurgazy_bolushbekov.product_informer.barcode_collection.entity.BarcodeWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface BarcodeDocDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBarcode(barcodeDoc: BarcodeDoc): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBarcodeDetails(barcodeDocDetails: List<BarcodeDocDetail>)

    @Update
    suspend fun updateBarcode(barcodeDoc: BarcodeDoc)

    @Delete
    suspend fun deleteBarcodeDoc(doc: BarcodeDoc)

    @Transaction
    suspend fun saveBarcodeDoc(barcodeDoc: BarcodeDoc, barcodeDocDetails: List<BarcodeDocDetail>) {
        val barcodeDocId = insertBarcode(barcodeDoc)
        val barcodeDocDetailsToSave = barcodeDocDetails.map { it.copy(barcodeDocId = barcodeDocId) }
        insertBarcodeDetails(barcodeDocDetailsToSave)
    }

    @Query("SELECT * FROM barcode")
    fun getAllBarcodeDocs(): Flow<List<BarcodeDoc>>

    @Query("SELECT * FROM barcode WHERE barcodeDocId = :barcodeDocId")
    fun getBarcodeDocById(barcodeDocId: Long): Flow<BarcodeDoc>

    @Transaction
    @Query("SELECT * FROM barcode WHERE barcodeDocId = :barcodeDocId")
    fun getBarcodeDocWithDetailsById(barcodeDocId: Long): Flow<BarcodeWithDetails>
}