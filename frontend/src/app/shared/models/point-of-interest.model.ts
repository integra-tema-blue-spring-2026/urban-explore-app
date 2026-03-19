export enum PointOfInterestType {
  MUSEUM = 'MUSEUM',
  PARK = 'PARK',
  CAFE = 'CAFE',
}

export interface PointOfInterest {
  id: string;
  name: string;
  description: string;
  address: string;
  type: PointOfInterestType;
  cityId: string;
}

export interface PointOfInterestFilter {
  name?: string;
  description?: string;
}
