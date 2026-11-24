import { Component, EventEmitter, Input, Output } from '@angular/core';
import { TicketResponse } from '../../../../core/models/ticket.model';
import { Trip } from '../../../trips/models/trip';
import { QrCodeComponent } from '../../../../shared/components/qr-code/qr-code.component';

@Component({
  selector: 'app-ticket-view',
  standalone: true,
  imports: [QrCodeComponent],
  template: `
    <div class="ticket-container printable-ticket">
      <div class="ticket-card glass">

        <!-- Ticket Top Banner -->
        <div class="ticket-banner">
          <div class="brand">
            <span class="brand-logo">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="ticket-brand-svg">
                <path d="M2 9V6a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v3"/>
                <path d="M2 9h20v9a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2z"/>
                <path d="M6 13h.01M10 13h.01M14 13h.01M18 13h.01"/>
              </svg>
            </span>
            <div class="brand-titles">
              <span class="brand-name">BusBook</span>
              <span class="brand-sub">INTERCITY EXPRESS PASS</span>
            </div>
            <span class="ticket-badge">OFFICIAL E-TICKET</span>
          </div>

          <div class="status-pill valid">
            <span class="status-dot"></span>
            CONFIRMED &amp; PAID
          </div>
        </div>

        <!-- Ticket Body -->
        <div class="ticket-body">
          <div class="ticket-left">

            <!-- Journey Route Card -->
            <div class="journey-display">
              <div class="station">
                <span class="station-label">ORIGIN</span>
                <h3 class="station-name">{{ trip.villeDepart }}</h3>
                <span class="station-time">{{ formatTime(trip.dateDepart) }}</span>
                <span class="station-date">{{ formatDate(trip.dateDepart) }}</span>
              </div>

              <div class="journey-arrow">
                <span class="bus-info">{{ trip.bus.type || 'Confort' }} &bull; {{ trip.bus.immatriculation || 'Standard' }}</span>
                <div class="arrow-visual">
                  <div class="arrow-dot start"></div>
                  <div class="arrow-line"></div>
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="bus-icon-svg">
                    <path d="M2 9V6a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v3"/>
                    <path d="M2 9h20v9a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2z"/>
                    <circle cx="7" cy="19" r="2"/>
                    <circle cx="17" cy="19" r="2"/>
                  </svg>
                  <div class="arrow-line"></div>
                  <div class="arrow-dot end"></div>
                </div>
                <span class="direct-label">DIRECT SERVICE</span>
              </div>

              <div class="station text-right">
                <span class="station-label">DESTINATION</span>
                <h3 class="station-name">{{ trip.villeArrivee }}</h3>
                <span class="station-time">{{ formatTime(trip.dateArrivee) }}</span>
                <span class="station-date">{{ formatDate(trip.dateArrivee) }}</span>
              </div>
            </div>

            <!-- Ticket Details Grid -->
            <div class="ticket-details-grid">
              <div class="detail-box">
                <span class="detail-label">PASSENGER NAME</span>
                <strong class="detail-val">{{ ticket.travelerName }}</strong>
              </div>

              <div class="detail-box seat-box">
                <span class="detail-label">SEAT ASSIGNED</span>
                <strong class="detail-val seat-highlight">Seat #{{ ticket.seatNumber }}</strong>
              </div>

              <div class="detail-box">
                <span class="detail-label">TICKET NUMBER</span>
                <strong class="detail-val mono">{{ ticket.ticketNumber }}</strong>
              </div>

              <div class="detail-box">
                <span class="detail-label">FARE PAID</span>
                <strong class="detail-val price-val">{{ trip.prix }} MAD</strong>
              </div>
            </div>

            <!-- Security notice -->
            <div class="security-banner">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="shield-svg">
                <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
              </svg>
              <span>Present this ticket on your smartphone or printed along with your national ID card at boarding.</span>
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
              <app-qr-code [value]="verificationUrl" [size]="145" />
            </div>
            <span class="scan-instruction">Scan to verify at /verify-ticket</span>
            <div class="barcode-stub">
              <span class="security-code">REF: #{{ ticket.reservationId }}</span>
              <span class="security-hash">{{ ticket.ticketNumber }}</span>
            </div>
          </div>
        </div>

        <!-- Ticket Footer & Action Controls -->
        <div class="ticket-footer no-print">
          <button class="btn-action btn-print" (click)="onPrint()" type="button">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="action-btn-svg">
              <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
              <polyline points="7 10 12 15 17 10"/>
              <line x1="12" y1="15" x2="12" y2="3"/>
            </svg>
            Télécharger / Imprimer le billet (PDF)
          </button>

          <button class="btn-action btn-home" (click)="onNewBooking.emit()" type="button">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="action-btn-svg">
              <circle cx="11" cy="11" r="8"/>
              <path d="m21 21-4.3-4.3"/>
            </svg>
            Nouvelle réservation
          </button>
        </div>

      </div>
    </div>
  `,
  styles: [`
    .ticket-container {
      width: 100%;
      max-width: 860px;
      margin: 0 auto;
      padding: 10px 0;
    }

    .ticket-card {
      border-radius: 24px;
      overflow: hidden;
      background: rgba(255, 255, 255, 0.85);
      border: 1px solid rgba(255, 255, 255, 0.8);
      box-shadow: 0 16px 40px rgba(0, 0, 0, 0.08);
    }

    .ticket-banner {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20px 28px;
      background: #1D1D1B;
      color: #ffffff;
    }

    .brand {
      display: flex;
      align-items: center;
      gap: 12px;
    }

    .brand-logo {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 36px;
      height: 36px;
      border-radius: 10px;
      background: rgba(255, 255, 255, 0.12);
    }

    .ticket-brand-svg {
      width: 20px;
      height: 20px;
      color: #ffffff;
    }

    .brand-titles {
      display: flex;
      flex-direction: column;
    }

    .brand-name {
      font-size: 19px;
      font-weight: 800;
      letter-spacing: -0.4px;
      color: #ffffff;
    }

    .brand-sub {
      font-size: 9px;
      font-weight: 800;
      letter-spacing: 1px;
      color: #9CA3AF;
    }

    .ticket-badge {
      font-size: 9px;
      font-weight: 800;
      letter-spacing: 1.5px;
      background: rgba(249, 146, 113, 0.2);
      padding: 4px 10px;
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
      box-shadow: 0 0 8px #57C352;
    }

    .ticket-body {
      display: flex;
      padding: 26px 28px;
      gap: 20px;
      position: relative;
    }

    .ticket-left {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 18px;
    }

    .journey-display {
      display: flex;
      align-items: center;
      justify-content: space-between;
      background: rgba(255, 255, 255, 0.7);
      padding: 16px 22px;
      border-radius: 16px;
      border: 1px solid rgba(0, 0, 0, 0.05);
    }

    .station {
      display: flex;
      flex-direction: column;
      gap: 2px;
    }

    .text-right {
      text-align: right;
    }

    .station-label {
      font-size: 9.5px;
      font-weight: 800;
      letter-spacing: 1.2px;
      color: var(--color-muted);
    }

    .station-name {
      margin: 0;
      font-size: 21px;
      font-weight: 800;
      color: var(--color-ink);
    }

    .station-time {
      font-size: 15px;
      font-weight: 800;
      color: var(--color-coral);
    }

    .station-date {
      font-size: 11px;
      color: var(--color-muted);
      font-weight: 600;
    }

    .journey-arrow {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 4px;
      flex: 1;
      padding: 0 16px;
    }

    .bus-info {
      font-size: 10px;
      font-weight: 700;
      color: var(--color-muted);
    }

    .arrow-visual {
      display: flex;
      align-items: center;
      gap: 6px;
      width: 100%;
    }

    .arrow-dot {
      width: 6px;
      height: 6px;
      border-radius: 50%;
      background: var(--color-coral);
    }

    .arrow-line {
      flex: 1;
      height: 2px;
      background: #D3DCB2;
    }

    .bus-icon-svg {
      width: 18px;
      height: 18px;
      color: #77766F;
    }

    .direct-label {
      font-size: 9px;
      font-weight: 800;
      letter-spacing: 1px;
      color: var(--color-leaf);
    }

    .ticket-details-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 12px;
    }

    .detail-box {
      display: flex;
      flex-direction: column;
      gap: 3px;
      padding: 12px 16px;
      background: rgba(255, 255, 255, 0.6);
      border-radius: 12px;
      border: 1px solid rgba(0, 0, 0, 0.04);
    }

    .detail-label {
      font-size: 9.5px;
      font-weight: 800;
      letter-spacing: 0.8px;
      color: var(--color-muted);
    }

    .detail-val {
      font-size: 14px;
      font-weight: 800;
      color: var(--color-ink);
    }

    .seat-highlight {
      color: #2563EB;
      font-size: 15px;
    }

    .price-val {
      color: #1b6317;
      font-size: 15px;
    }

    .mono {
      font-family: monospace;
      font-size: 12px;
      word-break: break-all;
    }

    .security-banner {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 10px 14px;
      background: rgba(0, 0, 0, 0.03);
      border-radius: 10px;
      font-size: 11px;
      color: #4b5563;
      line-height: 1.4;
    }

    .shield-svg {
      width: 16px;
      height: 16px;
      color: #4b5563;
      flex-shrink: 0;
    }

    /* Ticket Divider & Notches */
    .ticket-divider {
      width: 24px;
      display: flex;
      flex-direction: column;
      align-items: center;
      position: relative;
      margin: -26px 0;
    }

    .ticket-divider .circle {
      width: 24px;
      height: 24px;
      background: #D8E2DC;
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
      margin: 18px 0;
    }

    /* Right Stub */
    .ticket-right {
      width: 200px;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      gap: 12px;
      padding: 10px 0;
    }

    .qr-wrapper {
      padding: 8px;
      background: #ffffff;
      border-radius: 14px;
      box-shadow: 0 4px 14px rgba(0, 0, 0, 0.05);
    }

    .scan-instruction {
      font-size: 10.5px;
      color: var(--color-muted);
      text-align: center;
      font-weight: 600;
    }

    .barcode-stub {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 2px;
    }

    .security-code {
      font-size: 11px;
      font-weight: 800;
      color: var(--color-ink);
    }

    .security-hash {
      font-size: 9px;
      font-family: monospace;
      color: #9CA3AF;
    }

    /* Footer */
    .ticket-footer {
      display: flex;
      gap: 14px;
      padding: 18px 28px;
      background: rgba(255, 255, 255, 0.5);
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

    .action-btn-svg {
      width: 16px;
      height: 16px;
    }

    .btn-print {
      background: var(--color-ink);
      color: #ffffff;
    }

    .btn-print:hover {
      background: #333330;
      transform: translateY(-1px);
    }

    .btn-home {
      background: var(--color-coral);
      color: #ffffff;
    }

    .btn-home:hover {
      opacity: 0.95;
      transform: translateY(-1px);
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
      :host {
        -webkit-print-color-adjust: exact !important;
        print-color-adjust: exact !important;
      }
      .no-print {
        display: none !important;
      }
      .ticket-container {
        max-width: 100% !important;
        padding: 0 !important;
        margin: 0 !important;
      }
      .ticket-card {
        box-shadow: none !important;
        border: 1px solid #d1d5db !important;
        background: #ffffff !important;
      }
      .ticket-banner {
        background: #1D1D1B !important;
        color: #ffffff !important;
      }
    }
  `]
})
export class TicketViewComponent {
  @Input({ required: true }) ticket!: TicketResponse;
  @Input({ required: true }) trip!: Trip;
  @Output() onNewBooking = new EventEmitter<void>();

  get verificationUrl(): string {
    const origin = typeof window !== 'undefined' ? window.location.origin : 'http://localhost:4200';
    return `${origin}/verify-ticket?ticketNumber=${encodeURIComponent(this.ticket.ticketNumber)}`;
  }

  onPrint(): void {
    const originalTitle = document.title;
    document.title = `BusBook-Ticket-${this.ticket.ticketNumber}`;
    window.print();
    document.title = originalTitle;
  }

  formatTime(isoString: string): string {
    if (!isoString) return '';
    const parts = isoString.split('T');
    return parts.length > 1 ? parts[1].substring(0, 5) : isoString;
  }

  formatDate(isoString: string): string {
    if (!isoString) return '';
    return isoString.split('T')[0];
  }
}