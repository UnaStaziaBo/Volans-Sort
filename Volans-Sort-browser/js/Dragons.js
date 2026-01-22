class Dragon {
    constructor(name) {
        this.name = name;
    }
    getName() {
        return this.name;
    }
    specialDragonRule(field, moves) {}
    getDemoBoard() {
        return null;
    }
}

//================= Dragon A =========================
// Shuffle all letters except A in columns other
// than the column that contains all A
class DragonA extends Dragon {
    constructor() {
        super("Dragon A");
        this.isShuffledOnce = false;
    }

    specialDragonRule(field, moves) {
        this.applyDragonEffect(field);
    }

    getDemoBoard() {
        return [
            [" ", "B", " ", " ", "D", "C", " "],
            [" ", "B", "A", " ", "D", "C", "E"],
            [" ", "B", "A", " ", "D", "C", "E"],
            [" ", "B", "A", " ", "D", "C", "E"],
            ["A", "B", "A", "C", "D", "E", "E"],
        ];
    }

    applyDragonEffect(field) {
        if (this.isShuffledOnce) return;

        let foundColumn = -1;
        const otherLetters = [];

        for (let col = 0; col < field.getColumnCount(); col++) {
            for (let row = 0; row < field.getRowCount(); row++) {
                const cell = field.getCell(row, col);

                if (cell === "A") {
                    if (foundColumn === -1) foundColumn = col;
                    else if (foundColumn !== col) return; // if A is in different columns, there is no effect
                } else if (cell !== " ") {
                    otherLetters.push(cell);
                }
            }
        }

        if (foundColumn === -1) return;

        // Shuffle letters
        for (let i = otherLetters.length - 1; i > 0; i--) {
            const y = Math.floor(Math.random() * (i + 1));
            [otherLetters[i], otherLetters[y]] = [otherLetters[y], otherLetters[i]];
        }

        let index = 0;
        for (let col = 0; col < field.getColumnCount(); col++) {
            if (col === foundColumn) continue;

            for (let row = 0; row < field.getRowCount(); row++) {
                if (index < otherLetters.length) {
                    field.setCell(row, col, otherLetters[index++]);
                } else {
                    field.setCell(row, col, " ");
                }
            }
        }

        console.log(`${this.getName()}: Letters shuffled!`);
        this.isShuffledOnce = true;
    }
}
//====================================================

//================= Dragon B =========================
// If all B are collected in exactly one column
// delete all A letters
class DragonB extends Dragon {
    constructor() {
        super("Dragon B");
        this.effectUsed = false;
    }

    specialDragonRule(field, moves) {
        this.applyDragonEffect(field);
    }

    getDemoBoard() {
        return [
            [" ", " ", " ", " ", "A", "E", " "],
            ["B", " ", "D", " ", "C", "E", " "],
            ["B", "C", "D", " ", "E", "E", "B"],
            ["B", "C", "D", " ", "C", "D", "A"],
            ["B", "C", "D", "A", "E", "A", "A"],
        ];
    }

    applyDragonEffect(field) {
        if (this.effectUsed) return;

        let columnB = -1;
        let foundB = false;

        // Find a column that has at least one B and contains only B/spaces
        for (let col = 0; col < field.getColumnCount(); col++) {
            let hasB = false;
            let onlyB = true;

            for (let row = 0; row < field.getRowCount(); row++) {
                const cell = field.getCell(row, col);
                if (cell === "B") hasB = true;
                else if (cell !== " ") {
                    onlyB = false;
                    break;
                }
            }

            if (hasB && onlyB) {
                columnB = col;
                foundB = true;
                break;
            }
        }

        if (!foundB) return;

        // Check if there are any B in other columns
        for (let col = 0; col < field.getColumnCount(); col++) {
            if (col === columnB) continue;
            for (let row = 0; row < field.getRowCount(); row++) {
                if (field.getCell(row, col) === "B") return;
            }
        }

        // Delete all A
        for (let col = 0; col < field.getColumnCount(); col++) {
            for (let row = 0; row < field.getRowCount(); row++) {
                if (field.getCell(row, col) === "A") field.setCell(row, col, " ");
            }
        }

        this.effectUsed = true;
        console.log("Dragon B has done his job, say goodbye to A!");
    }
}
//====================================================

//================= Dragon C =========================
// If all C are collected in one column
// delete all letters except C and immediately set SOLVED
class DragonC extends Dragon {
    constructor() {
        super("Dragon C");
    }

    specialDragonRule(field, moves) {
        this.applyDragonEffect(field);
    }

    getDemoBoard() {
        return [
            ["B", " ", " ", " ", "D", " ", " "],
            ["B", " ", " ", "A", "D", "C", "E"],
            ["A", "C", " ", "A", "B", "C", "E"],
            ["E", "B", " ", "D", "A", "C", "E"],
            ["D", "B", " ", "D", "A", "C", "E"],
        ];
    }

