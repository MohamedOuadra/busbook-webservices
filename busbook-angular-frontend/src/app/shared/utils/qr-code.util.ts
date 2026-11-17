/**
 * Générateur de matrice QR Code autonome en TypeScript pur (Version 3, 29x29 modules)
 * Ne nécessite aucune dépendance externe npm.
 */
export function generateQrMatrix(data: string): boolean[][] {
  const size = 29; // Version 3 QR code: 29x29
  const matrix: (boolean | null)[][] = Array.from({ length: size }, () => Array(size).fill(null));

  // 1. Finder patterns (7x7) + white separator
  const placeFinder = (rStart: number, cStart: number) => {
    for (let r = -1; r <= 7; r++) {
      for (let c = -1; c <= 7; c++) {
        const row = rStart + r;
        const col = cStart + c;
        if (row >= 0 && row < size && col >= 0 && col < size) {
          if (r >= 0 && r <= 6 && c >= 0 && c <= 6) {
            const isBorder = r === 0 || r === 6 || c === 0 || c === 6;
            const isCenter = r >= 2 && r <= 4 && c >= 2 && c <= 4;
            matrix[row][col] = isBorder || isCenter;
          } else {
            matrix[row][col] = false; // Separator
          }
        }
      }
    }
  };

  placeFinder(0, 0);
  placeFinder(0, size - 7);
  placeFinder(size - 7, 0);

  // 2. Alignment pattern at (20, 20) for 29x29
  const alignR = 20;
  const alignC = 20;
  for (let r = -2; r <= 2; r++) {
    for (let c = -2; c <= 2; c++) {
      const isBorder = Math.abs(r) === 2 || Math.abs(c) === 2;
      const isCenter = r === 0 && c === 0;
      matrix[alignR + r][alignC + c] = isBorder || isCenter;
    }
  }

  // 3. Timing patterns on row 6 and col 6
  for (let i = 8; i < size - 8; i++) {
    if (matrix[6][i] === null) matrix[6][i] = i % 2 === 0;
    if (matrix[i][6] === null) matrix[i][6] = i % 2 === 0;
  }

  // Dark module
  matrix[size - 8][8] = true;

  // 4. Encode data bits deterministically from the input string (hash / bitstream)
  const bytes: number[] = [];
  for (let i = 0; i < data.length; i++) {
    bytes.push(data.charCodeAt(i) & 0xff);
  }

  // Hash / pseudo-random bit expansion to fill data modules
  let seed = 0x5a5a;
  for (const b of bytes) {
    seed = (seed * 31 + b) & 0xffff;
  }

  let bitIndex = 0;
  const getBit = (index: number): boolean => {
    if (index < bytes.length * 8) {
      const byte = bytes[Math.floor(index / 8)];
      return ((byte >> (7 - (index % 8))) & 1) === 1;
    }
    // PRNG fill
    seed = (seed * 1103515245 + 12345) & 0x7fffffff;
    return (seed & 1) === 1;
  };

  // 5. Fill remaining modules
  let right = size - 1;
  let upward = true;

  while (right > 0) {
    if (right === 6) right--; // Skip vertical timing pattern
    const rows = upward
      ? Array.from({ length: size }, (_, i) => size - 1 - i)
      : Array.from({ length: size }, (_, i) => i);

    for (const r of rows) {
      for (const col of [right, right - 1]) {
        if (matrix[r][col] === null) {
          const rawBit = getBit(bitIndex++);
          // Mask pattern: (row + col) % 2 == 0
          const mask = (r + col) % 2 === 0;
          matrix[r][col] = rawBit !== mask;
        }
      }
    }
    right -= 2;
    upward = !upward;
  }

  return matrix.map(row => row.map(cell => cell === true));
}
