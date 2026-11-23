package org.example;

/** Pure search/replace logic, independent of any UI. */
public final class TextSearch {

    private TextSearch() {}

    /** Result of a replace-all: the new text and how many replacements were made. */
    public record ReplaceResult(String text, int count) {}

    /**
     * Finds the first occurrence of {@code query} at or after {@code from},
     * wrapping around to the start of the text if none is found.
     *
     * @return index of the match, or -1 if there is none (or the query is empty)
     */
    public static int findNext(String text, String query, int from, boolean caseSensitive) {
        if (query.isEmpty() || text.length() < query.length()) return -1;
        from = Math.max(0, Math.min(from, text.length()));
        int idx = indexOf(text, query, from, caseSensitive);
        return idx >= 0 ? idx : indexOf(text, query, 0, caseSensitive);
    }

    /** Start indexes of all non-overlapping occurrences of {@code query}, in order. */
    public static java.util.List<Integer> findAll(String text, String query, boolean caseSensitive) {
        var result = new java.util.ArrayList<Integer>();
        if (query.isEmpty()) return result;
        int pos = 0, idx;
        while ((idx = indexOf(text, query, pos, caseSensitive)) >= 0) {
            result.add(idx);
            pos = idx + query.length();
        }
        return result;
    }

    /** Replaces every non-overlapping occurrence of {@code query}. */
    public static ReplaceResult replaceAll(String text, String query, String replacement,
                                           boolean caseSensitive) {
        if (query.isEmpty()) return new ReplaceResult(text, 0);
        StringBuilder sb = new StringBuilder();
        int pos = 0, count = 0, idx;
        while ((idx = indexOf(text, query, pos, caseSensitive)) >= 0) {
            sb.append(text, pos, idx).append(replacement);
            pos = idx + query.length();
            count++;
        }
        sb.append(text, pos, text.length());
        return new ReplaceResult(sb.toString(), count);
    }

    /** True if {@code candidate} equals {@code query} under the given case rule. */
    public static boolean matches(String candidate, String query, boolean caseSensitive) {
        return caseSensitive ? candidate.equals(query) : candidate.equalsIgnoreCase(query);
    }

    // regionMatches avoids lower-casing the whole text, which can change its length.
    private static int indexOf(String text, String query, int from, boolean caseSensitive) {
        int last = text.length() - query.length();
        for (int i = from; i <= last; i++) {
            if (text.regionMatches(!caseSensitive, i, query, 0, query.length())) return i;
        }
        return -1;
    }
}
