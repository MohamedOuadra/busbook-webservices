export interface Trip {
  id: number;
  villeDepart: string;
  villeArrivee: string;
  dateDepart: string;
  dateArrivee: string;
  prix: number;

  bus: {
    id: number;
    immatriculation: string;
    capacite: number;
    type: string;
  };
}