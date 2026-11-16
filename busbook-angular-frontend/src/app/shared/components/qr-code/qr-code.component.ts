import { Component, Input, OnChanges } from '@angular/core';
import { generateQrMatrix } from '../../utils/qr-code.util';

@Component({
  selector: 'app-qr-code',
  template: `
    <div class="qr-container" [style.width.px]="size" [style.height.px]="size">
      <svg
        [attr.viewBox]="'0 0 ' + (matrix.length + 4) + ' ' + (matrix.length + 4)"
        xmlns="http://www.w3.org/2000/svg"
        class="qr-svg"
      >
        <!-- Background blanc / quiet zone -->
        <rect
          x="0"
          y="0"
          [attr.width]="matrix.length + 4"
          [attr.height]="matrix.length + 4"
          fill="#ffffff"
          rx="1"
        />
        <!-- Modules noirs -->
        @for (row of matrix; track rIdx; let rIdx = $index) {
          @for (cell of row; track cIdx; let cIdx = $index) {
            @if (cell) {
              <rect
                [attr.x]="cIdx + 2"
                [attr.y]="rIdx + 2"
                width="1.02"
                height="1.02"
                fill="#1D1D1B"
              />
            }
          }
        }
      </svg>
    </div>
  `,
  styles: [`
    .qr-container {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      padding: 10px;
      background: #ffffff;
      border-radius: 16px;
      box-shadow: 0 4px 14px rgba(0, 0, 0, 0.08);
      border: 1px solid rgba(0, 0, 0, 0.05);
    }
    .qr-svg {
      width: 100%;
      height: 100%;
      display: block;
      shape-rendering: crispEdges;
    }
  `]
})
export class QrCodeComponent implements OnChanges {
  @Input({ required: true }) value: string = '';
  @Input() size: number = 180;

  matrix: boolean[][] = [];

  ngOnChanges(): void {
    if (this.value) {
      this.matrix = generateQrMatrix(this.value);
    } else {
      this.matrix = [];
    }
  }
}
