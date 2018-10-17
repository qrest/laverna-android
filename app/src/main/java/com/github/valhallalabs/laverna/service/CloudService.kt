package com.github.valhallalabs.laverna.service

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonValue

interface CloudService {

    fun pullProfiles(): Set<String>
    fun pullNotebooks(profileName: String): List<NotebookJson>
    fun pullNotes(profileName: String): List<NoteJson>
    fun pullTags(profileName: String): List<TagJson>

    fun pushProfiles(profiles: Set<String>)
    fun pushNotebooks(profileName: String, notebooks: List<NotebookJson>)
    fun pushNotes(profileName: String, notes: List<NoteJson>)
    fun pushTags(profileName: String, tags: List<TagJson>)



    abstract class JsonEntity {
        abstract val id: String
        abstract val type: EntityType

        enum class EntityType(@JsonValue val value: String) {
            NOTE("notes"), NOTEBOOK("notebooks"), TAG("tags")
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class NoteJson (
            override val id: String,
            override val type: EntityType,
            val title: String,
            val content: String?,
            val taskAll: Int?,
            val taskCompleted: Int?,
            val created: Long,
            val updated: Long,
            val notebookId: String,
            val isFavorite: Boolean,
            val trash: Boolean,
            val tags: List<Any>?,
            val files: List<Any>?
    ) : JsonEntity()

    data class NotebookJson (
            override val id: String,
            override val type: EntityType,
            var parentId: String,
            var name: String,
            var count: Int,
            var trash: Boolean,
            var created: Long,
            var updated: Long
    ) : JsonEntity()

    data class TagJson (
            override val id: String,
            override val type: EntityType,
            var name: String,
            var count: String,
            var trash: Boolean,
            var created: Long,
            var updated: Long
    ) : JsonEntity()

}
