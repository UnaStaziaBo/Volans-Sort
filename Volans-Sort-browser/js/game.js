const container = document.getElementById("game-container")

const field = new Field(container, {rows: 5, columns: 7})

field.generateField();
