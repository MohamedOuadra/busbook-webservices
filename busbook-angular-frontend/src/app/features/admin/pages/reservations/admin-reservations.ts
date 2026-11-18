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
    const status = this.filterStatus();
    const all = this.reservations();
    if (status === 'ALL') return all;
    return all.filter(r => r.status === status);
  });

  readonly statuses = ['ALL', 'PENDING', 'CONFIRMED', 'CANCELLED', 'EXPIRED'];

  ngOnInit(): void {
    this.loadReservations();
  }

  loadReservations(): void {
    this.loading.set(true);
    this.adminService.getAllReservations().subscribe({
      next: res => { this.reservations.set(res); this.loading.set(false); },
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
    if (!confirm(`Annuler la réservation #${r.id} ?`)) return;
    this.adminService.cancelReservation(r.id).subscribe({
      next: () => { this.success.set(`Réservation #${r.id} annulée.`); this.loadReservations(); },
      error: () => this.error.set('Impossible d\'annuler cette réservation.'),
    });
  }

  statusLabel(status: string): string {
    const labels: Record<string, string> = {
      PENDING: 'En attente', CONFIRMED: 'Confirmée',
      CANCELLED: 'Annulée', EXPIRED: 'Expirée',
    };
    return labels[status] ?? status;
  }
}
