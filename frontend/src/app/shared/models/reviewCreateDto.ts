export interface ReviewCreateDto {
    text: string;
    rating: number;
    userId: number;
    poiId: number;
}