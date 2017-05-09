package com.github.android.lvrn.lvrnproject.service.extension;

import com.github.android.lvrn.lvrnproject.BuildConfig;
import com.github.android.lvrn.lvrnproject.persistent.database.DatabaseManager;
import com.github.android.lvrn.lvrnproject.persistent.entity.Profile;
import com.github.android.lvrn.lvrnproject.persistent.entity.Tag;
import com.github.android.lvrn.lvrnproject.persistent.entity.Task;
import com.github.android.lvrn.lvrnproject.persistent.repository.extension.impl.NoteRepositoryImpl;
import com.github.android.lvrn.lvrnproject.persistent.repository.extension.impl.NotebookRepositoryImpl;
import com.github.android.lvrn.lvrnproject.persistent.repository.extension.impl.ProfileRepositoryImpl;
import com.github.android.lvrn.lvrnproject.persistent.repository.extension.impl.TagRepositoryImpl;
import com.github.android.lvrn.lvrnproject.persistent.repository.extension.impl.TaskRepositoryImpl;
import com.github.android.lvrn.lvrnproject.service.extension.impl.NoteServiceImpl;
import com.github.android.lvrn.lvrnproject.service.extension.impl.NotebookServiceImpl;
import com.github.android.lvrn.lvrnproject.service.extension.impl.ProfileServiceImpl;
import com.github.android.lvrn.lvrnproject.service.extension.impl.TagServiceImpl;
import com.github.android.lvrn.lvrnproject.service.extension.impl.TaskServiceImpl;
import com.github.android.lvrn.lvrnproject.service.form.NoteForm;
import com.github.android.lvrn.lvrnproject.service.form.ProfileForm;
import com.google.common.base.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Vadim Boitsov <vadimboitsov1@gmail.com>
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class NoteServiceImplTest {

    private Profile profile;

    private NoteService noteService;

    @Before
    public void setUp() {
        DatabaseManager.initializeInstance(RuntimeEnvironment.application);

        ProfileService profileService = new ProfileServiceImpl(new ProfileRepositoryImpl());
        profileService.openConnection();
        profileService.create(new ProfileForm("Temp profile"));
        profile = profileService.getAll().get(0);
        profileService.closeConnection();

        noteService = new NoteServiceImpl(
                new NoteRepositoryImpl(),
                new TaskServiceImpl(new TaskRepositoryImpl(), profileService),
                new TagServiceImpl(new TagRepositoryImpl(), profileService),
                profileService,
                new NotebookServiceImpl(new NotebookRepositoryImpl(), profileService));

        noteService.openConnection();
    }

    @Test
    public void serviceShouldCreateNote() {
        assertThat(noteService.create(new NoteForm(profile.getId(), null, "Note Title", "Content", false))
                .isPresent())
                .isTrue();
    }

    @Test
    public void serviceShouldNotCreateNote() {
        assertThat(noteService.create(new NoteForm(profile.getId(), null, "", "Content", false))
                .isPresent())
                .isFalse();

        assertThat(noteService.create(new NoteForm(null, null, "hjkh", "Content", false))
                .isPresent())
                .isFalse();

        assertThat(noteService.create(new NoteForm(profile.getId(), "dfdfs", "hjkh", "Content", false))
                .isPresent())
                .isFalse();
    }

    @Test
    public void serviceShouldUpdateNote() {
        Optional<String> noteIdOptional = noteService.create(new NoteForm(profile.getId(), null, "Note Title", "Content", false));
        assertThat(noteIdOptional.isPresent()).isTrue();

        assertThat(noteService.update(noteIdOptional.get(), new NoteForm(null, null, "new Title", "new content", true)))
                .isTrue();
    }

    @Test
    public void serviceShouldAddTagsAndTasksToDatabase() {
        String content = "Content\n #my_first_tag #my_second tag\n well#it's_not_a_tag\n"
                + "[] what about task?\n" +
                "[X] of course\n"
                + "[]That's not a task";

        Optional<String> noteIdOptional = noteService.create(new NoteForm(profile.getId(), null, "Note Title", content, false));
        assertThat(noteIdOptional.isPresent()).isTrue();

        TagService tagService = new TagServiceImpl(new TagRepositoryImpl(), new ProfileServiceImpl(new ProfileRepositoryImpl()));
        tagService.openConnection();
        List<Tag> tags = tagService.getByNote(noteIdOptional.get());
        tagService.closeConnection();

        assertThat(tags).hasSize(2);

        TaskService taskService = new TaskServiceImpl(new TaskRepositoryImpl(), new ProfileServiceImpl(new ProfileRepositoryImpl()));
        taskService.openConnection();
        List<Task> tasks = taskService.getByNote(noteIdOptional.get());
        taskService.closeConnection();

        assertThat(tasks).hasSize(2);
    }

    @Test
    public void serviceShouldUpdateTagsAndTasksInDatabase() {
        String content1 = "Content\n #my_first_tag #my_second tag\n well#it's_not_a_tag\n"
                + "[] what about task?\n" +
                "[X] of course\n"
                + "[]That's not a task";

        Optional<String> noteIdOptional = noteService.create(new NoteForm(profile.getId(), null, "Note Title", content1, false));
        assertThat(noteIdOptional.isPresent()).isTrue();

        String content2 = "Content\n #my_second tag_first_delted\n well#it''s_not_a_tag\n"
                + "#new_third_tag #and_new_fourth_tag\n"
                + "[X] of course\n"
                + "[]That''s not a task\n"
                + "[] new task, yeap\n"
                + "[X] another one";

        assertThat(noteService.update(noteIdOptional.get(), new NoteForm(profile.getId(), null, "new title", content2, true)));

        TagService tagService = new TagServiceImpl(new TagRepositoryImpl(), new ProfileServiceImpl(new ProfileRepositoryImpl()));
        tagService.openConnection();
        List<Tag> tags = tagService.getByNote(noteIdOptional.get());
        tagService.closeConnection();

        assertThat(tags).hasSize(3);

        TaskService taskService = new TaskServiceImpl(new TaskRepositoryImpl(), new ProfileServiceImpl(new ProfileRepositoryImpl()));
        taskService.openConnection();
        List<Task> tasks = taskService.getByNote(noteIdOptional.get());
        taskService.closeConnection();

        assertThat(tasks).hasSize(3);
    }





    @After
    public void finish() {
        noteService.closeConnection();
    }
}