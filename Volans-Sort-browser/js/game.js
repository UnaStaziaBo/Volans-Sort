const container = document.getElementById("game-container");
const ui = {    // elements UI
    moves: document.getElementById("moves"),
    score: document.getElementById("score"),
    maxMoves: document.getElementById("maxMoves"),
    currentLevel: document.getElementById("currentLevel"),
    rulesBox: document.getElementById("rulesBox"),
    pointsBox: document.getElementById("pointsBox"),
};
const rulesOverlay = document.createElement("div");
rulesOverlay.className = "rules-overlay";
rulesOverlay.innerHTML = `
  <div class="card">
    <div class="rules" id="rulesBoxOverlay"></div>
    <div class="hint"></div>
  </div>
`;
const rulesBoxOverlay = rulesOverlay.querySelector("#rulesBoxOverlay");
const rulesHint = rulesOverlay.querySelector(".hint");
const buttons = {
    new: document.getElementById("btn-new"),
    dragon: document.getElementById("btn-dragon"),
    skip: document.getElementById("btn-skip"),
};
const SCENES = {
    intro1: { bodyClass: "bg-intro-1", music: "resources/audio/beginning.mp3" },
    start2: { bodyClass: "bg-start2", music: "resources/audio/beginning.mp3" },
    start3: { bodyClass: "bg-start3", music: "resources/audio/beginning.mp3" },
    start4: { bodyClass: "bg-start4", music: "resources/audio/battleStart.mp3" },
    game:   { bodyClass: "bg-game",    music: "resources/audio/gameplay.mp3" },
    finishBase: { bodyClass: "bg-finish-base", music: "resources/audio/ending.mp3" },
};
const stepSceneKeys = (prefix, steps) =>
    Array.from({length: steps}, (_, i)=> `${prefix}_${i}`);
const FINISH_BRANCHES = {
    finishFirst: {
        rules: numberedRulesFiles("resources/rules/finish", "finish1", 6),
        scenes: stepSceneKeys("finishFirst", 6)
    },
    finishSecond : {
        rules: numberedRulesFiles("resources/rules/finish", "finish2", 6),
        scenes: stepSceneKeys("finishSecond", 6)
    },
    finishThird: {
        rules: numberedRulesFiles("resources/rules/finish", "finish3", 6),
        scenes: stepSceneKeys("finishThird", 6)
    },
    finishFourth : {
        rules: numberedRulesFiles("resources/rules/finish", "finish4", 6),
        scenes: stepSceneKeys("finishFourth", 6)
    },
    finishFifth: {
        rules: numberedRulesFiles("resources/rules/finish", "finish5", 6),
        scenes: stepSceneKeys("finishFifth", 6)
    },
};
const START2_SEQUENCE = {
    startScene: "start2",
    steps: [
        {rule: "resources/rules/start/start2.txt"},
        {rule: "resources/rules/start/start2_2.txt"},
        {scene:"start3", rule: "resources/rules/start/start3.txt" },
        {rule: "resources/rules/start/start3_1.txt"},
        {scene:"start4", rule: "resources/rules/start/start4.txt" },
        {rule: "resources/rules/start/start4_1.txt"},
    ]
}
const levels = [
    { id: 1, rows: 5, columns: 7, maxMoves: 50, dragon: new DragonA(), rules: ["resources/rules/levels/level1.txt", "resources/rules/levels/level1_1.txt"]},
    { id: 2, rows: 5, columns: 7, maxMoves: 50, dragon: new DragonB(), rules: ['resources/rules/levels/level2.txt']},
    { id: 3, rows: 5, columns: 7, maxMoves: 50, dragon: new DragonC(), rules: ['resources/rules/levels/level3.txt']},
    { id: 4, rows: 5, columns: 7, maxMoves: 50, dragon: new DragonD(), rules: ['resources/rules/levels/level4.txt']},
    { id: 5, rows: 5, columns: 8, maxMoves: 70, dragon: new DragonE(), rules: ['resources/rules/levels/level5.txt']},
];

