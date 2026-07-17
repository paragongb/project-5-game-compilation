// Simple, self-contained chess engine + UI.
// Supports: standard piece movement, captures, turn order, check detection,
// checkmate/stalemate detection, and pawn promotion (auto-queen).
// Not included (kept simple on purpose): castling, en passant.

(function () {
    const FILES = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'];
    const PIECE_UNICODE = {
        wK: '\u2654', wQ: '\u2655', wR: '\u2656', wB: '\u2657', wN: '\u2658', wP: '\u2659',
        bK: '\u265A', bQ: '\u265B', bR: '\u265C', bB: '\u265D', bN: '\u265E', bP: '\u265F',
    };

    function initialBoard() {
        const empty = () => Array(8).fill(null);
        const board = [
            ['bR', 'bN', 'bB', 'bQ', 'bK', 'bB', 'bN', 'bR'],
            ['bP', 'bP', 'bP', 'bP', 'bP', 'bP', 'bP', 'bP'],
            empty(), empty(), empty(), empty(),
            ['wP', 'wP', 'wP', 'wP', 'wP', 'wP', 'wP', 'wP'],
            ['wR', 'wN', 'wB', 'wQ', 'wK', 'wB', 'wN', 'wR'],
        ];
        return board;
    }

    function inBounds(r, c) {
        return r >= 0 && r < 8 && c >= 0 && c < 8;
    }

    function cloneBoard(board) {
        return board.map(row => row.slice());
    }

    // Pseudo-legal moves for a piece at (r, c), ignoring whether it leaves own king in check.
    function pseudoMoves(board, r, c) {
        const piece = board[r][c];
        if (!piece) return [];
        const color = piece[0];
        const type = piece[1];
        const moves = [];
        const enemy = color === 'w' ? 'b' : 'w';

        const addSlide = (dirs) => {
            for (const [dr, dc] of dirs) {
                let nr = r + dr, nc = c + dc;
                while (inBounds(nr, nc)) {
                    const target = board[nr][nc];
                    if (!target) {
                        moves.push([nr, nc]);
                    } else {
                        if (target[0] === enemy) moves.push([nr, nc]);
                        break;
                    }
                    nr += dr; nc += dc;
                }
            }
        };

        if (type === 'P') {
            const dir = color === 'w' ? -1 : 1;
            const startRow = color === 'w' ? 6 : 1;
            if (inBounds(r + dir, c) && !board[r + dir][c]) {
                moves.push([r + dir, c]);
                if (r === startRow && !board[r + 2 * dir][c]) {
                    moves.push([r + 2 * dir, c]);
                }
            }
            for (const dc of [-1, 1]) {
                const nr = r + dir, nc = c + dc;
                if (inBounds(nr, nc) && board[nr][nc] && board[nr][nc][0] === enemy) {
                    moves.push([nr, nc]);
                }
            }
        } else if (type === 'N') {
            const deltas = [[-2, -1], [-2, 1], [-1, -2], [-1, 2], [1, -2], [1, 2], [2, -1], [2, 1]];
            for (const [dr, dc] of deltas) {
                const nr = r + dr, nc = c + dc;
                if (inBounds(nr, nc) && (!board[nr][nc] || board[nr][nc][0] === enemy)) {
                    moves.push([nr, nc]);
                }
            }
        } else if (type === 'B') {
            addSlide([[-1, -1], [-1, 1], [1, -1], [1, 1]]);
        } else if (type === 'R') {
            addSlide([[-1, 0], [1, 0], [0, -1], [0, 1]]);
        } else if (type === 'Q') {
            addSlide([[-1, -1], [-1, 1], [1, -1], [1, 1], [-1, 0], [1, 0], [0, -1], [0, 1]]);
        } else if (type === 'K') {
            const deltas = [[-1, -1], [-1, 0], [-1, 1], [0, -1], [0, 1], [1, -1], [1, 0], [1, 1]];
            for (const [dr, dc] of deltas) {
                const nr = r + dr, nc = c + dc;
                if (inBounds(nr, nc) && (!board[nr][nc] || board[nr][nc][0] === enemy)) {
                    moves.push([nr, nc]);
                }
            }
        }
        return moves;
    }

    function findKing(board, color) {
        for (let r = 0; r < 8; r++) {
            for (let c = 0; c < 8; c++) {
                if (board[r][c] === color + 'K') return [r, c];
            }
        }
        return null;
    }

    function isSquareAttacked(board, r, c, byColor) {
        for (let rr = 0; rr < 8; rr++) {
            for (let cc = 0; cc < 8; cc++) {
                const piece = board[rr][cc];
                if (piece && piece[0] === byColor) {
                    const moves = pseudoMoves(board, rr, cc);
                    if (moves.some(([mr, mc]) => mr === r && mc === c)) return true;
                }
            }
        }
        return false;
    }

    function isInCheck(board, color) {
        const king = findKing(board, color);
        if (!king) return false;
        const enemy = color === 'w' ? 'b' : 'w';
        return isSquareAttacked(board, king[0], king[1], enemy);
    }

    // Legal moves: pseudo-legal moves that don't leave the mover's own king in check.
    function legalMoves(board, r, c) {
        const piece = board[r][c];
        if (!piece) return [];
        const color = piece[0];
        return pseudoMoves(board, r, c).filter(([nr, nc]) => {
            const copy = cloneBoard(board);
            copy[nr][nc] = copy[r][c];
            copy[r][c] = null;
            return !isInCheck(copy, color);
        });
    }

    function allLegalMoves(board, color) {
        const result = [];
        for (let r = 0; r < 8; r++) {
            for (let c = 0; c < 8; c++) {
                const piece = board[r][c];
                if (piece && piece[0] === color) {
                    for (const move of legalMoves(board, r, c)) {
                        result.push({ from: [r, c], to: move });
                    }
                }
            }
        }
        return result;
    }

    class ChessGame {
        constructor(rootEl) {
            this.root = rootEl;
            this.board = initialBoard();
            this.turn = 'w';
            this.selected = null;
            this.legalTargets = [];
            this.gameOver = false;
            this.render();
        }

        reset() {
            this.board = initialBoard();
            this.turn = 'w';
            this.selected = null;
            this.legalTargets = [];
            this.gameOver = false;
            this.render();
        }

        squareClicked(r, c) {
            if (this.gameOver) return;
            const piece = this.board[r][c];

            if (this.selected) {
                const isTarget = this.legalTargets.some(([tr, tc]) => tr === r && tc === c);
                if (isTarget) {
                    this.makeMove(this.selected, [r, c]);
                    this.selected = null;
                    this.legalTargets = [];
                    this.render();
                    return;
                }
                // Clicking another own piece re-selects instead of moving.
                if (piece && piece[0] === this.turn) {
                    this.selected = [r, c];
                    this.legalTargets = legalMoves(this.board, r, c);
                    this.render();
                    return;
                }
                this.selected = null;
                this.legalTargets = [];
                this.render();
                return;
            }

            if (piece && piece[0] === this.turn) {
                this.selected = [r, c];
                this.legalTargets = legalMoves(this.board, r, c);
                this.render();
            }
        }

        makeMove(from, to) {
            const [fr, fc] = from;
            const [tr, tc] = to;
            const piece = this.board[fr][fc];
            this.board[tr][tc] = piece;
            this.board[fr][fc] = null;

            // Auto-promote pawns that reach the last rank.
            if (piece[1] === 'P' && (tr === 0 || tr === 7)) {
                this.board[tr][tc] = piece[0] + 'Q';
            }

            this.turn = this.turn === 'w' ? 'b' : 'w';
            this.checkGameEnd();
        }

        checkGameEnd() {
            const moves = allLegalMoves(this.board, this.turn);
            const inCheck = isInCheck(this.board, this.turn);
            if (moves.length === 0) {
                this.gameOver = true;
                const who = this.turn === 'w' ? 'White' : 'Black';
                this.status = inCheck ? `Checkmate - ${who === 'White' ? 'Black' : 'White'} wins!` : 'Stalemate - draw.';
            } else {
                this.status = inCheck ? `${this.turn === 'w' ? 'White' : 'Black'} is in check.` : '';
            }
        }

        render() {
            this.root.innerHTML = '';

            const info = document.createElement('div');
            info.className = 'chess-status';
            const turnText = this.gameOver ? '' : `${this.turn === 'w' ? 'White' : 'Black'} to move.`;
            info.textContent = [turnText, this.status || ''].filter(Boolean).join(' ');
            this.root.appendChild(info);

            const table = document.createElement('table');
            table.className = 'chess-board';
            for (let r = 0; r < 8; r++) {
                const row = document.createElement('tr');
                for (let c = 0; c < 8; c++) {
                    const cell = document.createElement('td');
                    const isLight = (r + c) % 2 === 0;
                    cell.className = 'chess-square ' + (isLight ? 'light' : 'dark');

                    if (this.selected && this.selected[0] === r && this.selected[1] === c) {
                        cell.classList.add('selected');
                    }
                    if (this.legalTargets.some(([tr, tc]) => tr === r && tc === c)) {
                        cell.classList.add('legal-target');
                    }

                    const piece = this.board[r][c];
                    if (piece) {
                        cell.textContent = PIECE_UNICODE[piece];
                        cell.classList.add(piece[0] === 'w' ? 'white-piece' : 'black-piece');
                    }

                    cell.addEventListener('click', () => this.squareClicked(r, c));
                    row.appendChild(cell);
                }
                table.appendChild(row);
            }
            this.root.appendChild(table);

            const resetBtn = document.createElement('button');
            resetBtn.textContent = 'New Game';
            resetBtn.className = 'chess-reset-btn';
            resetBtn.addEventListener('click', () => this.reset());
            this.root.appendChild(resetBtn);
        }
    }

    window.ChessGame = ChessGame;
})();
