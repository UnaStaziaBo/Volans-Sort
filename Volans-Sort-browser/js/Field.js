class Field {
    constructor(container, options = {}) {
        this.container = container;

        this.rowCount = options.rows ?? 5;
        this.columnCount = options.columns ?? 7;

        this.field = [];
        this.state = "PLAYING";
        this.selectedCol = null;

        this.FieldState = {
            PLAYING: "PLAYING", SOLVED: "SOLVED", FAILED: "FAILED", SKIPPED: "SKIPPED", DRAGON: "DRAGON"
        }

        this.generateField();
        this.initEvents();
    }

     generateField(){

        this.field = [];
        for(let i = 0; i < this.rowCount; i++) {
            let row = [];
            for (let c = 0; c < this.columnCount; c++) {
                row.push(' ');
            }
            this.field.push(row);
        }

        const emptyColumns = this.chooseTwoColumns(this.columnCount);
        const letters = this.shuffleLetters(this.columnCount, this.rowCount, emptyColumns);
        this.fillField(letters, emptyColumns, this.columnCount, this.rowCount);
        this.renderBoard();
    }

    chooseTwoColumns(columnCount){
        let emptyColumn1 = Math.floor(Math.random() * columnCount);
        let emptyColumn2 = Math.floor(Math.random() * columnCount);


        while (emptyColumn2 === emptyColumn1) {
            emptyColumn2 = Math.floor(Math.random() * columnCount);
        }

        return [emptyColumn1, emptyColumn2];
    }

    shuffleLetters(columnCount, rowCount){
        const numberColumns = columnCount - 2;
        let letters = [];

        for (let col = 0; col < numberColumns; col++) {
            const letter = String.fromCharCode('A'.charCodeAt(0) + col); //generate the letters A, B...
            for (let row = 0; row < rowCount; row++) {
                letters.push(letter);
            }
        }

        this.shuffle(letters);
        return letters;
    }

    shuffle(letters) {
        for(let i = letters.length - 1; i > 0; i--) {
            const y = Math.floor(Math.random() * (i + 1));
            [letters[i], letters[y]] = [letters[y], letters[i]];
        }
        return letters;
    }

    fillField(letters, emptyColumns, columnCount, rowCount){
        let index = 0;
        for (let col = 0; col < columnCount; col++) {
            if (col === emptyColumns[0] || col === emptyColumns[1]) {
                //Leave two empty columns
                for (let row = 0; row < rowCount; row++) {
                    this.field[row][col] = ' ';
                }
            } else {
                for (let row = 0; row < rowCount; row++) {
                    this.field[row][col] = letters[index++];
                }
            }
        }
    }

     renderBoard() {
        this.container.innerHTML = '';

        for (let r = 0; r < this.rowCount; r++) {
            const rowDiv = document.createElement("div");
            rowDiv.className = "row";

            for (let c = 0; c < this.columnCount; c++) {
                const cellDiv = document.createElement("div");
                cellDiv.className = "cell";
                cellDiv.textContent = this.field[r][c];
                cellDiv.dataset.col = c.toString();

                if (this.selectedCol !== null && c === this.selectedCol - 1) {
                    cellDiv.classList.add("current-column");
                }

                rowDiv.appendChild(cellDiv);
            }

            this.container.appendChild(rowDiv);
        }
    }

    moveDragon(from, to) {
        if (!this.checkValidMove(from, to)) {
            console.log("\nInvalid move!");
            return false;
        }

        let fromCol = from - 1;
        let toCol = to - 1;
        let sourceRow = -1;
        let movingDragon = ' ';
        let topInTarget = ' ';

        //Find the top non-empty dragon and delete it
        for (let row = 0; row < this.rowCount; row++) {
            if (this.field[row][fromCol] !== ' ') {
                movingDragon = this.field[row][fromCol];
                this.field[row][fromCol] = ' ';
                sourceRow = row;
                break;
            }
        }

        if (movingDragon === ' ') {
            console.log("\nInvalid move! Column is empty.");
            return false;
        }

        //Find new non-empty dragon
        for (let row = 0; row < this.rowCount; row++) {
            if (this.field[row][toCol] !== ' ') {
                topInTarget = this.field[row][toCol];
                break;
            }
        }

        //Prevent a letter from being placed on a character of a different type
        if (topInTarget !== ' ' && topInTarget !== movingDragon) {
            console.log("\nInvalid move! You can only place on the same type.");
            this.field[sourceRow][fromCol] = movingDragon;
            return false;
        }

        //Move the dragon
        for (let row = this.rowCount - 1; row >= 0; row--) {
            if (this.field[row][toCol] === ' ') {
                this.field[row][toCol] = movingDragon;

                if (this.isSorted()) {
                    this.state = this.FieldState.SOLVED;
                }

                this.renderBoard();
                return true;
            }
        }

        console.log("\nInvalid move! Destination column is full.");
        this.field[sourceRow][fromCol] = movingDragon;
        return false;
    }

    checkValidMove(from, to) {
        return !(from === to || from < 1 || to < 1 || from > this.columnCount || to > this.columnCount);
    }

    isSorted() {
        for (let col = 0; col < this.columnCount; col++) {
            let firstDragon = this.field[0][col];
            for (let row = 1; row < this.rowCount; row++) {
                if (this.field[row][col] !== firstDragon && this.field[row][col] !== ' ') {
                    return false;
                }
            }
        }
        this.state = this.FieldState.SOLVED;
        return true;
    }

    initEvents() {
        this.container.addEventListener("click", (e) => {
            const cell = e.target.closest(".cell");
            if (!cell) return;
            const col = Number(cell.dataset.col) + 1;

            if (this.selectedCol === null) {
                this.selectedCol = col;
            } else {
                this.moveDragon(this.selectedCol, col);
                this.selectedCol = null;
            }

            this.renderBoard();
        });
    }

}