let awaitingRules = true;
let rulesQueue = [];
let rulesStep = 0;
let overlayMode = "rules";
let hintDelayTimer = null;
let hintTypingTimer = null;
let currentSceneKey = null;
let currentLevelIndex = 0;
let totalScore = 0;
let moves = 0;
let completedColumnIndexes = new Set();
let field = null;
let activeSequence = null;

document.body.appendChild(rulesOverlay);

// // ================== All game scenes =========================
addStepScenes(SCENES, {
    prefix: "finishFirst",
    bodyPrefix: "bg-finish-first",
    steps: 6,
    musicDefault: "resources/audio/ending.mp3",
    musicLast: "resources/audio/happyEnding.mp3",
});
addStepScenes(SCENES, {
    prefix: "finishSecond",
    bodyPrefix: "bg-finish-second",
    steps: 6,
    musicDefault: "resources/audio/ending.mp3",
    musicLast: "resources/audio/happyEnding.mp3",
});
addStepScenes(SCENES, {
    prefix: "finishThird",
    bodyPrefix: "bg-finish-third",
    steps: 6,
    musicDefault: "resources/audio/ending.mp3",
    musicLast: "resources/audio/happyEnding.mp3",
});
addStepScenes(SCENES, {
    prefix: "finishFourth",
    bodyPrefix: "bg-finish-fourth",
    steps: 6,
    musicDefault: "resources/audio/ending.mp3",
    musicLast: "resources/audio/happyEnding.mp3",
});
addStepScenes(SCENES, {
    prefix: "finishFifth",
    bodyPrefix: "bg-finish-fifth",
    steps: 6,
    musicDefault: "resources/audio/ending.mp3",
    musicLast: "resources/audio/happyEnding.mp3",
});
// ===============================================================

// ================== Main Gameplay ===========================
async function startIntro() {
    document.body.classList.remove("end-screen");

    await runSequence(makeSequence({
        mode: "intro",
        startScene: "intro1",
        steps: [{rule: "resources/rules/start/start1.txt"}],
        hintNext: "Tap/click anywhere to continue",
        hintLast: "Tap/click anywhere to start",
        onDone: async () => {
            await runStart2();
        },
    }));
}

async function runStart2() {
    await runSequence(makeSequence({
        mode: "start2",
        startScene: START2_SEQUENCE.startScene,
        steps: START2_SEQUENCE.steps,
        hintNext: "Tap/click anywhere to continue",
        hintLast: "Tap/click anywhere to start",
        onDone: async () => {
            overlayMode = "rules";
            awaitingRules = false;
            document.body.classList.remove("awaiting-rules");

            setScene("game");
            newGame();
        },
    }));
}

function newGame() {
    overlayMode = "rules";
    currentLevelIndex = 0;
    totalScore = 0;
    moves = 0;
    completedColumnIndexes = new Set();
    startLevel(currentLevelIndex);
}

function startLevel(index) {
    moves = 0;
    completedColumnIndexes.clear();

    if (index >= levels.length) {
        void showFinishScreen();
        return;
    }

    currentLevelIndex = index;
    const level = levels[currentLevelIndex];

    container.innerHTML = "";
    completedColumnIndexes.clear();

    awaitingRules = true;
    resetHint();
    rulesQueue = Array.isArray(level.rules)
        ? level.rules
        : (level.ruleText ? [level.ruleText] : []);

    rulesStep = 0;
    document.body.classList.add("awaiting-rules");

    if (field) field.destroy();

    field = new Field(container, level, {
        getMoves: () => moves,
        setMoves: (v) => (moves = v),
        getTotalScore: () => totalScore,
        setTotalScore: (v) => (totalScore = v),
        completedColumnIndexes,
        dragon: level.dragon,
        onHUDUpdate: () => {
            renderHUD();
        },

        // Called when level is solved
        onSolved: () => {
            //+500 for solving the level
            totalScore += 500;
            completedColumnIndexes.clear();
            renderHUD();

            startLevel(currentLevelIndex + 1);
        },

        // Called when moves are full
        onFailed: () => {
            if (typeof window.showLosePopup === "function") {
                window.showLosePopup("Moves are over!");
            }
            setTimeout(() => newGame(), 1100);
        },
    });

    renderHUD();
    void renderRules();
}

