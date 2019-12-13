package ru.olegivo.afs.common.db

abstract class AfsDaoTest<DAO>(daoProvider: (AfsDatabase).() -> DAO) :
    BaseDaoTest<AfsDatabase, DAO>(
        AfsDatabase::class.java, daoProvider)