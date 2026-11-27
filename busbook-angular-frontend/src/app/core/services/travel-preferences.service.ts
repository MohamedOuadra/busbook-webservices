import { Injectable } from '@angular/core';

export interface TravelPreferences {
  preferredSeat: string;
  defaultCity: string;
  smsNotifications: boolean;
  emailNotifications: boolean;
  promoAlerts: boolean;
  currency: string;
  language: string;
}

export const DEFAULT_TRAVEL_PREFERENCES: TravelPreferences = {
  preferredSeat: 'window', defaultCity: 'Casablanca', smsNotifications: true,
  emailNotifications: true, promoAlerts: false, currency: 'MAD', language: 'fr',
};

@Injectable({ providedIn: 'root' })
export class TravelPreferencesService {
  load(userId: number | null): TravelPreferences {
    try {
      return this.normalize(JSON.parse(localStorage.getItem(this.key(userId)) || 'null'));
    } catch {
      return { ...DEFAULT_TRAVEL_PREFERENCES };
    }
  }

  save(userId: number | null, preferences: TravelPreferences): boolean {
    try {
      localStorage.setItem(this.key(userId), JSON.stringify(this.normalize(preferences)));
      return true;
    } catch {
      return false;
    }
  }

  private key(userId: number | null): string {
    return `busbook_travel_preferences_v1_${userId ?? 'guest'}`;
  }

  private normalize(value: unknown): TravelPreferences {
    const data = value && typeof value === 'object' ? value as Record<string, unknown> : {};
    const choice = (field: keyof TravelPreferences, allowed: string[]) =>
      typeof data[field] === 'string' && allowed.includes(data[field] as string)
        ? data[field] as string : DEFAULT_TRAVEL_PREFERENCES[field] as string;
    const flag = (field: 'smsNotifications' | 'emailNotifications' | 'promoAlerts') =>
      typeof data[field] === 'boolean' ? data[field] as boolean : DEFAULT_TRAVEL_PREFERENCES[field];
    return {
      preferredSeat: choice('preferredSeat', ['window', 'aisle', 'any']),
      defaultCity: choice('defaultCity', ['Casablanca', 'Marrakech', 'Agadir', 'Rabat', 'Tanger', 'Fes']),
      currency: choice('currency', ['MAD', 'EUR', 'USD']),
      language: choice('language', ['fr', 'en', 'ar']),
      smsNotifications: flag('smsNotifications'), emailNotifications: flag('emailNotifications'),
      promoAlerts: flag('promoAlerts'),
    };
  }
}
