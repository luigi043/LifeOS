package com.luigi.lifeos.sync

import kotlinx.coroutines.flow.Flow

/**
 * Contract implemented by each feature's sync adapter. The sync engine
 * iterates over registered adapters during a sync pass rather than
 * hard-coding feature knowledge.
 *
 * Pull: stream remote changes since [since].
 * Push: collect dirty local rows and serialise them to a transport DTO.
 */
interface Syncable<T> {
    suspend fun pull(since: Long): List<T>
    suspend fun push(dirty: List<T>)
    fun observeDirty(): Flow<List<T>>
}
