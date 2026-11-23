package org.example;

/**
 * A region of the document that matched a search.
 *
 * @param start inclusive start offset
 * @param end   exclusive end offset
 * @param score relevance, 1.0 for exact matches; semantic providers can use lower values
 */
public record Match(int start, int end, double score) {}
