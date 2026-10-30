package com.example.data.repository

import com.example.data.local.dao.NoteDao
import com.example.data.local.entity.NoteItem
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {
    val allNotes: Flow<List<NoteItem>> = noteDao.getAllNotes()

    fun searchNotes(query: String): Flow<List<NoteItem>> = noteDao.searchNotes(query)

    fun getNoteById(id: Long): Flow<NoteItem?> = noteDao.getNoteById(id)

    suspend fun getNoteByIdSync(id: Long): NoteItem? = noteDao.getNoteByIdSync(id)

    suspend fun getPendingNoteRemindersSync(currentTime: Long): List<NoteItem> =
        noteDao.getPendingNoteRemindersSync(currentTime)

    suspend fun insertNote(note: NoteItem): Long = noteDao.insertNote(note)

    suspend fun updateNote(note: NoteItem) = noteDao.updateNote(note)

    suspend fun deleteNote(note: NoteItem) = noteDao.deleteNote(note)

    suspend fun deleteNoteById(id: Long) = noteDao.deleteNoteById(id)

    suspend fun togglePinned(id: Long, pinned: Boolean) = noteDao.updateNotePinned(id, pinned)

    suspend fun toggleFavorite(id: Long, favorite: Boolean) = noteDao.updateNoteFavorite(id, favorite)

    suspend fun deleteAllNotes() = noteDao.deleteAllNotes()
}
