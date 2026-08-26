CREATE TABLE books (
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    total_copies INT NOT NULL CHECK (total_copies > 0),
    available_copies INT NOT NULL CHECK (available_copies >= 0 AND available_copies <= total_copies),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE readers (
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE borrow_tickets (
    id SERIAL PRIMARY KEY,
    reader_id INT NOT NULL REFERENCES readers(id),
    book_id INT NOT NULL REFERENCES books(id),
    status VARCHAR(20) NOT NULL CHECK (status IN ('BORROWED', 'RETURNED')),
    borrowed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    returned_at TIMESTAMP
);
