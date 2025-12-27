import { FileTypeEnum } from './file-type.enum';

export interface FileStore {
  id?: string;
  ownerUserId?: string;
  relatedEntityType?: string;
  relatedEntityId?: string;
  fileKey?: string;
  fileName?: string;
  fileType?: FileTypeEnum;
  sizeBytes?: number;
  checksum?: string;
  uploadedById?: string;
  uploadedAt?: string;
}
