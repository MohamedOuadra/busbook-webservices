import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-countdown-timer',
  template: `
    <div class="timer-card glass" [class.warning]="isWarning && !isExpired" [class.expired]="isExpired">
      <div class="timer-header">
        <div class="timer-icon-badge">
          @if (isExpired) {
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="timer-svg">
              <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
              <line x1="12" y1="9" x2="12" y2="13"/>
              <line x1="12" y1="17" x2="12.01" y2="17"/>
            </svg>
          } @else if (isWarning) {
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="timer-svg pulse">
              <circle cx="12" cy="12" r="10"/>
              <polyline points="12 6 12 12 16 14"/>
            </svg>
          } @else {
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="timer-svg">
              <circle cx="12" cy="12" r="10"/>
              <polyline points="12 6 12 12 14 10"/>
            </svg>
          }
        </div>
        <div class="timer-text">
          <span class="timer-label">TEMPORARY RESERVATION HOLD</span>
          <div class="timer-countdown">
            @if (isExpired) {
              <span class="expired-text">EXPIRED</span>
            } @else {
              <span class="time-digits">{{ formattedTime }}</span>
              <span class="time-sub">remaining to complete payment</span>
            }
          </div>
        </div>
      </div>

      <div class="progress-bar-container">
        <div
          class="progress-bar-fill"
          [style.width.%]="progressPercentage"
          [class.progress-warning]="isWarning && !isExpired"
          [class.progress-expired]="isExpired"
        ></div>
      </div>

      @if (isExpired) {
        <p class="timer-note alert-danger">
          The 10-minute hold has expired. This seat has been released and is available to other passengers.
        </p>
      } @else if (isWarning) {
        <p class="timer-note alert-warn">
          Less than 2 minutes remaining! Complete your payment now before the seat is released.
        </p>
      } @else {
        <p class="timer-note">
          Your seat is held exclusively for you. Please finalize payment within the 10-minute window.
        </p>
      }
    </div>
  `,
  styles: [`
    .timer-card {
      width: 100%;
      padding: 16px 20px;
      border-radius: 18px;
      transition: all 0.3s ease;
      display: flex;
      flex-direction: column;
      gap: 12px;
    }

    .timer-card.warning {
      border-color: rgba(249, 146, 113, 0.6);
      background: rgba(254, 243, 239, 0.65);
    }

    .timer-card.expired {
      border-color: rgba(239, 68, 68, 0.6);
      background: rgba(254, 226, 226, 0.7);
    }

    .timer-header {
      display: flex;
      align-items: center;
      gap: 14px;
    }

    .timer-icon-badge {
      width: 44px;
      height: 44px;
      border-radius: 14px;
      background: rgba(255, 255, 255, 0.85);
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 22px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
      flex-shrink: 0;
    }

    .timer-svg {
      width: 22px;
      height: 22px;
      color: var(--color-ink);
    }

    .pulse {
      animation: pulse 1s infinite alternate;
    }

    @keyframes pulse {
      0% { transform: scale(1); opacity: 0.8; }
      100% { transform: scale(1.15); opacity: 1; }
    }

    .timer-text {
      display: flex;
      flex-direction: column;
      gap: 2px;
    }

    .timer-label {
      font-size: 10px;
      font-weight: 800;
      letter-spacing: 1.2px;
      color: var(--color-muted);
    }

    .timer-countdown {
      display: flex;
      align-items: baseline;
      gap: 8px;
    }

    .time-digits {
      font-size: 26px;
      font-weight: 800;
      color: var(--color-ink);
      font-variant-numeric: tabular-nums;
      letter-spacing: 1px;
    }

    .warning .time-digits {
      color: #E25B36;
    }

    .expired-text {
      font-size: 22px;
      font-weight: 800;
      color: #DC2626;
      letter-spacing: 1.5px;
    }

    .time-sub {
      font-size: 12px;
      color: var(--color-muted);
      font-weight: 500;
    }

    .progress-bar-container {
      width: 100%;
      height: 6px;
      background: rgba(0, 0, 0, 0.06);
      border-radius: 999px;
      overflow: hidden;
    }

    .progress-bar-fill {
      height: 100%;
      background: var(--color-leaf);
      border-radius: 999px;
      transition: width 1s linear;
    }

    .progress-warning {
      background: var(--color-coral) !important;
    }

    .progress-expired {
      background: #DC2626 !important;
    }

    .timer-note {
      margin: 0;
      font-size: 12px;
      color: var(--color-muted);
      line-height: 1.4;
    }

    .alert-warn {
      color: #C04321;
      font-weight: 600;
    }

    .alert-danger {
      color: #B91C1C;
      font-weight: 700;
    }
  `]
})
export class CountdownTimerComponent {
  @Input() seconds: number = 600;
  @Input() isExpired: boolean = false;

  get formattedTime(): string {
    const s = Math.max(0, this.seconds);
    const m = Math.floor(s / 60);
    const remainder = s % 60;
    return `${m.toString().padStart(2, '0')}:${remainder.toString().padStart(2, '0')}`;
  }

  get isWarning(): boolean {
    return this.seconds > 0 && this.seconds <= 120;
  }

  get progressPercentage(): number {
    if (this.isExpired || this.seconds <= 0) return 0;
    // Total default is 10 minutes = 600 seconds
    return Math.min(100, Math.max(0, (this.seconds / 600) * 100));
  }
}
