package org.deletethis.blitzspot.app.dao;

public class DbConfig {
    public static final String ENGINES = "ENGINES";
    public static final String ENGINE_ID = "ID";
    public static final String ENGINE_DATA = "DATA";
    public static final String ENGINE_ORDERING = "ORDERING";

    public static final String HISTORY = "HISTORY";
    public static final String HISTORY_ID = "ID";
    public static final String HISTORY_NORMALIZED_QUERY = "NORMALIZED_QUERY";
    public static final String HISTORY_LAST_USED = "LAST_USED";

    public static final String CHOICES = "HISTORY_SUGGESTIONS";
    public static final String CHOICE_ID = "ID";
    public static final String CHOICE_HISTORY_ID = "HISTORY_ID";
    public static final String CHOICE_ENGINE_KEY = "ENGINE_KEY";
    public static final String CHOICE_LAST_USED = "LAST_USED";
    public static final String CHOICE_QUERY = "QUERY";
    public static final String CHOICE_DESCRIPTION = "DESCRIPTION";
    public static final String CHOICE_URL = "URL";

    public static final String FILE = "stuff.sqlite";
    public static final int VERSION = 1;
}
