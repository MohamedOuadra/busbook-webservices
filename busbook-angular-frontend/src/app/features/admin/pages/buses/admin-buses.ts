import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AdminService, Bus } from '../../../../core/services/admin.service';

@Component({
  selector: 'app-admin-buses',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './admin-buses.html',
  styleUrl: './admin-buses.css',
})
export class AdminBuses implements OnInit {
  private adminService = inject(AdminService);
  private fb = inject(FormBuilder);

  buses = signal<Bus[]>([]);
  loading = signal(true);
  saving = signal(false);
  error = signal('');
  success = signal('');

  showForm = signal(false);
  editingBus = signal<Bus | null>(null);

  readonly busTypes = ['Standard', 'Confort', 'VIP', 'Executive'];

  form: FormGroup = this.fb.group({
    immatriculation: ['', [Validators.required, Validators.minLength(4)]],
    capacite:        [null, [Validators.required, Validators.min(10), Validators.max(100)]],
    type:            ['', Validators.required],
  });

  ngOnInit(): void {
    this.loadBuses();
  }

  loadBuses(): void {
    this.loading.set(true);
    this.adminService.getBuses().subscribe({
      next: buses => { this.buses.set(buses); this.loading.set(false); },
      error: () => {
        this.error.set('Impossible de charger les bus. Vérifiez que catalog-service est démarré.');
        this.loading.set(false);
      },
    });
  }

  openCreate(): void {
    this.form.reset();
    this.editingBus.set(null);
    this.showForm.set(true);
    this.error.set('');
    this.success.set('');
  }

  openEdit(bus: Bus): void {
    this.editingBus.set(bus);
    this.form.patchValue({ immatriculation: bus.immatriculation, capacite: bus.capacite, type: bus.type });
    this.showForm.set(true);
    this.error.set('');
    this.success.set('');
  }

  closeForm(): void {
    this.showForm.set(false);
    this.editingBus.set(null);
    this.form.reset();
  }

  submit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }

    this.saving.set(true);
    this.error.set('');

    const payload: Omit<Bus, 'id'> = this.form.value;
    const editing = this.editingBus();

    const request$ = editing?.id
      ? this.adminService.updateBus(editing.id, payload)
      : this.adminService.createBus(payload);

    request$.subscribe({
      next: () => {
        this.success.set(editing ? 'Bus modifié avec succès !' : 'Bus créé avec succès !');
        this.saving.set(false);
        this.closeForm();
        this.loadBuses();
      },
      error: (err) => {
        this.error.set(err.error?.message || 'Une erreur est survenue.');
        this.saving.set(false);
      },
    });
  }

  deleteBus(bus: Bus): void {
    if (!confirm(`Supprimer le bus ${bus.immatriculation} ?`)) return;

    this.adminService.deleteBus(bus.id!).subscribe({
      next: () => { this.success.set('Bus supprimé.'); this.loadBuses(); },
      error: () => this.error.set('Impossible de supprimer ce bus (il est peut-être utilisé dans un trajet).'),
    });
  }

  hasError(field: string): boolean {
    const c = this.form.get(field);
    return !!(c?.invalid && c?.touched);
  }
}
