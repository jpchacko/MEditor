package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextSearchTest {

    @Test
    void findsFromGivenPosition() {
        assertEquals(0, TextSearch.findNext("abc abc", "abc", 0, true));
        assertEquals(4, TextSearch.findNext("abc abc", "abc", 1, true));
    }

    @Test
    void wrapsAround() {
        assertEquals(0, TextSearch.findNext("abc abc", "abc", 5, true));
    }

    @Test
    void notFoundReturnsMinusOne() {
        assertEquals(-1, TextSearch.findNext("hello", "xyz", 0, true));
        assertEquals(-1, TextSearch.findNext("hi", "longer query", 0, true));
        assertEquals(-1, TextSearch.findNext("hello", "", 0, true));
    }

    @Test
    void caseSensitivity() {
        assertEquals(-1, TextSearch.findNext("Hello", "hello", 0, true));
        assertEquals(0, TextSearch.findNext("Hello", "hello", 0, false));
    }

    @Test
    void outOfRangeStartIsClamped() {
        assertEquals(0, TextSearch.findNext("abc", "abc", 99, true));
        assertEquals(0, TextSearch.findNext("abc", "abc", -5, true));
    }

    @Test
    void replaceAllCountsAndReplaces() {
        var r = TextSearch.replaceAll("a-b-A", "a", "xx", false);
        assertEquals("xx-b-xx", r.text());
        assertEquals(2, r.count());
    }

    @Test
    void replaceAllCaseSensitive() {
        var r = TextSearch.replaceAll("a-b-A", "a", "x", true);
        assertEquals("x-b-A", r.text());
        assertEquals(1, r.count());
    }

    @Test
    void replaceAllDoesNotRescanReplacement() {
        var r = TextSearch.replaceAll("aaa", "a", "aa", true);
        assertEquals("aaaaaa", r.text());
        assertEquals(3, r.count());
    }

    @Test
    void replaceAllWithEmptyQueryIsNoop() {
        var r = TextSearch.replaceAll("abc", "", "x", true);
        assertEquals("abc", r.text());
        assertEquals(0, r.count());
    }

    @Test
    void matchesRespectsCase() {
        assertTrue(TextSearch.matches("Abc", "aBC", false));
        assertEquals(false, TextSearch.matches("Abc", "aBC", true));
    }

    @Test
    void findAllReturnsEveryStart() {
        assertEquals(java.util.List.of(0, 4, 8), TextSearch.findAll("ab, ab, AB", "ab", false));
        assertEquals(java.util.List.of(0, 4), TextSearch.findAll("ab, ab, AB", "ab", true));
        assertEquals(java.util.List.of(), TextSearch.findAll("abc", "", true));
        assertEquals(java.util.List.of(0, 2), TextSearch.findAll("aaaa", "aa", true));
    }
}
