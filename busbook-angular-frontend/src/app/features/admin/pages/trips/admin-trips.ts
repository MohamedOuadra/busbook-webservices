import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AdminService, Bus, CreateTripRequest } from '../../../../core/services/admin.service';
import { Trip } from '../../../trips/models/trip';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-admin-trips',
  standalone: true,
  imports: [ReactiveFormsModule, DatePipe],
  templateUrl: './admin-trips.html',
  styleUrl: './admin-trips.css',
})
export class AdminTrips implements OnInit {
  private adminService = inject(AdminService);
  private fb = inject(FormBuilder);

  trips = signal<Trip[]>([]);
  buses = signal<Bus[]>([]);
  loading = signal(true);
  saving = signal(false);
  error = signal('');
  success = signal('');

  showForm = signal(false);
  editingTrip = signal<Trip | null>(null);

  form: FormGroup = this.fb.group({
    villeDepart:  ['', Validators.required],
    villeArrivee: ['', Validators.required],
    dateDepart:   ['', Validators.required],
    dateArrivee:  ['', Validators.required],
    prix:         [null, [Validators.required, Validators.min(1)]],
    busId:        [null, Validators.required],
  });

  ngOnInit(): void {
    this.loadAll();
  }

  loadAll(): void {
    this.loading.set(true);
    this.adminService.getTrips().subscribe({
      next: trips => {
        this.trips.set(trips);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les trajets. Vérifiez que catalog-service est démarré.');
        this.loading.set(false);
      },
    });

    this.adminService.getBuses().subscribe({
      next: buses => this.buses.set(buses),
    });
  }

  openCreate(): void {
    this.form.reset();
    this.editingTrip.set(null);
    this.showForm.set(true);
    this.error.set('');
    this.success.set('');
  }

  openEdit(trip: Trip): void {
    this.editingTrip.set(trip);
    this.form.patchValue({
      villeDepart:  trip.villeDepart,
      villeArrivee: trip.villeArrivee,
      dateDepart:   trip.dateDepart.substring(0, 16),
      dateArrivee:  trip.dateArrivee.substring(0, 16),
      prix:         trip.prix,
      busId:        trip.bus?.id,
    });
    this.showForm.set(true);
    this.error.set('');
    this.success.set('');
  }

  closeForm(): void {
    this.showForm.set(false);
    this.editingTrip.set(null);
    this.form.reset();
  }

  submit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }

    this.saving.set(true);
    this.error.set('');

    const payload: CreateTripRequest = this.form.value;
    const editing = this.editingTrip();

    const request$ = editing
      ? this.adminService.updateTrip(editing.id, payload)
      : this.adminService.createTrip(payload);

    request$.subscribe({
      next: () => {
        this.success.set(editing ? 'Trajet modifié avec succès !' : 'Trajet créé avec succès !');
        this.saving.set(false);
        this.closeForm();
        this.loadAll();
      },
      error: (err) => {
        this.error.set(err.error?.message || 'Une erreur est survenue.');
        this.saving.set(false);
      },
    });
  }

  deleteTrip(trip: Trip): void {
    if (!confirm(`Supprimer le trajet ${trip.villeDepart} vers ${trip.villeArrivee} ?`)) return;

    this.adminService.deleteTrip(trip.id).subscribe({
      next: () => {
        this.success.set('Trajet supprimé.');
        this.loadAll();
      },
      error: () => this.error.set('Impossible de supprimer ce trajet.'),
    });
  }

  hasError(field: string): boolean {
    const control = this.form.get(field);
    return !!(control?.invalid && control?.touched);
  }
}
