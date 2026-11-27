import { isTravelDate, todayDate } from './travel-date';

describe('travel dates', () => {
  it('formats local calendar values rather than UTC values', () => {
    vi.useFakeTimers();
    try {
      vi.setSystemTime(new Date(2026, 10, 24, 0, 15));
      expect(todayDate()).toBe('2026-11-24');
    } finally {
      vi.useRealTimers();
    }
  });

  it('rejects impossible dates and accepts leap days', () => {
    expect(isTravelDate('2026-02-30')).toBe(false);
    expect(isTravelDate('2026-02-29')).toBe(false);
    expect(isTravelDate('2028-02-29')).toBe(true);
    expect(isTravelDate('not-a-date')).toBe(false);
  });
});
