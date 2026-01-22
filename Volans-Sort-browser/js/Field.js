class Field {
    constructor(container, options = {}, hooks = {}) {
        this.container = container;
        this.rowCount = options.rows ?? 5;
        this.columnCount = options.columns ?? 7;
        this.maxMoves = options.maxMoves ?? 100;
        this.field = [];
        this.selectedCol = null;
        this._forcedSolved = false;
        this._isDragonDemo = false;
        this.hooks = {          // Hooks to communicate with game.js
            getMoves: hooks.getMoves ?? (() => 0),
            setMoves: hooks.setMoves ?? (() => {}),
            getTotalScore: hooks.getTotalScore ?? (() => 0),
            setTotalScore: hooks.setTotalScore ?? (() => {}),
            completedColumnIndexes: hooks.completedColumnIndexes ?? new Set(),
            onHUDUpdate: hooks.onHUDUpdate ?? (() => {}),
            onSolved: hooks.onSolved ?? (() => {}),
            onFailed: hooks.onFailed ?? (() => {}),
        };
        this.hooks.dragon = hooks.dragon ?? null;
        this.generateField();
        this._handleClick = this._handleClick.bind(this);
        this.initEvents();
        this.renderBoard();
    }

    // =================== Getting field elements ================
    getRowCount() {
        return this.rowCount;
    }

    getColumnCount() {
        return this.columnCount;
    }

    getCell(row, col) {
        return this.field[row][col];
    }

    setCell(row, col, value) {
        this.field[row][col] = value;
    }
    // =========================================================

    forceSolved() {            // for DragonC
        this._forcedSolved = true;
    }


    generateField() {
        this.field = [];
        for (let r = 0; r < this.rowCount; r++) {
            const row = [];
            for (let c = 0; c < this.columnCount; c++) row.push(" ");
            this.field.push(row);
        }

        const emptyColumns = this.chooseTwoColumns(this.columnCount);
        const letters = this.shuffleLetters(this.columnCount, this.rowCount);
        this.fillField(letters, emptyColumns, this.columnCount, this.rowCount);
    }

    chooseTwoColumns(columnCount) {
        let c1 = Math.floor(Math.random() * columnCount);
        let c2 = Math.floor(Math.random() * columnCount);
        while (c2 === c1) c2 = Math.floor(Math.random() * columnCount);
        return [c1, c2];
    }

    shuffleLetters(columnCount, rowCount) {
        const numberColumns = columnCount - 2;
        const letters = [];

        for (let col = 0; col < numberColumns; col++) {
            const letter = String.fromCharCode("A".charCodeAt(0) + col);
            for (let row = 0; row < rowCount; row++) letters.push(letter);
        }

        this.shuffle(letters);
        return letters;
    }

    shuffle(arr) {
        for (let i = arr.length - 1; i > 0; i--) {
            const y = Math.floor(Math.random() * (i + 1));
            [arr[i], arr[y]] = [arr[y], arr[i]];
        }
        return arr;
    }

    fillField(letters, emptyColumns, columnCount, rowCount) {
        let index = 0;
        for (let col = 0; col < columnCount; col++) {
            if (col === emptyColumns[0] || col === emptyColumns[1]) {
                for (let row = 0; row < rowCount; row++) this.field[row][col] = " ";
            } else {
                for (let row = 0; row < rowCount; row++) this.field[row][col] = letters[index++];
            }
        }
    }

    renderBoard() {
        this.container.innerHTML = "";

        for (let r = 0; r < this.rowCount; r++) {
            const rowDiv = document.createElement("div");
            rowDiv.className = "row";

            for (let c = 0; c < this.columnCount; c++) {
                const cellDiv = document.createElement("div");
                cellDiv.className = "cell";
                const val = this.field[r][c];
                cellDiv.innerHTML = "";

                // Assigning names to letters
                if (val === "A") {
                    const d = document.createElement("div");
                    d.className = "redDragon";
                    cellDiv.appendChild(d);
                } else if (val === "B") {
                    const d = document.createElement("div");
                    d.className = "blueDragon";
                    cellDiv.appendChild(d);
                } else if (val === "C") {
                    const d = document.createElement("div");
                    d.className = "yellowDragon";
                    cellDiv.appendChild(d);
                } else if (val === "D") {
                    const d = document.createElement("div");
                    d.className = "purpleDragon";
                    cellDiv.appendChild(d);
                } else if (val === "E") {
                    const d = document.createElement("div");
                    d.className = "pinkDragon";
                    cellDiv.appendChild(d);
                } else if (val === "F") {
                    const d = document.createElement("div");
                    d.className = "yummy";
                    cellDiv.appendChild(d);
                } else {
                    const s = document.createElement("span");
                    s.className = "cell-content";
                    s.textContent = val;
                    cellDiv.appendChild(s);
                }

                cellDiv.dataset.col = String(c);

                // Highlight selected column
                if (this.selectedCol !== null && c === this.selectedCol - 1) {
                    cellDiv.classList.add("current-column");
                }

                rowDiv.appendChild(cellDiv);
            }

            this.container.appendChild(rowDiv);
        }
    }

    checkValidMove(from, to) {
        return !(from === to || from < 1 || to < 1 || from > this.columnCount || to > this.columnCount);
    }

    moveDragon(from, to) {
        if (!this.checkValidMove(from, to)) return false;

        const fromCol = from - 1;
        const toCol = to - 1;

        let sourceRow = -1;
        let moving = " ";
        let topInTarget = " ";

        // Remove top element from source column
        for (let row = 0; row < this.rowCount; row++) {
            if (this.field[row][fromCol] !== " ") {
                moving = this.field[row][fromCol];
                this.field[row][fromCol] = " ";
                sourceRow = row;
                break;
            }
        }

        if (moving === " ") return false;

        // Find top in target column
        for (let row = 0; row < this.rowCount; row++) {
            if (this.field[row][toCol] !== " ") {
                topInTarget = this.field[row][toCol];
                break;
            }
        }

        // Prevent placing on different type
        if (topInTarget !== " " && topInTarget !== moving) {
            this.field[sourceRow][fromCol] = moving;
            return false;
        }

        // Place into the lowest empty cell in target column
        for (let row = this.rowCount - 1; row >= 0; row--) {
            if (this.field[row][toCol] === " ") {
                this.field[row][toCol] = moving;
                return true;
            }
        }

        this.field[sourceRow][fromCol] = moving;
        return false;
    }

    isSorted() {
        for (let col = 0; col < this.columnCount; col++) {
            const first = this.field[0][col];
            for (let row = 1; row < this.rowCount; row++) {
                if (this.field[row][col] !== first && this.field[row][col] !== " ") return false;
            }
        }
        return true;
    }

    isColumnCompleted(col) {
        const first = this.field[0][col];
        if (first === " ") return false;

        for (let row = 1; row < this.rowCount; row++) {
            if (this.field[row][col] !== first) return false;
        }
        return true;
    }

    showDragonDemo(demoBoard) {     //for the show dragon button
        if (!demoBoard) return;

        // Copy demo board into current field
        for (let r = 0; r < this.rowCount; r++) {
            for (let c = 0; c < this.columnCount; c++) {
                const v = demoBoard[r]?.[c] ?? " ";
                this.field[r][c] = v;
            }
        }

        this._isDragonDemo = true;
        this.selectedCol = null;
        this.renderBoard();
        this.hooks.onHUDUpdate();
    }

    initEvents() {
        this.container.addEventListener("click", this._handleClick);
    }

    _handleClick(e) {
        if (this._isDragonDemo) {
            this._isDragonDemo = false;
            this.selectedCol = null;
            this.renderBoard();
            this.hooks.onHUDUpdate();

            if (this._forcedSolved || this.isSorted()) {
                this._forcedSolved = false;
                this.hooks.onSolved();
            }
            return;
        }

        const cell = e.target.closest(".cell");
        if (!cell) return;

        if (this._isDragonDemo) {
            this._isDragonDemo = false;
            this.selectedCol = null;
            this.renderBoard();
            this.hooks.onHUDUpdate();
        }

        const chosenCol = Number(cell.dataset.col) + 1;
        if (this.selectedCol === null) {
            this.selectedCol = chosenCol;
            this.renderBoard();
            return;
        }

        const fromCol = this.selectedCol;
        const toCol = chosenCol;
        this.selectedCol = null;
        const newMoves = this.hooks.getMoves() + 1;
        this.hooks.setMoves(newMoves);
        const moveSuccess = this.moveDragon(fromCol, toCol);

        if (moveSuccess) {
            this.hooks.setTotalScore(this.hooks.getTotalScore() + 10);

            for (let col = 0; col < this.columnCount; col++) {
                if (!this.hooks.completedColumnIndexes.has(col) && this.isColumnCompleted(col)) {
                    this.hooks.completedColumnIndexes.add(col);
                    this.hooks.setTotalScore(this.hooks.getTotalScore() + 100);
                }
            }

            if (this.hooks.dragon) {
                const before = JSON.stringify(this.field);
                this.hooks.dragon.specialDragonRule(this, newMoves);
                const after = JSON.stringify(this.field);
                const dragonName = this.hooks.dragon.getName?.() ?? "";
                const activated = (before !== after);
                if (activated) {
                    console.log(`${dragonName} effect activated.`);
                    if (typeof window.showDragonPopup === "function") {
                        window.showDragonPopup("Dragon is here!");
                    }
                }
            }

            if (this._forcedSolved) {
                this._forcedSolved = false;
                this.renderBoard();
                this.hooks.onHUDUpdate();
                this.hooks.onSolved();
                return;
            }

            if (this.isSorted()) {
                this.renderBoard();
                this.hooks.onHUDUpdate();
                this.hooks.onSolved();
                return;
            }
        }

        if (newMoves >= this.maxMoves) {
            this.renderBoard();
            this.hooks.onHUDUpdate();
            this.hooks.onFailed();
            return;
        }

        this.renderBoard();
        this.hooks.onHUDUpdate();
    }
    destroy() {
        this.container.removeEventListener("click", this._handleClick);
    }
}