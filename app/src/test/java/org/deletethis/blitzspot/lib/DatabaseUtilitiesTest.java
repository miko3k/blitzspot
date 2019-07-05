package org.deletethis.blitzspot.lib;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DatabaseUtilitiesTest {
    @Test
    public void normalizeTest() {
        assertEquals("a", DatabaseUtilities.normalize("a  "));
        assertEquals("a", DatabaseUtilities.normalize("aaaa"));
        assertEquals("a", DatabaseUtilities.normalize("   a"));
        assertEquals("ab cdef gho", DatabaseUtilities.normalize("  ÄB \n CDef\tgHÖ     "));
        assertEquals("haha du", DatabaseUtilities.normalize("haha \uD83D\uDE02 du"));
    }
}