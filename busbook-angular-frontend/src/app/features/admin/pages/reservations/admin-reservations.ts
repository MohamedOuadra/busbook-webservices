import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { AdminReservation, AdminService } from '../../../../core/services/admin.service';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-admin-reservations',
  standalone: true,
  imports: [DatePipe],
  templateUrl: './admin-reservations.html',
  styleUrl: './admin-reservations.css',
})
export class AdminReservations implements OnInit {
  private adminService = inject(AdminService);

  reservations = signal<AdminReservation[]>([]);
  loading = signal(true);
  error = signal('');
  success = signal('');
  filterStatus = signal<string>('ALL');

  filtered = computed(() => {
    const filter = this.filterStatus();
    const all = this.reservations();
    if (filter === 'ALL') return all;
    return all.filter(r => this.normalizeStatus(r.status) === filter);
  });

  readonly filterOptions = [
    { key: 'ALL', label: 'Toutes les réservations' },
    { key: 'CONFIRMED', label: 'Confirmées' },
    { key: 'PENDING', label: 'En attente' },
    { key: 'CANCELLED', label: 'Annulées' },
    { key: 'EXPIRED', label: 'Expirées' },
  ];

  ngOnInit(): void {
    this.loadReservations();
  }

  loadReservations(): void {
    this.loading.set(true);
    this.error.set('');
    this.adminService.getAllReservations().subscribe({
      next: (res) => {
        this.reservations.set(res || []);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les réservations. Vérifiez que reservation-service est démarré.');
        this.loading.set(false);
      },
    });
  }

  setFilter(status: string): void {
    this.filterStatus.set(status);
  }

  cancelReservation(r: AdminReservation): void {
    if (!confirm(`Confirmer l'annulation de la réservation #${r.id} ?`)) return;
    this.adminService.cancelReservation(r.id).subscribe({
      next: () => {
        this.success.set(`La réservation #${r.id} a été annulée avec succès.`);
        this.loadReservations();
      },
      error: () => this.error.set("Impossible d'annuler cette réservation."),
    });
  }

  normalizeStatus(status: string): string {
    if (!status) return 'UNKNOWN';
    const s = status.toUpperCase();
    if (s === 'CONFIRMED' || s === 'CONFIRMEE') return 'CONFIRMED';
    if (s === 'PENDING' || s === 'EN_ATTENTE') return 'PENDING';
    if (s === 'CANCELLED' || s === 'ANNULEE') return 'CANCELLED';
    if (s === 'EXPIRED' || s === 'EXPIREE') return 'EXPIRED';
    return s;
  }

  statusLabel(status: string): string {
    const norm = this.normalizeStatus(status);
    const labels: Record<string, string> = {
      CONFIRMED: 'Confirmée',
      PENDING: 'En attente',
      CANCELLED: 'Annulée',
      EXPIRED: 'Expirée',
    };
    return labels[norm] ?? status;
  }

  getStatusClass(status: string): string {
    return 'status-' + this.normalizeStatus(status).toLowerCase();
  }

  canCancel(status: string): boolean {
    const norm = this.normalizeStatus(status);
    return norm === 'PENDING' || norm === 'CONFIRMED';
  }

  getSeatDisplay(r: AdminReservation): string {
    if (r.seatNumber) return `Siège ${r.seatNumber}`;
    if (r.seatNumbers && r.seatNumbers.length > 0) return `Sièges: ${r.seatNumbers.join(', ')}`;
    if (r.seatId) return `Siège #${r.seatId}`;
    return '—';
  }

  getTravelerId(r: AdminReservation): number | string {
    return r.travelerId || r.userId || '—';
  }
}