function skipLevel() {
    if (!field) return;

    if (field._isDragonDemo) {
        field._isDragonDemo = false;
        field.selectedCol = null;
        field.renderBoard();
    }

    const nextIndex = currentLevelIndex + 1;

    if (nextIndex >= levels.length) {
        field.destroy();
        field = null;
        completedColumnIndexes.clear();
        moves = 0;
        renderHUD();
        void showFinishScreen();
        return;
    }

    field.destroy();
    field = null;

    currentLevelIndex = nextIndex;
    moves = 0;
    completedColumnIndexes.clear();

    renderHUD();
    startLevel(currentLevelIndex);
}

function activateDragon() {
    // Show demo board for current dragon
    const level = levels[currentLevelIndex];
    if (!level?.dragon || !field) return;

    const demo = level.dragon.getDemoBoard?.();
    field.showDragonDemo(demo);
}

async function showFinishScreen() {
    if (field) {
        field.destroy();
        field = null;
    }
    container.innerHTML = "";

    await runSequence(makeSequence({
        mode: "finish",
        startScene: "finishBase",
        steps: [{ rule: "resources/rules/finish/finish.txt" }],
        hintNext: "Tap/click anywhere to continue",
        hintLast: "Tap/click anywhere to continue",
        onDone: async () => {
            await showFinalPointsToast(totalScore, 2000);

            const branchKey = getFinishBranch(totalScore);
            const branch = FINISH_BRANCHES[branchKey] ?? FINISH_BRANCHES.finishFirst;

            await runSequence(makeSequence({
                mode: "finish",
                startScene: branch.scenes[0],
                steps: branch.rules.map((rule, i) => ({
                    rule,
                    scene: branch.scenes[i],
                })),
                hintNext: "Tap/click anywhere to continue",
                hintLast: "Tap/click anywhere to start again",
                onDone: async () => {
                    await runRestart();
                },
            }));
        },
    }));
}

async function runRestart() {
    await runSequence(makeSequence({
        mode: "restart",
        startScene: "intro1",
        steps: [{ rule: "resources/rules/restart.txt" }],
        hintNext: "Tap/click anywhere to continue",
        hintLast: "Tap/click anywhere to start",
        onDone: async () => {
            await startIntro();
        },
    }));
}

function startAfterRules() {
    if (!awaitingRules) return;

    awaitingRules = false;
    document.body.classList.remove("awaiting-rules");

    renderHUD();
    if (field) field.renderBoard();
    resetHint();
}

function getFinishBranch(score) {
    if (score < 1500) return "finishFirst";
    if (score < 3000) return "finishSecond";
    if (score < 4500) return "finishThird";
    if (score < 6000) return "finishFourth";
    return "finishFifth";
}

if (buttons.new) buttons.new.addEventListener("click", newGame);
if (buttons.skip) buttons.skip.addEventListener("click", skipLevel);
if (buttons.dragon) buttons.dragon.addEventListener("click", activateDragon);
// ===============================================================

// ================== Rules processing ===========================
function numberedRulesFiles(basePath, name, count, {startAt0 = false} = {}){
    const arr = [];
    for (let i = 0; i < count; i++) {
        if (i === 0 && !startAt0) arr.push(`${basePath}/${name}.txt`);
        else arr.push(`${basePath}/${name}_${startAt0 ? i : i}.txt`);
    }
    return arr;
}

async function loadRuleText(path) {
    try {
        const res = await fetch(path, { cache: "no-store" });

        if (!res.ok) {
            throw new Error(`HTTP ${res.status} while loading ${path}`);
        }

        return await res.text();
    } catch (e) {
        console.error("Rules load failed:", e);
        return `Rules could not be loaded.\n\nPath: ${path}\n\nTip: run via Live Server / http:// (not file://)`;
    }
}

