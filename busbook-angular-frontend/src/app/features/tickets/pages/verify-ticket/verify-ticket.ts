import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { TicketApiService } from '../../../../core/services/ticket.service';
import { TicketResponse } from '../../../../core/models/ticket.model';
import { QrCodeComponent } from '../../../../shared/components/qr-code/qr-code.component';

@Component({
  selector: 'app-verify-ticket-page',
  imports: [CommonModule, FormsModule, RouterLink, QrCodeComponent],
  templateUrl: './verify-ticket.html',
  styleUrl: './verify-ticket.css',
})
export class VerifyTicketPage implements OnInit {
  ticketNumber = '';
  ticket?: TicketResponse;

  isLoading = false;
  hasSearched = false;
  errorMessage: string | null = null;

  private ticketApi = inject(TicketApiService);
  private route = inject(ActivatedRoute);
  private cdr = inject(ChangeDetectorRef);

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
    this.cdr.markForCheck();

    this.ticketApi.verifyTicket(trimmed).subscribe({
      next: (tkt) => {
        this.ticket = tkt;
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage =
          err.error?.message || `No valid ticket found with number "${trimmed}".`;
        this.cdr.markForCheck();
      },
    });
  }
}
