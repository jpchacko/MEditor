package org.example;

import java.util.List;

/** Plain substring search. */
public class LiteralSearchProvider implements SearchProvider {

    @Override
    public List<Match> search(String text, String query, boolean caseSensitive) {
        return TextSearch.findAll(text, query, caseSensitive).stream()
                .map(i -> new Match(i, i + query.length(), 1.0))
                .toList();
    }
}
