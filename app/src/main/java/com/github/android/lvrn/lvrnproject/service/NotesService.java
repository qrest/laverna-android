package com.github.android.lvrn.lvrnproject.service;

import com.github.android.lvrn.lvrnproject.persistent.entity.impl.Note;
import com.github.android.lvrn.lvrnproject.persistent.entity.impl.Notebook;
import com.github.android.lvrn.lvrnproject.persistent.entity.impl.Tag;
import com.github.android.lvrn.lvrnproject.service.core.ProfileDependedService;

import java.util.List;

/**
 * @author Vadim Boitsov <vadimboitsov1@gmail.com>
 */

public interface NotesService extends ProfileDependedService<Note> {

    void create(String profileId,
                String notebookId,
                String title,
                String content,
                boolean isFavorite) throws IllegalArgumentException;

    List<Note> getByNotebook(Notebook notebook, int from, int amount);

    List<Note> getByTag(Tag tag, int from, int amount);
}
