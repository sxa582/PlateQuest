package com.platequest.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends Activity {
    private static final String PREFS = "plate_quest_data";
    private static final String KEY_HISTORY = "recent_history";
    private static final String KEY_PLATE_COUNT = "plate_count";

    private static final int INK = Color.rgb(15, 32, 58);
    private static final int MIDNIGHT = Color.rgb(23, 43, 82);
    private static final int DEEP_PURPLE = Color.rgb(75, 65, 168);
    private static final int INDIGO = Color.rgb(102, 87, 232);
    private static final int SKY = Color.rgb(83, 181, 255);
    private static final int MINT = Color.rgb(38, 197, 138);
    private static final int MINT_DARK = Color.rgb(21, 148, 99);
    private static final int GOLD = Color.rgb(255, 191, 73);
    private static final int BG = Color.rgb(244, 247, 252);
    private static final int CARD = Color.WHITE;
    private static final int TEXT = Color.rgb(36, 49, 72);
    private static final int MUTED = Color.rgb(111, 124, 148);
    private static final int BORDER = Color.rgb(222, 230, 241);
    private static final int SOFT_PURPLE = Color.rgb(241, 239, 255);
    private static final int SOFT_MINT = Color.rgb(231, 250, 242);
    private static final int SOFT_BLUE = Color.rgb(235, 247, 255);
    private static final int SOFT_GOLD = Color.rgb(255, 248, 228);
    private static final int DANGER = Color.rgb(190, 57, 78);

    private SharedPreferences prefs;
    private FrameLayout content;
    private TextView playNav;
    private TextView progressNav;
    private int selectedPosition = 0;
    private boolean formattingInput = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        Window window = getWindow();
        window.setStatusBarColor(MIDNIGHT);
        window.setNavigationBarColor(Color.WHITE);

        setContentView(buildAppShell());
        showHome();
    }

    private View buildAppShell() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);

        root.addView(buildHeader(), new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(78)));

        content = new FrameLayout(this);
        root.addView(content, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        root.addView(buildBottomNav(), new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(78)));
        return root;
    }

    private View buildHeader() {
        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(18), dp(10), dp(18), dp(10));
        header.setBackground(gradient(MIDNIGHT, DEEP_PURPLE, 0));

        TextView logo = text("PQ", 16, Color.WHITE, Typeface.BOLD);
        logo.setGravity(Gravity.CENTER);
        logo.setLetterSpacing(0.04f);
        logo.setBackground(roundRect(Color.argb(38, 255, 255, 255), 16,
                Color.argb(90, 255, 255, 255), 1));
        header.addView(logo, new LinearLayout.LayoutParams(dp(50), dp(50)));

        LinearLayout titles = new LinearLayout(this);
        titles.setOrientation(LinearLayout.VERTICAL);
        titles.setPadding(dp(13), 0, 0, 0);
        TextView title = text("PLATE QUEST", 19, Color.WHITE, Typeface.BOLD);
        title.setLetterSpacing(0.08f);
        TextView subtitle = text("Collect every character, one spot at a time", 11,
                Color.rgb(211, 220, 242), Typeface.NORMAL);
        titles.addView(title);
        titles.addView(subtitle);
        header.addView(titles, new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView positions = text("7\nSPOTS", 10, Color.WHITE, Typeface.BOLD);
        positions.setGravity(Gravity.CENTER);
        positions.setLineSpacing(0, 0.95f);
        positions.setBackground(roundRect(Color.argb(35, 255, 255, 255), 15,
                Color.argb(75, 255, 255, 255), 1));
        header.addView(positions, new LinearLayout.LayoutParams(dp(52), dp(48)));
        return header;
    }

    private View buildBottomNav() {
        LinearLayout nav = new LinearLayout(this);
        nav.setGravity(Gravity.CENTER);
        nav.setPadding(dp(12), dp(9), dp(12), dp(9));
        nav.setBackground(roundRect(Color.WHITE, 0, BORDER, 1));
        nav.setElevation(dp(8));

        playNav = navButton("●", "PLAY");
        progressNav = navButton("▦", "COLLECTION");
        playNav.setOnClickListener(v -> showHome());
        progressNav.setOnClickListener(v -> showProgress());

        nav.addView(playNav, new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f));
        LinearLayout.LayoutParams second = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        second.setMargins(dp(8), 0, 0, 0);
        nav.addView(progressNav, second);
        return nav;
    }

    private TextView navButton(String icon, String label) {
        TextView tv = text(icon + "\n" + label, 11, MUTED, Typeface.BOLD);
        tv.setGravity(Gravity.CENTER);
        tv.setLineSpacing(0, 1.05f);
        tv.setClickable(true);
        return tv;
    }

    private void setNavState(boolean home) {
        playNav.setTextColor(home ? INDIGO : MUTED);
        progressNav.setTextColor(home ? MUTED : INDIGO);
        playNav.setBackground(home ? roundRect(SOFT_PURPLE, 16, SOFT_PURPLE, 0) : null);
        progressNav.setBackground(home ? null : roundRect(SOFT_PURPLE, 16, SOFT_PURPLE, 0));
    }

    private void showHome() {
        setNavState(true);
        content.removeAllViews();

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);

        LinearLayout column = new LinearLayout(this);
        column.setOrientation(LinearLayout.VERTICAL);
        column.setPadding(dp(16), dp(16), dp(16), dp(28));

        column.addView(buildHeroCard());
        column.addView(space(14));
        column.addView(buildEntryCard());
        column.addView(space(14));
        column.addView(buildPositionDashboard());
        column.addView(space(14));
        column.addView(buildMissingPreviewCard());
        column.addView(space(14));
        column.addView(buildRecentCard());
        column.addView(space(14));

        TextView safety = text("Passenger mode only  •  Never enter plates while driving", 11,
                Color.rgb(132, 99, 31), Typeface.BOLD);
        safety.setGravity(Gravity.CENTER);
        safety.setPadding(dp(12), dp(12), dp(12), dp(12));
        safety.setBackground(roundRect(SOFT_GOLD, 14, Color.rgb(245, 218, 148), 1));
        column.addView(safety);

        scroll.addView(column);
        content.addView(scroll);
    }

    private View buildHeroCard() {
        List<Set<String>> all = loadAllPositions();
        int found = PlateTracker.totalFound(all);
        int percent = PlateTracker.percentComplete(found);
        int plates = prefs.getInt(KEY_PLATE_COUNT, 0);

        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setPadding(dp(20), dp(20), dp(20), dp(18));
        hero.setBackground(gradient(MIDNIGHT, DEEP_PURPLE, 24));
        hero.setElevation(dp(5));
        hero.setClipToOutline(true);

        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout heading = new LinearLayout(this);
        heading.setOrientation(LinearLayout.VERTICAL);
        TextView eyebrow = text("THE ROAD-TRIP LETTER HUNT", 10,
                Color.rgb(188, 206, 245), Typeface.BOLD);
        eyebrow.setLetterSpacing(0.09f);
        TextView title = text("How complete is\nyour collection?", 27,
                Color.WHITE, Typeface.BOLD);
        title.setLineSpacing(dp(1), 1f);
        title.setPadding(0, dp(5), 0, 0);
        heading.addView(eyebrow);
        heading.addView(title);
        top.addView(heading, new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView percentBadge = text(percent + "%", 24, Color.WHITE, Typeface.BOLD);
        percentBadge.setGravity(Gravity.CENTER);
        percentBadge.setBackground(roundRect(Color.argb(36, 255, 255, 255), 22,
                Color.argb(84, 255, 255, 255), 1));
        top.addView(percentBadge, new LinearLayout.LayoutParams(dp(78), dp(78)));
        hero.addView(top);

        hero.addView(space(18));
        ProgressBar progress = horizontalProgress(PlateTracker.TOTAL_SLOTS, found, SKY,
                Color.argb(38, 255, 255, 255));
        hero.addView(progress, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(10)));

        LinearLayout stats = new LinearLayout(this);
        stats.setPadding(0, dp(14), 0, 0);
        stats.addView(heroStat(String.valueOf(plates), "PLATES"),
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        stats.addView(heroStat(String.valueOf(found), "DISCOVERIES"),
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        stats.addView(heroStat(String.valueOf(PlateTracker.TOTAL_SLOTS - found), "TO GO"),
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        hero.addView(stats);
        return hero;
    }

    private View heroStat(String value, String label) {
        LinearLayout block = new LinearLayout(this);
        block.setOrientation(LinearLayout.VERTICAL);
        block.setGravity(Gravity.CENTER);
        block.addView(text(value, 19, Color.WHITE, Typeface.BOLD));
        TextView labelView = text(label, 9, Color.rgb(196, 209, 239), Typeface.BOLD);
        labelView.setLetterSpacing(0.06f);
        block.addView(labelView);
        return block;
    }

    private View buildEntryCard() {
        LinearLayout card = card();

        LinearLayout heading = new LinearLayout(this);
        heading.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout stack = new LinearLayout(this);
        stack.setOrientation(LinearLayout.VERTICAL);
        stack.addView(sectionLabel("LOG A PLATE"));
        stack.addView(text("What did you spot?", 20, INK, Typeface.BOLD));
        heading.addView(stack, new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView newBadge = text("+ NEW", 10, MINT_DARK, Typeface.BOLD);
        newBadge.setGravity(Gravity.CENTER);
        newBadge.setBackground(roundRect(SOFT_MINT, 14, Color.rgb(169, 230, 205), 1));
        heading.addView(newBadge, new LinearLayout.LayoutParams(dp(62), dp(32)));
        card.addView(heading);
        card.addView(space(14));

        LinearLayout plateFrame = new LinearLayout(this);
        plateFrame.setOrientation(LinearLayout.VERTICAL);
        plateFrame.setPadding(dp(8), dp(7), dp(8), dp(8));
        plateFrame.setBackground(roundRect(Color.rgb(250, 252, 255), 18, INK, 2));

        TextView plateStrip = text("PLATE QUEST  •  SEVEN POSITION CHALLENGE", 8,
                Color.WHITE, Typeface.BOLD);
        plateStrip.setGravity(Gravity.CENTER);
        plateStrip.setLetterSpacing(0.06f);
        plateStrip.setPadding(dp(6), dp(5), dp(6), dp(5));
        plateStrip.setBackground(roundRect(INDIGO, 10, INDIGO, 0));
        plateFrame.addView(plateStrip);

        EditText plateInput = new EditText(this);
        plateInput.setSingleLine(true);
        plateInput.setTextSize(30);
        plateInput.setTextColor(INK);
        plateInput.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        plateInput.setGravity(Gravity.CENTER);
        plateInput.setHint("ABC-1234");
        plateInput.setHintTextColor(Color.rgb(177, 185, 200));
        plateInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        plateInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
        plateInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
        plateInput.setBackgroundColor(Color.TRANSPARENT);
        plateInput.setPadding(dp(8), 0, dp(8), 0);
        plateFrame.addView(plateInput, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(68)));
        card.addView(plateFrame);

        plateInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable editable) {
                if (formattingInput) return;
                formattingInput = true;
                String clean = PlateTracker.normalize(editable.toString());
                if (clean.length() > PlateTracker.POSITION_COUNT) {
                    clean = clean.substring(0, PlateTracker.POSITION_COUNT);
                }
                String formatted = PlateTracker.formatForDisplay(clean);
                plateInput.setText(formatted);
                plateInput.setSelection(formatted.length());
                formattingInput = false;
            }
        });

        plateInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                recordPlate(plateInput);
                return true;
            }
            return false;
        });

        card.addView(space(12));
        TextView record = actionButton("RECORD THIS PLATE  →");
        record.setOnClickListener(v -> recordPlate(plateInput));
        card.addView(record, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(56)));

        TextView helper = text("Each letter or number is credited only in the exact position where it appears.",
                11, MUTED, Typeface.NORMAL);
        helper.setGravity(Gravity.CENTER);
        helper.setPadding(dp(6), dp(10), dp(6), 0);
        card.addView(helper);
        return card;
    }

    private void recordPlate(EditText input) {
        String clean = PlateTracker.normalize(input.getText().toString());
        if (!PlateTracker.isValid(clean)) {
            input.setError("Enter exactly seven letters or numbers");
            input.requestFocus();
            return;
        }

        List<Set<String>> all = loadAllPositions();
        List<PlateTracker.Discovery> discoveries = PlateTracker.findNewDiscoveries(clean, all);

        SharedPreferences.Editor editor = prefs.edit();
        for (PlateTracker.Discovery discovery : discoveries) {
            Set<String> updated = new HashSet<>(all.get(discovery.positionIndex));
            updated.add(discovery.character);
            editor.putStringSet(positionKey(discovery.positionIndex), updated);
        }
        editor.putInt(KEY_PLATE_COUNT, prefs.getInt(KEY_PLATE_COUNT, 0) + 1);
        saveHistory(editor, PlateTracker.formatForDisplay(clean));
        editor.apply();

        input.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
        hideKeyboard(input);
        input.setText("");

        String message;
        if (discoveries.isEmpty()) {
            message = "Plate saved. No new position discoveries this time.";
        } else if (discoveries.size() == 1) {
            PlateTracker.Discovery d = discoveries.get(0);
            message = "New find: " + d.character + " in position " + (d.positionIndex + 1) + ".";
        } else {
            message = discoveries.size() + " new position discoveries!";
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        showHome();
    }

    private View buildPositionDashboard() {
        List<Set<String>> all = loadAllPositions();
        LinearLayout card = card();

        LinearLayout heading = new LinearLayout(this);
        heading.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout titles = new LinearLayout(this);
        titles.setOrientation(LinearLayout.VERTICAL);
        titles.addView(sectionLabel("SUCCESS BY POSITION"));
        titles.addView(text("Your seven checkpoints", 20, INK, Typeface.BOLD));
        heading.addView(titles, new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView open = smallLink("VIEW ALL");
        open.setOnClickListener(v -> showProgress());
        heading.addView(open);
        card.addView(heading);
        card.addView(space(14));

        LinearLayout firstRow = new LinearLayout(this);
        for (int i = 0; i < 4; i++) {
            firstRow.addView(positionTile(i, all.get(i)), tileParams(i));
        }
        card.addView(firstRow);
        card.addView(space(8));

        LinearLayout secondRow = new LinearLayout(this);
        for (int i = 4; i < 7; i++) {
            secondRow.addView(positionTile(i, all.get(i)), tileParams(i - 4));
        }
        card.addView(secondRow);
        return card;
    }

    private LinearLayout.LayoutParams tileParams(int indexInRow) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, dp(102), 1f);
        if (indexInRow > 0) lp.setMargins(dp(8), 0, 0, 0);
        return lp;
    }

    private View positionTile(int index, Set<String> discovered) {
        int count = discovered.size();
        int percent = Math.round(count * 100f / PlateTracker.CHARACTERS.length());

        LinearLayout tile = new LinearLayout(this);
        tile.setOrientation(LinearLayout.VERTICAL);
        tile.setGravity(Gravity.CENTER);
        tile.setPadding(dp(8), dp(8), dp(8), dp(8));
        tile.setBackground(roundRect(index % 2 == 0 ? SOFT_PURPLE : SOFT_BLUE,
                16, index % 2 == 0 ? Color.rgb(215, 208, 255) : Color.rgb(198, 230, 250), 1));
        tile.setClickable(true);

        TextView pos = text("P" + (index + 1), 10, index % 2 == 0 ? INDIGO : Color.rgb(32, 132, 194),
                Typeface.BOLD);
        pos.setLetterSpacing(0.05f);
        tile.addView(pos);
        tile.addView(text(percent + "%", 20, INK, Typeface.BOLD));
        TextView found = text(count + " / 36", 10, MUTED, Typeface.BOLD);
        tile.addView(found);

        ProgressBar bar = horizontalProgress(36, count, index % 2 == 0 ? INDIGO : SKY, BORDER);
        LinearLayout.LayoutParams barLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(6));
        barLp.setMargins(0, dp(7), 0, 0);
        tile.addView(bar, barLp);

        tile.setOnClickListener(v -> {
            selectedPosition = index;
            showProgress();
        });
        return tile;
    }

    private View buildMissingPreviewCard() {
        List<Set<String>> all = loadAllPositions();
        int focus = closestIncompletePosition(all);
        Set<String> discovered = all.get(focus);
        List<String> missingLetters = missingCharacters("ABCDEFGHIJKLMNOPQRSTUVWXYZ", discovered);
        List<String> missingNumbers = missingCharacters("0123456789", discovered);

        LinearLayout card = card();
        LinearLayout heading = new LinearLayout(this);
        heading.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout titles = new LinearLayout(this);
        titles.setOrientation(LinearLayout.VERTICAL);
        titles.addView(sectionLabel("NEXT TARGETS"));
        titles.addView(text("Position " + (focus + 1) + " is closest to complete", 18,
                INK, Typeface.BOLD));
        heading.addView(titles, new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView badge = text((36 - discovered.size()) + " LEFT", 10, Color.rgb(132, 93, 25),
                Typeface.BOLD);
        badge.setGravity(Gravity.CENTER);
        badge.setBackground(roundRect(SOFT_GOLD, 14, Color.rgb(242, 216, 151), 1));
        heading.addView(badge, new LinearLayout.LayoutParams(dp(68), dp(34)));
        card.addView(heading);
        card.addView(space(13));

        card.addView(targetRow("LETTERS", missingLetters, SOFT_PURPLE, INDIGO));
        card.addView(space(8));
        card.addView(targetRow("NUMBERS", missingNumbers, SOFT_BLUE, Color.rgb(33, 133, 194)));

        TextView open = smallLink("OPEN FULL MISSING LIST  →");
        open.setGravity(Gravity.CENTER);
        open.setPadding(0, dp(14), 0, 0);
        final int selected = focus;
        open.setOnClickListener(v -> {
            selectedPosition = selected;
            showProgress();
        });
        card.addView(open);
        return card;
    }

    private View targetRow(String label, List<String> missing, int fill, int accent) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(dp(12), dp(11), dp(12), dp(11));
        row.setBackground(roundRect(fill, 14, fill, 0));

        TextView title = text(label + "  •  " + missing.size() + " remaining", 10, accent,
                Typeface.BOLD);
        title.setLetterSpacing(0.04f);
        row.addView(title);

        String value = missing.isEmpty() ? "Complete" : String.join("  ", missing);
        TextView items = text(value, 13, missing.isEmpty() ? MINT_DARK : TEXT, Typeface.BOLD);
        items.setLineSpacing(dp(4), 1f);
        items.setPadding(0, dp(6), 0, 0);
        row.addView(items);
        return row;
    }

    private int closestIncompletePosition(List<Set<String>> all) {
        int bestIndex = 0;
        int bestCount = -1;
        for (int i = 0; i < all.size(); i++) {
            int count = all.get(i).size();
            if (count < 36 && count > bestCount) {
                bestCount = count;
                bestIndex = i;
            }
        }
        if (bestCount == -1) return 0;
        return bestIndex;
    }

    private View buildRecentCard() {
        LinearLayout card = card();
        LinearLayout heading = new LinearLayout(this);
        heading.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout titles = new LinearLayout(this);
        titles.setOrientation(LinearLayout.VERTICAL);
        titles.addView(sectionLabel("RECENT PLATES"));
        titles.addView(text("Your latest sightings", 20, INK, Typeface.BOLD));
        heading.addView(titles, new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView local = text("ON DEVICE", 9, MINT_DARK, Typeface.BOLD);
        local.setGravity(Gravity.CENTER);
        local.setBackground(roundRect(SOFT_MINT, 13, Color.rgb(169, 230, 205), 1));
        heading.addView(local, new LinearLayout.LayoutParams(dp(72), dp(31)));
        card.addView(heading);
        card.addView(space(12));

        List<String> history = loadHistory();
        if (history.isEmpty()) {
            LinearLayout empty = new LinearLayout(this);
            empty.setOrientation(LinearLayout.VERTICAL);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(dp(14), dp(22), dp(14), dp(22));
            empty.setBackground(roundRect(BG, 14, BORDER, 1));
            empty.addView(text("◌", 26, Color.rgb(176, 187, 205), Typeface.NORMAL));
            TextView line = text("No plates yet. Your first discovery starts here.", 12,
                    MUTED, Typeface.BOLD);
            line.setGravity(Gravity.CENTER);
            line.setPadding(0, dp(6), 0, 0);
            empty.addView(line);
            card.addView(empty);
        } else {
            for (int i = 0; i < Math.min(5, history.size()); i++) {
                card.addView(recentPlateRow(history.get(i), i));
                if (i < Math.min(5, history.size()) - 1) card.addView(space(7));
            }
        }
        return card;
    }

    private View recentPlateRow(String plateValue, int index) {
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(10), dp(9), dp(10), dp(9));
        row.setBackground(roundRect(index % 2 == 0 ? BG : Color.WHITE, 13, BORDER, 1));

        TextView number = text(String.valueOf(index + 1), 11, MUTED, Typeface.BOLD);
        number.setGravity(Gravity.CENTER);
        number.setBackground(roundRect(Color.WHITE, 10, BORDER, 1));
        row.addView(number, new LinearLayout.LayoutParams(dp(32), dp(32)));

        TextView plate = text(plateValue, 16, INK, Typeface.BOLD);
        plate.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        plate.setPadding(dp(11), 0, 0, 0);
        row.addView(plate, new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView done = text("FOUND  ✓", 9, MINT_DARK, Typeface.BOLD);
        done.setGravity(Gravity.CENTER);
        done.setBackground(roundRect(SOFT_MINT, 11, SOFT_MINT, 0));
        row.addView(done, new LinearLayout.LayoutParams(dp(70), dp(30)));
        return row;
    }

    private void showProgress() {
        setNavState(false);
        content.removeAllViews();

        ScrollView scroll = new ScrollView(this);
        scroll.setClipToPadding(false);
        LinearLayout column = new LinearLayout(this);
        column.setOrientation(LinearLayout.VERTICAL);
        column.setPadding(dp(16), dp(16), dp(16), dp(28));

        column.addView(buildProgressHero());
        column.addView(space(14));
        column.addView(buildPositionSelector());
        column.addView(space(14));
        column.addView(buildCharacterGridCard());
        column.addView(space(14));
        column.addView(buildAllPositionSummary());
        column.addView(space(14));

        TextView reset = text("RESET ALL GAME PROGRESS", 12, DANGER, Typeface.BOLD);
        reset.setGravity(Gravity.CENTER);
        reset.setPadding(dp(12), dp(15), dp(12), dp(15));
        reset.setBackground(roundRect(Color.rgb(255, 241, 244), 14,
                Color.rgb(245, 190, 201), 1));
        reset.setOnClickListener(v -> confirmReset());
        column.addView(reset);

        scroll.addView(column);
        content.addView(scroll);
    }

    private View buildProgressHero() {
        Set<String> discovered = loadPosition(selectedPosition);
        int percent = Math.round(discovered.size() * 100f / 36f);

        LinearLayout hero = new LinearLayout(this);
        hero.setGravity(Gravity.CENTER_VERTICAL);
        hero.setPadding(dp(18), dp(18), dp(18), dp(18));
        hero.setBackground(gradient(MIDNIGHT, DEEP_PURPLE, 22));
        hero.setElevation(dp(5));
        hero.setClipToOutline(true);

        TextView position = text(String.valueOf(selectedPosition + 1), 30,
                Color.WHITE, Typeface.BOLD);
        position.setGravity(Gravity.CENTER);
        position.setBackground(roundRect(Color.argb(34, 255, 255, 255), 22,
                Color.argb(85, 255, 255, 255), 1));
        hero.addView(position, new LinearLayout.LayoutParams(dp(76), dp(76)));

        LinearLayout middle = new LinearLayout(this);
        middle.setOrientation(LinearLayout.VERTICAL);
        middle.setPadding(dp(15), 0, dp(12), 0);
        TextView label = text("POSITION COLLECTION", 9,
                Color.rgb(194, 209, 241), Typeface.BOLD);
        label.setLetterSpacing(0.08f);
        middle.addView(label);
        middle.addView(text(discovered.size() + " of 36 found", 22,
                Color.WHITE, Typeface.BOLD));
        ProgressBar bar = horizontalProgress(36, discovered.size(), SKY,
                Color.argb(40, 255, 255, 255));
        LinearLayout.LayoutParams barLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(8));
        barLp.setMargins(0, dp(9), 0, 0);
        middle.addView(bar, barLp);
        hero.addView(middle, new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView pct = text(percent + "%", 16, Color.WHITE, Typeface.BOLD);
        pct.setGravity(Gravity.CENTER);
        pct.setBackground(roundRect(Color.argb(32, 255, 255, 255), 18,
                Color.argb(80, 255, 255, 255), 1));
        hero.addView(pct, new LinearLayout.LayoutParams(dp(58), dp(48)));
        return hero;
    }

    private View buildPositionSelector() {
        LinearLayout selector = card();
        selector.setPadding(dp(12), dp(12), dp(12), dp(12));
        selector.addView(sectionLabel("CHOOSE A POSITION"));
        selector.addView(space(9));

        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER);
        for (int i = 0; i < PlateTracker.POSITION_COUNT; i++) {
            final int index = i;
            TextView position = text(String.valueOf(i + 1), 14,
                    i == selectedPosition ? Color.WHITE : MUTED, Typeface.BOLD);
            position.setGravity(Gravity.CENTER);
            position.setBackground(roundRect(i == selectedPosition ? INDIGO : BG,
                    14, i == selectedPosition ? INDIGO : BORDER, 1));
            position.setOnClickListener(v -> {
                selectedPosition = index;
                showProgress();
            });
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, dp(46), 1f);
            if (i > 0) lp.setMargins(dp(5), 0, 0, 0);
            row.addView(position, lp);
        }
        selector.addView(row);
        return selector;
    }

    private View buildCharacterGridCard() {
        Set<String> discovered = loadPosition(selectedPosition);
        LinearLayout card = card();

        LinearLayout heading = new LinearLayout(this);
        heading.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout stack = new LinearLayout(this);
        stack.setOrientation(LinearLayout.VERTICAL);
        stack.addView(sectionLabel("CHARACTER GRID"));
        stack.addView(text("Position " + (selectedPosition + 1) + " collection", 20,
                INK, Typeface.BOLD));
        heading.addView(stack, new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView remaining = text((36 - discovered.size()) + " LEFT", 10,
                discovered.size() == 36 ? MINT_DARK : INDIGO, Typeface.BOLD);
        remaining.setGravity(Gravity.CENTER);
        remaining.setBackground(roundRect(discovered.size() == 36 ? SOFT_MINT : SOFT_PURPLE,
                14, discovered.size() == 36 ? Color.rgb(169, 230, 205) : Color.rgb(214, 208, 255), 1));
        heading.addView(remaining, new LinearLayout.LayoutParams(dp(68), dp(34)));
        card.addView(heading);
        card.addView(space(14));

        String chars = PlateTracker.CHARACTERS;
        for (int rowIndex = 0; rowIndex < 6; rowIndex++) {
            LinearLayout row = new LinearLayout(this);
            for (int col = 0; col < 6; col++) {
                int charIndex = rowIndex * 6 + col;
                String character = String.valueOf(chars.charAt(charIndex));
                boolean found = discovered.contains(character);
                TextView chip = text(found ? character + " ✓" : character, found ? 12 : 15,
                        found ? MINT_DARK : MUTED, Typeface.BOLD);
                chip.setGravity(Gravity.CENTER);
                chip.setBackground(roundRect(found ? SOFT_MINT : BG, 12,
                        found ? Color.rgb(154, 225, 194) : BORDER, 1));
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, dp(48), 1f);
                if (col > 0) lp.setMargins(dp(6), 0, 0, 0);
                row.addView(chip, lp);
            }
            card.addView(row);
            if (rowIndex < 5) card.addView(space(7));
        }

        card.addView(space(18));
        card.addView(missingGroup("MISSING LETTERS",
                missingCharacters("ABCDEFGHIJKLMNOPQRSTUVWXYZ", discovered), SOFT_PURPLE, INDIGO));
        card.addView(space(10));
        card.addView(missingGroup("MISSING NUMBERS",
                missingCharacters("0123456789", discovered), SOFT_BLUE, Color.rgb(33, 133, 194)));
        return card;
    }

    private View missingGroup(String label, List<String> missing, int fill, int accent) {
        LinearLayout group = new LinearLayout(this);
        group.setOrientation(LinearLayout.VERTICAL);
        group.setPadding(dp(13), dp(12), dp(13), dp(12));
        group.setBackground(roundRect(missing.isEmpty() ? SOFT_MINT : fill, 14,
                missing.isEmpty() ? Color.rgb(169, 230, 205) : fill, 0));

        LinearLayout heading = new LinearLayout(this);
        heading.setGravity(Gravity.CENTER_VERTICAL);
        TextView labelView = text(label, 10, missing.isEmpty() ? MINT_DARK : accent,
                Typeface.BOLD);
        labelView.setLetterSpacing(0.05f);
        heading.addView(labelView, new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        heading.addView(text(missing.size() + " remaining", 10, MUTED, Typeface.BOLD));
        group.addView(heading);

        TextView values = text(missing.isEmpty() ? "Complete! Nothing missing." : String.join("  ", missing),
                13, missing.isEmpty() ? MINT_DARK : TEXT, Typeface.BOLD);
        values.setLineSpacing(dp(4), 1f);
        values.setPadding(0, dp(7), 0, 0);
        group.addView(values);
        return group;
    }

    private List<String> missingCharacters(String candidates, Set<String> discovered) {
        List<String> missing = new ArrayList<>();
        for (int i = 0; i < candidates.length(); i++) {
            String character = String.valueOf(candidates.charAt(i));
            if (!discovered.contains(character)) missing.add(character);
        }
        return missing;
    }

    private View buildAllPositionSummary() {
        List<Set<String>> all = loadAllPositions();
        LinearLayout card = card();

        LinearLayout heading = new LinearLayout(this);
        heading.setOrientation(LinearLayout.VERTICAL);
        heading.addView(sectionLabel("ALL POSITIONS"));
        heading.addView(text("Collection overview", 20, INK, Typeface.BOLD));
        card.addView(heading);
        card.addView(space(12));

        for (int i = 0; i < PlateTracker.POSITION_COUNT; i++) {
            int count = all.get(i).size();
            int percent = Math.round(count * 100f / 36f);
            LinearLayout row = new LinearLayout(this);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(dp(10), dp(10), dp(10), dp(10));
            row.setBackground(roundRect(i == selectedPosition ? SOFT_PURPLE : BG,
                    13, i == selectedPosition ? Color.rgb(214, 208, 255) : BORDER, 1));

            TextView pos = text(String.valueOf(i + 1), 13,
                    i == selectedPosition ? Color.WHITE : INK, Typeface.BOLD);
            pos.setGravity(Gravity.CENTER);
            pos.setBackground(roundRect(i == selectedPosition ? INDIGO : Color.WHITE,
                    11, i == selectedPosition ? INDIGO : BORDER, 1));
            row.addView(pos, new LinearLayout.LayoutParams(dp(40), dp(40)));

            LinearLayout barStack = new LinearLayout(this);
            barStack.setOrientation(LinearLayout.VERTICAL);
            barStack.setPadding(dp(11), 0, dp(10), 0);
            barStack.addView(text("Position " + (i + 1) + "  •  " + count + " found",
                    12, TEXT, Typeface.BOLD));
            ProgressBar bar = horizontalProgress(36, count,
                    i == selectedPosition ? INDIGO : MINT, BORDER);
            LinearLayout.LayoutParams barLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, dp(7));
            barLp.setMargins(0, dp(7), 0, 0);
            barStack.addView(bar, barLp);
            row.addView(barStack, new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            TextView pct = text(percent + "%", 12,
                    i == selectedPosition ? INDIGO : MUTED, Typeface.BOLD);
            pct.setGravity(Gravity.CENTER);
            row.addView(pct, new LinearLayout.LayoutParams(dp(48), dp(40)));

            final int index = i;
            row.setOnClickListener(v -> {
                selectedPosition = index;
                showProgress();
            });
            card.addView(row);
            if (i < PlateTracker.POSITION_COUNT - 1) card.addView(space(8));
        }
        return card;
    }

    private void confirmReset() {
        new AlertDialog.Builder(this)
                .setTitle("Reset Plate Quest?")
                .setMessage("This permanently clears every discovered character and recent plate on this device.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Reset", (dialog, which) -> {
                    prefs.edit().clear().apply();
                    selectedPosition = 0;
                    Toast.makeText(this, "Game progress reset", Toast.LENGTH_SHORT).show();
                    showProgress();
                })
                .show();
    }

    private LinearLayout card() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(16), dp(16), dp(16));
        card.setBackground(roundRect(CARD, 20, BORDER, 1));
        card.setElevation(dp(3));
        card.setClipToOutline(true);
        return card;
    }

    private TextView sectionLabel(String value) {
        TextView tv = text(value, 10, INDIGO, Typeface.BOLD);
        tv.setLetterSpacing(0.08f);
        tv.setPadding(0, 0, 0, dp(3));
        return tv;
    }

    private TextView smallLink(String value) {
        TextView tv = text(value, 10, INDIGO, Typeface.BOLD);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(dp(10), dp(7), dp(10), dp(7));
        tv.setBackground(roundRect(SOFT_PURPLE, 13, SOFT_PURPLE, 0));
        return tv;
    }

    private TextView actionButton(String value) {
        TextView button = text(value, 13, Color.WHITE, Typeface.BOLD);
        button.setGravity(Gravity.CENTER);
        button.setLetterSpacing(0.04f);
        button.setBackground(gradient(INDIGO, SKY, 16));
        button.setElevation(dp(3));
        button.setClickable(true);
        return button;
    }

    private TextView text(String value, float sp, int color, int style) {
        TextView tv = new TextView(this);
        tv.setText(value);
        tv.setTextSize(sp);
        tv.setTextColor(color);
        tv.setTypeface(Typeface.create("sans-serif", style));
        return tv;
    }

    private ProgressBar horizontalProgress(int max, int value, int progressColor, int trackColor) {
        ProgressBar progress = new ProgressBar(this, null,
                android.R.attr.progressBarStyleHorizontal);
        progress.setMax(max);
        progress.setProgress(value);
        progress.setProgressTintList(ColorStateList.valueOf(progressColor));
        progress.setProgressBackgroundTintList(ColorStateList.valueOf(trackColor));
        return progress;
    }

    private View space(int heightDp) {
        Space space = new Space(this);
        space.setLayoutParams(new LinearLayout.LayoutParams(1, dp(heightDp)));
        return space;
    }

    private GradientDrawable roundRect(int fill, int radiusDp, int stroke, int strokeDp) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(fill);
        drawable.setCornerRadius(dp(radiusDp));
        if (strokeDp > 0) drawable.setStroke(dp(strokeDp), stroke);
        return drawable;
    }

    private GradientDrawable gradient(int start, int end, int radiusDp) {
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR, new int[]{start, end});
        drawable.setCornerRadius(dp(radiusDp));
        return drawable;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private String positionKey(int index) {
        return "position_" + index;
    }

    private Set<String> loadPosition(int index) {
        Set<String> stored = prefs.getStringSet(positionKey(index), new HashSet<>());
        return stored == null ? new HashSet<>() : new HashSet<>(stored);
    }

    private List<Set<String>> loadAllPositions() {
        List<Set<String>> all = new ArrayList<>();
        for (int i = 0; i < PlateTracker.POSITION_COUNT; i++) all.add(loadPosition(i));
        return all;
    }

    private List<String> loadHistory() {
        String raw = prefs.getString(KEY_HISTORY, "");
        List<String> result = new ArrayList<>();
        if (raw == null || raw.isEmpty()) return result;
        String[] parts = raw.split("\\n");
        for (String part : parts) if (!part.trim().isEmpty()) result.add(part.trim());
        return result;
    }

    private void saveHistory(SharedPreferences.Editor editor, String plate) {
        List<String> existing = loadHistory();
        List<String> updated = new ArrayList<>();
        updated.add(plate);
        for (String old : existing) {
            if (!old.equals(plate) && updated.size() < 8) updated.add(old);
        }
        editor.putString(KEY_HISTORY, String.join("\n", updated));
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
