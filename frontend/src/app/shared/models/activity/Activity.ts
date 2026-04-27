export type ActivityType = 'REVIEW_CREATED' | 'POI_CREATED' | 'QUEST_COMPLETED';

export interface ActivityDto {
  id: string;
  userId: string;
  username: string;
  userAvatarUrl?: string;
  activityType: ActivityType;
  actionDescription: string;
  targetId: string;
  targetType: string;
  targetName: string;
  targetData?: string;
  createdAt: string;
}
