import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Trip } from '../../../trips/models/trip';
import { ReservationResponse } from '../../../../core/models/reservation.model';
import { PaymentMethod } from '../../../../core/models/payment.model';
import { CountdownTimerComponent } from '../countdown-timer/countdown-timer.component';

@Component({
  selector: 'app-payment-section',
  imports: [CommonModule, FormsModule, CountdownTimerComponent],
  template: `
    <div class="payment-card glass">
      <div class="payment-header">
        <span class="section-label">PAYMENT & CONFIRMATION</span>
        <h2>Complete your booking</h2>
        <p>Your seat is temporarily reserved. Finalize payment before time expires.</p>
      </div>

      <!-- Minuteur 10 minutes -->
      <app-countdown-timer
        [seconds]="remainingSeconds"
        [isExpired]="isExpired"
      />

      <!-- Message d'erreur s'il y a lieu -->
      @if (errorMessage) {
        <div class="error-banner">
          <span class="error-icon">❌</span>
          <span class="error-text">{{ errorMessage }}</span>
        </div>
      }

      @if (!isExpired) {
        <div class="payment-methods">
          <span class="methods-label">SELECT PAYMENT METHOD</span>

          <div class="methods-grid">
            <label
              class="method-option"
              [class.active]="selectedMethod === 'CARTE_BANCAIRE'"
            >
              <input
                type="radio"
                name="paymentMethod"
                value="CARTE_BANCAIRE"
                [(ngModel)]="selectedMethod"
              />
              <div class="method-content">
                <span class="method-icon">💳</span>
                <div class="method-info">
                  <strong>Credit Card</strong>
                  <small>Visa, Mastercard, CMI</small>
                </div>
              </div>
            </label>

            <label
              class="method-option"
              [class.active]="selectedMethod === 'PORTEFEUILLE_MOBILE'"
            >
              <input
                type="radio"
                name="paymentMethod"
                value="PORTEFEUILLE_MOBILE"
                [(ngModel)]="selectedMethod"
              />
              <div class="method-content">
                <span class="method-icon">📱</span>
                <div class="method-info">
                  <strong>Mobile Wallet</strong>
                  <small>Orange Money, inwi money, etc.</small>
                </div>
              </div>
            </label>

            <label
              class="method-option"
              [class.active]="selectedMethod === 'ESPECES'"
            >
              <input
                type="radio"
                name="paymentMethod"
                value="ESPECES"
                [(ngModel)]="selectedMethod"
              />
              <div class="method-content">
                <span class="method-icon">💵</span>
                <div class="method-info">
                  <strong>Cash at Counter</strong>
                  <small>Pay directly at the bus station</small>
                </div>
              </div>
            </label>
          </div>
        </div>

        <!-- Simulation pour tests QA -->
        <div class="simulation-toggle">
          <label class="toggle-container">
            <input type="checkbox" [(ngModel)]="simulateFailure" />
            <span class="toggle-text">Simulate payment failure (QA testing)</span>
          </label>
        </div>

        <!-- Boutons d'action -->
        <div class="payment-actions">
          <button
            class="btn-pay"
            [disabled]="isProcessing || isExpired"
            (click)="onConfirmPayment()"
          >
            @if (isProcessing) {
              <span class="spinner"></span>
              Processing payment...
            } @else {
              <span>Confirm & Pay {{ trip.prix }} MAD</span>
              <span>🔒</span>
            }
          </button>

          <button
            class="btn-cancel"
            [disabled]="isProcessing"
            (click)="cancel.emit()"
          >
            Cancel Reservation
          </button>
        </div>
      } @else {
        <!-- Vue si expirée -->
        <div class="expired-actions">
          <button class="btn-restart" (click)="cancel.emit()">
            <span>🔄</span>
            Seat Released - Choose Another Seat
          </button>
        </div>
      }
    </div>
  `,
  styles: [`
    .payment-card {
      width: 100%;
      padding: 24px;
      border-radius: 24px;
      display: flex;
      flex-direction: column;
      gap: 20px;
    }

    .payment-header .section-label {
      color: var(--color-muted);
      font-size: 10px;
      font-weight: 800;
      letter-spacing: 1.5px;
    }

    .payment-header h2 {
      margin: 6px 0;
      color: var(--color-ink);
      font-size: 22px;
      font-weight: 800;
    }

    .payment-header p {
      margin: 0;
      color: var(--color-muted);
      font-size: 12px;
    }

    .error-banner {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 12px 16px;
      background: rgba(239, 68, 68, 0.15);
      border: 1px solid rgba(239, 68, 68, 0.3);
      border-radius: 12px;
      color: #991B1B;
      font-size: 12px;
      font-weight: 600;
    }

    .payment-methods {
      display: flex;
      flex-direction: column;
      gap: 10px;
    }

    .methods-label {
      font-size: 10px;
      font-weight: 800;
      letter-spacing: 1.2px;
      color: var(--color-muted);
    }

    .methods-grid {
      display: flex;
      flex-direction: column;
      gap: 10px;
    }

    .method-option {
      display: flex;
      align-items: center;
      padding: 14px 16px;
      border-radius: 14px;
      background: rgba(255, 255, 255, 0.5);
      border: 1.5px solid rgba(0, 0, 0, 0.06);
      cursor: pointer;
      transition: all 0.2s ease;
    }

    .method-option input {
      display: none;
    }

    .method-option.active {
      border-color: var(--color-coral);
      background: rgba(255, 255, 255, 0.9);
      box-shadow: 0 4px 14px rgba(249, 146, 113, 0.15);
    }

    .method-content {
      display: flex;
      align-items: center;
      gap: 14px;
    }

    .method-icon {
      font-size: 24px;
    }

    .method-info {
      display: flex;
      flex-direction: column;
      gap: 2px;
    }

    .method-info strong {
      font-size: 13px;
      font-weight: 700;
      color: var(--color-ink);
    }

    .method-info small {
      font-size: 11px;
      color: var(--color-muted);
    }

    .simulation-toggle {
      padding: 8px 4px;
    }

    .toggle-container {
      display: inline-flex;
      align-items: center;
      gap: 8px;
      font-size: 11px;
      color: var(--color-muted);
      cursor: pointer;
    }

    .payment-actions {
      display: flex;
      flex-direction: column;
      gap: 10px;
    }

    .btn-pay {
      display: flex;
      align-items: center;
      justify-content: space-between;
      width: 100%;
      padding: 16px 20px;
      border: none;
      border-radius: 14px;
      background: var(--color-leaf);
      color: white;
      font-size: 13px;
      font-weight: 800;
      cursor: pointer;
      transition: opacity 0.2s ease;
    }

    .btn-pay:hover:not(:disabled) {
      opacity: 0.9;
    }

    .btn-pay:disabled {
      opacity: 0.5;
      cursor: not-allowed;
    }

    .btn-cancel {
      width: 100%;
      padding: 12px;
      background: transparent;
      border: 1px solid rgba(0, 0, 0, 0.1);
      border-radius: 12px;
      color: var(--color-muted);
      font-size: 12px;
      font-weight: 700;
      cursor: pointer;
    }

    .btn-cancel:hover {
      background: rgba(0, 0, 0, 0.04);
      color: var(--color-ink);
    }

    .expired-actions {
      width: 100%;
    }

    .btn-restart {
      width: 100%;
      padding: 16px;
      border: none;
      border-radius: 14px;
      background: var(--color-coral);
      color: white;
      font-size: 13px;
      font-weight: 800;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
    }

    .spinner {
      width: 16px;
      height: 16px;
      border: 2px solid #ffffff;
      border-top-color: transparent;
      border-radius: 50%;
      display: inline-block;
      animation: spin 0.8s linear infinite;
    }

    @keyframes spin {
      to { transform: rotate(360deg); }
    }
  `]
})
export class PaymentSectionComponent {
  @Input({ required: true }) trip!: Trip;
  @Input({ required: true }) seatNumber!: number;
  @Input({ required: true }) reservation!: ReservationResponse;
  @Input() remainingSeconds: number = 600;
  @Input() isExpired: boolean = false;
  @Input() isProcessing: boolean = false;
  @Input() errorMessage: string | null = null;

  @Output() pay = new EventEmitter<{ paymentMethod: PaymentMethod; simulateFailure: boolean }>();
  @Output() cancel = new EventEmitter<void>();

  selectedMethod: PaymentMethod = 'CARTE_BANCAIRE';
  simulateFailure: boolean = false;

  onConfirmPayment(): void {
    if (this.isExpired || this.isProcessing) return;
    this.pay.emit({
      paymentMethod: this.selectedMethod,
      simulateFailure: this.simulateFailure
    });
  }
}
