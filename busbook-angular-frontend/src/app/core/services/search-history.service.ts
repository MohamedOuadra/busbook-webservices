import { Injectable, signal } from '@angular/core';
import { TripSearch } from '../../features/trips/models/trip-search';
import { isTravelDate } from '../../shared/utils/travel-date';

@Injectable({ providedIn: 'root' })
export class SearchHistoryService {
  private readonly key = 'busbook_recent_searches_v1';
  private readonly entries = signal<TripSearch[]>(this.load());
  readonly recent = this.entries.asReadonly();

  remember(criteria: TripSearch): boolean {
    const entry = { ...criteria, departure: criteria.departure.trim(), destination: criteria.destination.trim() };
    if (!entry.departure || !entry.destination || !isTravelDate(entry.date)) return true;
    const next = [entry, ...this.entries().filter(item =>
      item.departure.toLowerCase() !== entry.departure.toLowerCase() ||
      item.destination.toLowerCase() !== entry.destination.toLowerCase() || item.date !== entry.date
    )].slice(0, 5);
    return this.save(next);
  }

  clear(): boolean { return this.save([]); }

  private save(entries: TripSearch[]): boolean {
    try {
      localStorage.setItem(this.key, JSON.stringify(entries));
      this.entries.set(entries);
      return true;
    } catch {
      return false;
    }
  }

  private load(): TripSearch[] {
    try {
      const value: unknown = JSON.parse(localStorage.getItem(this.key) || '[]');
      if (!Array.isArray(value)) return [];
      return value.filter((item): item is TripSearch => item &&
        typeof item.departure === 'string' && !!item.departure.trim() &&
        typeof item.destination === 'string' && !!item.destination.trim() &&
        typeof item.date === 'string' && isTravelDate(item.date)
      ).slice(0, 5);
    } catch {
      return [];
    }
  }
}
