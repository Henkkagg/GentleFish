javascript:(function() {
    function getChessMoves() {
        let moves = [];
        document.querySelectorAll(".main-line-row").forEach(row => {
            let moveNumber = row.getAttribute("data-whole-move-number");
            let whiteMoveElem = row.querySelector(".white-move");
            let blackMoveElem = row.querySelector(".black-move");

            let whiteMove = extractMove(whiteMoveElem);
            let blackMove = blackMoveElem ? extractMove(blackMoveElem) : "";

            moves.push(`${moveNumber}. ${whiteMove} ${blackMove}`.trim());
        });
        return moves.join(" ");
    }

    function extractMove(moveElem) {
        if (!moveElem) return "";
        let pieceElem = moveElem.querySelector("[data-figurine]");
        let piece = pieceElem ? pieceElem.getAttribute("data-figurine") : "";
        let moveText = moveElem.innerText.trim();
        return piece ? `${piece}${moveText}` : moveText;
    }

    function chessNotationToSquare(notation) {
        const fileMap = { "a": 1, "b": 2, "c": 3, "d": 4, "e": 5, "f": 6, "g": 7, "h": 8 };
        const rank = parseInt(notation[1]);
        const file = fileMap[notation[0]];

        if (isNaN(rank) || isNaN(file)) return null;
        return `${file}${rank}`;
    }

    function highlightSquare(square) {
        const board = document.querySelector("wc-chess-board.board#board-play-computer");
        if (!board) {
            console.warn("Chessboard not found!");
            return;
        }

        const highlight = document.createElement("div");
        highlight.className = `highlight square-${square}`;
        highlight.setAttribute("data-test-element", "highlight");

        highlight.style.backgroundColor = "transparent"; 
        highlight.style.border = "3px solid rgba(0, 0, 255, 1)";
        highlight.style.opacity = "1";
        highlight.style.boxSizing = "border-box";

        board.appendChild(highlight);
    }

    let ws = new WebSocket("wss://localhost:8443/gentlefish");

    ws.onopen = function() {
        alert("Connected to Gentlefish service! ✅");
        console.log("Connected to WebSocket!");

        function findMoveList() {
            let potentialLists = [...document.querySelectorAll("*")].filter(el =>
                el.querySelector(".main-line-row")
            );
            return potentialLists.length > 0 ? potentialLists[0] : null;
        }

        function setupObserver(moveList) {
            let lastSentMoves = "";

            let currentMoves = getChessMoves();
            ws.send(JSON.stringify(currentMoves));
            console.log("Sent moves:", currentMoves);
            lastSentMoves = currentMoves;

            let observer = new MutationObserver(() => {
                let newMoves = getChessMoves();
                if (newMoves !== lastSentMoves) {
                    lastSentMoves = newMoves;
                    ws.send(JSON.stringify(newMoves));
                    console.log("Sent moves:", newMoves);
                }
            });

            observer.observe(moveList, { childList: true, subtree: true });
        }

        let moveList = findMoveList();
        if (moveList) {
            setupObserver(moveList);
        } else {
            console.warn("No moves detected yet");
            let retryInterval = setInterval(() => {
                moveList = findMoveList();
                if (moveList) {
                    clearInterval(retryInterval);
                    setupObserver(moveList);
                }
            }, 1000);
        }
    };

    ws.onmessage = function(event) {
        const move = event.data.trim();
        console.log("Best move received:", move);

        if (move.length === 4) {
            const fromSquare = chessNotationToSquare(move.substring(0, 2));
            const toSquare = chessNotationToSquare(move.substring(2, 4));

            console.log(`Highlighting squares: ${fromSquare}, ${toSquare}`);

            document.querySelectorAll(".highlight").forEach(el => el.remove());
            highlightSquare(fromSquare);
            highlightSquare(toSquare);
        }
    };

    ws.onerror = function(error) {
        alert("Could not connect to Gentlefish service ❌");
        console.error("WebSocket Error:", error);
    };
})();