async function renderRules() {
    if (!rulesBoxOverlay) return;

    if (!rulesQueue.length) {
        startAfterRules();
        return;
    }

    rulesBoxOverlay.textContent = "Loading rules...";
    const text = await loadRuleText(rulesQueue[rulesStep]);
    rulesBoxOverlay.textContent = text;
    resetHint();

    const isLastCard = (rulesStep === rulesQueue.length - 1);
    const textHint = isLastCard
        ? "Tap/click anywhere to start"
        : "Tap/click anywhere to continue";

    typeHintText(textHint);
}
// ===============================================================

// ==================== Sequence part ============================
function makeSequence ({mode, startScene, steps, hintNext, hintLast, onDone }) {
    return { mode, startScene, steps, hintNext, hintLast, onDone, i: 0};
}

async function runSequence(seq) {
    overlayMode = seq.mode;
    awaitingRules = true;
    document.body.classList.add("awaiting-rules");

    if (seq.startScene) setScene(seq.startScene);

    seq.i = 0;
    await renderSequenceStep(seq);
    activeSequence = seq;
}

async function renderSequenceStep(seq){
    const step = seq.steps[seq.i];

    if (step.scene) setScene(step.scene);

    rulesBoxOverlay.textContent = "Loading...";
    rulesBoxOverlay.textContent = await loadRuleText(step.rule);
    resetHint();
    const isLast = seq.i === seq.steps.length - 1;
    typeHintText(isLast? seq.hintLast : seq.hintNext);
}

async function nextSequenceStep(){
    const seq = activeSequence;
    if (!seq || !awaitingRules) return;

    if (seq.i < seq.steps.length - 1){
        seq.i += 1;
        await renderSequenceStep(seq);
        return;
    }

    activeSequence = null;
    await seq.onDone?.();
}
// ===============================================================

// ==================== Scene display ============================
class AudioManager {
    constructor({ defaultVolume = 0.35, fadeMs = 700 } = {}) {
        this.defaultVolume = defaultVolume;
        this.fadeMs = fadeMs;
        this.current = null;
        this.currentSrc = null;
        this._unlocked = false;
        this._pendingSrc = null;
        this._token = 0;

        const unlock = () => {
            this._unlocked = true;
            document.removeEventListener("pointerdown", unlock);
            document.removeEventListener("keydown", unlock);

            if (this._pendingSrc) {
                const s = this._pendingSrc;
                this._pendingSrc = null;
                this.playLoop(s).catch(() => {});
            }
        };

        document.addEventListener("pointerdown", unlock, { once: true });
        document.addEventListener("keydown", unlock, { once: true });
    }

    // Avoid overlapping music on top of the previous one
    async playLoop(src, { volume = this.defaultVolume } = {}) {
        if (!src) return;
        if (this.currentSrc === src && this.current && !this.current.paused) return;

        if (!this._unlocked) {
            this._pendingSrc = src;
            return;
        }

        const myToken = ++this._token;
        const prev = this.current;
        const fadePrevPromise = prev ? this._fade(prev, 0, this.fadeMs) : Promise.resolve();
        const next = new Audio(src);
        next.loop = true;
        next.preload = "auto";
        next.volume = 0;
        try {
            await next.play();
        } catch (e) {
            return;
        }

        if (myToken !== this._token) {
            next.pause();
            next.src = "";
            return;
        }
        this.current = next;
        this.currentSrc = src;

        await fadePrevPromise;
        if (prev) {
            prev.pause();
            prev.src = "";
        }

        if (myToken === this._token) {
            await this._fade(next, volume, this.fadeMs);
        }
    }

    _fade(audioEl, target, ms) {
        const start = audioEl.volume;
        const delta = target - start;

        if (ms <= 0) {
            audioEl.volume = target;
            return Promise.resolve();
        }
        const t0 = performance.now();

        return new Promise((resolve) => {
            const tick = (t) => {
                const k = Math.min(1, (t - t0) / ms);
                audioEl.volume = start + delta * k;
                if (k < 1) requestAnimationFrame(tick);
                else resolve();
            };
            requestAnimationFrame(tick);
        });
    }
}

const audio = new AudioManager({ defaultVolume: 0.5, fadeMs: 700 });

