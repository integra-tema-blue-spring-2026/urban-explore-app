export interface poi {
  id?: string;
  name: string;
  description: string;
  address: string;
  type: string;
  cityId: string;
  status?: PoiStatus;
}

export enum PoiStatus {
  PENDING = 'PENDING',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED',
}
