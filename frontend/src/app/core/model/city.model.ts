export enum CityStatus {
  PENDING = 'PENDING',
  APPROVED = 'APPROVED',
}

export type City = {
  id: string;
  name: string;
  country: string;
  description: string;
  population: number;
  imageUrl: string;
  status: CityStatus;
}
