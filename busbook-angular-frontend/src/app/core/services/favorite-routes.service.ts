import { Injectable, signal } from '@angular/core';

export interface FavoriteRoute { departure: string; destination: string; }

function sameRoute(a: FavoriteRoute, b: FavoriteRoute): boolean {
  return a.departure.toLowerCase() === b.departure.toLowerCase() &&
    a.destination.toLowerCase() === b.destination.toLowerCase();
}

@Injectable({ providedIn: 'root' })
export class FavoriteRoutesService {
  private readonly key = 'busbook_favorite_routes_v1';
  private readonly entries = signal<FavoriteRoute[]>(this.load());
  readonly routes = this.entries.asReadonly();

  add(route: FavoriteRoute): boolean {
    const entry = { departure: route.departure.trim(), destination: route.destination.trim() };
    if (!entry.departure || !entry.destination) return false;
    if (this.entries().some(item => sameRoute(item, entry))) return true;
    if (this.entries().length >= 20) return false;
    return this.save([...this.entries(), entry]);
  }

  remove(route: FavoriteRoute): boolean {
    return this.save(this.entries().filter(item => !sameRoute(item, route)));
  }

  private save(entries: FavoriteRoute[]): boolean {
    try {
      localStorage.setItem(this.key, JSON.stringify(entries));
      this.entries.set(entries);
      return true;
    } catch {
      return false;
    }
  }

  private load(): FavoriteRoute[] {
    try {
      const value: unknown = JSON.parse(localStorage.getItem(this.key) || '[]');
      if (!Array.isArray(value)) return [];
      const routes: FavoriteRoute[] = [];
      for (const item of value) {
        if (!item || typeof item.departure !== 'string' || typeof item.destination !== 'string') continue;
        const entry = { departure: item.departure.trim(), destination: item.destination.trim() };
        if (entry.departure && entry.destination && !routes.some(route => sameRoute(route, entry))) routes.push(entry);
        if (routes.length === 20) break;
      }
      return routes;
    } catch {
      return [];
    }
  }
}
