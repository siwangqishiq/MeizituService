CREATE TABLE IF NOT EXISTS section (
    sid INTEGER PRIMARY KEY AUTOINCREMENT,
    content VARCHAR(2000),
    link VARCHAR(2000),
    refer VARCHAR(2000),
    image VARCHAR(3000),
    imageCount INT,
    updateTime LONG,
    extra VARCHAR(1000)
);

CREATE TABLE IF NOT EXISTS image (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    sid LONG,
    url VARCHAR(3000),
    refer VARCHAR(3000),
    name VARCHAR(1000),
    updateTime LONG,
    extra VARCHAR(1000)
);

