# Gentlefish

Gentlefish is a for-fun chess assistant that integrates with Chess.com games against bots. Instead of playing optimally, it uses Stockfish to find moves that just barely lose, keeping the game close and exciting — whether you're playing against Bot Martin (250 Elo) or Maximum (3200 Elo). 

The suggested move is highlighted directly on the live chessboard.

By default, Gentlefish plays to always stay slightly worse than its opponent. However, if you'd like to test the limits of Stockfish, there is an option to generate the strongest moves instead.

> Note: Gentlefish acts as a GUI for Stockfish, which must be downloaded separately from [here.](https://github.com/official-stockfish/Stockfish)

![Demo video against Bot Martin](https://github.com/user-attachments/assets/cae976af-b196-4aa7-ad06-429c59e3a08f)

---

## How It Works
Gentlefish consists of two parts:
1. A **JavaScript client** that runs as a bookmarklet in the browser.
2. A **Kotlin/Ktor backend** that runs Stockfish, processes moves, and handles WebSocket communication.

Modern browsers require all WebSocket connections to be encrypted (WSS) when communicating with an HTTPS website. To simplify setup, a pre-configured JKS dummy certificate is included in the repository, eliminating the need for additional TLS configuration.

### Program Flow
1. The JavaScript client reads moves from the Chess.com DOM and sends them to the backend via WebSocket.
2. The backend parses the moves and generates a FEN string using ChessLib.
3. The FEN is fed into Stockfish, with two move generation options (selectable from the GUI):
   - Losing mode (default) - Tries to stay slightly worse.
   - Winning mode - Plays normally to win.
4. The proposed move is sent back to the client via WebSocket.
5. The JavaScript client highlights the starting and ending squares for the move.

---

## How Does Losing Mode Work?
Gentlefish modifies Stockfish’s move selection to always slightly lose:
1. It generates the top 5 best moves (`multipv 5`).
2. It checks if any move gives an evaluation between **-1 and -3 centipawns**.
3. If no move fits the range, it picks the move closest to **0 centipawns** (neutral).

---

## Libraries Used
- [Ktor](https://github.com/ktorio/ktor) - WebSocket server for backend communication.
- [Koin](https://github.com/InsertKoinIO/koin) - Dependency injection for the Kotlin backend.
- [ChessLib](https://github.com/bhlangonijr/chesslib) - Parses and converts PGN/FEN chess notations.
- [Aurora](https://github.com/kirill-grouchnikov/aurora) - GUI framework for the desktop app.
- [Compose Multiplatform File Picker](https://github.com/Wavesonics/compose-multiplatform-file-picker) - Lets users select the Stockfish binary.
