CREATE TABLE ENGINES(
	ID INTEGER PRIMARY KEY,
	DATA BLOB NOT NULL,
	ORDERING REAL NOT NULL
);

CREATE TABLE HISTORY(
	ID INTEGER PRIMARY KEY,
	NORMALIZED_QUERY TEXT NOT NULL COLLATE BINARY UNIQUE,
	LAST_USED INTEGER NOT NULL
);

CREATE TABLE HISTORY_SUGGESTIONS(
	ID INTEGER PRIMARY KEY,
	HISTORY_ID INTEGER NOT NULL REFERENCES HISTORY(ID) ON UPDATE RESTRICT ON DELETE CASCADE,
	ENGINE_KEY TEXT NOT NULL,
	LAST_USED INTEGER NOT NULL,
	QUERY TEXT NOT NULL,
	DESCRIPTION TEXT,
	URL TEXT,
	CONSTRAINT UNIQUE_SUGGESTION UNIQUE(HISTORY_ID, ENGINE_KEY)
);

-- the end