function setScene(sceneKey) {
    const scene = SCENES[sceneKey];
    if (!scene) return;

    // background
    const allBgClasses = Object.values(SCENES).map(s => s.bodyClass);
    document.body.classList.remove(...allBgClasses);
    document.body.classList.add(scene.bodyClass);

    // music
    audio.playLoop(scene.music).catch(() => {});
    currentSceneKey = sceneKey;
}

function addStepScenes(target, { prefix, bodyPrefix, steps, musicLast, musicDefault}) {
    for (let i = 0; i < steps; i++) {
        target[`${prefix}_${i}`] = {
            bodyClass: `${bodyPrefix}-${i}`,
            music: (musicLast != null && i === steps - 1) ? musicLast : musicDefault,
        };
    }
}

function renderHUD() {
    const level = levels[currentLevelIndex];

    if (ui.moves) ui.moves.textContent = String(moves);
    if (ui.score) ui.score.textContent = String(totalScore);

    if (ui.maxMoves) ui.maxMoves.textContent = String(level.maxMoves);
    if (ui.currentLevel) ui.currentLevel.textContent = String(level.id);

    if (ui.rulesBox) ui.rulesBox.style.display = "none";

    if (ui.pointsBox) {
        ui.pointsBox.style.display = awaitingRules ? "none" : (moves > 0 ? "block" : "none");
    }
}
// ===============================================================

// ==================== Hints and Popup part =====================
function typeHintText(text, delayMs = 5000, speedMs = 40) {
    if (!rulesHint) return;

    clearTimeout(hintDelayTimer);
    clearInterval(hintTypingTimer);

    rulesHint.textContent = "";

    hintDelayTimer = setTimeout(() => {
        let i = 0;
        hintTypingTimer = setInterval(() => {
            rulesHint.textContent += text[i] ?? "";
            i++;
            if (i >= text.length) clearInterval(hintTypingTimer);
        }, speedMs);
    }, delayMs);
}

function resetHint() {
    clearTimeout(hintDelayTimer);
    clearInterval(hintTypingTimer);

    hintDelayTimer = null;
    hintTypingTimer = null;

    if (rulesHint) rulesHint.textContent = "";
}

function showDragonPopup(text = "РОР") {
    let el = document.getElementById("dragonToast");
    if (!el) {
        el = document.createElement("div");
        el.id = "dragonToast";
        el.className = "dragon-toast";
        document.body.appendChild(el);
    }

    el.textContent = text;

    // restart animation
    el.classList.remove("show");
    void el.offsetWidth;
    el.classList.add("show");
}

function showLosePopup(text = "Moves are over!") {
    let el = document.getElementById("loseToast");
    if (!el) {
        el = document.createElement("div");
        el.id = "loseToast";
        el.className = "lose-toast";
        document.body.appendChild(el);
    }

    el.textContent = text;

    // restart animation
    el.classList.remove("show");
    void el.offsetWidth;
    el.classList.add("show");
}

function showFinalPointsToast(points, ms = 3000) {
    return new Promise((resolve) => {
        let el = document.getElementById("finalPointsToast");
        if (!el) {
            el = document.createElement("div");
            el.id = "finalPointsToast";
            el.className = "final-points-toast";
            document.body.appendChild(el);
        }

        el.textContent = `Total points: ${points}`;

        // restart show animation
        el.classList.remove("hide");
        el.classList.remove("show");
        void el.offsetWidth;
        el.classList.add("show");

        setTimeout(() => {
            el.classList.remove("show");
            void el.offsetWidth;
            el.classList.add("hide");

            setTimeout(resolve, 900);
        }, ms);
    });
}
// ===============================================================

rulesOverlay.addEventListener("click", async () => {
    if (!awaitingRules) return;

    if(activeSequence) {
        await nextSequenceStep();
        return;
    }

    if (rulesStep < rulesQueue.length - 1) {
        rulesStep += 1;

        await renderRules();
        return;
    }
    startAfterRules();
});


window.showLosePopup = showLosePopup;
window.newGame = newGame;
window.showDragonPopup = showDragonPopup;

void startIntro();