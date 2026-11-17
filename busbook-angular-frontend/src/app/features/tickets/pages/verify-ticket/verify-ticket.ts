import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { TicketApiService } from '../../../../core/services/ticket.service';
import { TicketResponse } from '../../../../core/models/ticket.model';
import { QrCodeComponent } from '../../../../shared/components/qr-code/qr-code.component';

@Component({
  selector: 'app-verify-ticket-page',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, QrCodeComponent],
  templateUrl: './verify-ticket.html',
  styleUrl: './verify-ticket.css',
})
export class VerifyTicketPage implements OnInit {
  ticketNumber = '';

  private readonly _ticket = signal<TicketResponse | undefined>(undefined);
  private readonly _isLoading = signal(false);
  private readonly _hasSearched = signal(false);
  private readonly _errorMessage = signal<string | null>(null);

  get ticket(): TicketResponse | undefined {
    return this._ticket();
  }
  set ticket(val: TicketResponse | undefined) {
    this._ticket.set(val);
  }

  get isLoading(): boolean {
    return this._isLoading();
  }
  set isLoading(val: boolean) {
    this._isLoading.set(val);
  }

  get hasSearched(): boolean {
    return this._hasSearched();
  }
  set hasSearched(val: boolean) {
    this._hasSearched.set(val);
  }

  get errorMessage(): string | null {
    return this._errorMessage();
  }
  set errorMessage(val: string | null) {
    this._errorMessage.set(val);
  }

  private ticketApi = inject(TicketApiService);
  private route = inject(ActivatedRoute);

  ngOnInit(): void {
    this.route.queryParamMap.subscribe((params) => {
      const code = params.get('ticketNumber') || params.get('code');
      if (code) {
        this.ticketNumber = code.trim();
        this.onVerify();
      }
    });
  }

  onVerify(): void {
    const trimmed = this.ticketNumber.trim();
    if (!trimmed) return;

    this.isLoading = true;
    this.errorMessage = null;
    this.ticket = undefined;
    this.hasSearched = true;

    this.ticketApi.verifyTicket(trimmed).subscribe({
      next: (tkt) => {
        this.ticket = tkt;
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage =
          err.error?.message || `No valid ticket found with number "${trimmed}".`;
      },
    });
  }

  fillSample(sample: string): void {
    this.ticketNumber = sample;
    this.onVerify();
  }

  formatDate(dateStr?: string): string {
    if (!dateStr) return '—';
    try {
      const d = new Date(dateStr);
      return d.toLocaleDateString('fr-FR', {
        day: '2-digit',
        month: 'short',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
      });
    } catch {
      return dateStr;
    }
  }
}
