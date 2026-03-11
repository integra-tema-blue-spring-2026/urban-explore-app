export enum PointOfInterestType {
  MUSEUM = 'MUSEUM',
  PARK = 'PARK',
  CAFE = 'CAFE',
}

export type PointOfInterest = {
  id: string;
  name: string;
  description: string;
  address: string;
  type: PointOfInterestType;
  cityId: string;
}

export type PointOfInterestFilter = {
  name?: string;
  description?: string;
}
