CREATE TABLE Config (
  Key TEXT UNIQUE NOT NULL,
  Value TEXT
);

INSERT INTO Config VALUES ('TicketAmount', '200');
INSERT INTO Config VALUES ('AdminCode', 'password');

CREATE TABLE Ticket (
  PlayerID  INT NOT NULL,
  TicketNum INT UNIQUE NOT NULL,
  DateSelected  TEXT NOT NULL
);

CREATE TABLE Player (
  ID INT UNIQUE NOT NULL,
  Name TEXT NOT NULL,
  Code TEXT UNIQUE NOT NULL,
  PurchasedTickets INT NOT NULL,
  DateAdded  TEXT NOT NULL
);
