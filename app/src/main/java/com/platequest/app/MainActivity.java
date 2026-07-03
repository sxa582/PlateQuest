package com.platequest.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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

    private static final int NAVY = Color.rgb(18, 33, 63);
    private static final int NAVY_SOFT = Color.rgb(32, 52, 91);
    private static final int PURPLE = Color.rgb(108, 92, 231);
    private static final int PURPLE_LIGHT = Color.rgb(239, 236, 255);
    private static final int GREEN = Color.rgb(35, 178, 109);
    private static final int GREEN_LIGHT = Color.rgb(229, 249, 239);
    private static final int TEXT = Color.rgb(28, 36, 52);
    private static final int MUTED = Color.rgb(107, 116, 135);
    private static final int BG = Color.rgb(246, 248, 252);
    private static final int BORDER = Color.rgb(224, 229, 239);
    private static final int WHITE = Color.WHITE;

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
        window.setStatusBarColor(NAVY);
        window.setNavigationBarColor(WHITE);

        setContentView(buildAppShell());
        showHome();
    }

    private View buildAppShell() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);

        root.addView(buildHeader(), new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(92)));

        content = new FrameLayout(this);
        root.addView(content, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        root.addView(buildBottomNav(), new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(76)));
        return root;
    }

    private View buildHeader() {
        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(20), dp(12), dp(20), dp(12));
        header.setBackgroundColor(NAVY);

        TextView mark = text("PQ", 17, WHITE, Typeface.BOLD);
        mark.setGravity(Gravity.CENTER);
        mark.setBackground(roundRect(PURPLE, 16, PURPLE, 0));
        header.addView(mark, new LinearLayout.LayoutParams(dp(52), dp(52)));

        LinearLayout titles = new LinearLayout(this);
        titles.setOrientation(LinearLayout.VERTICAL);
        titles.setPadding(dp(14), 0, 0, 0);
        TextView title = text("PLATE QUEST", 20, WHITE, Typeface.BOLD);
        title.setLetterSpacing(0.08f);
        TextView subtitle = text("Every position is a new challenge", 12,
                Color.rgb(190, 202, 227), Typeface.NORMAL);
        titles.addView(title);
        titles.addView(subtitle);
        header.addView(titles, new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView badge = text("7", 15, WHITE, Typeface.BOLD);
        badge.setGravity(Gravity.CENTER);
        badge.setBackground(roundRect(NAVY_SOFT, 14, Color.rgb(73, 94, 136), 1));
        header.addView(badge, new LinearLayout.LayoutParams(dp(40), dp(40)));
        return header;
    }

    private View buildBottomNav() {
        LinearLayout nav = new LinearLayout(this);
        nav.setGravity(Gravity.CENTER);
        nav.setPadding(dp(12), dp(8), dp(12), dp(8));
        nav.setBackground(roundRect(WHITE, 0, BORDER, 1));

        playNav = navButton("●\nPLAY");
        progressNav = navButton("▦\nPROGRESS");
        playNav.setOnClickListener(v -> showHome());
        progressNav.setOnClickListener(v -> showProgress());

        nav.addView(playNav, new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f));
        nav.addView(progressNav, new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f));
        return nav;
    }

    private TextView navButton(String value) {
        TextView tv = text(value, 11, MUTED, Typeface.BOLD);
        tv.setGravity(Gravity.CENTER);
        tv.setLineSpacing(0, 1.1f);
        return tv;
    }

    private void setNavState(boolean home) {
        playNav.setTextColor(home ? PURPLE : MUTED);
        progressNav.setTextColor(home ? MUTED : PURPLE);
        playNav.setBackground(home ? roundRect(PURPLE_LIGHT, 14, PURPLE_LIGHT, 0) : null);
        progressNav.setBackground(home ? null : roundRect(PURPLE_LIGHT, 14, PURPLE_LIGHT, 0));
    }

    private void showHome() {
        setNavState(true);
        content.removeAllViews();

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        LinearLayout column = new LinearLayout(this);
        column.setOrientation(LinearLayout.VERTICAL);
        column.setPadding(dp(18), dp(20), dp(18), dp(26));

        TextView eyebrow = text("LICENSE PLATE SCAVENGER GAME", 11, PURPLE, Typeface.BOLD);
        eyebrow.setLetterSpacing(0.08f);
        column.addView(eyebrow);

        TextView headline = text("Spot it. Log it.\nComplete the grid.", 30, NAVY, Typeface.BOLD);
        headline.setPadding(0, dp(4), 0, dp(8));
        column.addView(headline);

        TextView intro = text("Enter a seven-character plate. Each character is checked off in the position where you found it.",
                14, MUTED, Typeface.NORMAL);
        intro.setLineSpacing(dp(3), 1f);
        column.addView(intro);

        column.addView(space(18));
        column.addView(buildEntryCard());
        column.addView(space(14));
        column.addView(buildOverviewCard());
        column.addView(space(14));
        column.addView(buildRecentCard());
        column.addView(space(14));

        TextView safety = text("Passenger mode only. Never enter plates while driving.", 12, MUTED, Typeface.BOLD);
        safety.setGravity(Gravity.CENTER);
        safety.setPadding(dp(12), dp(10), dp(12), dp(10));
        safety.setBackground(roundRect(Color.rgb(255, 249, 230), 12,
                Color.rgb(245, 214, 119), 1));
        column.addView(safety);

        scroll.addView(column);
        content.addView(scroll);
    }

    private View buildEntryCard() {
        LinearLayout card = card();

        TextView label = text("ENTER A PLATE", 11, MUTED, Typeface.BOLD);
        label.setLetterSpacing(0.08f);
        card.addView(label);
        card.addView(space(8));

        EditText plateInput = new EditText(this);
        plateInput.setSingleLine(true);
        plateInput.setTextSize(26);
        plateInput.setTextColor(NAVY);
        plateInput.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        plateInput.setGravity(Gravity.CENTER);
        plateInput.setHint("TGL-7815");
        plateInput.setHintTextColor(Color.rgb(170, 178, 195));
        plateInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        plateInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
        plateInput.setBackground(roundRect(BG, 15, BORDER, 1));
        plateInput.setPadding(dp(14), 0, dp(14), 0);
        card.addView(plateInput, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(68)));

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

        card.addView(space(12));
        Button record = new Button(this);
        record.setText("RECORD PLATE");
        record.setTextColor(WHITE);
        record.setTextSize(14);
        record.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        record.setAllCaps(false);
        record.setBackground(roundRect(PURPLE, 15, PURPLE, 0));
        record.setOnClickListener(v -> recordPlate(plateInput));
        card.addView(record, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(56)));

        TextView helper = text("A–Z and 0–9 are tracked independently in all seven positions.",
                11, MUTED, Typeface.NORMAL);
        helper.setGravity(Gravity.CENTER);
        helper.setPadding(0, dp(10), 0, 0);
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
            message = "Plate recorded. No new position discoveries this time.";
        } else if (discoveries.size() == 1) {
            PlateTracker.Discovery d = discoveries.get(0);
            message = "New discovery: " + d.character + " in position " + (d.positionIndex + 1) + ".";
        } else {
            message = discoveries.size() + " new position discoveries!";
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        showHome();
    }

    private View buildOverviewCard() {
        List<Set<String>> all = loadAllPositions();
        int found = PlateTracker.totalFound(all);
        int percent = PlateTracker.percentComplete(found);
        int plates = prefs.getInt(KEY_PLATE_COUNT, 0);

        LinearLayout card = card();
        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout textStack = new LinearLayout(this);
        textStack.setOrientation(LinearLayout.VERTICAL);
        textStack.addView(text("YOUR EXPEDITION", 11, MUTED, Typeface.BOLD));
        textStack.addView(text(percent + "% complete", 24, NAVY, Typeface.BOLD));
        top.addView(textStack, new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView score = text(found + " / " + PlateTracker.TOTAL_SLOTS, 12, GREEN, Typeface.BOLD);
        score.setGravity(Gravity.CENTER);
        score.setPadding(dp(12), dp(8), dp(12), dp(8));
        score.setBackground(roundRect(GREEN_LIGHT, 18, GREEN_LIGHT, 0));
        top.addView(score);
        card.addView(top);

        card.addView(space(12));
        ProgressBar progress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progress.setMax(PlateTracker.TOTAL_SLOTS);
        progress.setProgress(found);
        progress.setProgressTintList(android.content.res.ColorStateList.valueOf(PURPLE));
        progress.setProgressBackgroundTintList(android.content.res.ColorStateList.valueOf(BORDER));
        card.addView(progress, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(10)));

        card.addView(space(14));
        LinearLayout stats = new LinearLayout(this);
        stats.addView(statBlock(String.valueOf(plates), "PLATES LOGGED"),
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        stats.addView(statBlock(String.valueOf(found), "UNIQUE FINDS"),
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        stats.addView(statBlock(String.valueOf(PlateTracker.TOTAL_SLOTS - found), "REMAINING"),
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        card.addView(stats);

        TextView open = text("View the full position grid  →", 13, PURPLE, Typeface.BOLD);
        open.setGravity(Gravity.CENTER);
        open.setPadding(0, dp(18), 0, 0);
        open.setOnClickListener(v -> showProgress());
        card.addView(open);
        return card;
    }

    private View statBlock(String value, String label) {
        LinearLayout block = new LinearLayout(this);
        block.setOrientation(LinearLayout.VERTICAL);
        block.setGravity(Gravity.CENTER);
        block.addView(text(value, 20, NAVY, Typeface.BOLD));
        TextView labelView = text(label, 9, MUTED, Typeface.BOLD);
        labelView.setLetterSpacing(0.05f);
        block.addView(labelView);
        return block;
    }

    private View buildRecentCard() {
        LinearLayout card = card();
        LinearLayout heading = new LinearLayout(this);
        heading.setGravity(Gravity.CENTER_VERTICAL);
        heading.addView(text("RECENT PLATES", 11, MUTED, Typeface.BOLD),
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        TextView local = text("SAVED ON DEVICE", 9, GREEN, Typeface.BOLD);
        local.setPadding(dp(8), dp(5), dp(8), dp(5));
        local.setBackground(roundRect(GREEN_LIGHT, 12, GREEN_LIGHT, 0));
        heading.addView(local);
        card.addView(heading);
        card.addView(space(8));

        List<String> history = loadHistory();
        if (history.isEmpty()) {
            TextView empty = text("No plates logged yet. Your first discovery starts here.",
                    13, MUTED, Typeface.NORMAL);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(dp(10), dp(18), dp(10), dp(18));
            empty.setBackground(roundRect(BG, 12, BORDER, 1));
            card.addView(empty);
        } else {
            for (int i = 0; i < Math.min(5, history.size()); i++) {
                LinearLayout row = new LinearLayout(this);
                row.setGravity(Gravity.CENTER_VERTICAL);
                row.setPadding(dp(12), dp(10), dp(12), dp(10));
                row.setBackground(roundRect(i % 2 == 0 ? BG : WHITE, 10, BORDER, 1));
                TextView plate = text(history.get(i), 16, NAVY, Typeface.BOLD);
                plate.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
                row.addView(plate, new LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                row.addView(text("✓ recorded", 11, GREEN, Typeface.BOLD));
                card.addView(row);
                if (i < Math.min(5, history.size()) - 1) card.addView(space(6));
            }
        }
        return card;
    }

    private void showProgress() {
        setNavState(false);
        content.removeAllViews();

        ScrollView scroll = new ScrollView(this);
        LinearLayout column = new LinearLayout(this);
        column.setOrientation(LinearLayout.VERTICAL);
        column.setPadding(dp(18), dp(20), dp(18), dp(28));

        column.addView(text("POSITION GRID", 11, PURPLE, Typeface.BOLD));
        TextView title = text("Build your collection", 28, NAVY, Typeface.BOLD);
        title.setPadding(0, dp(4), 0, dp(4));
        column.addView(title);
        column.addView(text("A character counts separately in every position.",
                14, MUTED, Typeface.NORMAL));

        column.addView(space(18));
        column.addView(buildPositionSelector());
        column.addView(space(14));
        column.addView(buildCharacterGridCard());
        column.addView(space(14));
        column.addView(buildAllPositionSummary());
        column.addView(space(14));

        TextView reset = text("Reset all game progress", 13, Color.rgb(191, 58, 73), Typeface.BOLD);
        reset.setGravity(Gravity.CENTER);
        reset.setPadding(dp(12), dp(14), dp(12), dp(14));
        reset.setBackground(roundRect(Color.rgb(255, 240, 242), 13,
                Color.rgb(245, 188, 196), 1));
        reset.setOnClickListener(v -> confirmReset());
        column.addView(reset);

        scroll.addView(column);
        content.addView(scroll);
    }

    private View buildPositionSelector() {
        LinearLayout selector = card();
        selector.setPadding(dp(10), dp(10), dp(10), dp(10));

        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER);
        for (int i = 0; i < PlateTracker.POSITION_COUNT; i++) {
            final int index = i;
            TextView position = text(String.valueOf(i + 1), 14,
                    i == selectedPosition ? WHITE : MUTED, Typeface.BOLD);
            position.setGravity(Gravity.CENTER);
            position.setBackground(roundRect(i == selectedPosition ? PURPLE : BG,
                    13, i == selectedPosition ? PURPLE : BORDER, 1));
            position.setOnClickListener(v -> {
                selectedPosition = index;
                showProgress();
            });
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, dp(48), 1f);
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
        stack.addView(text("POSITION " + (selectedPosition + 1), 11, MUTED, Typeface.BOLD));
        stack.addView(text(discovered.size() + " of 36 found", 22, NAVY, Typeface.BOLD));
        heading.addView(stack, new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        TextView pct = text(Math.round(discovered.size() * 100f / 36f) + "%", 13, PURPLE, Typeface.BOLD);
        pct.setGravity(Gravity.CENTER);
        pct.setBackground(roundRect(PURPLE_LIGHT, 20, PURPLE_LIGHT, 0));
        heading.addView(pct, new LinearLayout.LayoutParams(dp(54), dp(40)));
        card.addView(heading);
        card.addView(space(14));

        String chars = PlateTracker.CHARACTERS;
        for (int rowIndex = 0; rowIndex < 6; rowIndex++) {
            LinearLayout row = new LinearLayout(this);
            for (int col = 0; col < 6; col++) {
                int charIndex = rowIndex * 6 + col;
                String character = String.valueOf(chars.charAt(charIndex));
                boolean found = discovered.contains(character);
                TextView chip = text(character + (found ? " ✓" : ""), found ? 13 : 15,
                        found ? GREEN : MUTED, Typeface.BOLD);
                chip.setGravity(Gravity.CENTER);
                chip.setBackground(roundRect(found ? GREEN_LIGHT : BG, 12,
                        found ? Color.rgb(150, 225, 188) : BORDER, 1));
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, dp(48), 1f);
                if (col > 0) lp.setMargins(dp(6), 0, 0, 0);
                row.addView(chip, lp);
            }
            card.addView(row);
            if (rowIndex < 5) card.addView(space(7));
        }
        return card;
    }

    private View buildAllPositionSummary() {
        List<Set<String>> all = loadAllPositions();
        LinearLayout card = card();
        card.addView(text("ALL POSITIONS", 11, MUTED, Typeface.BOLD));
        card.addView(space(10));

        for (int i = 0; i < PlateTracker.POSITION_COUNT; i++) {
            int count = all.get(i).size();
            LinearLayout row = new LinearLayout(this);
            row.setGravity(Gravity.CENTER_VERTICAL);

            TextView pos = text(String.valueOf(i + 1), 13, WHITE, Typeface.BOLD);
            pos.setGravity(Gravity.CENTER);
            pos.setBackground(roundRect(i == selectedPosition ? PURPLE : NAVY_SOFT,
                    11, NAVY_SOFT, 0));
            row.addView(pos, new LinearLayout.LayoutParams(dp(38), dp(38)));

            LinearLayout barStack = new LinearLayout(this);
            barStack.setOrientation(LinearLayout.VERTICAL);
            barStack.setPadding(dp(10), 0, 0, 0);
            TextView label = text("Position " + (i + 1), 12, TEXT, Typeface.BOLD);
            barStack.addView(label);
            ProgressBar bar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
            bar.setMax(36);
            bar.setProgress(count);
            bar.setProgressTintList(android.content.res.ColorStateList.valueOf(i == selectedPosition ? PURPLE : GREEN));
            bar.setProgressBackgroundTintList(android.content.res.ColorStateList.valueOf(BORDER));
            barStack.addView(bar, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, dp(8)));
            row.addView(barStack, new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            TextView amount = text(count + "/36", 12, MUTED, Typeface.BOLD);
            amount.setPadding(dp(10), 0, 0, 0);
            row.addView(amount);
            row.setOnClickListener(v -> {
                int index = Integer.parseInt(((TextView) ((LinearLayout) v).getChildAt(0)).getText().toString()) - 1;
                selectedPosition = index;
                showProgress();
            });
            card.addView(row);
            if (i < PlateTracker.POSITION_COUNT - 1) card.addView(space(10));
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
        card.setBackground(roundRect(WHITE, 18, BORDER, 1));
        card.setElevation(dp(2));
        return card;
    }

    private TextView text(String value, float sp, int color, int style) {
        TextView tv = new TextView(this);
        tv.setText(value);
        tv.setTextSize(sp);
        tv.setTextColor(color);
        tv.setTypeface(Typeface.DEFAULT, style);
        return tv;
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
