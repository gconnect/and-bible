package net.bible.android.control.footnoteandref;

import net.bible.android.BibleApplication;
import net.bible.android.activity.R;
import net.bible.android.control.ApplicationScope;
import net.bible.android.control.page.CurrentPageManager;
import net.bible.android.control.page.window.ActiveWindowPageManagerProvider;
import net.bible.android.control.versification.BibleTraverser;
import net.bible.android.view.activity.base.Dialogs;
import net.bible.service.format.Note;

import org.apache.commons.lang3.StringUtils;
import org.crosswire.jsword.passage.VerseRange;
import org.crosswire.jsword.versification.BookName;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/** Support the Compare Translations screen
 * 
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's author.
 */
@ApplicationScope
public class FootnoteAndRefControl {

	private final BibleTraverser bibleTraverser;

	private final ActiveWindowPageManagerProvider activeWindowPageManagerProvider;

	@SuppressWarnings("unused")
	private static final String TAG = "FootnoteAndRefControl";

	@Inject
	public FootnoteAndRefControl(BibleTraverser bibleTraverser, ActiveWindowPageManagerProvider activeWindowPageManagerProvider) {
		this.bibleTraverser = bibleTraverser;
		this.activeWindowPageManagerProvider = activeWindowPageManagerProvider;
	}

	public List<Note> getCurrentPageFootnotesAndReferences() {
		try {
			return getCurrentPageManager().getCurrentPage().getCurrentPageFootnotesAndReferences();
		} catch (Exception e) {
			Dialogs.getInstance().showErrorMsg(R.string.error_occurred, e);
			return new ArrayList<>();
		}
	}
	
	public String getTitle(VerseRange verseRange) {
		StringBuilder stringBuilder = new StringBuilder();
		boolean wasFullBookname = BookName.isFullBookName();
		BookName.setFullBookName(false);
		
		stringBuilder.append(BibleApplication.getApplication().getString(R.string.notes))
					 .append(": ")
					 .append(verseRange.getName());
		
		BookName.setFullBookName(wasFullBookname);
		return stringBuilder.toString();
	}
	
	/** Shuffle verseRange forward but stay in same chapter because those are the only notes fetched
	 */
	public VerseRange next(VerseRange verseRange) {
		return bibleTraverser.getNextVerseRange(getCurrentPageManager().getCurrentPassageDocument(), verseRange, false);
	}

	/** Shuffle verseRange backward but stay in same chapter because those are the only notes fetched
	 */
	public VerseRange previous(VerseRange verseRange) {
		return bibleTraverser.getPreviousVerseRange(getCurrentPageManager().getCurrentPassageDocument(), verseRange, false);
	}
	
	public CurrentPageManager getCurrentPageManager() {
		return activeWindowPageManagerProvider.getActiveWindowPageManager();
	}

	/**
	 * Jump to the verse in the ref
	 * If the osisRef is available then use that because sometimes the noteText itself misses out the book of the bible
	 */
	public void navigateTo(Note note) {
		String ref;
		if (StringUtils.isNotEmpty(note.getOsisRef())) {
			ref = note.getOsisRef();
		} else {
			ref = note.getNoteText();
		}

		CurrentPageManager currentPageControl = activeWindowPageManagerProvider.getActiveWindowPageManager();
		currentPageControl.getCurrentBible().setKey(ref);
		currentPageControl.showBible();
	}
}
