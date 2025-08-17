CREATE TABLE attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    date DATE NOT NULL,
    present BOOLEAN NOT NULL DEFAULT FALSE,
    notes VARCHAR(500),
    marked_by VARCHAR(50),
    FOREIGN KEY (student_id) REFERENCES student(id)
);

