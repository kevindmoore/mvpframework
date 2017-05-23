package com.mastertechsoftware.mvpframework

import com.mastertechsoftware.easysqllibrary.sql.DatabaseManager
import com.mastertechsoftware.easysqllibrary.sql.ReflectTableInterface
import com.mastertechsoftware.easysqllibrary.sql.upgrade.UpgradeStrategy


/**
 * Handles all aspects of Data management
 */
open class DataManager {
    var databaseManager : DatabaseManager

    init {
        databaseManager = DatabaseManager.getInstance()
    }

    /**
     * Set the upgrade strategy
     * @param upgradeStrategy
     */
    fun setUpgradeStrategy(upgradeStrategy: UpgradeStrategy) {
        databaseManager.setUpgradeStrategy(upgradeStrategy)
    }

    fun addDatabase(dbName: String, mainTableName: String, vararg types: Class<out ReflectTableInterface>) {
        databaseManager.addDatabase(dbName, mainTableName, *types)
    }

    fun addDatabase(dbName: String, mainTableName: String, version: Int, vararg types: Class<out ReflectTableInterface>) {
        databaseManager.addDatabase(dbName, mainTableName, version, *types)
    }

    fun getAllItems(dbName: String, type: Class<out ReflectTableInterface>) : MutableList<out ReflectTableInterface> {
        return databaseManager.getAllItems(dbName, type)
    }

    fun addItem(dbname: String, type: Class<out ReflectTableInterface>, item: ReflectTableInterface) : Int {
        return databaseManager.addItem(dbname, type, item)
    }

    fun updateItem(dbName: String, type: Class<out ReflectTableInterface>, data: ReflectTableInterface)  {
        databaseManager.updateItem(dbName, type, data)
    }

    fun deleteAllItems(dbName: String, type: Class<out ReflectTableInterface>) {
        databaseManager.deleteAllItems(dbName, type)
    }

    fun deleteItem(dbName: String, type: Class<out ReflectTableInterface>, id : Int)  {
        databaseManager.deleteItem(dbName, type, id)
    }

    // NOTE: If you delete the database, you'll need to rebuild your code
    fun deleteDatabase(dbName: String) {
        databaseManager.deleteDatabase(dbName)
    }

}