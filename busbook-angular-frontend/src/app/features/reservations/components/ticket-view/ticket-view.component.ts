import { Component, EventEmitter, Input, Output } from '@angular/core';
import { TicketResponse } from '../../../../core/models/ticket.model';
import { Trip } from '../../../trips/models/trip';
import { QrCodeComponent } from '../../../../shared/components/qr-code/qr-code.component';

@Component({
  selector: 'app-ticket-view',
  imports: [QrCodeComponent],
  template: `
    <div class="ticket-container">
      <div class="ticket-card glass">
        <!-- Ticket Top Banner -->
        <div class="ticket-banner">
          <div class="brand">
            <span class="brand-logo">🚌</span>
            <span class="brand-name">BusBook</span>
            <span class="ticket-badge">E-TICKET OFFICIAL</span>
          </div>
          <div class="status-pill valid">
            <span class="status-dot"></span>
            CONFIRMED & PAID
          </div>
        </div>

        <!-- Ticket Body -->
        <div class="ticket-body">
          <div class="ticket-left">
            <div class="journey-display">
              <div class="station">
                <span class="station-label">DEPARTURE</span>
                <h3 class="station-name">{{ trip.villeDepart }}</h3>
                <span class="station-time">{{ formatTime(trip.dateDepart) }}</span>
                <span class="station-date">{{ formatDate(trip.dateDepart) }}</span>
              </div>

              <div class="journey-arrow">
                <div class="arrow-line"></div>
                <span class="bus-icon">➔</span>
                <span class="bus-info">{{ trip.bus.type }} • {{ trip.bus.immatriculation }}</span>
              </div>

              <div class="station">
                <span class="station-label">ARRIVAL</span>
                <h3 class="station-name">{{ trip.villeArrivee }}</h3>
                <span class="station-time">{{ formatTime(trip.dateArrivee) }}</span>
                <span class="station-date">{{ formatDate(trip.dateArrivee) }}</span>
              </div>
            </div>

            <div class="ticket-details-grid">
              <div class="detail-box">
                <span class="detail-label">PASSENGER NAME</span>
                <strong class="detail-val">{{ ticket.travelerName }}</strong>
              </div>

              <div class="detail-box">
                <span class="detail-label">SEAT NUMBER</span>
                <strong class="detail-val seat-highlight">Seat #{{ ticket.seatNumber }}</strong>
              </div>

              <div class="detail-box">
                <span class="detail-label">TICKET NUMBER</span>
                <strong class="detail-val mono">{{ ticket.ticketNumber }}</strong>
              </div>

              <div class="detail-box">
                <span class="detail-label">PRICE PAID</span>
                <strong class="detail-val">{{ trip.prix }} MAD</strong>
              </div>
            </div>
          </div>

          <!-- Perforated separator -->
          <div class="ticket-divider">
            <div class="circle top"></div>
            <div class="dash-line"></div>
            <div class="circle bottom"></div>
          </div>

          <!-- Ticket Right: QR Code & Verification -->
          <div class="ticket-right">
            <div class="qr-wrapper">
              <app-qr-code [value]="ticket.qrCode" [size]="150" />
            </div>
            <span class="scan-instruction">Scan for boarding verification</span>
            <span class="security-code">ID: #{{ ticket.reservationId }}</span>
          </div>
        </div>

        <!-- Ticket Footer & Action Controls -->
        <div class="ticket-footer no-print">
          <button class="btn-action btn-print" (click)="onPrint()">
            <span>🖨️</span>
            Print / Save Ticket
          </button>
          <button class="btn-action btn-home" (click)="onNewBooking.emit()">
            <span>🏠</span>
            Book Another Journey
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .ticket-container {
      width: 100%;
      max-width: 820px;
      margin: 0 auto;
      padding: 10px 0;
    }

    .ticket-card {
      border-radius: 24px;
      overflow: hidden;
      background: rgba(255, 255, 255, 0.75);
      border: 1px solid rgba(255, 255, 255, 0.6);
      box-shadow: 0 16px 40px rgba(0, 0, 0, 0.08);
    }

    .ticket-banner {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 18px 24px;
      background: #1D1D1B;
      color: #ffffff;
    }

    .brand {
      display: flex;
      align-items: center;
      gap: 10px;
    }

    .brand-logo {
      font-size: 22px;
    }

    .brand-name {
      font-size: 20px;
      font-weight: 800;
      letter-spacing: -0.5px;
    }

    .ticket-badge {
      font-size: 9px;
      font-weight: 700;
      letter-spacing: 1.5px;
      background: rgba(255, 255, 255, 0.15);
      padding: 4px 8px;
      border-radius: 6px;
      color: #F99271;
    }

    .status-pill {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 11px;
      font-weight: 800;
      padding: 6px 14px;
      border-radius: 999px;
    }

    .status-pill.valid {
      background: rgba(87, 195, 82, 0.2);
      color: #57C352;
    }

    .status-dot {
      width: 8px;
      height: 8px;
      border-radius: 50%;
      background: #57C352;
    }

    .ticket-body {
      display: flex;
      padding: 24px;
      gap: 16px;
      position: relative;
    }

    .ticket-left {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 22px;
    }

    .journey-display {
      display: flex;
      align-items: center;
      justify-content: space-between;
      background: rgba(255, 255, 255, 0.6);
      padding: 16px 20px;
      border-radius: 16px;
      border: 1px solid rgba(0, 0, 0, 0.04);
    }

    .station {
      display: flex;
      flex-direction: column;
      gap: 2px;
    }

    .station-label {
      font-size: 9px;
      font-weight: 800;
      letter-spacing: 1.2px;
      color: var(--color-muted);
    }

    .station-name {
      margin: 0;
      font-size: 20px;
      font-weight: 800;
      color: var(--color-ink);
    }

    .station-time {
      font-size: 14px;
      font-weight: 700;
      color: var(--color-coral);
    }

    .station-date {
      font-size: 11px;
      color: var(--color-muted);
    }

    .journey-arrow {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 4px;
      flex: 1;
      padding: 0 16px;
    }

    .arrow-line {
      width: 100%;
      height: 2px;
      background: #D3DCB2;
      position: relative;
    }

    .bus-icon {
      font-size: 16px;
      color: #77766F;
    }

    .bus-info {
      font-size: 10px;
      font-weight: 600;
      color: var(--color-muted);
    }

    .ticket-details-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 14px;
    }

    .detail-box {
      display: flex;
      flex-direction: column;
      gap: 3px;
      padding: 10px 14px;
      background: rgba(255, 255, 255, 0.5);
      border-radius: 12px;
      border: 1px solid rgba(0, 0, 0, 0.03);
    }

    .detail-label {
      font-size: 9px;
      font-weight: 800;
      letter-spacing: 1px;
      color: var(--color-muted);
    }

    .detail-val {
      font-size: 13px;
      font-weight: 700;
      color: var(--color-ink);
    }

    .seat-highlight {
      color: #2563EB;
      font-size: 15px;
    }

    .mono {
      font-family: monospace;
      font-size: 11px;
    }

    .ticket-divider {
      width: 20px;
      display: flex;
      flex-direction: column;
      align-items: center;
      position: relative;
      margin: -24px 0;
    }

    .ticket-divider .circle {
      width: 24px;
      height: 24px;
      background: #E8C9B0;
      border-radius: 50%;
      position: absolute;
    }

    .ticket-divider .circle.top {
      top: -12px;
    }

    .ticket-divider .circle.bottom {
      bottom: -12px;
    }

    .ticket-divider .dash-line {
      flex: 1;
      border-left: 2px dashed rgba(0, 0, 0, 0.15);
      margin: 16px 0;
    }

    .ticket-right {
      width: 210px;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      gap: 10px;
      padding: 10px;
    }

    .qr-wrapper {
      padding: 4px;
    }

    .scan-instruction {
      font-size: 10px;
      color: var(--color-muted);
      text-align: center;
      font-weight: 500;
    }

    .security-code {
      font-size: 9px;
      font-family: monospace;
      color: #9CA3AF;
    }

    .ticket-footer {
      display: flex;
      gap: 14px;
      padding: 18px 24px;
      background: rgba(255, 255, 255, 0.4);
      border-top: 1px solid rgba(0, 0, 0, 0.05);
    }

    .btn-action {
      flex: 1;
      padding: 12px 18px;
      border-radius: 12px;
      font-weight: 800;
      font-size: 13px;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      transition: all 0.2s ease;
      border: none;
    }

    .btn-print {
      background: var(--color-ink);
      color: #ffffff;
    }

    .btn-print:hover {
      background: #333330;
    }

    .btn-home {
      background: var(--color-coral);
      color: #ffffff;
    }

    .btn-home:hover {
      opacity: 0.9;
    }

    @media (max-width: 768px) {
      .ticket-body {
        flex-direction: column;
      }
      .ticket-divider {
        display: none;
      }
      .ticket-right {
        width: 100%;
      }
    }

    @media print {
      .no-print {
        display: none !important;
      }
      body {
        background: #ffffff !important;
      }
      .ticket-card {
        box-shadow: none !important;
        border: 1px solid #cccccc !important;
      }
    }
  `]
})
export class TicketViewComponent {
  @Input({ required: true }) ticket!: TicketResponse;
  @Input({ required: true }) trip!: Trip;
  @Output() onNewBooking = new EventEmitter<void>();

  onPrint(): void {
    window.print();
  }

  formatTime(isoString: string): string {
    if (!isoString) return '';
    const parts = isoString.split('T');
    return parts.length > 1 ? parts[1].substring(0, 5) : isoString;
  }

  formatDate(isoString: string): string {
    if (!isoString) return '';
    const datePart = isoString.split('T')[0];
    return datePart;
  }
}
