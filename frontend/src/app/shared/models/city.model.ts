export enum CityStatus {
  PENDING = 'PENDING',
  APPROVED = 'APPROVED'
}

export interface City {
  id?: string;
  name: string;
  country: string;
  description: string;
  population: number;
  imageUrl: string;
  status: CityStatus;
}

