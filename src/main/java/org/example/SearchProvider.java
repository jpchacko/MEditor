package org.example;

import java.util.List;

/**
 * Strategy for finding matches in a document. The search bar only depends on
 * this interface, so a semantic implementation can be dropped in later.
 */
public interface SearchProvider {

    /** Returns matches ordered by position in the text. */
    List<Match> search(String text, String query, boolean caseSensitive);
}