    applyDragonEffect(field) {
        let columnC = -1;
        let foundC = false;

        // Find a column that has at least one C and contains only C
        for (let col = 0; col < field.getColumnCount(); col++) {
            let hasC = false;
            let onlyC = true;

            for (let row = 0; row < field.getRowCount(); row++) {
                const cell = field.getCell(row, col);
                if (cell === "C") hasC = true;
                else if (cell !== " ") {
                    onlyC = false;
                    break;
                }
            }

            if (hasC && onlyC) {
                columnC = col;
                foundC = true;
                break;
            }
        }

        if (!foundC) return;

        // Check if there are any C in other columns
        for (let col = 0; col < field.getColumnCount(); col++) {
            if (col === columnC) continue;
            for (let row = 0; row < field.getRowCount(); row++) {
                if (field.getCell(row, col) === "C") return;
            }
        }

        // Delete everything except C
        for (let row = 0; row < field.getRowCount(); row++) {
            for (let col = 0; col < field.getColumnCount(); col++) {
                if (field.getCell(row, col) !== "C") field.setCell(row, col, " ");
            }
        }

        console.log("Why did you collect dragon C now?");
        field.forceSolved();
    }
}
//====================================================

//================= Dragon D =========================
// Delete all D after move 15
class DragonD extends Dragon {
    constructor() {
        super("Dragon D");
        this.effectUsed = false;
    }

    specialDragonRule(field, moves) {
        if (moves < 15) return;
        if (this.effectUsed) return;

        let hasD = false;
        for (let row = 0; row < field.getRowCount(); row++) {
            for (let col = 0; col < field.getColumnCount(); col++) {
                if (field.getCell(row, col) === "D") { hasD = true; break; }
            }
            if (hasD) break;
        }

        if (!hasD) {
            this.effectUsed = true;
            return;
        }

        for (let row = 0; row < field.getRowCount(); row++) {
            for (let col = 0; col < field.getColumnCount(); col++) {
                if (field.getCell(row, col) === "D") field.setCell(row, col, " ");
            }
        }

        this.effectUsed = true;
        console.log("It's time for Dragon D to go");
    }

    getDemoBoard() {
        return [
            [" ", "B", "C", " ", " ", " ", " "],
            [" ", "B", "D", "C", " ", " ", " "],
            ["D", "A", "A", "E", "C", "E", " "],
            ["D", "E", "A", "B", "C", "E", "B"],
            ["D", "A", "A", "D", "C", "E", "B"],
        ];
    }
}
//====================================================

//================= Dragon E =========================
// When all F are in one column, find an empty column,
// remove all E and drop them into that empty column
class DragonE extends Dragon {
    constructor() {
        super("Dragon E");
        this.eCollected = false;
    }

    specialDragonRule(field, moves) {
        if (this.eCollected) return;
        if (!this.areAllFInOneColumn(field)) return;

        const emptyCol = this.findEmptyColumn(field);
        if (emptyCol === -1) {
            console.log("All F collected, but no empty column found for E.");
            return;
        }

        let eCount = 0;

        // Remove all E and count them
        for (let col = 0; col < field.getColumnCount(); col++) {
            for (let row = 0; row < field.getRowCount(); row++) {
                if (field.getCell(row, col) === "E") {
                    field.setCell(row, col, " ");
                    eCount++;
                }
            }
        }

        // Drop E into empty column bottom-up
        let row = field.getRowCount() - 1;
        while (eCount > 0 && row >= 0) {
            field.setCell(row, emptyCol, "E");
            row--;
            eCount--;
        }

        console.log(`Dragon F collected all E into column ${emptyCol + 1}!`);
        this.eCollected = true;
    }

    areAllFInOneColumn(field) {
        let columnWithF = -1;

        for (let col = 0; col < field.getColumnCount(); col++) {
            for (let row = 0; row < field.getRowCount(); row++) {
                const cell = field.getCell(row, col);
                if (cell === "F") {
                    if (columnWithF === -1) columnWithF = col;
                    else if (columnWithF !== col) return false;
                }
            }
        }

        return columnWithF !== -1;
    }

    findEmptyColumn(field) {
        for (let col = 0; col < field.getColumnCount(); col++) {
            let isEmpty = true;
            for (let row = 0; row < field.getRowCount(); row++) {
                if (field.getCell(row, col) !== " ") {
                    isEmpty = false;
                    break;
                }
            }
            if (isEmpty) return col;
        }
        return -1;
    }

    getDemoBoard() {
        return [
            [" ", "D", " ", " ", " ", " ", " ", "E"],
            [" ", "C", "B", " ", " ", "B", "F", "E"],
            ["D", "C", "D", "A", " ", "B", "F", "A"],
            ["D", "C", "E", "A", "F", "B", "F", "E"],
            ["E", "C", "C", "A", "D", "B", "F", "A"],
        ];
    }
}
//====================================================